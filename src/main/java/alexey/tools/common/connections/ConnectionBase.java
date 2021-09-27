package alexey.tools.common.connections;

import java.io.IOException;
import java.net.SocketOption;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectionKey;

public abstract class ConnectionBase<R, W> implements Connection<R, W> {

    protected volatile Listener<R, W> listener;
    protected          SelectionKey   selectionKey;



    protected ConnectionBase(Listener<R, W> listener) {
        this.listener = listener;
    }



    public <T> void setOption(SocketOption<T> name, T value) throws IOException {
        getChannel().setOption(name, value);
    }

    public <T> T getOption(SocketOption<T> name) throws IOException {
        return getChannel().getOption(name);
    }



    protected abstract NetworkChannel getChannel();

    protected abstract void close() throws IOException;

    protected abstract void wakeup();

    protected void safeClose() {
        try { close(); } catch (Throwable e) { notifyErrorSilently(e); }
    }

    protected void wantOperation(int op, boolean value) {
        if (value)
            selectionKey.interestOpsOr(op); else
            selectionKey.interestOpsAnd(~op);
        wakeup();
    }

    protected boolean hasOperation(int op) {
        return (selectionKey.interestOps() & op) != 0;
    }

    protected void notifyError(Throwable e) {
        listener.onError(this, e);
    }

    protected void notifyErrorSilently(Throwable e) {
        try { notifyError(e); } catch (Throwable ignored) { }
    }

    protected void notifyDisconnect() {
        listener.onDisconnect(this);
    }

    protected void notifyRead(R message) {
        listener.onRead(this, message);
    }

    protected void notifyConnect() {
        listener.onConnect(this);
    }

    protected void notifyWrite() {
        listener.onWrite(this);
    }



    @Override
    public boolean wantRead() {
        return hasOperation(SelectionKey.OP_READ);
    }

    @Override
    public boolean wantConnect() {
        return hasOperation(SelectionKey.OP_CONNECT);
    }

    @Override
    public void wantRead(boolean value) {
        wantOperation(SelectionKey.OP_READ, value);
    }

    @Override
    public void wantConnect(boolean value) {
        wantOperation(SelectionKey.OP_CONNECT, value);
    }

    @Override
    public void setListener(Listener<R, W> listener) {
        if (listener == null) throw new NullPointerException("Listener can't be null!");
        this.listener = listener;
    }

    @Override
    public Listener<R, W> getListener() {
        return listener;
    }
}
