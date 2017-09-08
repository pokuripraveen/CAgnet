package com.unique.agent.network;

import android.text.TextUtils;
import android.util.Pair;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by praveenpokuri on 17/08/17.
 */

class UrlBuilder {

    private String mBaseUrl;
    private Map<String, String> mUrlParams;
    private boolean mGenerateEncodedUrl = true;
    private boolean mSortUrlParams = true;
    /**
     * Constructs Empty URL
     */
    public UrlBuilder() {
        super();
        mBaseUrl = "";
        mUrlParams = new HashMap<>();
    }

    /**
     * Creates the {@link UrlBuilder} based on an existing URL.
     *
     * @param url Provided URL.
     *
     * @throws IllegalArgumentException When provided URL is invalid.
     */
    public UrlBuilder(String url) throws IllegalArgumentException {
        this();
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Provided URL can not be null or empty");
        }
        mBaseUrl = getBaseUrl(url);
        mUrlParams = geUrlParams(url);
    }


    public String getBaseUrl(final String url) {
        String baseUrl = url;
        if (url == null) {
            baseUrl = "";
        } else {
            final int startIndex = url.indexOf('?');
            if (startIndex > 0) {
                baseUrl = url.substring(0, startIndex);
            }
        }
        return baseUrl;
    }

    public Map<String,String> geUrlParams(String url) {
        final Map<String, String> parameters = new HashMap<>();
        if(url != null){
            final int startIndex = url.indexOf('?');
            String paramString = startIndex > 0 ? url.substring(startIndex+1) : "";
            String[] paramPairs = paramString.split("&");
            String[] valuePair;
            for(String pair : paramPairs){
                valuePair = pair.split("=");
                if(valuePair.length == 2){
                    parameters.put(valuePair[0],valuePair[1]);
                }
            }

        }
        return parameters;
    }

    /**
     * Returns a encoded string using the format required by
     * {@code application/x-www-form-urlencoded} MIME content type.
     * @return Encoded string, or null if encoding fails.
     */
    public static String getUrlEncodedValue(String value) {
        String param = value;
        try {
            param = URLEncoder.encode(value, "UTF-8");
        } catch (Exception ex) {
            // using UTF8 - this should not occur
        }
        return param;
    }

    /**
     * Returns the Scheme associated with the URL. If not Scheme is provided, HTTP is assumed
     * @return Scheme (e.g. http) or empty string if no scheme
     */
    public static String getScheme(String url) {
        final int endIndex = url.indexOf("://");
        if(endIndex < 0) {
            return "";
        }
        return url.substring(0, endIndex);
    }

    /**
     * Returns the Host Associated with a URL
     * E.g. http://foo:80/bar would return 'foo:80'
     * @param url The Url
     * @return Host for the URL
     */
    public static String getHostWithPort(String url) {
        int startIndex = url.indexOf("://");
        if(startIndex < 0) {
            startIndex = 0;
        } else {
            startIndex = startIndex + 3;
        }

        int endIndex = url.indexOf("/", startIndex);
        if(endIndex < 0) {
            endIndex = url.length();
        }

        if(startIndex == endIndex) {
            return "";
        }
        return url.substring(startIndex, endIndex);
    }

    /**
     * Returns the Host Associated with a URL
     * E.g. http://foo:80/bar would return 'foo'
     * @param url The Url
     * @return Host for the URL
     */
    public static String getHost(String url) {
        String hostWithPort = getHostWithPort(url);
        int endIndex = hostWithPort.indexOf(":");
        if(endIndex > 0) {
            hostWithPort = hostWithPort.substring(0, endIndex);
        }
        return hostWithPort;
    }

    /**
     * Returns the Port Number if one is provided. If no port number, returns empty string
     * @param url The Url to scan for a port number.
     * @return Port number or empty string
     */
    public static String getPort(final String url) {
        String hostWithPort = getHostWithPort(url);
        int startIndex = hostWithPort.indexOf(":");
        if(startIndex < 0) {
            return "";
        }
        return hostWithPort.substring(startIndex+1);
    }

    /**
     * Appends path to base url
     * @param path The path to append
     */
    public UrlBuilder appendPath(String path) {
        if(path.endsWith("/")) {
            path = path.substring(0, path.length()-1);
        }

        if(!mBaseUrl.endsWith("/") && path.length() > 0) {
            mBaseUrl = mBaseUrl + "/";
        }
        mBaseUrl = mBaseUrl + path;
        return this;
    }

    public UrlBuilder setScheme(String scheme) {
        String oldScheme = getScheme(mBaseUrl);
        if(TextUtils.isEmpty(oldScheme)) {
            if(!scheme.endsWith("://")) {
                scheme = scheme + "://";
            }
            mBaseUrl = scheme + mBaseUrl;
        } else {
            mBaseUrl = scheme + mBaseUrl.substring(oldScheme.length());
        }
        return this;
    }

    /**
     * Sets the 'Host' part of a URL
     * @param host The host to set
     * @return UrlBuilder
     */
    public UrlBuilder setHost(String host) {
        String oldHost = getHost(mBaseUrl);
        mBaseUrl = mBaseUrl.replace(oldHost, host);
        return this;
    }

    /**
     * Sets the port part of the url.
     * @param port Port to setx
     * @return UrlBuilder
     */
    public UrlBuilder setPort(String port) {
        String host = getHost();
        String oldPort = getPort(mBaseUrl);
        if(TextUtils.isEmpty(oldPort)) {
            int startIndex = mBaseUrl.indexOf(host) + host.length();
            mBaseUrl = mBaseUrl.substring(0, startIndex) + ":" + port + mBaseUrl.substring(startIndex);
        } else {
            int startIndex = mBaseUrl.indexOf(oldPort);
            mBaseUrl = mBaseUrl.substring(0, startIndex) + port + mBaseUrl.substring(startIndex + oldPort.length());
        }
        return this;
    }

    /**
     * Adds Parameters to the URL
     * @param key The parameter to add - must not be NULL
     * @param value The value to associate with the key - must not be NULL
     */
    public UrlBuilder addParam(final String key, final String value) {
        mUrlParams.put(key, value);
        return this;
    }

    /**
     * Adds collection of params from a map.
     * @param params
     */
    public UrlBuilder addParams(final Map<String, String> params) {
        mUrlParams.putAll(params);
        return this;
    }

    /**
     * Adds list of params
     * @param params
     * @return
     */
    public UrlBuilder addParams(final List<Pair<String, String>> params) {
        for (final Pair<String, String> pair : params) {
            mUrlParams.put(pair.first, pair.second);
        }
        return this;
    }

    /**
     * Returns the current host
     * @return Host for the current provided url
     */
    public String getHost() {
        return getHost(mBaseUrl);
    }

    /**
     * Returns the Current Port
     * @return Will return empty string if no port
     */
    public String getPort() {
        return getPort(mBaseUrl);
    }

    /**
     * Returns the Scheme associated with the request.
     * @return The scheme associated with the request (if no scheme, http is assumed)
     */
    public String getScheme() {
        String scheme = getScheme(mBaseUrl);
        if(TextUtils.isEmpty(scheme)) {
            return "http";
        }
        return scheme;
    }

    /**
     * Gets the URL Parameters that will be attached to the URL
     * @return Map of known Url Params
     */
    public Map<String, String> getParams() {
        return mUrlParams;
    }

    /**
     * Returns the Parameters as they will be sent if URL Encoding is enabled.
     * @return Map of the known url params encoded for transmission.
     */
    public Map<String, String> getEncodedParams() {
        final Map<String, String> encodedParams = new HashMap<>();
        for (final String key : mUrlParams.keySet()) {
            encodedParams.put(getUrlEncodedValue(key), getUrlEncodedValue(mUrlParams.get(key)));
        }
        return encodedParams;
    }

    /**
     * Returns the base url with no parameters.
     * @return
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    @Override
    public String toString() {
        return join(mBaseUrl, getQueryString());
    }

    /**
     * Joins the URL To the Query String
     * @param url
     * @param queryString
     * @return
     */
    private String join(final String url, final String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return url;
        }
        if (url == null || url.isEmpty()) {
            return queryString;
        }
        char append;
        if (url.contains("?")) {
            append = '&';
        } else {
            append = '?';
        }
        return url + append + queryString;
    }

    /**
     * Generates a Query string that can be appended to the URL.
     * @return query string
     */
    private String getQueryString() {
        Map<String, String> params;
        if (mGenerateEncodedUrl) {
            params = getEncodedParams();
        } else {
            params = getParams();
        }

        List<Pair<String, String>> pairedParams = getPairedParams(params);
        pairedParams = removeEmptyParams(pairedParams);

        if (mSortUrlParams) {
            pairedParams = sortList(pairedParams);
        }

        final StringBuilder stringBuilder = new StringBuilder();
        String sep = "";
        for (final Pair<String, String> pair : pairedParams) {
            stringBuilder.append(sep).append(pair.first).append("=").append(pair.second);
            sep = "&";
        }
        return stringBuilder.toString();
    }

    /**
     * Returns sortable list of the params (So we can optionally sort :P)
     * @param params
     * @return
     */
    private List<Pair<String, String>> getPairedParams(final Map<String, String> params) {
        final List<Pair<String, String>> list = new ArrayList<>();
        for (final String key : params.keySet()) {
            list.add(new Pair<>(key, params.get(key)));
        }
        return list;
    }

    /**
     * Empty parameters can break some requests like the login request.
     */
    private List<Pair<String, String>> removeEmptyParams(final List<Pair<String, String>> src) {
        final List<Pair<String, String>> dest = new ArrayList<>(src);
        Pair<String, String> next;
        for (final Iterator<Pair<String, String>> iterator = dest.iterator(); iterator.hasNext(); ) {
            next = iterator.next();
            if (next.first == null || next.second == null || next.first.isEmpty() || next.second.isEmpty()) {
                iterator.remove();
            }
        }
        return dest;
    }

    /**
     * Sorts the list of params by key
     * @param params
     * @return
     */
    private List<Pair<String, String>> sortList(final List<Pair<String, String>> params) {
        // Use a new list in case params is immutable.
        final List<Pair<String, String>> sorted = new ArrayList<>(params);
        Collections.sort(sorted, new Comparator<Pair<String, String>>() {

            @Override
            public int compare(final Pair<String, String> a, final Pair<String, String> b) {
                return a.first.compareTo(b.first);
            }
        });
        return sorted;
    }

}
