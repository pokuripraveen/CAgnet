package com.unique.agent.interfaces;

import com.unique.agent.actions.Action;
import com.unique.agent.concurrent.ThreadPool;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public interface ActionExecutor<REQ, RES> {
    void execute(Action<REQ,RES> action, REQ request,ActionCallback<RES> responseCallback);
    void shutdown();

    ThreadPool getThreadPool();
}
