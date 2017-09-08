package com.unique.agent.network;

/**
 * Created by praveenpokuri on 18/08/17.
 */


import java.util.concurrent.Callable;

/**
 * Callable that has a priority so that tasks with higher priority will be run before tasks with a
 * lower priority, if they are both on the queue.
 *
 * @since 2014-03-24
 */
public interface PrioritizedCallable<V> extends Callable<V>, Prioritized {
}
