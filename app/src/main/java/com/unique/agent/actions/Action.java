package com.unique.agent.actions;

import android.os.Handler;
import android.os.Looper;

import com.unique.agent.error.ErrorInfo;
import com.unique.agent.interfaces.ActionCallback;
import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 09/08/17.
 * THis return success or failure on Main Thread
 */

public abstract class Action<REQ, RES> {

    private ActionCallback mActionCallback;
    private Handler mHandler;
    private boolean mIsCancelled;

    public Action() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    protected abstract void runAction(REQ request);

    protected final boolean isCancelled() {
        return mIsCancelled;
    }

    public final void cancel() {
        mIsCancelled = true;
    }

    final void execute(REQ request, ActionCallback<RES> callback) {
        mActionCallback = callback;
        mIsCancelled = false;

        try {
            Log.i("PRAV", this.getClass().getName() + "runAction");
            runAction(request);
        } catch (Exception e) {
            e.printStackTrace();
           // sendFailure(e);
        }
    }

    protected void sendSuccess(RES response) {
        mHandler.post(new RunOnMainUIRunnable<RES>(response, mActionCallback));
    }

    protected void sendFailure(ErrorInfo e) {
        mHandler.post(new RunOnMainUIRunnable<RES>(e, mActionCallback));
    }

    static class RunOnMainUIRunnable<T> implements Runnable {

        private T mResponse;
        private ErrorInfo mException;
        private ActionCallback<T> mCallback;

        RunOnMainUIRunnable(T response, ActionCallback<T> callback) {
            mResponse = response;
            mCallback = callback;
        }

        RunOnMainUIRunnable(ErrorInfo e, ActionCallback<T> callback) {
            mException = e;
            mCallback = callback;
        }

        @Override
        public void run() {
            if (mException != null) {
                mCallback.onError(mException);
            } else {
                mCallback.onSuccess(mResponse);
            }
        }

    }
}
