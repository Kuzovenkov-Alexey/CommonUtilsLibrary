package alexey.tools.common.connections;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public interface Connection<R, W> {

    interface Listener <R, W> {
        default void onConnect(@NotNull Connection<R, W> connection) {}
        default void onDisconnect(@NotNull Connection<R, W> connection) {}

        default void onRead(@NotNull Connection<R, W> connection, R message) {}
        default void onWrite(@NotNull Connection<R, W> connection) {}

        default void onError(@NotNull Connection<R, W> connection, @NotNull Throwable error) {}

        Listener DEFAULT = new Listener() {};
    }

    default void setListener(Listener<R, W> listener) {}
    @SuppressWarnings("unchecked")
    default Listener<R, W> getListener() { return Listener.DEFAULT; }

    default boolean send(W message) throws IOException { return false; }

    default void wantRead(boolean value) {}
    default void wantConnect(boolean value) {}

    default boolean wantRead() { return false; }
    default boolean wantConnect() { return false; }

    default void initialize(String address) throws IOException {}
    default String getAddress() { return ""; }

    default void shutdown() {}

    default boolean isTerminating() { return false; }
    default boolean isTerminated() { return true; }

    default void awaitTermination(int milliseconds) throws InterruptedException {}
    default void awaitTermination() throws InterruptedException { awaitTermination(0); }

    default Object attachment() { return null; }
    default void attach(Object ob) {}

    Connection DEFAULT = new Connection() {};
}
