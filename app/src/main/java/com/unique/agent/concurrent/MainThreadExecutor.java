package com.unique.agent.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class MainThreadExecutor implements Executor,ThreadPool {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable runnable) {
        handler.post(runnable);
    }

    /**
     * Posts the task to run.
     *
     * @param task
     * @return true if task was posted, false if the task was not posted because the thread was dead.
     */
    @Override
    public boolean post(Runnable task) {
        return false;
    }

    /**
     * @param task If task implements Prioritized then it will be taken off of the queue in order of priority (highest
     *             first).
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return null;
    }

    /**
     * @param task If task implements Prioritized then it will be taken off of the queue in order of priority (highest
     *             first).
     */
    @Override
    public Future<Void> submit(Runnable task) {
        return null;
    }

    /**
     * @see ThreadPoolExecutor#shutdown()
     */
    @Override
    public void shutdown() {

    }

    /**
     * Calls Thread.sleep()
     * Hook for unit testing so unit tests are not slowed down.
     *
     * @param milliseconds
     */
    @Override
    public void sleep(int milliseconds) throws InterruptedException {

    }
}
