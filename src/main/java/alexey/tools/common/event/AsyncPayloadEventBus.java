package alexey.tools.common.event;

import alexey.tools.common.collections.ObjectCollection;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.Executor;

public class AsyncPayloadEventBus <P> extends PayloadEventBus <P> {

    private final Executor executor;



    public AsyncPayloadEventBus(Executor executor) {
        this.executor = executor;
    }



    public void postAsync(P payload, @NotNull Object event) {
        execute(groups.get(event.getClass()), payload, event);
    }

    public <T> void postAsync(Class<T> type, P payload, T event) {
        execute(groups.get(type), payload, event);
    }

    @SuppressWarnings("unchecked")
    private void execute(ObjectCollection<PayloadEventListener> listeners, P payload, Object event) {
        executor.execute(() -> {
            for (PayloadEventListener listener : listeners) listener.process(payload, event);
        });
    }
}
