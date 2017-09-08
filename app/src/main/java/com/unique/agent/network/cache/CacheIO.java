package com.unique.agent.network.cache;

/**
 * Created by praveenpokuri on 18/08/17.
 */

import android.content.Context;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.unique.agent.utils.FileUtils;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * Handle all operations that deal directly with the internal or external
 * storage on the device.<br/>
 * <br/>
 * This class should only be used within the context of the cache manager
 * and no where else.  With this in mind, it is assumed that data and
 * arguments have been cleansed before getting here.
 */
final class CacheIO {
    /**
     * File operations.
     */
    public static enum Operation {
        /**
         * Write data to storage.
         */
        WRITE,

        /**
         * Read data from storage.
         */
        READ
    }

    private static final int BUFFER_SIZE = 1024;

    private static CacheIO sInstance;
    private boolean mHasStarted;
    private File mStorageDirectory;
    private DiskLruCache mDiskLru;


    /**
     * Once instance of {@link CacheIO} exists so that the
     * appropriate operations without using broadcasts.
     *
     * @return The existing instance of {@link CacheIO}.
     */
    static synchronized CacheIO getInstance() {
        if (sInstance == null) {
            sInstance = new CacheIO();
        }
        return sInstance;
    }

    private CacheIO() { }

    /**
     * Perform the initialization and other sanity checks.<br/>
     * <br/>
     * When called, the storage directory is created if it does
     * not exist and the LRU cache is started.  You may wish to
     * call this asynchronously.
     *
     * @param context The context of the application.
     * @param configuration {@link CacheConfiguration} to use.
     * @throws MediaUnavailableException If the storage is not available for whatever reason.
     */
    public synchronized void start(final Context context, final CacheConfiguration configuration) throws MediaUnavailableException {
        if (mHasStarted) {
            return;
        }

        mStorageDirectory = configuration.getStorageDirectory(context);
        if (!mStorageDirectory.exists()) {
            try {
                File file = new File(mStorageDirectory, ".nomedia");
                file.mkdirs();
                FileUtils.createNewFileApi21(file);
            } catch (IOException e) {
                throw new MediaUnavailableException();
            }
        }

        try {
            long cacheSize = configuration.getCacheSize();
            mDiskLru = DiskLruCache.open(mStorageDirectory, 1, 1, cacheSize);
            mHasStarted = true;
        } catch (IOException e) {
            // TODO Handle this better.
            throw new RuntimeException(e);
        }
    }

    /**
     * Write data to the cache.  This should not be called directly.
     * Use with care if you do.
     *
     * @param file       A {@link File} referencing the cache entry.
     * @param container  An instance of {@link CacheEntryContainer}.
     * @param obfuscator An instance of {@link DefaultObfuscator} or null if nothing is being used.
     */
    synchronized void writeToStorage(final File file, final IDataCacheContainer container, final DefaultObfuscator obfuscator) throws IOException {
        String key = file.getName();
        DiskLruCache.Editor editor = mDiskLru.edit(key);

        BufferedOutputStream outputStream = new BufferedOutputStream(editor.newOutputStream(0), BUFFER_SIZE);
        byte[] data = SerializationUtils.serialize(container);

        if (obfuscator != null) {
            data = obfuscator.encrypt(data);
        }

        outputStream.write(data);
        outputStream.close();
        editor.commit();

        mDiskLru.flush();
    }

    /**
     * Read the data from a file in the cache.
     * @param file The {@link File} to read from.
     * @param obfuscator An instance of {@link DefaultObfuscator} or null if nothing is being used.
     * @return A {@link CacheEntryContainer} if successful, {@code null} if not.
     */
    synchronized IDataCacheContainer readFromStorage(final File file,
                                                     final DefaultObfuscator obfuscator) throws IOException {
        final DiskLruCache.Snapshot snapshot = mDiskLru.get(file.getName());
        if (snapshot == null) {
            return null;
        }
        final InputStream stream = snapshot.getInputStream(0);
        IDataCacheContainer container = null;

        try {
            if (obfuscator != null) {
                byte[] data = new byte[(int) snapshot.getLength(0)];
                if (data.length > 0) {
                    int count = stream.read(data);
                    if (count > 0) {
                        container = (IDataCacheContainer) SerializationUtils.deserialize(data);
                    }
                }
            } else {
                container = (IDataCacheContainer)SerializationUtils.deserialize(stream);
            }
        } catch (EOFException e) {
            Log.e(getClass().getName(), "Error reading from cache: " + e);
        } catch (SerializationException e) {
            /*
             * Must catch this exception.  Should the class CacheEntryContainer
             * change package or name, existing cache entries will not
             * deserialize properly because the class used no longer exists.
             *
             * Nothing special needs to be done here as data will be null
             * and it should look to the caller as if the cache entry expired.
             */
        } finally {
            snapshot.close();
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(getClass().getName(), "Error closing stream: " + e);
            }
        }

        // The data in the container is null when it has
        // expired internally.
        if (container == null || container.getData() == null) {
            removeFile(file.getName());
        }

        return container;
    }

    /**
     * Removes key from the cache.
     *
     * @param fileName Key name.
     * @return {@code true} in case of success, {@code false} otherwise.
     * @throws IOException
     */
    boolean removeFile(final String fileName) throws IOException {
        return mDiskLru != null && mDiskLru.remove(fileName);
    }
}
