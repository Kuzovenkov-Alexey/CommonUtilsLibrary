package alexey.tools.common.connections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract class TCPConnection<R, W> extends ConnectionBase<R, W> {

    final protected ByteBuffer          output;
    final protected ByteBuffer          input;
    final protected Serialization<R, W> serialization;
    protected       int                 currentObjectSize = 0;
    protected       boolean             outputRead        = false;
    protected       SocketChannel       socketChannel;



    protected TCPConnection(Listener<R, W> listener, Serialization<R, W> serialization,
                            ByteBuffer input, ByteBuffer output) {
        super(listener);
        this.serialization = serialization;
        this.input = input;
        this.output = output;
    }

    @SuppressWarnings("unchecked")
    protected TCPConnection(Serialization<R, W> serialization,
                            ByteBuffer input, ByteBuffer output) {
        this(Listener.DEFAULT, serialization, input, output);
    }



    protected void writeOperation() throws IOException {
        synchronized (output) {
            if (outputRead) {
                do {
                    if (socketChannel.write(output) == 0) return;

                } while (output.hasRemaining());
                outputRead = false;
            } else {
                output.flip();
                do {
                    if (socketChannel.write(output) == 0) { outputRead = true; return; }
                } while (output.hasRemaining());
            }
            output.clear();
            selectionKey.interestOpsAnd(~SelectionKey.OP_WRITE);
        }
        notifyWrite();
    }

    protected boolean send(ByteBuffer source) throws IOException {
        synchronized (output) {
            if (outputRead) {
                output.compact();
                outputRead = false;
            }
            int start = output.position();
            output.put(source);
            return start == 0 && write();
        }
    }

    protected boolean write() throws IOException {
        output.flip();
        do {
            if (socketChannel.write(output) == 0) {
                outputRead = true;
                selectionKey.interestOpsOr(SelectionKey.OP_WRITE);
                wakeup();
                return false;
            }
        } while (output.hasRemaining());
        output.clear();
        return true;
    }

    protected void receive() {
        int remaining = input.position();
        if (currentObjectSize == 0) {
            if (remaining < 4) return;
            input.flip();
            receiveObjectSize();
            if (input.hasRemaining()) {
                if ((remaining -= 4) < currentObjectSize) { input.compact(); return; }
            } else { input.clear(); return; }
        } else {
            if (remaining < currentObjectSize) return;
            input.flip();
        }
        int start, deserialized;
        R message;
        do {
            start = input.position();
            message = serialization.read(this, input);
            deserialized = input.position() - start;
            if (deserialized != currentObjectSize) throw new FakeObjectSize();
            notifyRead(message);
            if ((remaining -= deserialized) < 4) { currentObjectSize = 0; break; }
            receiveObjectSize();
        } while ((remaining -= 4) >= currentObjectSize);
        if (remaining == 0)
            input.clear(); else
            input.compact();
    }

    protected void receiveObjectSize() {
        currentObjectSize = input.getInt();
        if (currentObjectSize > input.capacity()) throw new BigObject(currentObjectSize);
    }

    protected boolean read() throws IOException {
        return socketChannel.read(input) == -1;
    }



    @Override
    protected void close() throws IOException {
        socketChannel.close();
    }

    @Override
    protected NetworkChannel getChannel() {
        return socketChannel;
    }



    @Override
    public boolean send(W message) throws IOException {
        synchronized (output) {
            if (outputRead) {
                output.compact();
                outputRead = false;
            }
            int start = output.position();
            try {
                int begin = start + 4;
                output.position(begin);
                serialization.write(this, output, message);
                int end = output.position();
                output.position(start);
                output.putInt(end - begin);
                output.position(end);
            } catch (Throwable e) {
                output.position(start);
                throw e;
            }
            return start == 0 && write();
        }
    }

    @Override
    public String getAddress() {
        Socket socket = socketChannel.socket();
        return socket.getInetAddress().getHostAddress() + '/' + socket.getPort();
    }

    @Override
    public void initialize(String address) throws IOException {
        throw new IllegalStateException("Already initialized!");
    }



    public static class BigObject extends IllegalStateException {
        final public int size;

        public BigObject(int size) {
            super("Big object (" + size + ")!");
            this.size = size;
        }
    }

    public static class FakeObjectSize extends IllegalStateException {
        public FakeObjectSize() {
            super("Fake object size!");
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static InetSocketAddress toInetSocketAddress(@NotNull String source) {
        int slash = source.lastIndexOf('/');
        return new InetSocketAddress(
                source.substring(0, slash),
                Integer.parseInt(source, slash + 1, source.length(), 10)
        );
    }
}
