package com.unique.agent.concurrent;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class PriorityThreadFactory implements ThreadFactory {

    /**
     * Appendix to use for the name.
     */
    private static final String APPENDIX = "-Thread";

    private final int mThreadPriority;
    private final String mThreadName;

    public PriorityThreadFactory(String name, int threadPriority) {
        mThreadPriority = threadPriority;
        mThreadName = name;
    }

    @Override
    public Thread newThread(@NonNull final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    android.os.Process.setThreadPriority(mThreadPriority);
                } catch (Throwable t) {

                }
                runnable.run();
            }
        };

        Thread thread = new Thread(wrapperRunnable);
        thread.setName(mThreadName + APPENDIX);
        return thread;
    }
}
