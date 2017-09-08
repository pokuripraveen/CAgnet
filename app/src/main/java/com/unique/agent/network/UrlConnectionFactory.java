package com.unique.agent.network;

import java.net.HttpURLConnection;
import java.net.URI;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public class UrlConnectionFactory {
    private HostnameVerifier mHostnameVerifier;

    /**
     * WARNING: Setting this value can leave the app open to Man in the Middle attacks! AllowAllHostnameVerifier should
     *          NOT be used in production.
     *
     * @param hostnameVerifier HostnameVerifier to use for https connections. If null then no explicit hostname verifier
     *                         is set.
     */
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        mHostnameVerifier = hostnameVerifier;
    }

    /**
     * @see #setHostnameVerifier
     */
    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    public HttpURLConnection openConnection(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URI(url).toURL().openConnection();
            changeHostnameVerifier(connection);
            return connection;
        } catch (Exception ex) {
            throw new RuntimeException("Could not open url "+url, ex);
        }
    }

    private void changeHostnameVerifier(final HttpURLConnection connection) {
        if (connection instanceof HttpsURLConnection && mHostnameVerifier != null) {
            HttpsURLConnection secure = (HttpsURLConnection) connection;
            secure.setHostnameVerifier(mHostnameVerifier);
        }
    }
}
