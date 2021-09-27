package alexey.tools.common.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AsyncRunnable {

    private boolean running = true;
    private boolean mustRelax = true;
    private boolean executing = false;

    private final Lock lock = new ReentrantLock();
    private final Condition work = lock.newCondition();
    private final Condition workDone = lock.newCondition();

    public AsyncRunnable() {
        new Thread(new Worker()).start();
    }



    private class Worker implements Runnable {
        @Override
        public void run() {
            lock.lock();
            main();
            if (executing) workDone();
            lock.unlock();
        }
    }

    private void main() {
        if (!running || !executing && workWait()) return;
        runTask();
        while (running) {
            if (mustRelax) {
                workDone();
                if (workWait()) return;
            }
            runTask();
        }
    }

    private boolean workWait() {
        try {
            work.await();
        } catch (Throwable e) {
            running = false;
        }
        return !running;
    }

    private void workDone() {
        executing = false;
        workDone.signalAll();
    }

    private void runTask() {
        lock.unlock();
        try {
            run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        lock.lock();
    }



    public boolean isRunning() {
        lock.lock();
        try {
            return running;
        } finally {
            lock.unlock();
        }
    }

    public boolean isExecuting() {
        lock.lock();
        try {
            return executing;
        } finally {
            lock.unlock();
        }
    }

    public boolean isPaused() {
        lock.lock();
        try {
            return mustRelax;
        } finally {
            lock.unlock();
        }
    }

    public void pause(boolean value) {
        lock.lock();
        mustRelax = value;
        lock.unlock();
    }

    public void resume() {
        lock.lock();
        try {
            if (!running || executing) return;
            executing = true;
            work.signal();
        } finally {
            lock.unlock();
        }
    }

    public void await() throws InterruptedException {
        lock.lock();
        if (executing) workDone.await();
        lock.unlock();
    }

    public void shutdown() {
        lock.lock();
        try {
            if (!running) return;
            running = false;
            if (executing) return;
            work.signal();
        } finally {
            lock.unlock();
        }
    }

    public abstract void run();
}
