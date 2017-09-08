package com.unique.agent.network;

import com.unique.agent.error.NetworkErrorInfo;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public interface INetworkResponseListener {
    /**
     * Notify client that request was finished successfully.
     * If an exception is thrown then onError is called.
     * If caching has been set than this should throw an exception if the response could not be parsed correctly so
     * that it will not be stored in the cache.
     *
             * @param response the NetworkResponse returned from server.
            */
    void onFinished(NetworkRequest request, NetworkResponse response);

    /**
     * Notify client that there was an error completing their request.
     *
     * @param error NetworkErrorInfo with a code from {@code NetworkErrorCodes}.
     *              If HTTP_CLIENT or HTTP_SERVER contextData contains Object[3] with statusCode, statusMessage, and
     *              response (a byte[] or a String if the byte[] could be converted to a String).
     *              If RESOURCE_ACCESS then QPError exception is set.
     */
    void onError(NetworkRequest request, NetworkErrorInfo error);

    /**
     * Notify client that the request was aborted. Say the client specifically requested to abort the request, now they
     * can get a callback when the abort actually happens.
     *
     * @param cancelled Original NetworkRequest that was cancelled.
     */
    void onAborted(NetworkRequest cancelled);

}
