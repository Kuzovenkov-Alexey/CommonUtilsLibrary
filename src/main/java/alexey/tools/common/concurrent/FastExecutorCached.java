package alexey.tools.common.concurrent;

import alexey.tools.common.collections.ObjectCollection;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FastExecutorCached implements AdvancedExecutor {

    private final ObjectCollection<Runnable> tasks = new ObjectCollection<>(Runnable.class);
    private final Lock lock = new ReentrantLock();
    private final Condition workDone = lock.newCondition();
    private final Condition newCommand = lock.newCondition();

    public final int maxThreads;
    private int activeThreads = 0;
    private int totalThreads = 0;
    private boolean running = true;



    public FastExecutorCached() {
        this.maxThreads = Runtime.getRuntime().availableProcessors();
    }

    public FastExecutorCached(int maxThreads) {
        this.maxThreads = Math.max(maxThreads, 2);
    }



    private class Worker implements Runnable {
        @Override
        public void run() {
            lock.lock();
            if (running) loop();
            cancelWork();
            lock.unlock();
        }
    }

    private void loop() {
        activeThreads++;
        do {
            if (tasks.isEmpty()) {
                if (--activeThreads == 0) workDone.signalAll();
                do {
                    try {
                        newCommand.await();
                    } catch (Throwable e) { return; }
                    if (!running) return;
                } while (tasks.isEmpty());
                activeThreads++;
            }
            Runnable task = tasks.unsafeRemoveLast();
            lock.unlock();
            try {
                task.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            lock.lock();
        } while (running);
        activeThreads--;
    }

    private void cancelWork() {
        if (--totalThreads > 0 || tasks.isEmpty()) return;
        tasks.clear();
        workDone.signalAll();
    }



    @Override
    public void execute(@NotNull Runnable command) {
        try {
            lock.lock();
            if (!running) return;
            tasks.add(command);
            if (tasks.size() + activeThreads <= totalThreads) { newCommand.signal(); return; }
            if (totalThreads == maxThreads) return;
            totalThreads++;
        } finally {
            lock.unlock();
        }
        try {
            new Thread(new Worker()).start();
        } catch (Throwable e) {
            lock.lock();
            cancelWork();
            lock.unlock();
            throw e;
        }
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            if (!running) return;
            running = false;
            if (activeThreads + tasks.size() >= totalThreads) return;
            newCommand.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void await() throws InterruptedException {
        lock.lock();
        try {
            if (tasks.isEmpty() && activeThreads == 0) return;
            workDone.await();
        } finally {
            lock.unlock();
        }
    }
}
