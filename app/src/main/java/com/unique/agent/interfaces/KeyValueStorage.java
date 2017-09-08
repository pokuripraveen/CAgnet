package com.unique.agent.interfaces;

import java.util.Set;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public interface KeyValueStorage {
    /**
     * Retrieve a boolean value from the storage.
     *
     * @param key      The name of the key to retrieve.
     * @param defValue Value to return if this key does not exist.
     * @return the key value if it exists, or defValue
     */
    boolean getBoolean(String key, boolean defValue);

    /**
     * Retrieve a float value from the storage.
     *
     * @param key      The name of the key to retrieve.
     * @param defValue Value to return if this key does not exist.
     * @return the key value if it exists, or defValue
     */
    float getFloat(String key, float defValue);

    /**
     * Retrieve a int value from the storage.
     *
     * @param key      The name of the key to retrieve.
     * @param defValue Value to return if this key does not exist.
     */
    int getInt(String key, int defValue);

    /**
     * Retrieve a long value from the storage.
     *
     * @param key      The name of the key to retrieve.
     * @param defValue Value to return if this key does not exist.
     * @return the key value if it exists, or defValue
     */
    long getLong(String key, long defValue);

    /**
     * Retrieve a string value from the storage.
     *
     * @param key      The name of the key to retrieve.
     * @param defValue Value to return if this key does not exist.
     * @return the key value if it exists, or defValue
     */
    String getString(String key, String defValue);

    /**
     * Retrieve a set of String values from storage
     */
    Set<String> getStringSet(String key, Set<String> defValues);

    /**
     * Set a boolean value in the storage.
     *
     * @param key   The name of the key to modify.
     * @param value The new value for the key.
     */
    void putBoolean(String key, boolean value);

    /**
     * Set a float value in the storage.
     *
     * @param key   The name of the key to modify.
     * @param value The new value for the key.
     */
    void putFloat(String key, float value);

    /**
     * Set a int value in the storage.
     *
     * @param key   The name of the key to modify.
     * @param value The new value for the key.
     */
    void putInt(String key, int value);

    /**
     * Set a long value in the storage.
     *
     * @param key   The name of the key to modify.
     * @param value The new value for the key.
     */
    void putLong(String key, long value);

    /**
     * Set a String value in the storage.
     *
     * @param key   The name of the key to modify.
     * @param value The new value for the key.
     */
    void putString(String key, String value);

    /**
     * Set a set of String values in storage
     */
    void putStringSet(String key, Set<String> values);

    /**
     * Set a String value in the storage atomically.
     *
     * @param key   The name of the key to modify.
     * @param value The new value for the key.
     */
    boolean putStringAtomically(String key, String value);

    /**
     * Mark that a key value should be removed from storage.
     *
     * @param key The name of the key to remove.
     */
    public void remove(String key);

    /**
     * Removes a key value from storage atomically.
     *
     * @param key The name of the key to remove.
     */
    public boolean removeAtomically(String key);

}
