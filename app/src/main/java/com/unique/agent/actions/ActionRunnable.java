package com.unique.agent.actions;

import com.unique.agent.interfaces.ActionCallback;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class ActionRunnable<REQ, RES> implements Runnable{

    private Action<REQ, RES> mAction;
    private ActionCallback<RES> mCallback;
    private REQ mRequest;

    public ActionRunnable(Action<REQ, RES> action, REQ request, ActionCallback<RES> callback) {
        mAction = action;
        mCallback = callback;
        mRequest = request;
    }

    @Override
    public void run() {
        mAction.execute(mRequest, mCallback);
        mAction = null;
        mCallback = null;
        mRequest = null;
    }
}

