package alexey.tools.common.event;

public interface EventListener <T> {
    void process(T event);
}
