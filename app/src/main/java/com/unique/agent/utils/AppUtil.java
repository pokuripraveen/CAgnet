package com.unique.agent.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by praveenpokuri on 04/07/17.
 */

public class AppUtil {



    @Nullable
    public static String encode(@NonNull String password) {
        try {

            String data = Base64.encodeToString(password.getBytes("UTF-8"), Base64.NO_WRAP);
            return data;

        } catch (UnsupportedEncodingException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Nullable
    public static String decode(@NonNull final String encyPassword) {
        try {
            byte[] decode = Base64.decode(encyPassword.getBytes("UTF-8"), Base64.NO_WRAP);
            String password = new String(decode, "UTF-8");
            return password;

        } catch (UnsupportedEncodingException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        } else {
            return regionMatches(str1, true, 0, str2, 0, Math.max(str1.length(), str2.length()));
        }
    }

    /**
     * Green implementation of regionMatches.
     *
     * @param cs the {@code CharSequence} to be processed
     * @param ignoreCase whether or not to be case insensitive
     * @param thisStart the index to start on the {@code cs} CharSequence
     * @param substring the {@code CharSequence} to be looked for
     * @param start the index to start on the {@code substring} CharSequence
     * @param length character length of the region
     * @return whether the region matched
     */
    private static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart,
                                 CharSequence substring, int start, int length)    {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        } else {
            // TODO: Implement rather than convert to String
            return cs.toString().regionMatches(ignoreCase, thisStart, substring.toString(), start, length);
        }
    }
}
