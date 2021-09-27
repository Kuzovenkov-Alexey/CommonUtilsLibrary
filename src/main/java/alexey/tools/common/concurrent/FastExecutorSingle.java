package alexey.tools.common.concurrent;

import alexey.tools.common.collections.ObjectCollection;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FastExecutorSingle implements AdvancedExecutor {

    private final ObjectCollection<Runnable> tasks = new ObjectCollection<>();
    private final Lock lock = new ReentrantLock();
    private final Condition newCommand = lock.newCondition();
    private final Condition workDone = lock.newCondition();

    private boolean running = true;
    private boolean executing = false;

    public FastExecutorSingle() {
        new Thread(new Worker()).start();
    }



    private class Worker implements Runnable {
        @Override
        public void run() {
            lock.lock();
            loop();
            lock.unlock();
        }
    }

    private void loop() {
        if (!running || !executing && workWait()) { cancelWork(); return; }
        runTask();
        while (running) {
            if (tasks.isEmpty()) {
                workDone();
                if (workWait()) { cancelWork(); return; }
            }
            runTask();
        }
        workDone();
        if (tasks.isNotEmpty()) tasks.clear();
    }

    private boolean workWait() {
        try {
            newCommand.await();
        } catch (Throwable ignored) {
            running = false;
        }
        return !running;
    }

    private void runTask() {
        Runnable task = tasks.unsafeRemoveLast();
        lock.unlock();
        try {
            task.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        lock.lock();
    }

    private void workDone() {
        executing = false;
        workDone.signalAll();
    }

    private void cancelWork() {
        if (tasks.isEmpty()) return;
        tasks.clear();
        workDone();
    }



    @Override
    public void execute(@NotNull Runnable command) {
        lock.lock();
        try {
            if (!running) return;
            tasks.add(command);
            if (executing) return;
            executing = true;
            newCommand.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            if (!running) return;
            running = false;
            if (executing) return;
            newCommand.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void await() throws InterruptedException {
        lock.lock();
        if (executing) workDone.await();
        lock.unlock();
    }
}
