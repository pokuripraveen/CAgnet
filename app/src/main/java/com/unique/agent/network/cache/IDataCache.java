package com.unique.agent.network.cache;

import java.util.List;
import java.util.Map;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public interface IDataCache {
    /**
     * Write arbitrary data to the cache. This call is synchronous and should not be called from the UI thread.
     *
     * @param key The cache key where this data could normally be found.
     * @param data The data to write.
     * @param expiry The expiry time in milliseconds for the cache entry.
     * @throws RuntimeException If the cache could not be saved.
     */
    void writeToCache(String key, byte[] data, long expiry);

    /**
     * Write arbitrary data to the cache. This call is synchronous and should not be called from the UI thread.
     *
     * @param key     The cache key where this data could normally be found.
     * @param headers Collection of HTTP header fields.
     * @param data    The data to write.
     * @param expiry  The expiry time in milliseconds for the cache entry.
     */
    void writeToCache(String key, Map<String, List<String>> headers, byte[] data, long expiry);

    /**
     * @return Data ({@link IDataCacheContainer}) that was previously set in the cache,
     *         or null if the cache has expired or it never existed.
     */
    IDataCacheContainer retrieveCachedData(String key);
}
