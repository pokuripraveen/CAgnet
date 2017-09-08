package com.unique.agent.interfaces;

import com.unique.agent.error.ErrorInfo;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public interface ActionCallback<RES> {
    void onSuccess(RES response);
    void onError(ErrorInfo exception);
}
