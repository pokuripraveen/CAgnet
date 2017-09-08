package com.unique.agent.actions;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.unique.agent.concurrent.MainThreadExecutor;
import com.unique.agent.concurrent.ThreadPool;
import com.unique.agent.interfaces.ActionCallback;
import com.unique.agent.interfaces.ActionExecutor;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class UIActionExecutor<REQ,RES> implements ActionExecutor<REQ,RES>{

    Executor mExecutor;

    public UIActionExecutor(){
        mExecutor = new MainThreadExecutor();
    }

    @Override
    public void execute(Action<REQ, RES> action, REQ request,  ActionCallback<RES> callback) {
        ActionRunnable<REQ, RES> actionRunnable = new ActionRunnable<>(action, request, callback);
        mExecutor.execute(actionRunnable);

    }

    @Override
    public void shutdown() {
        if (mExecutor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) mExecutor).shutdownNow();
        }
    }

    @Override
    public ThreadPool getThreadPool() {
        return (ThreadPool) mExecutor;
    }
}
