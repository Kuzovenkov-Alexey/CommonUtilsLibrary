package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class TCPClient<R, W> extends TCPConnection<R, W> implements Connection<R, W> {

    protected final Selector selector = Selector.open();
    protected final Thread worker;



    public TCPClient(Listener<R, W> listener, Serialization<R, W> serialization,
                     int inputSize, int outputSize) throws IOException {
        super(listener, serialization, ByteBuffer.allocate(inputSize), ByteBuffer.allocate(outputSize));
        try {
            socketChannel = selector.provider().openSocketChannel();
            selectionKey = socketChannel.configureBlocking(false).register(selector,
                    SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
            worker = new Thread(new Worker());
            worker.start();
        } catch (Throwable e) {
            if (socketChannel != null) close(); else selector.close();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public TCPClient(Serialization<R, W> serialization, int inputSize, int outputSize) throws IOException {
        this(Listener.DEFAULT, serialization, inputSize, outputSize);
    }

    public TCPClient(Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(serialization, bufferSizes, bufferSizes);
    }

    public TCPClient(Serialization<R, W> serialization) throws IOException {
        this(serialization, 16384);
    }

    public TCPClient(Listener<R, W> listener, Serialization<R, W> serialization, int bufferSizes) throws IOException {
        this(listener, serialization, bufferSizes, bufferSizes);
    }

    public TCPClient(Listener<R, W> listener, Serialization<R, W> serialization) throws IOException {
        this(listener, serialization, 16384);
    }



    public void initialize(int port) throws IOException {
        if (socketChannel.connect(new InetSocketAddress(port))) selector.wakeup();
    }

    public void initialize(@NotNull String address, int port) throws IOException {
        if (socketChannel.connect(new InetSocketAddress(address, port))) selector.wakeup();
    }

    @Override
    public void initialize(@NotNull String address) throws IOException {
        if (socketChannel.connect(toInetSocketAddress(address))) selector.wakeup();
    }

    @Override
    public void shutdown() {
        worker.interrupt();
    }

    @Override
    public void awaitTermination(int milliseconds) throws InterruptedException {
        worker.join(milliseconds);
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
    protected void wakeup() {
        if (Thread.currentThread() != worker) selector.wakeup();
    }

    @Override
    protected void close() throws IOException {
        try { super.close(); } finally { selector.close(); }
    }



    protected class Worker implements Runnable {

        @Override
        public void run() {
            try {
                do {
                    if (worker.isInterrupted()) return;
                    try {
                        int selected = selector.select();
                        if (socketChannel.isConnected()) break;
                        if (selected == 0) continue;
                        selector.selectedKeys().clear();
                        if (socketChannel.finishConnect()) break;
                    } catch (Throwable e) { notifyErrorSilently(e); }
                } while (true);
                notifyConnect();
                while (!worker.isInterrupted()) {
                    try {
                        if (selector.select() == 0) continue;
                        int readyOps = selectionKey.readyOps();
                        selector.selectedKeys().clear();
                        if (readyOps != SelectionKey.OP_READ) {
                            writeOperation();
                            if (readyOps == SelectionKey.OP_WRITE) return;
                        }
                        if (read()) break;
                        receive();
                    } catch (Throwable e) { notifyErrorSilently(e); }
                }
                notifyDisconnect();
            } catch (Throwable e) {
                notifyErrorSilently(e);
            } finally {
                safeClose();
            }
        }

    }
}
