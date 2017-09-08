package com.unique.agent.concurrent;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public interface Postable {

    /**
     * Posts the task to run.
     *
     * @return true if task was posted, false if the task was not posted because the thread was dead.
     */
    boolean post(Runnable task);

}
