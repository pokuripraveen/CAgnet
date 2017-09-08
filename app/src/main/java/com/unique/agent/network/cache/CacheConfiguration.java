package com.unique.agent.network.cache;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Method;

import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public final class CacheConfiguration {

    private StorageLocation mCacheStorageLocation;
    private String mSubDirectory;
    private long mCacheSize;
    private CacheKeyGenerator mKeyGenerator;
    private String mStorageDirectory;

    private CacheConfiguration() {

    }

    /**
     * @return The configured {@link StorageLocation}.
     */
    public StorageLocation getStorageLocation() {
        return mCacheStorageLocation;
    }

    /**
     * @return The configured sub directory.
     */
    public String getSubDirectory() {
        return mSubDirectory;
    }

    /**
     * @return The configured cache size.
     */
    public long getCacheSize() {
        return mCacheSize;
    }

    /**
     * @return The configured {@link CacheKeyGenerator}.
     */
    public CacheKeyGenerator getKeyGenerator() {
        return mKeyGenerator;
    }

    /**
     * Get the absolute path for the storage directory.
     *
     * @param context The application context.
     * @return A {@link File} for the storage location.
     * @throws MediaUnavailableException
     */
    public File getStorageDirectory(Context context) throws MediaUnavailableException {
        if (mStorageDirectory == null) {
            StringBuffer directory = null;

            if (mCacheStorageLocation == StorageLocation.INTERNAL) {
                directory = new StringBuffer(context.getFilesDir().getAbsolutePath());
            }
            else if (mCacheStorageLocation == StorageLocation.EXTERNAL) {
                if (! isWriteExternalAllowed(context)) {
                    throw new MediaUnavailableException("WRITE_EXTERNAL_STORAGE permission not granted.");
                }

                String state = Environment.getExternalStorageState();
                if (! state.equals(Environment.MEDIA_MOUNTED)) {
                    throw new MediaUnavailableException("External storage not mounted.");
                }

                directory = getExternalDirectory(context);
            }
            else {
                throw new RuntimeException("Unhandled location " + mCacheStorageLocation);
            }

            if (mSubDirectory != null && directory != null) {
                directory.append(mSubDirectory);
            }

            mStorageDirectory = (directory == null) ? null : directory.toString();
        }

        return mStorageDirectory == null ? null : new File(mStorageDirectory);
    }

    private boolean isWriteExternalAllowed(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = context.getPackageName();
        int allowed = manager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, name);
        return allowed == PackageManager.PERMISSION_GRANTED;
    }

    private StringBuffer getExternalDirectory(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            String packageName = context.getPackageName();
            String baseExternal = Environment.getExternalStorageDirectory().getAbsolutePath();
            return new StringBuffer(String.format("%s/Android/data/%s/files", baseExternal, packageName));
        } else {
            try {
                Method externalMethod = Context.class.getMethod("getExternalFilesDir", String.class);
                if (null != externalMethod) {
                    File externalDir = (File)externalMethod.invoke(context, (Object)null);
                    if (externalDir != null) {
                        if (!externalDir.exists()) {
                            externalDir.mkdirs();
                        }
                        return new StringBuffer(externalDir.getAbsolutePath());
                    }
                }
            } catch (NoSuchMethodException e) {
                Log.e("PRAV", "Can not get getExternalFilesDir method: ", e);
            } catch (Exception e) {
                Log.e("PRAV", "getExternalFilesDir method invocation failed: ", e);
            }
            return null;
        }
    }

    /**
     * Build a new instance of {@link CacheConfiguration} with sensible defaults.
     */
    public static final class Builder {

        private StorageLocation mCacheStorageLocation = StorageLocation.INTERNAL;
        private String mSubDirectory = null;
        private long mCacheSize = 20971520;
        private CacheKeyGenerator mKeyGenerator = new CacheKeyGenerator() { };

        /**
         * Set the storage location for the cache.  Default is {@link StorageLocation#INTERNAL}.
         * @param storageLocation The storage location.
         * @return This {@link Builder} object.
         */
        public Builder setStorageLocation(StorageLocation storageLocation) {
            mCacheStorageLocation = storageLocation;
            return this;
        }

        /**
         * Set the sub directory to use in the cache.  Default is {@code null}.
         * @param subDirectory The sub directory to use in the cache.
         * @return This {@link Builder} object.
         */
        public Builder setSubDirectory(String subDirectory) {
            if (subDirectory != null) {
                String dir = subDirectory.replaceAll("^\\/+|\\/+$", "");
                mSubDirectory = dir;
            }
            else {
                mSubDirectory = null;
            }

            return this;
        }

        /**
         * Set the size of the cache.
         * @param size The size of the cache.
         * @return This {@link Builder} object.
         */
        public Builder setCacheSize(long size) {
            mCacheSize = size;
            return this;
        }

        /**
         * Set a custom cache key generator.  Default is {@link CacheKeyGenerator}.
         * @param generator The {@link CacheKeyGenerator} to use.
         * @return This {@link Builder} object.
         */
        public Builder setKeyGenerator(CacheKeyGenerator generator) {
            mKeyGenerator = generator;
            return this;
        }

        /**
         * Create a new instance of {@link CacheConfiguration} with the values
         * set in this builder.
         * @return A new {@link CacheConfiguration} object.
         * @throws IllegalStateException If any of the proper settings has made the configuration unusable.
         */
        public CacheConfiguration build() throws IllegalStateException {
            CacheConfiguration rtn = new CacheConfiguration();
            if (mCacheStorageLocation == null) {
                throw new IllegalStateException("Storage location can not be null.");
            }
            if (mCacheSize <= 0) {
                throw new IllegalStateException("Cache size can not be 0.");
            }
            if (mKeyGenerator == null) {
                throw new IllegalStateException("Key generator can not be null.");
            }

            rtn.mCacheStorageLocation = mCacheStorageLocation;
            rtn.mSubDirectory = mSubDirectory;
            rtn.mCacheSize = mCacheSize;
            rtn.mKeyGenerator = mKeyGenerator;

            return rtn;
        }
    }



    /**
     * Flag to determine the location of the cache storage.
     */
    public static enum StorageLocation {
        /**
         * Use the internal file storage.
         * @since API 1
         */
        INTERNAL,
        /**
         * Use the external file storage.  When used, the cache
         * may become unavailable should the external storage
         * be removed or unmounted.
         * @since API 1
         */
        EXTERNAL
    }
}
