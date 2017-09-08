package com.unique.agent.network;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public interface INetworkConnectivityListener {
    /**
     * Called when network status changes.
     *
     * @param status the new {@link NetworkStatus}
     */
    void onNetworkStatusChanged(NetworkStatus status);
}
