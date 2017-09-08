package com.unique.agent.interfaces;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public interface CancellableActionExecutor {
    void post(Runnable runnable);
    void post(Runnable runnable, Object cancellationToken);
    void postAtTime(Runnable runnable, Object cancellationToken, long timeInUpTimeMillisBase);
    void postDelayed(Runnable runnable, long delayMillis);
    void cancel(Runnable runnable);
    void cancelAll(Object cancellationToken);
    void shutDown();
}
