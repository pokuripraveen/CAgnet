package com.unique.agent.network.cache;

/**
 * Created by praveenpokuri on 18/08/17.
 */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract class to provide a standard way of generating
 * cache keys. This will also allow special implementations
 * that required special keys that do not use the default.<br/>
 * <br/>
 * Generally, just creating an annonymous instance of this will be
 * sufficient.  Any special cases will have to override {@link CacheKeyGenerator#generateKey(String)}.
 */
public abstract class CacheKeyGenerator {
    private static final MessageDigest MESSAGE_DIGEST = getDigestInstance();

    private static MessageDigest getDigestInstance() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // TODO RuntimeException?
            return null;
        }
    }

    /**
     * Generate an MD5 string.  The encoding will be padded with 0's to
     * ensure a full 32 size.<br/>
     * <br/>
     * If you override this method, calling super() will result in an
     * MD5 encoded value being returned.
     *
     * @param source The source to use.
     * @return An MD5 encoded, lower case version of the source.
     */
    public synchronized String generateKey(String source) {
        MESSAGE_DIGEST.reset();
        MESSAGE_DIGEST.update(source.getBytes(), 0, source.length());
        String rtn = new BigInteger(1, MESSAGE_DIGEST.digest()).toString(16);
        if (rtn.length() < 32) {
            StringBuffer padded = new StringBuffer(rtn);
            do {
                padded.insert(0, "0");
            } while(padded.length() < 32);

            return padded.toString().toLowerCase();
        }
        else {
            return rtn.toLowerCase();
        }
    }
}
