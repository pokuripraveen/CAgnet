package com.unique.agent.utils;

import android.os.Build;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public final class AndroidApiLevelUtils {
    /**
     * Default constructor.
     */
    private AndroidApiLevelUtils() {
        super();
    }

    /**
     * @return {@code true} if Android OS API level is greater then or equal to 23 M, {@code false} otherwise.
     */
    public static boolean isApi23M() {
        return getApiLevel() >= Build.VERSION_CODES.M;
    }

    /**
     * @return {@code true} if Android OS API level is greater then or equal to 21, {@code false} otherwise.
     */
    public static boolean isApi21() {
        return getApiLevel() >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * @return {@code true} if Android OS API level is greater then or equal to 19, {@code false} otherwise.
     */
    public static boolean isApi19() {
        return getApiLevel() >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * @return {@code true} if Android OS API level is greater then or equal to 18, {@code false} otherwise.
     */
    public static boolean isApi18() {
        return getApiLevel() >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * @return {@code true} if Android OS API level is greater then or equal to 17, {@code false} otherwise.
     */
    public static boolean isApi17() {
        return getApiLevel() >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * @return {@code true} if Android OS API level is greater then or equal to 16, {@code false} otherwise.
     */
    public static boolean isApi16() {
        return getApiLevel() >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * @return {@code true} if Android OS API level is greater then or equal to 14, {@code false} otherwise.
     */
    public static boolean isApi14() {
        return getApiLevel() >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * @return {@code true} if Android OS API level is greater then or equal to 13, {@code false} otherwise.
     */
    public static boolean isApi13() {
        return getApiLevel() >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    /**
     * @return The user-visible SDK version of the framework.
     */
    public static int getApiLevel() {
        return Build.VERSION.SDK_INT;
    }
}
