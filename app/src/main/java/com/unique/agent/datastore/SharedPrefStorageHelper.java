package com.unique.agent.datastore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

import com.unique.agent.interfaces.KeyValueStorage;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class SharedPrefStorageHelper implements KeyValueStorage {

    private SharedPreferences sharedPreferences;

    public SharedPrefStorageHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    @Override
    public void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    @Override
    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    @Override
    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    @Override
    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    @Override
    public void putStringSet(String key, Set<String> values) {
        sharedPreferences.edit().putStringSet(key, values).apply();
    }

    @Override
    public boolean putStringAtomically(String key, String value) {
        return sharedPreferences.edit().putString(key, value).commit();
    }

    @Override
    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    @Override
    public boolean removeAtomically(String key) {
        return sharedPreferences.edit().remove(key).commit();
    }
}
