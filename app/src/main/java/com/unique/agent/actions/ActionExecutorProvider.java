package com.unique.agent.actions;

import com.unique.agent.interfaces.ActionExecutor;
import com.unique.agent.interfaces.CancellableActionExecutor;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class ActionExecutorProvider {

    public static ActionExecutor getUIActionExecutor() {
        return new UIActionExecutor();
    }

    public static ActionExecutor getParallelExecutor() {
        return new ParallelActionExecutor();
    }

    public static CancellableActionExecutor getParallelCancellableExecutor() {
        return new ParallelCancellableActionExecutor();
    }
}
