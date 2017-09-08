package com.unique.agent.network;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.Validate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class WaitingSerialRequest implements Future<NetworkResponse> {

    private NetworkRequest mRequest;
    private Future<NetworkResponse> mFuture;
    private boolean mIsCancelled = false;
    private boolean mMayInterrupt;

    public WaitingSerialRequest(NetworkRequest request) {
        mRequest = request;
    }

    public synchronized void setSubmittedFuture(Future<NetworkResponse> future) {
        Validate.notNull(future, "future");
        mFuture = future;
        if (mIsCancelled) {
            mFuture.cancel(mMayInterrupt);
        }
        notifyAll();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        mIsCancelled = true;
        mMayInterrupt = mayInterruptIfRunning;
        if (mFuture == null) {
            return true;
        }
        return mFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        if (mFuture == null) {
            return mIsCancelled;
        }
        return mFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        if (mFuture == null) {
            return false;
        }
        return mFuture.isDone();
    }

    @Override
    public NetworkResponse get() throws InterruptedException, ExecutionException {
        if (mFuture == null) {
            wait();
        }
        return mFuture.get();
    }

    @Override
    public NetworkResponse get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (mFuture == null) {
            wait(TimeUnit.MICROSECONDS.convert(timeout, unit));
        }
        return mFuture.get(timeout, unit);
    }
    public NetworkRequest getRequest() {
        return mRequest;
    }
}
