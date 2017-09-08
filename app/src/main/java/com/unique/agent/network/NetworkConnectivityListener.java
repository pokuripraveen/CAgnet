package com.unique.agent.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.unique.agent.utils.SystemUtils;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class NetworkConnectivityListener {
    private final Context mContext;
    private final ConnectivityManager mConnectivityManager;
    private final Set<INetworkConnectivityListener> mListeners = Collections.synchronizedSet(
            new HashSet<INetworkConnectivityListener>()
    );
    private boolean mIsRegistered;
    private NetworkStatus mLastStatus;

    /**
     * Member field to keep reference to the Connectivity listener.
     */
    private final BroadcastReceiver mReceiver = new ConnectivityBroadcastReceiver(this);

    /**
     * Listener for the Connectivity state change event.
     */
    private static final class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        /**
         * Reference to the outer class.
         */
        private final WeakReference<NetworkConnectivityListener> mReference;

        /**
         * Constructor.
         *
         * @param listener Reference to the outer class.
         */
        public ConnectivityBroadcastReceiver(final NetworkConnectivityListener listener) {
            mReference = new WeakReference<>(listener);
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final NetworkConnectivityListener listener = mReference.get();
            if (listener == null) {
                return;
            }
            listener.onConnectivityChanged(intent);
        }
    }

    public NetworkConnectivityListener(final Context context) {
        mContext = context;
        mConnectivityManager = SystemUtils.getConnectivityManager(context);
    }

    synchronized public void addNetworkConnectivityListener(INetworkConnectivityListener listener) {
        mListeners.add(listener);
        if (!mIsRegistered) {
            registerReceiver();
        }
    }

    synchronized public void removeNetworkConnectivityListener(INetworkConnectivityListener listener) {
        mListeners.remove(listener);
        if (mIsRegistered && mListeners.isEmpty()) {
            unregisterReceiver();
        }
    }

    public Set<NetworkConnectivityListener> getNetworkConnectivityListeners() {
        return new HashSet(mListeners);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mReceiver, filter);
        mIsRegistered = true;
    }

    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
        mIsRegistered = false;
    }

    private void onConnectivityChanged(final Intent intent) {
        if (intent == null) {
            Log.w("PRAV", "Intent is null");
            return;
        }
        final String action = intent.getAction();
        Log.d("PRAV","Action "+action+"  , extras  " + intent.getExtras());
        if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            return;
        }
        notifyStatusChanged(getNetworkStatus());
    }

    private static INetworkConnectivityListener[] NOTIFY_STATUS_DUMMY_ARRAY = new INetworkConnectivityListener[0];

    private void notifyStatusChanged(final NetworkStatus status) {
        if (mLastStatus != status) {
            mLastStatus = status;

            // Some mListeners were removing themselves in this event.
            final INetworkConnectivityListener[] tempCopy = mListeners.toArray(NOTIFY_STATUS_DUMMY_ARRAY);

            for (final INetworkConnectivityListener listener : tempCopy) {
                listener.onNetworkStatusChanged(status);
            }
        }
    }

    public NetworkStatus getNetworkStatus() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkTypeToStatus(networkInfo.getType());
        }
        return NetworkStatus.NO_NETWORK_ACCESS;
    }

    public String getNetworkName() {
        String networkName = "Unknown";
        try {
            NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    networkName = activeNetworkInfo.getTypeName();
                } else {
                    networkName = activeNetworkInfo.getSubtypeName();
                }
            }
        } catch (Exception e) {
            Log.e("PRAV","ConnectivityMonitor Error in getNetworkName()."+ e);
        }
        return networkName;
    }

    private NetworkStatus networkTypeToStatus(int type) {
        switch (type) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
                return NetworkStatus.WIFI_NETWORK_ACCESS;
            case ConnectivityManager.TYPE_ETHERNET:
                return NetworkStatus.ETHERNET_NETWORK_ACCESS;
            default:
                return NetworkStatus.CELL_NETWORK_ACCESS;
        }
    }
}
