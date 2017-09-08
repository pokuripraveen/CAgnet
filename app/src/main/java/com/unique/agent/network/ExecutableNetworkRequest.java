package com.unique.agent.network;

import java.util.Arrays;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class ExecutableNetworkRequest extends NetworkRequest{

    /** @see #getMethod() */
    private HTTPMethod mMethod = null;

    /** @see #getPostData() */
    private byte[] mPostData;

    private NetworkUploadInterface mUploadInterface;

    /**
     * Instantiate a new NetworkRequest object with the specified URL.
     *
     * @param url the URL to execute this request.
     * @throws IllegalArgumentException When provided URL is invalid.
     */
    public ExecutableNetworkRequest(String url) throws IllegalArgumentException {
        super(url);
    }

    /**
     * @returnn HTTP method to use, whether GET or POST. Used by INetworkManager#execute().
     */
    public HTTPMethod getMethod() {
        return mMethod;
    }

    /** @see #getMethod() */
    public void setMethod(HTTPMethod method) {
        mMethod = method;
    }

    /**
     * @return Data that can be used for the POST body. This can alternately be specified in INetworkManager.post().
     *         If set than #setMethod is ignored and POST is always used.
     */
    public byte[] getPostData() {
        return Arrays.copyOf(mPostData, mPostData.length);
    }

    /** @see #getPostData() */
    public void setPostData(byte[] postData) {
        mPostData = Arrays.copyOf(postData, postData.length);
    }

    /**
     * @return NetworkUploadInterface that will upload POST data to the server.
     *         If set than #setMethod is ignored and POST is always used.
     */
    public NetworkUploadInterface getUploadInterface() {
        return mUploadInterface;
    }

    /** @see #getUploadInterface() */
    public void setUploadInterface(NetworkUploadInterface uploadInterface) {
        mUploadInterface = uploadInterface;
    }

}
