package com.unique.agent.network.cache;

import android.content.Context;

import com.unique.agent.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class CacheManager {
    /**
     * Default expiry time of 3hrs<br/>
     * <br/>
     * You should try to respect what the CacheControl
     * header says.  The current implementation of the cache
     * uses the {@code IRunnableResultsListener} interface which
     * does not provide this information.
     */
    public static final long DEFAULT_EXPIRY = 10800000;

    private static CacheManager sInstance;
    private boolean mHasStarted;
    private Context mContext;
    private CacheConfiguration mConfiguration;
    private DefaultObfuscator mDefaultObfuscator;

    public static synchronized CacheManager getInstance() {
        if (sInstance == null) {
            sInstance = new CacheManager();
        }
        return sInstance;
    }

    private CacheManager() {

    }

    /**
     * Start the cache manager.  This will also start the {@code DiskLruCache}.
     * This could take a while depending on the cache size.  You may want to
     * run this asynchronously.
     *
     * @param context The application context.
     * @param configuration The configuration for the cache.
     * @throws MediaUnavailableException
     */
    public void start(Context context, CacheConfiguration configuration) throws MediaUnavailableException {
        start(context, configuration, null);
    }

    /**
     * Start the cache manager.  This will also start the {@code DiskLruCache}.
     * This could take a while depending on the cache size.  You may want to
     * run this asynchronously.
     *
     * @param context The application context.
     * @param configuration The configuration for the cache.
     * @param obfuscator An instance of {@link DefaultObfuscator}.
     * @throws MediaUnavailableException
     */
    public void start(Context context, CacheConfiguration configuration, DefaultObfuscator obfuscator) throws MediaUnavailableException {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null.");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("configuration can not be null.");
        }

        synchronized(this) {
            if (mHasStarted) {
                return;
            }

            mContext = context.getApplicationContext();
            mConfiguration = configuration;
            mDefaultObfuscator = obfuscator;
            CacheIO.getInstance().start(mContext, mConfiguration);
            mHasStarted = true;
        }
    }

    /**
     * Create a new {@link File} object referencing the cache entry.  This should not
     * be called directly.  Use with care if you do.
     *
     * @param filename The name of the file.  Should come from {@link CacheKeyGenerator}.
     * @return A new {@link File} object.
     * @throws NullPointerException From {@code new File()}.
     * @throws IOException
     */
    public File getCacheFile(final String filename) throws NullPointerException, IOException {
        final File directory = mConfiguration.getStorageDirectory(mContext);
        final File rtn = new File(directory, filename);
        if (!rtn.exists()) {
            FileUtils.createNewFileApi21(rtn);
        }

        return rtn;
    }

    public boolean doesFileExist(String filename) throws IOException {
        File directory = mConfiguration.getStorageDirectory(mContext);
        File rtn = new File(directory, filename);
        return rtn.exists();
    }

    /**
     * @return The {@link CacheConfiguration.StorageLocation} that is being used.
     */
    public CacheConfiguration.StorageLocation getStorageLocation() {
        return mConfiguration.getStorageLocation();
    }
    /**
     * @return The sub directory that files will be stored in.  May be {@code null}.
     */
    public String getSubDirectory() {
        return mConfiguration.getSubDirectory();
    }

    /**
     * @return The {@link CacheKeyGenerator} that is being used.
     */
    public CacheKeyGenerator getCacheKeyGenerator() {
        return mConfiguration.getKeyGenerator();
    }

    /**
     * Generate a cache key.
     *
     * @param source The raw source.
     * @return An encoded key.
     */
    public String generateCacheKey(String source) {
        CacheKeyGenerator generator = mConfiguration.getKeyGenerator();
        return generator.generateKey(source);
    }

    /*
     * Check if the manager has been started.
     */
    private void checkIfStarted() throws IllegalStateException {
        synchronized(this) {
            if (!mHasStarted) {
                throw new IllegalStateException("Manager has not been started.");
            }
        }
    }

    /**
     * Perform a synchronous write to the storage.
     *
     * @param file The {@link File} to read.
     * @param data The data to write.
     * @param expiry How long from now should this entry expire.
     * @throws IOException if an error occurred writing the file.
     * @throws IllegalStateException if the manager has not been started.
     */
    public void write(File file, byte[] data, long expiry) throws IOException, IllegalStateException {
        write(file, null, data, expiry);
    }

    /**
     * Perform a synchronous write to the storage.
     *
     * @param file    The {@link File} to read.
     * @param headers The collection of HTTP header fields.
     * @param data    The data to write.
     * @param expiry  How long from now should this entry expire.
     * @throws IOException
     * @throws IllegalStateException
     */
    public void write(final File file, final Map<String, List<String>> headers, final byte[] data,
                      final long expiry) throws IOException, IllegalStateException {
        checkIfStarted();
        long expiryTime = (expiry == 0) ? 0 : Math.min(System.currentTimeMillis() + expiry, Long.MAX_VALUE);
        final IDataCacheContainer container = new CacheEntryContainer(headers, data, expiryTime);
        CacheIO.getInstance().writeToStorage(file, container, mDefaultObfuscator);
    }

    /**
     * Perform a synchronous read from the storage.
     *
     * @param file The {@link File} to read.
     * @throws IOException if an error occurred reading the file.
     * @throws IllegalStateException if the manager has not been started.
     * @return {@link CacheEntryContainer} on success, {@code null} on failure.
     */
    public IDataCacheContainer readFromStorage(File file) throws IOException, IllegalStateException {
        checkIfStarted();
        return CacheIO.getInstance().readFromStorage(file, mDefaultObfuscator);
    }

    /**
     * Gets the buckets with cached files in the cache directory.
     * Assume that there are no subdirectories included.
     *
     * @return Collection of the cached file buckets.
     *
     * @throws MediaUnavailableException
     */
    public Map<Long, List<File>> getCachedFileBuckets() throws MediaUnavailableException {
        final Map<Long, List<File>> map = new TreeMap<>();
        if (mConfiguration == null) {
            return map;
        }
        if (mContext == null) {
            return map;
        }
        final File directory = mConfiguration.getStorageDirectory(mContext);
        if (directory == null) {
            return map;
        }
        final File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return map;
        }

        // TODO : Implement BFS or DFS instead of simple loop. Assume we have no sub dirs.

        for (final File file : files) {
            if (file == null) {
                continue;
            }
            if (file.isDirectory()) {
                continue;
            }
            if (!file.exists()) {
                continue;
            }

            // Ignores physical lock files, but do not forget to clean them as well!
            if (file.getName().endsWith(".0")) {
                continue;
            }
            // Different files may be saved withing 1 millisecond.
            // Introduce additional collection for each timestamp in order to differentiate them.
            final long lastModified = file.lastModified();
            List<File> filesBucket = map.get(lastModified);
            if (filesBucket == null) {
                filesBucket = new ArrayList<>();
                map.put(lastModified, filesBucket);
            }
            filesBucket.add(file);

            //aLog().d(LOG_TAG + " Cached file:" + file.getName());
        }
        return map;
    }

    /**
     * Gets exact number of the cached files in the cache space.
     *
     * @param buckets Buckets with the cached files.
     *
     * @return The number of the cached files in the cache space.
     */
    public static int getCachedFilesNumber(final Map<Long, List<File>> buckets) {
        int counter = 0;
        for (final Map.Entry<Long, List<File>> entry : buckets.entrySet()) {
            if (entry == null) {
                continue;
            }
            final List<File> collection = entry.getValue();
            if (collection == null || collection.isEmpty()) {
                continue;
            }
            counter += collection.size();
        }
        return counter;
    }

    /**
     * Clear cache space from obsolete files. Removes any files that has most earlier modify date.
     *
     * @param map                 Collection of the file buckets.
     * @param filesNumberToRemove Number of files to be removed.
     */
    public void clearObsoleteCachedFiles(final Map<Long, List<File>> map, int filesNumberToRemove) {

        if (map == null || map.isEmpty()) {
            return;
        }
        Log.d("Need to clean " + filesNumberToRemove + " timestamped file buckets");

        // Iterate over file buckets
        outerLoop: for (final Map.Entry<Long, List<File>> entry : map.entrySet()) {
            // Iterate over single bucket that associated with single timestamp
            final List<File> files = entry.getValue();
            for (final File file : files) {
                if (file == null) {
                    continue;
                }
                if (!file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    continue;
                }
                if (file.getName().endsWith(".0")) {
                    continue;
                }

                boolean result = false;
                // First - remove key from cache key store
                try {
                    result = CacheIO.getInstance().removeFile(file.getName());
                } catch (final IOException e) {
                    Log.e("Can not remove cache file: " + e.getMessage());
                }
                Log.d("Cached key for '" + file.getName() + "' deleted:" + result);

                // Then remove physical file
                result = file.delete();
                Log.d("Cached file '" + file.getName() + "' deleted:" + result);

                // Finally, clear physical lock file from cache if the one exists
                final File lockFile = new File(file.getAbsolutePath(), file.getName() + ".0");
                if (lockFile.exists()) {
                    result = lockFile.delete();
                    Log.d("Cached lock file '" + lockFile.getName() + "' deleted:" + result);
                }

                filesNumberToRemove--;
                if (filesNumberToRemove == 0) {
                    Log.d("Cached files has been removed successfully");
                    break outerLoop;
                }
            }
        }
    }

    /**
     * Deletes all files and directories under cache directory.
     *
     * @return {@code true} in case of success, {@code false} otherwise.
     * @throws MediaUnavailableException
     */
    boolean clearCacheDirectory() throws MediaUnavailableException {
        if (mConfiguration == null) {
            return false;
        }
        if (mContext == null) {
            return false;
        }
        final File directory = mConfiguration.getStorageDirectory(mContext);
        if (directory == null) {
            return false;
        }

        // Note - can not use directory.delete() because it is a system directory
        // for this reason, delete all files in loop

        final File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        boolean finalResult = false;
        for (final File file : files) {
            if (file == null) {
                continue;
            }
            final boolean result = file.delete();
            if (!finalResult && result) {
                finalResult = true;
            }
        }
        return finalResult;
    }
}
