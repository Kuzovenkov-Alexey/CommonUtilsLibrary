package alexey.tools.common.event;

import alexey.tools.common.collections.ObjectCollection;
import org.jetbrains.annotations.NotNull;
import java.util.IdentityHashMap;
import java.util.Map;

public class PayloadEventBus <P> {

    protected final Map<Class<?>, ObjectCollection<PayloadEventListener>> groups = new IdentityHashMap<>();



    public <T> void register(Class<? extends T> type, PayloadEventListener<P, T> listener) {
        ObjectCollection<PayloadEventListener> listeners = groups.get(type);
        if (listeners == null) {
            listeners = new ObjectCollection<>(2);
            groups.put(type, listeners);
        }
        listeners.add(listener);
    }

    public <T> void unregister(Class<? extends T> type, PayloadEventListener<P, T> listener) {
        ObjectCollection<PayloadEventListener> listeners = groups.get(type);
        if (listeners == null) return;
        listeners.removeReference(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void post(Class<T> type, P payload, @NotNull Iterable<? extends T> events) {
        ObjectCollection<PayloadEventListener> listeners = groups.get(type);
        for (T event : events) for (PayloadEventListener listener : listeners) listener.process(payload, event);
    }

    @SuppressWarnings("unchecked")
    public void post(P payload, @NotNull Object event) {
        for (PayloadEventListener listener : groups.get(event.getClass())) listener.process(payload, event);
    }

    @SuppressWarnings("unchecked")
    public <T> void post(Class<T> type, P payload, T event) {
        for (PayloadEventListener listener : groups.get(type)) listener.process(payload, event);
    }

    public void clear() {
        groups.clear();
    }
}
