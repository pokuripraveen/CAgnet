package com.unique.agent.network;

import java.util.concurrent.TimeUnit;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class NetworkConfiguration {
     /** The default network connection timeout.
     */
    public static final int DEFAULT_NETWORK_CONNECTION_TIMEOUT_MS = (int) TimeUnit.MILLISECONDS.convert(16, TimeUnit.SECONDS);

    /**
     * The default network request timeout.
     */
    public static final int DEFAULT_NETWORK_REQUEST_TIMEOUT_MS = (int) TimeUnit.MILLISECONDS.convert(32, TimeUnit.SECONDS);

    /**
     * The default number of retries permitted for this network request.
     */
    public static final int DEFAULT_NETWORK_REQUEST_RETRIES = 1;

    private long mNetworkConnectionTimeoutMS = DEFAULT_NETWORK_CONNECTION_TIMEOUT_MS;
    private long mNetworkRequestTimeoutMS = DEFAULT_NETWORK_REQUEST_TIMEOUT_MS;
    private int mNetworkRequestRetries = DEFAULT_NETWORK_REQUEST_RETRIES;
    private boolean mAllowNetworkRequests = true;

    /**
     * Default Network Connection Timeout Value
     * @return Default Connection timeout in Milliseconds
     */
    public long getDefaultNetworkConnectionTimeoutMS() {
        return mNetworkConnectionTimeoutMS;
    }

    /**
     * Sets the Default Network Connection Timeout in milliseconds
     * @param timeout
     * @param unit The unit of measurement for the timeout.
     */
    public void setDefaultNetworkConnectionTimeoutMS(long timeout, TimeUnit unit) {
        mNetworkConnectionTimeoutMS = unit.toMillis(timeout);
    }

    /**
     * Returns the Default Network Timeout in Milliseconds
     * @return Default network request timeout in milliseconds
     */
    public long getDefaultNetworkRequestTimeoutMS() {
        return mNetworkRequestTimeoutMS;
    }

    /**
     * Sets the Default Network Timeout
     * @param timeout The timeout
     * @param unit Unit of measurement for time
     */
    public void setDefaultNetworkRequestTimeout(long timeout, TimeUnit unit) {
        mNetworkRequestTimeoutMS = unit.toMillis(timeout);
    }

    /**
     * @return Default number of network retries on a failure.
     */
    public int getDefaultNetworkFailureRetries() {
        return mNetworkRequestRetries;
    }

    /**
     * Sets the default number of retries to attempt on network failure.
     * @param retries Number of retries
     */
    public void setDefaultNetworkFailureRetries(int retries) {
        mNetworkRequestRetries = retries;
    }


    /**
     * Indicates whether or not network requests should be allowed. If this is false, then it indicates
     * that all network requests through the manager should fail.
     * @param allowNetworkRequests
     */
    public void setAllowNetworkRequests(boolean allowNetworkRequests) {
        mAllowNetworkRequests = allowNetworkRequests;
    }

    /**
     * Returns whether or not network requests are allowed
     * @return Boolean indicating whether or not networking is allowed.
     */
    public boolean getAllowNetworkRequests() {
        return mAllowNetworkRequests;
    }

}
