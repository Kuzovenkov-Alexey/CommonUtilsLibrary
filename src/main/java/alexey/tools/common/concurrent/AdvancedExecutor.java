package alexey.tools.common.concurrent;

import java.util.concurrent.Executor;

public interface AdvancedExecutor extends Executor {
    void await() throws InterruptedException;
    void shutdown();
}
