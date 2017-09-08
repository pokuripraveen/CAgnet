package com.unique.agent.actions;

import android.os.AsyncTask;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.unique.agent.concurrent.DefaultExecutorSupplier;
import com.unique.agent.concurrent.ThreadPool;
import com.unique.agent.interfaces.ActionCallback;
import com.unique.agent.interfaces.ActionExecutor;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class ParallelActionExecutor<REQ,RES> implements ActionExecutor<REQ,RES> {

    private Executor mExecutor;

    public ParallelActionExecutor() {
        mExecutor = DefaultExecutorSupplier.getInstance().forPriorityBackgroundTasks();
    }

    public ThreadPool getThreadPool(){
        return (ThreadPool) mExecutor;
    }

    @Override
    public void execute(Action<REQ, RES> action, REQ request, ActionCallback<RES> callback) {
        ActionRunnable<REQ, RES> actionRunnable = new ActionRunnable<>(action, request, callback);
        mExecutor.execute(actionRunnable);
    }

    @Override
    public void shutdown() {
        if (mExecutor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) mExecutor).shutdownNow();
        }
    }
}
