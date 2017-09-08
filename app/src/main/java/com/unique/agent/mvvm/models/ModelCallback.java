package com.unique.agent.mvvm.models;

import com.unique.agent.error.ErrorInfo;

/**
 * Created by praveenpokuri on 16/08/17.
 */

public interface ModelCallback<RES> {
    void onResponse(RES response);
    void onError(ErrorInfo error);
}
