package alexey.tools.common.event;

import alexey.tools.common.collections.ObjectCollection;
import org.jetbrains.annotations.NotNull;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class SimpleEventBus implements EventBus {

    protected final Map<Class<?>, ObjectCollection<Consumer>> groups = new IdentityHashMap<>();



    @Override
    public <T> void register(@NotNull Class<? extends T> type, @NotNull Consumer<T> listener) {
        ObjectCollection<Consumer> listeners = groups.get(type);
        if (listeners == null) {
            listeners = new ObjectCollection<>(2);
            groups.put(type, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public <T> void unregister(@NotNull Class<? extends T> type, @NotNull Consumer<T> listener) {
        ObjectCollection<Consumer> listeners = groups.get(type);
        if (listeners == null) return;
        listeners.removeReference(listener);
    }



    @Override
    @SuppressWarnings("unchecked")
    public <T> void post(@NotNull Iterable<? extends T> events, @NotNull Class<T> type) {
        ObjectCollection<Consumer> listeners = groups.get(type);
        for (T event : events) for (Consumer listener : listeners) listener.accept(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void post(@NotNull Object event) {
        for (Consumer listener : groups.get(event.getClass())) listener.accept(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void post(@NotNull T event, @NotNull Class<T> type) {
        for (Consumer listener : groups.get(type)) listener.accept(event);
    }



    @Override
    @SuppressWarnings("unchecked")
    public void post(@NotNull Object event, @NotNull Executor context) {
        ObjectCollection<Consumer> listeners = groups.get(event.getClass());
        context.execute(() -> { for (Consumer listener : listeners) listener.accept(event); });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void post(@NotNull T event, @NotNull Class<T> type, @NotNull Executor context) {
        ObjectCollection<Consumer> listeners = groups.get(type);
        context.execute(() -> { for (Consumer listener : listeners) listener.accept(event); });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void post(@NotNull Iterable<? extends T> events, @NotNull Class<T> type, @NotNull Executor context) {
        ObjectCollection<Consumer> listeners = groups.get(type);
        context.execute(() -> { for (T event : events) for (Consumer listener : listeners) listener.accept(event); });
    }



    @Override
    public void clear() {
        groups.clear();
    }
}
