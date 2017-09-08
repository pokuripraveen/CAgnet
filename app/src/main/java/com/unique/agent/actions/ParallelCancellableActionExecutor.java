package com.unique.agent.actions;

import android.os.SystemClock;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.unique.agent.concurrent.DefaultExecutorSupplier;
import com.unique.agent.interfaces.CancellableActionExecutor;

/**
 * Created by praveenpokuri on 09/08/17.
 */
public class ParallelCancellableActionExecutor implements CancellableActionExecutor {

    private final Object LOCK = new Object();

    private ScheduledThreadPoolExecutor executor;
    private ConcurrentHashMap<Runnable, Future> futures;
    private ConcurrentHashMap<Object, Set<Runnable>> cancellationTokenToRunnableMap;

    private class DestructibleRunnable implements Runnable {

        private final Runnable workRunnable;
        private final Object cancellationToken;

        public DestructibleRunnable(Runnable workRunnable, Object cancellationToken) {
            this.workRunnable = workRunnable;
            this.cancellationToken = cancellationToken;
        }

        @Override
        public void run() {
            // do actual work
            workRunnable.run();

            // cleanup from futures map and from cancellation tokens map
            synchronized (LOCK) {
                futures.remove(this);

                if (cancellationToken == null){
                    return;
                }
                final Set<Runnable> runnableList = cancellationTokenToRunnableMap.get(cancellationToken);
                if (runnableList != null) {
                    runnableList.remove(this);
                    if (runnableList.isEmpty()){
                        cancellationTokenToRunnableMap.remove(cancellationToken);
                    }
                }
            }
        }

        /*
            equals and hashCode delegate to the workRunnable ones so cancel(runnable) still works
         */
        @Override
        public boolean equals(Object obj) {
            return workRunnable.equals(obj);
        }

        @Override
        public int hashCode() {
            return workRunnable.hashCode();
        }
    }

    public ParallelCancellableActionExecutor() {
        executor = DefaultExecutorSupplier.getInstance().forScheduledBackgroundTasks();
        futures = new ConcurrentHashMap<>();
        cancellationTokenToRunnableMap = new ConcurrentHashMap<>();
    }

    @Override
    public void post(Runnable runnable) {
        DestructibleRunnable destructibleRunnable = new DestructibleRunnable(runnable, null);
        synchronized (LOCK) {
            final Future<?> future = executor.submit(destructibleRunnable);
            futures.put(destructibleRunnable, future);
        }
    }

    @Override
    public void post(Runnable runnable, Object cancellationToken) {
        DestructibleRunnable destructibleRunnable = new DestructibleRunnable(runnable, cancellationToken);
        synchronized (LOCK) {
            final Future<?> future = executor.submit(destructibleRunnable);
            futures.put(destructibleRunnable, future);
            saveCancellationTokenAndRunnable(destructibleRunnable, cancellationToken);
        }
    }

    @Override
    public void postDelayed(Runnable runnable, long delayMillis) {
        DestructibleRunnable destructibleRunnable = new DestructibleRunnable(runnable, null);
        synchronized (LOCK) {
            final ScheduledFuture<?> future = executor.schedule(destructibleRunnable, delayMillis, TimeUnit.MILLISECONDS);
            futures.put(destructibleRunnable, future);
        }
    }

    @Override
    public void postAtTime(Runnable runnable, Object cancellationToken, long timeInUpTimeMillisBase) {
        final long delay = timeInUpTimeMillisBase - SystemClock.uptimeMillis();
        DestructibleRunnable destructibleRunnable = new DestructibleRunnable(runnable, cancellationToken);
        synchronized (LOCK) {
            final ScheduledFuture<?> future = executor.schedule(destructibleRunnable, delay, TimeUnit.MILLISECONDS);
            futures.put(destructibleRunnable, future);
            saveCancellationTokenAndRunnable(destructibleRunnable, cancellationToken);
        }

    }

    @Override
    public void cancel(Runnable runnable) {
        cancel(runnable, true);
    }

    public void cancel(Runnable runnable, boolean cleanFromCancelationToken) {
        Future future;
        synchronized (LOCK) {
            future = futures.remove(runnable);
        }
        if (future != null) {
            future.cancel(true);
        }

        if (!cleanFromCancelationToken){
            return;
        }
        synchronized (LOCK) {
            for (Iterator<Set<Runnable>> iterator = cancellationTokenToRunnableMap.values().iterator(); iterator.hasNext(); ) {
                Set<Runnable> runnableList = iterator.next();
                runnableList.remove(runnable);
                if (runnableList.isEmpty()){
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void cancelAll(Object cancellationToken) {
        Set<Runnable> runnableList;
        synchronized (LOCK) {
            runnableList = cancellationTokenToRunnableMap.remove(cancellationToken);
            if (runnableList == null) {
                return;
            }
            for (Runnable runnable : runnableList) {
                cancel(runnable, false);
            }
        }

    }

    @Override
    public void shutDown() {
        executor.shutdown();
        futures.clear();
        cancellationTokenToRunnableMap.clear();
    }

    private void saveCancellationTokenAndRunnable(Runnable runnable, Object cancellationToken) {
        synchronized (LOCK) {
            Set<Runnable> runnableList = cancellationTokenToRunnableMap.get(cancellationToken);
            if (runnableList == null) {
                runnableList = new HashSet<>();
            }
            runnableList.add(runnable);
            cancellationTokenToRunnableMap.put(cancellationToken, runnableList);
        }
    }
}

