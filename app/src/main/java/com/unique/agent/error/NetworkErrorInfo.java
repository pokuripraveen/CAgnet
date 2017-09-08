package com.unique.agent.error;

import com.unique.agent.network.NetworkRequest;
import com.unique.agent.network.NetworkResponse;

import java.util.Arrays;

import okhttp3.OkHttpClient;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class NetworkErrorInfo extends ErrorInfo {

    /**
     * The status code returned if none is provided/available.
     */
    public static final int INVALID_HTTP_STATUS_CODE = -1;

    private int mHttpStatusCode;
    private String mHttpResponseMessage;
    private NetworkRequest mNetworkRequest;
    private NetworkResponse mNetworkResponse;
    private String mNetworkResponseString;

    protected NetworkErrorInfo(String area, int errorCode) {
        super(area, errorCode);
        mHttpStatusCode = INVALID_HTTP_STATUS_CODE;
    }

    /**
     * Return the HTTP Status Code if one is provided.
     *
     * @return HTTP Status coded if provided or {@link NetworkErrorInfo#INVALID_HTTP_STATUS_CODE}.
     */
    public int getHttpStatusCode() {
        return mHttpStatusCode;
    }

    /**
     * Returns the HTTP Response Message if one is provided.
     *
     * @return HTTP Response Message.
     */
    public String getHttpResponseMessage() {
        return mHttpResponseMessage;
    }

    /**
     * Returns the Network Request that the error is associated with (if provided).
     *
     * @return
     */
    public NetworkRequest getNetworkRequest() {
        return mNetworkRequest;
    }

    /**
     * Returns the Network Response object associated with the error if available.
     *
     * @return NetworkResponse object or null.
     */
    public NetworkResponse getNetworkResponse() {
        return mNetworkResponse;
    }

    /**
     * Returns the URL associated with the request if available
     *
     * @return The network url if available.
     */
    public String getUrl() {
        if (mNetworkRequest != null) {
            return mNetworkRequest.getUrl();
        }
        return null;
    }

    /**
     * Returns a stringified version of the network response if provided.
     *
     * @return
     */
    public String getNetworkResponseString() {
        if (mNetworkResponseString != null) {
            return mNetworkResponseString;
        }
        if (mNetworkResponse != null) {
            return mNetworkResponse.toString();
        }
        return null;
    }

    @Override
    public NetworkErrorInfo getNetworkError() {
        return this;
    }

    @Override
    public String toString() {
        return "NetworkErrorInfo{" +
                "  mBase=" + super.toString() +
                ", mHttpStatusCode=" + mHttpStatusCode +
                ", mHttpResponseMessage='" + mHttpResponseMessage + '\'' +
                ", mNetworkRequest=" + mNetworkRequest +
                ", mNetworkResponse=" + mNetworkResponse +
                ", mNetworkResponseString='" + mNetworkResponseString + '\'' +
                '}';
    }

    public static class Builder extends GenericErrorBuilder<NetworkErrorInfo, Builder> {

        private int mHttpStatusCode = INVALID_HTTP_STATUS_CODE;
        private String mHttpResponseMessage;
        private NetworkRequest mNetworkRequest;
        private NetworkResponse mNetworkResponse;
        private String mNetworkResponseString;
        private byte[] mResponse;

        public Builder(NetworkErrorCode errorCode) {
            super(errorCode);
        }

        public Builder setHttpStatusCode(int httpStatusCode) {
            mHttpStatusCode = httpStatusCode;
            return this;
        }

        public Builder setHttpResponseMessage(String httpResponseMessage) {
            mHttpResponseMessage = httpResponseMessage;
            return this;
        }

        public Builder setNetworkRequest(NetworkRequest request) {
            mNetworkRequest = request;
            return this;
        }

        public Builder setNetworkResponse(NetworkResponse response) {
            mNetworkResponse = response;
            return this;
        }

        public Builder setNetworkResponseString(String response) {
            mNetworkResponseString = response;
            return this;
        }

        public Builder setResponse(byte[] response) {
            mResponse = Arrays.copyOf(response, response.length);
            return this;
        }

        @Override
        protected NetworkErrorInfo createErrorInfo() {
            NetworkErrorInfo errorInfo = new NetworkErrorInfo(mArea, mErrorCode);
            errorInfo.mHttpStatusCode = mHttpStatusCode;
            errorInfo.mHttpResponseMessage = mHttpResponseMessage;
            errorInfo.mNetworkRequest = mNetworkRequest;
            errorInfo.mNetworkResponse = mNetworkResponse;
            errorInfo.mNetworkResponseString = mNetworkResponseString;
            appendErrorDescription("; status="+mHttpStatusCode);
            if (mNetworkRequest != null) {
                String url = errorInfo.mNetworkRequest.getUrl();
                if(url != null) {
                    appendErrorDescription("; url=" + url);
                }
            }
            return errorInfo;
        }
    }


}
