package com.unique.agent.concurrent;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class PriorityRunnable implements Runnable {
    private final Priority priority;

    public PriorityRunnable(Priority priority) {
        this.priority = priority;
    }

    @Override
    public void run() {
        // nothing to do here.
    }

    public Priority getPriority() {
        return priority;
    }
}
