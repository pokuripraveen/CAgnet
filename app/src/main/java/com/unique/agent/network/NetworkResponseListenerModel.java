package com.unique.agent.network;

import com.unique.agent.error.NetworkErrorCode;
import com.unique.agent.error.NetworkErrorInfo;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class NetworkResponseListenerModel implements INetworkResponseListener {

    private INetworkResponseListener wrapped = null;

    /** Create NetworkResponseListener that does nothing. */
    public NetworkResponseListenerModel() {
    }

    public NetworkResponseListenerModel(INetworkResponseListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void onFinished(NetworkRequest request, NetworkResponse response) {
        if (wrapped != null) {
            wrapped.onFinished(request, response);
        }
    }

    @Override
    public void onError(NetworkRequest request, NetworkErrorInfo error) {
        if (wrapped != null) {
            wrapped.onError(request, error);
        }
    }

    @Override
    public void onAborted(NetworkRequest request) {
        if (wrapped != null) {
            wrapped.onAborted(request);
        } else {
            onError(request, new NetworkErrorInfo.Builder(NetworkErrorCode.UNKNOWN_ERROR)
                    .setErrorDescription("Network Request " + request.getUrl() + " aborted")
                    .build());
        }
    }
}
