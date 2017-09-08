package com.unique.agent.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class PriorityThreadPoolExecutor extends BaseThreadPoolExecutor {
    /**
     * Any BlockingQueue may be used to transfer and hold submitted tasks. The use of this queue interacts with pool sizing:

     * If fewer than corePoolSize threads are running, the Executor always prefers adding a new thread rather than queuing.
     * If corePoolSize or more threads are running, the Executor always prefers queuing a request rather than adding a new thread.
     * If a request cannot be queued, a new thread is created unless this would exceed maximumPoolSize, in which case,
     * the task will be rejected
     *
     * There are three general strategies for queuing:

     * Direct handoffs. A good default choice for a work queue is a SynchronousQueue that hands off tasks to threads without otherwise holding them.
     * Here, an attempt to queue a task will fail if no threads are immediately available to run it, so a new thread will be constructed.
     * This policy avoids lockups when handling sets of requests that might have internal dependencies.
     * Direct handoffs generally require unbounded maximumPoolSizes to avoid rejection of new submitted tasks.
     * This in turn admits the possibility of unbounded thread growth when commands continue to arrive on average faster than they can
     * be processed.
     *
     * Unbounded queues. Using an unbounded queue (for example a LinkedBlockingQueue without a predefined capacity) will cause new tasks
     * to wait in the queue when all corePoolSize threads are busy. Thus, no more than corePoolSize threads will ever be created.
     * (And the value of the maximumPoolSize therefore doesn't have any effect.) This may be appropriate when each task is completely
     * independent of others, so tasks cannot affect each others execution; for example, in a web page server.
     * While this style of queuing can be useful in smoothing out transient bursts of requests, it admits the possibility of
     * unbounded work queue growth when commands continue to arrive on average faster than they can be processed.
     *
     * Bounded queues. A bounded queue (for example, an ArrayBlockingQueue) helps prevent resource exhaustion when used with finite
     * maximumPoolSizes, but can be more difficult to tune and control. Queue sizes and maximum pool sizes may be traded off for e
     * ach other: Using large queues and small pools minimizes CPU usage, OS resources, and context-switching overhead, but can lead
     * to artificially low throughput. If tasks frequently block (for example if they are I/O bound), a system may be able to schedule
     * time for more threads than you otherwise allow. Use of small queues generally requires larger pool sizes, which keeps CPUs busier
     * but may encounter unacceptable scheduling overhead, which also decreases throughput.
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param threadFactory
     */

    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                      TimeUnit unit, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit,new PriorityBlockingQueue<>(16, new PrioritizedComparator<Runnable>()), threadFactory);
    }

    public PriorityThreadPoolExecutor(int corePoolSize, final ThreadFactory threadFactory) {
        // PriorityBlockingQueue is never full so maximum number of threads is not used.
        // Core threads never timeout so the timeout values are never used.
        super(
                corePoolSize, Integer.MAX_VALUE, Long.MAX_VALUE, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(16, new PrioritizedComparator<Runnable>()), threadFactory
        );
    }

    /**
     * @return PrioritizedFutureTask
     */
    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, T value) {
        return new PrioritizedFutureTask<T>(runnable, value);
    }

    /**
     * @return PrioritizedFutureTask
     */
    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new PrioritizedFutureTask<T>(callable);
    }

}

