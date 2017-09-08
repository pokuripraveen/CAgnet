package com.unique.agent.network.cache;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by praveenpokuri on 18/08/17.
 */

class DefaultObfuscator {
    private byte[] mKey;
    private byte[] mIv = {10, 12, 11, 12, 11, 10, 12, 11, 12, 11, 12, 11, 12, 11, 12, 11};

    /**
     * Create a new instance.
     * @param keySeed The seed to use for encryption.
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public DefaultObfuscator(byte[] keySeed)  {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            //Android 4.2 changed the default provider for SecureRandom to use OpenSSL instead of the previous Crypto provider.
            //This reverts to the old behavior

            Provider crypto = Security.getProvider("Crypto");
            System.out.println("Checking Provider");
            SecureRandom sr;
            if (crypto != null) {
                System.out.println("Using Crypto Provider");
                // We are on Android, specify the Crypto provider (http://alinberce.wordpress.com/2013/02/12/javax-crypto-badpaddingexception-pad-block-corrupted/)
                sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            } else {
                sr = SecureRandom.getInstance("SHA1PRNG");
            }

            System.out.println(sr.getProvider());
            sr.setSeed(keySeed);
            kgen.init(128, sr);
            SecretKey skey = kgen.generateKey();
            mKey = skey.getEncoded();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public DefaultObfuscator() {
        this(new byte[]{12,35,1,98,70,11,12,15,4,6,7,2,8,4,7,1});
    }

    /**
     * Get an encrypted byte array.
     *
     * @param clear The unencrypted byte data.
     * @return Encrypted byte data.
     * @throws GeneralSecurityException
     */
    public byte[] encrypt(byte[] clear) {
        byte[] encrypted;
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            encrypted = cipher.doFinal(clear);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
        return encrypted;
    }

    /**
     * Get an unencrypted byte array.
     *
     * @param encrypted The encrypted byte data.
     * @return The unencrypted byte data.
     * @throws GeneralSecurityException
     */
    public byte[] decrypt(byte[] encrypted) {

        byte[] decrypted;
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            decrypted = cipher.doFinal(encrypted);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
        return decrypted;
    }

    public Cipher getCipher(int cryptMode) throws GeneralSecurityException {
        SecretKeySpec skeySpec = new SecretKeySpec(mKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(cryptMode, skeySpec, new IvParameterSpec(mIv));
        return cipher;
    }
}
