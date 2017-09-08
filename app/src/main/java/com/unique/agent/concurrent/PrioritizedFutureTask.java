package com.unique.agent.concurrent;

import com.unique.agent.network.CancelledListener;
import com.unique.agent.network.NetworkPriority;
import com.unique.agent.network.Prioritized;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by praveenpokuri on 21/08/17.
 */

public class PrioritizedFutureTask<V> extends FutureTask<V> implements Prioritized {

    private int priority = NetworkPriority.DEFAULT.getPriority();
    private Object task;

    public PrioritizedFutureTask(Callable<V> callable) {
        super(callable);
        task = callable;
        setPriorityFromTask();
    }

    public PrioritizedFutureTask(Runnable runnable, V result) {
        super(runnable, result);
        task = runnable;
        setPriorityFromTask();
    }

    private void setPriorityFromTask() {
        if (task instanceof Prioritized) {
            setPriority(((Prioritized) task).getPriority());
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = super.cancel(mayInterruptIfRunning);
        if (cancelled && task instanceof CancelledListener) {
            ((CancelledListener) task).onCancelled();
        }
        return cancelled;
    };
}
