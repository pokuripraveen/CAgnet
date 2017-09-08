package com.unique.agent.actions.action;

import com.unique.agent.actions.Action;
import com.unique.agent.base.AppCore;
import com.unique.agent.error.NetworkErrorInfo;
import com.unique.agent.model.UserLabor;
import com.unique.agent.network.INetworkResponseListener;
import com.unique.agent.network.NetworkRequest;
import com.unique.agent.network.NetworkResponse;

import java.util.HashMap;

import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class LoginAction extends Action<UserLabor,UserLabor> {

    private UserLabor mUserLabor;

    public LoginAction(UserLabor userLabor) {
        mUserLabor = userLabor;
    }

    @Override
    protected void runAction(UserLabor request) {
       Log.i("PRAV"," I am running Login Action ");
        NetworkRequest nwRequest = new NetworkRequest("https://httpbin.org/get");
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        nwRequest.setRawHeaders(headers);
        nwRequest.setResponseListener(new LoginResponseListener());
        AppCore.getInstance().getNetworkManager().get(nwRequest);
    }

    private class LoginResponseListener implements INetworkResponseListener {
        /**
         * Notify client that request was finished successfully.
         * If an exception is thrown then onError is called.
         * If caching has been set than this should throw an exception if the response could not be parsed correctly so
         * that it will not be stored in the cache.
         *
         * @param request
         * @param response the NetworkResponse returned from server.
         */
        @Override
        public void onFinished(NetworkRequest request, NetworkResponse response) {
            Log.i("PRAV"," onFinished "+new String(response.getResponseBytes()));
            sendSuccess(new UserLabor());
        }

        /**
         * Notify client that there was an error completing their request.
         *
         * @param request
         * @param error   NetworkErrorInfo with a code from {@code NetworkErrorCodes}.
         *                If HTTP_CLIENT or HTTP_SERVER contextData contains Object[3] with statusCode, statusMessage, and
         *                response (a byte[] or a String if the byte[] could be converted to a String).
         */
        @Override
        public void onError(NetworkRequest request, NetworkErrorInfo error) {
            Log.i("PRAV"," onError "+error.getErrorCode());
             sendFailure(error);
        }

        /**
         * Notify client that the request was aborted. Say the client specifically requested to abort the request, now they
         * can get a callback when the abort actually happens.
         *
         * @param cancelled Original NetworkRequest that was cancelled.
         */
        @Override
        public void onAborted(NetworkRequest cancelled) {
            sendFailure(null);
        }
    }
}
