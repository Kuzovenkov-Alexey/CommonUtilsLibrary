package alexey.tools.common.connections;

import alexey.tools.common.collections.IndexedObject;
import alexey.tools.common.collections.IndexedObjectCollection;
import alexey.tools.common.collections.ObjectStorage;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class TCPServer<R, W> extends ConnectionBase<R, W> {

    protected final Selector selector;
    protected final ServerSocketChannel serverSocketChannel;
    protected final Thread worker;
    protected final Serialization<R, W> serialization;
    protected final ByteBuffer output;
    protected final int inputSize;

    protected final IndexedObjectCollection<TCPServer.TCPRemoteClient> connections =
            new IndexedObjectCollection<>(4);

    protected final ObjectStorage<TCPServer.TCPRemoteClient> needDisconnect =
            new ObjectStorage<>( 2);



    public TCPServer(Listener<R, W> listener, Serialization<R, W> serialization,
                     int inputSize, int outputSize) throws IOException {
        super(listener);
        output = ByteBuffer.allocate(outputSize);
        selector = Selector.open();
        try {
            serverSocketChannel = selector.provider().openServerSocketChannel();
        } catch (Throwable e) {
            selector.close();
            throw e;
        }
        try {
            selectionKey = serverSocketChannel.configureBlocking(false).register(selector, SelectionKey.OP_ACCEPT);
            worker = new Thread(new Worker());
            worker.start();
        } catch (Throwable e) {
            try { serverSocketChannel.close(); } finally { selector.close(); }
            throw e;
        }
        this.serialization = serialization;
        this.inputSize = inputSize;
    }

    @SuppressWarnings("unchecked")
    public TCPServer(Serialization<R, W> serialization, int inputSize, int outputSize) throws IOException {
        this(Listener.DEFAULT, serialization, inputSize, outputSize);
    }

    public TCPServer(Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(serialization, bufferSizes, bufferSizes);
    }

    public TCPServer(Serialization<R, W> serialization) throws IOException {
        this(serialization, 16384);
    }

    public TCPServer(Listener<R, W> listener, Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(listener, serialization, bufferSizes, bufferSizes);
    }

    public TCPServer(Listener<R, W> listener, Serialization<R, W> serialization) throws IOException {
        this(listener, serialization, 16384);
    }



    protected void closeClients(@NotNull Iterator<SelectionKey> keys) throws IOException {
        if (keys.hasNext()) try { keys.next().channel().close(); } finally { closeClients(keys); }
    }

    protected void configureConnection(@NotNull TCPConnection<?, ?> client) throws IOException {
        client.setOption(StandardSocketOptions.SO_RCVBUF,    768  );
        client.setOption(StandardSocketOptions.SO_SNDBUF,    768  );
        client.setOption(StandardSocketOptions.TCP_NODELAY,  true );
        client.setOption(StandardSocketOptions.IP_TOS,       0x14 );
        client.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
    }



    @Override
    protected void close() throws IOException {
        try { closeClients(selector.keys().iterator()); } finally { selector.close(); }
    }

    @Override
    protected void wakeup() {
        if (Thread.currentThread() != worker) selector.wakeup();
    }

    @Override
    protected NetworkChannel getChannel() {
        return serverSocketChannel;
    }



    @Override
    public boolean send(W message) throws IOException {
        Set<SelectionKey> keys = selector.keys();
        boolean fullWrite = true;
        synchronized (output) {
            try {
                int start = output.position();
                int begin = start + 4;
                output.position(begin);
                serialization.write(this, output, message);
                int end = output.position();
                output.position(start);
                output.putInt(end - begin);
                output.limit(end);
                synchronized (connections) {
                    for (TCPServer.TCPRemoteClient connection : connections) {
                        output.rewind();
                        if (!connection.send(output)) fullWrite = false;
                    }
                }
            } finally {
                output.clear();
            }
        }
        return fullWrite;
    }

    @Override
    public void wantRead(boolean value) {
        if (value)
            synchronized (connections) {
                for (TCPServer.TCPRemoteClient connection : connections)
                    connection.selectionKey.interestOpsOr(SelectionKey.OP_READ);
            }
        else
            synchronized (connections) {
                for (TCPServer.TCPRemoteClient connection : connections)
                    connection.selectionKey.interestOpsAnd(~SelectionKey.OP_READ);
            }
        wakeup();
    }

    @Override
    public void wantConnect(boolean value) {
        wantOperation(SelectionKey.OP_ACCEPT, value);
    }

    @Override
    public boolean wantRead() {
        synchronized (connections) {
            for (TCPServer.TCPRemoteClient connection : connections) if (connection.wantRead()) return true;
        }
        return false;
    }

    @Override
    public boolean wantConnect() {
        return hasOperation(SelectionKey.OP_ACCEPT);
    }

    @Override
    public void initialize(String address) throws IOException {
        serverSocketChannel.bind(TCPConnection.toInetSocketAddress(address));
    }

    public void initialize(int port) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(port));
    }

    @Override
    public String getAddress() {
        ServerSocket socket = serverSocketChannel.socket();
        return socket.getInetAddress().getHostAddress() + '/' + socket.getLocalPort();
    }

    @Override
    public void shutdown() {
        worker.interrupt();
    }

    @Override
    public boolean isTerminated() {
        return !worker.isAlive();
    }

    @Override
    public boolean isTerminating() {
        return worker.isInterrupted() && worker.isAlive();
    }

    @Override
    public void awaitTermination(int milliseconds) throws InterruptedException {
        worker.join(milliseconds);
    }



    protected class Worker implements Runnable {

        @Override
        public void run() {
            try {
                while (!worker.isInterrupted()) {
                    try {
                        // ============================== Process clients ==============================
                        if (selector.select() != 0) {
                            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                            while (selectedKeys.hasNext()) {
                                processSelectedKey(selectedKeys.next());
                                selectedKeys.remove();
                            }
                        }
                        // ============================== Disconnect clients ==============================
                        TCPServer.TCPRemoteClient connection;
                        do {
                            synchronized (needDisconnect) {
                                if (needDisconnect.isEmpty()) break;
                                connection = needDisconnect.unsafeRemoveLast();
                            }
                            if (connection.index == 1) connection.shutdownNow();
                        } while (true);
                        // ============================== End ==============================
                    } catch (Throwable e) { notifyErrorSilently(e); }
                }
                synchronized (connections) {
                    while (connections.isNotEmpty()) connections.unsafeRemoveLast().disconnect();
                }
            } catch (Throwable e) {
                notifyErrorSilently(e);
            } finally {
                safeClose();
            }
        }

        protected void processSelectedKey(@NotNull SelectionKey selectedKey) throws IOException {
            TCPServer.TCPRemoteClient connection = (TCPServer.TCPRemoteClient) selectedKey.attachment();
            // ============================== Accept clients ==============================
            if (connection == null) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel == null) return;
                try {
                    connection = new TCPRemoteClient(serialization, ByteBuffer.allocate(inputSize),
                            ByteBuffer.allocate(output.capacity()), socketChannel);
                    try {
                        connection.notifyConnect();
                        configureConnection(connection);
                    } catch (Throwable e) {
                        connection.notifyErrorSilently(e);
                        connection.terminate();
                        return;
                    }
                    synchronized (connections) { connections.add(connection); }
                } catch (Throwable e) {
                    notifyErrorSilently(e);
                    socketChannel.close();
                }
                return;
            }
            // ============================== Read write clients ==============================
            try {
                int readyOps = selectionKey.readyOps();
                if (readyOps != SelectionKey.OP_READ) {
                    connection.writeOperation();
                    if (readyOps == SelectionKey.OP_WRITE) return;
                }
                connection.readOperation();
            } catch (Throwable e) {
                connection.notifyErrorSilently(e);
            }
            // ============================== End ==============================
        }
    }

    protected class TCPRemoteClient extends TCPConnection<R, W> implements IndexedObject {

        protected          int    index        = -1;
        protected          byte   shutdown     =  0;
        protected final    Object shutdownLock = new Object();
        protected volatile Object attachment;



        protected TCPRemoteClient(Serialization<R, W> serialization,
                                  ByteBuffer input, ByteBuffer output,
                                  @NotNull SocketChannel socketChannel) throws IOException {
            super(TCPServer.this.listener, serialization, input, output);
            selectionKey = socketChannel.configureBlocking(false).register(selector, SelectionKey.OP_READ, this);
            this.socketChannel = socketChannel;
        }



        protected void readOperation() throws IOException {
            if (read()) {
                synchronized (shutdownLock) {
                    if (shutdown != 0) return;
                    shutdown = 1;
                }
                shutdownNow();
            } else receive();
        }

        protected void shutdownNow() {
            synchronized (connections) { connections.unsafeRemoveReference(this); }
            disconnect();
        }

        protected void disconnect() {
            try {
                notifyDisconnect();
            } catch (Throwable e) {
                notifyErrorSilently(e);
            }
            terminate();
        }

        protected void terminate() {
            safeClose();
            synchronized (shutdownLock) {
                shutdown = 2;
                shutdownLock.notifyAll();
            }
        }



        @Override
        public void shutdown() {
            synchronized (shutdownLock) {
                if (shutdown != 0) return;
                shutdown = 1;
            }
            synchronized (needDisconnect) { needDisconnect.add(this); }
            wakeup();
        }

        @Override
        public boolean isTerminating() {
            synchronized (shutdownLock) {
                return shutdown == 1;
            }
        }

        @Override
        public boolean isTerminated() {
            synchronized (shutdownLock) {
                return shutdown == 2;
            }
        }

        @Override
        public void awaitTermination(int milliseconds) throws InterruptedException {
            synchronized (shutdownLock) {
                if (shutdown == 2) return;
                shutdownLock.wait(milliseconds);
            }
        }

        @Override
        protected void wakeup() {
            TCPServer.this.wakeup();
        }

        @Override
        public void attach(Object ob) {
            attachment = ob;
        }

        @Override
        public Object attachment() {
            return attachment;
        }

        @Override
        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }
    }
}
