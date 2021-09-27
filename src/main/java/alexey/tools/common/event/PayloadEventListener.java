package alexey.tools.common.event;

public interface PayloadEventListener<P, T> {
    void process(P payload, T event);

}
