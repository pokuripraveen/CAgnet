package com.unique.agent.network;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public enum NetworkStatus {
    /** No network available. */
    NO_NETWORK_ACCESS,

    /** Only cellular network access is available. */
    CELL_NETWORK_ACCESS,

    /** WiFi network access is available. */
    WIFI_NETWORK_ACCESS,

    /** Ethernet network access is available */
    ETHERNET_NETWORK_ACCESS
}
