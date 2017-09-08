package com.unique.agent.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class FileUtils {

    /**
     * Default constructor.
     */
    private FileUtils() {
        super();
    }

    /**
     * This method re-throws any {@link Exception} as {@link IOException}.<br>
     * This is temporary solution and it is designed in order to eliminate throwing
     * {@link android.system.ErrnoException} by {@link File#createNewFile()} when client code
     * expecting only {@link IOException}.
     * <br>
     * Appropriate <a href="https://code.google.com/p/android/issues/detail?id=197260">report</a>
     * was created against Google Android SDK 21 - 23.
     *
     * @param file Instance of the {@link File} to perform operation over.
     *
     * @return {@code true} if the file has been created, {@code false} if it already exists.
     *
     * @throws IllegalArgumentException In case of provided reference is {@code null}.
     * @throws IOException In any cases when it is impossible to create new file.
     */
    public static boolean createNewFileApi21(final File file)
            throws IllegalArgumentException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("");
        }
        try {
            return file.createNewFile();
        } catch (final Exception exception) {
            // Re-throw exception as IOException
            throw new IOException(exception);
        }
    }
}
