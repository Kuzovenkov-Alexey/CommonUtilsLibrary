package alexey.tools.common.event;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface EventBus {
    <T> void register(@NotNull Class<? extends T> type, @NotNull Consumer<T> listener);
    <T> void unregister(@NotNull Class<? extends T> type, @NotNull Consumer<T> listener);

    void post(@NotNull Object event);
    <T> void post(@NotNull T event, @NotNull Class<T> type);
    <T> void post(@NotNull Iterable<? extends T> events, @NotNull Class<T> type);

    default void post(@NotNull Object event, @NotNull Executor context) { context.execute(() -> post(event)); }
    default <T> void post(@NotNull T event, @NotNull Class<T> type,
                          @NotNull Executor context) { context.execute(() -> post(event, type)); }
    default <T> void post(@NotNull Iterable<? extends T> events, @NotNull Class<T> type,
                          @NotNull Executor context) { context.execute(() -> post(events, type)); }

    void clear();
}
