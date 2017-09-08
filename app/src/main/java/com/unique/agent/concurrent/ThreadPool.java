package com.unique.agent.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public interface ThreadPool extends Postable{
    /**
     * @param task If task implements Prioritized then it will be taken off of the queue in order of priority (highest
     *             first).
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * @param task If task implements Prioritized then it will be taken off of the queue in order of priority (highest
     *             first).
     */
    Future<Void> submit(Runnable task);

    /** @see java.util.concurrent.ThreadPoolExecutor#shutdown() */
    void shutdown();

    /**
     * Calls Thread.sleep()
     * Hook for unit testing so unit tests are not slowed down.
     */
    void sleep(int milliseconds) throws InterruptedException;

}
