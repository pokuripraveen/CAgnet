package com.unique.agent.network.cache;

import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class DataCacheManager implements IDataCache {
    private CacheManager cache;
    /**
     * Default is -1, means number of files is not controlled, could be as much as possible.
     */
    public static final int DEFAULT_MAX_CACHED_ITEMS = -1;

    /**
     * Max number of files saved in cache space.
     */
    private int mMaxCachedItems = DEFAULT_MAX_CACHED_ITEMS;

    public DataCacheManager(final CacheManager cache, final int maxCachedItems) {
        Validate.notNull(cache, "cache");
        this.cache = cache;
        mMaxCachedItems = maxCachedItems;
    }

    @Override
    public void writeToCache(String key, byte[] data, long expiry) {
        writeToCache(key, null, data, expiry);
    }

    @Override
    public void writeToCache(String key, Map<String, List<String>> headers, byte[] data, long expiry) {
        if (cache == null) {
            Log.e("Can not write to cache, cache is null");
            return;
        }

        // Keep track on cached space
        clearCacheIfNeed();

        final String filename = cache.generateCacheKey(key);
        try {
            final File file = cache.getCacheFile(filename);
            cache.write(file, headers, data, expiry);
        } catch (final IOException ex) {
            throw new RuntimeException("Cannot write " + data.length + " bytes to " + filename, ex);
        }
    }

    @Override
    public IDataCacheContainer retrieveCachedData(String key) {
        return null;
    }

    /**
     * Keep track on cached space, remove the oldest files after {@link #mMaxCachedItems} amount.
     */
    private void clearCacheIfNeed() {
        Map<Long, List<File>> map = null;
        try {
            map = cache.getCachedFileBuckets();
        } catch (final MediaUnavailableException e) {
            Log.e("Can not get cached files:" + e.getMessage());
        }
        if (map == null || map.isEmpty()) {
            return;
        }
        final int cachedFilesNumber = CacheManager.getCachedFilesNumber(map);
        Log.d("ClearCacheIfNeed, size:" + cachedFilesNumber + ", mMaxCachedItems:" + mMaxCachedItems);
        if (cachedFilesNumber >= mMaxCachedItems) {
            cache.clearObsoleteCachedFiles(map, cachedFilesNumber - mMaxCachedItems);
        }
    }
}
