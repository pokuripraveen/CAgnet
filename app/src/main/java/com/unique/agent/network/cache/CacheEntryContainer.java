package com.unique.agent.network.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class CacheEntryContainer implements IDataCacheContainer {
    private static final long serialVersionUID = 1L;

    /* Raw byte data. */
    private byte[] mData;
    /* Expiry time in ms */
    private long mExpiryTime;

    /**
     * Collection of HTTP header fields. Avoid to use {@link java.util.TreeMap} as implementation.
     * It has issue with serialization.
     */
    private Map<String, List<String>> mHeaders;

    public CacheEntryContainer(byte[] data) {
        mData = Arrays.copyOf(data, data.length);
    }

    public CacheEntryContainer(byte[] data, long expiryTime) {
        mData = Arrays.copyOf(data, data.length);
        mExpiryTime = expiryTime;
    }

    public CacheEntryContainer(final Map<String, List<String>> headers,
                               final byte[] data, final long expiryTime) {
        if (headers != null) {
            // Cast any implementation to the HashMap for the serialization issue.
            // TreeMap is risky because different implementations may contain reference to comparator
            // that can contain references to no-serializable objects or event worse - do not implement
            // serializable at all.
            mHeaders = new HashMap<>(headers);
        }
        mData = Arrays.copyOf(data, data.length);
        mExpiryTime = expiryTime;
    }

    @Override
    public byte[] getData() {
        if (hasExpired()) {
            return null;
        }
        return Arrays.copyOf(mData, mData.length);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        if (hasExpired()) {
            return null;
        }
        return mHeaders;
    }

    @Override
    public String toString() {
        return "CacheEntryContainer [mData=" + (mData == null ? "null" : "not null")
                + ", mHeaders=" + (mHeaders == null ? "null" : "not null")
                + ", mExpiryTime=" + mExpiryTime + "]";
    }

    private boolean hasExpired() {
        if (mExpiryTime == 0) {
            return false;
        }

        long current = System.currentTimeMillis();
        return mExpiryTime < current;
    }

}
