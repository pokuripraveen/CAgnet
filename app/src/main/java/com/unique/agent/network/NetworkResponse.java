package com.unique.agent.network;

import com.unique.agent.error.NetworkErrorInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.unique.agent.utils.AppUtil.equalsIgnoreCase;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class NetworkResponse {
    /**
     * The value of the HTTP response-header field - "ETag".<br>
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-2.3">ETag</a>
     */
    private static final String HEADER_ETAG = "Etag";

    /**
     * The value of the HTTP response-header field - "Last-Modified".<br>
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-2.2">Last-Modified</a>
     */
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";

    /**
     * Response headers from server. Most of the time the List will contain only one element, but it
     * is possible to have a header listed more than once in the response.
     */
    private Map<String,List<String>> mHeaders;

    private byte[] mResponseBytes;

    private int mDuration;

    private boolean mIsFromCache;

    private NetworkRequest mNetworkRequest;

    /**
     * {@link NetworkErrorInfo} object that represents information about Network Response different then 200 OK.
     */
    private NetworkErrorInfo mNetworkErrorInfo;

    /**
     * The response code returned by the remote HTTP server.
     */
    private int mResponseCode;

    /** Use Builder */
    private NetworkResponse() {
        super();
    }

    /**
     * Get the response headers sent from server.
     * @return the HashMap containing response headers.
     */
    public Map<String,List<String>> getHeaders() {
        return mHeaders;
    }

    /** @return Content of the HTTP response as raw bytes. */
    public byte[] getResponseBytes() {
        return Arrays.copyOf(mResponseBytes, mResponseBytes.length);
    }

    /** @return Milliseconds taken from thread start to completed. Useful for checking bandwidth. */
    public int getDuration() {
        return mDuration;
    }

    /** @return true if the data came from the cache, false if the data came from the network. */
    public boolean isFromCache() {
        return mIsFromCache;
    }

    /**
     * Gets the value of the HTTP response-header field - "ETag".
     *
     * @return The value of the HTTP response-header field - "ETag". Can return null if the header
     *         is not exist in the response.
     */
    public String getETag() {
        return getHeaderSingleParameter(HEADER_ETAG);
    }

    /**
     * Gets the value of the HTTP response-header field - "Last-Modified".
     *
     * @return The value of the HTTP response-header field - "Last-Modified".
     *         Can return null if the header is not exist in the response.
     */
    public String getLastModified() {
        return getHeaderSingleParameter(HEADER_LAST_MODIFIED);
    }


    /**
     * Returns the network request object that was made.
     * @return Network Request if available.
     */
    public NetworkRequest getRequest() {
        return mNetworkRequest;
    }

    /**
     * @return The response code returned by the remote HTTP server.
     */
    public int getResponseCode() {
        return mResponseCode;
    }

    /**
     * Looking for the provided header name in the collection of headers.
     * Try to find it with ignore case sensitivity.
     *
     * @param headerParameter The name of the HTTP header.
     *
     * @return The value of the provided HTTP header, or can return {@code null} if the one
     *         not existing.
     */
    private String getHeaderSingleParameter(final String headerParameter) {
        if (mHeaders == null || mHeaders.isEmpty()) {
            return null;
        }
        // Trying to look up for the header's value by ignoring case sensitivity.
        // The reason - different servers give different case for the same names, for example
        // "ETag" or "Etag"
        for (final Map.Entry<String, List<String>> entry : mHeaders.entrySet()) {
            if (equalsIgnoreCase(entry.getKey(), headerParameter)) {
                final List<String> values = entry.getValue();
                if (values == null || values.isEmpty()) {
                    return null;
                }
                return values.get(0);
            }
        }
        return null;
    }

    /**
     * @return {@link NetworkErrorInfo} object that represents information about Network Response different then 200 OK.
     */
    public NetworkErrorInfo getNetworkErrorInfo() {
        return mNetworkErrorInfo;
    }


    /** Builder so that NetworkResponse can be immutable. */
    public static class Builder {

        private final NetworkResponse response = new NetworkResponse();

        /**
         * Set the server response headers.
         * @param headers the HashMap containing server headers reply.
         */
        public Builder setHeaders(Map<String, List<String>> headers) {
            response.mHeaders = headers;
            return this;
        }

        public Builder setResponseBytes(byte[] responseBytes) {
            response.mResponseBytes = Arrays.copyOf(responseBytes, responseBytes.length);
            return this;
        }

        /** @see #getDuration */
        public Builder setDuration(int duration) {
            response.mDuration = duration;
            return this;
        }

        /** @see #isFromCache() */
        public Builder setFromCache(boolean isFromCache) {
            response.mIsFromCache = isFromCache;
            return this;
        }

        /** @see #getRequest() */
        public Builder setRequest(NetworkRequest request) {
            response.mNetworkRequest = request;
            return this;
        }

        /**
         * Sets the {@link NetworkErrorInfo} object that represents information about Network Response
         * different then 200 OK.
         *
         * @param networkErrorInfo The {@link NetworkErrorInfo} object
         *
         * @return {@link NetworkResponse.Builder}
         */
        public Builder setNetworkErrorInfo(final NetworkErrorInfo networkErrorInfo) {
            response.mNetworkErrorInfo = networkErrorInfo;
            return this;
        }

        /**
         * Sets the response code returned by the remote HTTP server.
         *
         * @param responseCode The response code returned by the remote HTTP server.
         *
         * @return {@link NetworkResponse.Builder}
         */
        public Builder setResponseCode(final int responseCode) {
            response.mResponseCode = responseCode;
            return this;
        }

        /** @return NetworkResponse with values set. */
        public NetworkResponse build() {
            return response;
        }
    }


}
