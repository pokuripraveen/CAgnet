package com.unique.agent.network;

import com.unique.agent.concurrent.Postable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by praveenpokuri on 17/08/17.
 */

public class NetworkRequest {

    /**
     * Header key for "Content-Encoding"
     *
     * The Content-Encoding entity header is used to compress the media-type.
     * When present, its value indicates which encodings were applied to the entity-body.
     * It lets the client know, how to decode in order to obtain the media-type referenced by the Content-Type header
     *
     * Content-Encoding: gzip (A format using the Lempel-Ziv coding (LZ77), with a 32-bit CRC. This is originally the format of the UNIX gzip program, content-encoding should recognize x-gzip as an alias)
     * Content-Encoding: compress (A format using the Lempel-Ziv-Welch (LZW) algorithm. The value name was taken from the UNIX compress program)
     * Content-Encoding: deflate (Using the zlib structure (defined in RFC 1950), with the deflate compression algorithm (defined in RFC 1951))
     * Content-Encoding: identity (Indicates the identity function (i.e. no compression, nor modification). This token, except if explicitly specified, is always deemed acceptable.)
     * Content-Encoding: br (A format using the Brotli algorithm.)

     * // Multiple, in the order in which they were applied
     * Content-Encoding: gzip, identity
     * Content-Encoding: deflate, gzip
     *
     * On the client side, you can advertise a list of compression schemes that will be sent along in an HTTP request.
     * The Accept-Encoding header is used for negotiating content encoding
     * Ex: Accept-Encoding: gzip, deflate
     */
    public static final String HEADER_KEY_CONTENT_ENCODING = "Content-Encoding";

    /**
     * Header key for "Content-Length"
     * The Content-Length entity header is indicating the size of the entity-body, in decimal number of octets, sent to the recipient
     */
    public static final String CONTENT_LENGTH_HEADER_KEY = "Content-Length";

    /**
     * Header Key for "Content-Type"
     * The Content-Type entity header is used to indicate the media type of the resource.
     * In responses, a Content-Type header tells the client what the content type of the returned content actually is.
     * Browsers will do MIME sniffing in some cases and will not necessarily follow the value of this header;
     * to prevent this behavior, the header X-Content-Type-Options can be set to nosniff.
     * In requests, (such as POST or PUT), the client tells the server what type of data is actually sent
     */
    public static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";

    /**
     * Header key for "If-None-Match"
     * The If-None-Match HTTP request header makes the request conditional.For GET and HEAD methods, the server will send back the requested resource, with a 200 status,
     * only if it doesn't have an ETag matching the given ones. For other methods, the request will be processed only if the eventually existing resource's ETag doesn't match any of the values listed.
     * When the condition fails for GET and HEAD methods, then the server must return HTTP status code 304 (Not Modified).For methods that apply
     * server-side changes, the status code 412 (Precondition Failed) is used.Note that the server generating a 304 response MUST generate any of the following header fields that would have been sent
     * in a 200 (OK) response to the same request: Cache-Control, Content-Location, Date, ETag, Expires, and Vary
     *
     * The comparison with the stored ETag uses the weak comparison algorithm, meaning two files are considered identical not only if they are identical byte to byte, but if the content is equivalent.
     * For example, two pages that would differ only by the date of generation in the footer would be considered as identical.
     *
     * When used in combination with If-Modified-Since, it has precedence (if the server supports it).
     */
    public static final String IF_NONE_MATCH_HEADER_KEY = "If-None-Match";

    /**
     * Header key for "If-Modified-Since"
     * The If-Modified-Since request HTTP header makes the request conditional:
     * the server will send back the requested resource, with a 200 status, only if it has been last modified after the given date.
     * If the request has not been modified since, the response will be a 304 without any body;
     * the Last-Modified header will contain the date of last modification.
     * Unlike If-Unmodified-Since, If-Modified-Since can only be used with a GET or HEAD
     *
     * When used in combination with If-None-Match, it is ignored, unless the server doesn't support If-None-Match.
     *
     *The most common use case is to update a cached entity that has no associated ETag.
     */
    public static final String IF_MODIFIED_SINCE_HEADER_KEY = "If-Modified-Since";

    /**
     * The headers (if any) to be sent with this request. Header content is based off key/value pairs.
     */
    private Map<String, String> mRawHeader = new HashMap<>();


    /** @see #getPriority() */
    private int mPriority = NetworkPriority.DEFAULT.getPriority();

    /**
     * The URL of the request.
     */
    private String mUrl;

    private Postable mHandler;

    private String mSerialRunId;

    /* ===== TIME OUT PARMS ======= */

    /**
     * connection timeout means that a server is taking too long to reply to a data request made from the device.
     * Connection timeout show up when there isn't a reply and a server request is not fulfilled in a predetermined length of time.
     * Timeout errors can happen for a number of reasons. The server, the requesting device, the network hardware and even an Internet connection can be at fault.
     */
    private int mConnectionTimeout = -1;

    /**
     * A socket timeout is dedicated to monitor the continuous incoming data flow. If the data flow is interrupted for the specified timeout
     * the connection is regarded as stalled/broken. Of course this only works with connections where data is received all the time
     */
    private int mRequestTimeout = -1;


    private Integer mCacheTimeout = null;

    /**
     * Parameters to go with this request.
     * Get requests have these values added to the query string of the URL.
     */
    private Map<String,String> mParameters = new HashMap<>();

    private int mRetryAttempts;


    private boolean mETAGSupportEnabled = true;


    /**
     * The listener interested on whether this request was finished, aborted, errored or
     * the current upload/download progress.
     */
    private INetworkResponseListener mResponseListener;

    /**
     * Instantiate a new NetworkRequest object with the specified URL.
     *
     * @param url the URL to execute this request.
     *
     * @throws IllegalArgumentException When provided URL is invalid.
     */
    public NetworkRequest(final String url) throws IllegalArgumentException {
        final UrlBuilder urlBuilder = new UrlBuilder(url);
        this.mUrl = urlBuilder.getBaseUrl(url);
        this.mParameters = urlBuilder.geUrlParams(url);
    }

    /**
     * Whether it is necessary to process the "ETag" header field for the next HTTP request.<br>
     * Default value is {@code true}.<br>
     * @see <a href="http://tools.ietf.org/html/rfc7232#section-2.3">ETag</a>
     *
     * @return {@code true} if it is necessary to process the "ETag" header, {@code false} otherwise.
     */
    public boolean isETagSupportEnabled() {
        return mETAGSupportEnabled;
    }

    /**
     * Whether it is necessary to process the "ETag" header field for the next HTTP request.<br>
     * @see <a href="http://tools.ietf.org/html/rfc7232#section-2.3">ETag</a>
     *
     * @param value {@code true} if it is necessary to process the "ETag" header, {@code false} otherwise.
     */
    public void setETagSupportEnabled(final boolean value) {
        mETAGSupportEnabled = value;
    }

    /**
     * Get the URL of this object.
     *
     * @return the URL of this request object.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Return the headers to be sent with the request.
     *
     * @return the headers HashMap to be sent with this Network Request.
     */
    public Map<String, String> getRawHeaders() {
        return mRawHeader;
    }

    /**
     * Set the headers of this request.
     * Content-Type is set through here.
     * The default Content-Type of post bytes requests is "application/x-www-form-urlencoded".
     * The default Content-Type of post upload file requests is "multipart/form-data", a boundary is added.
     *
     * @param rawHeaders the HashMap containing headers in a key/value pair structure.
     * @throws IllegalArgumentException If rawHeaders is {@code null}.
     */
    public void setRawHeaders(Map<String,String> rawHeaders) {
        this.mRawHeader = rawHeaders;
    }

    /**
     * Adds a header to this request (e.g. Content-Type). See {@link #setRawHeaders(Map)} for more details.
     * @param key The key for the header value
     * @param value The value to put in the header.
     */
    public void addRawHeader(String key, String value) {
        this.mRawHeader.put(key, value);
    }

    /**
     * When a timeout happens INetworkResponseListener#onError() is called with NetworkErrorCode.RESOURCE_ACCESS code and
     * SocketTimeoutException as the exception.
     * Note: A value less than 0 indicates that the NetworkManager should use the default value from
     *
     * @return Milliseconds before making a connection times out.
     *
     */
    public int getConnectionTimeout() {
        return mConnectionTimeout;
    }

    /** #see getConnectionTimeout() */
    public void setConnectionTimeout(int connectionTimeout) {
        this.mConnectionTimeout = connectionTimeout;
    }

    /**
     * When a timeout happens INetworkResponseListener#onError() is called with NetworkErrorCode.RESOURCE_ACCESS code and
     * SocketTimeoutException as the exception.
     * Note: A value less than 0 indicates that the NetworkManager should use the default value from
     *
     * @return Milliseconds before the request times out after an initial connection has already been made.
     */
    public int getRequestTimeout() {
        return mRequestTimeout;
    }

    /** @see #getRequestTimeout() */
    public void setRequestTimeout(int requestTimeout) {
        this.mRequestTimeout = requestTimeout;
    }

    /**
     * Milliseconds before the cached item expires.
     * Note: Caching a POST request may not be desired, as POSTs are not usually idempotent.
     *
     * @return Milliseconds before the cached item expires and a new network requests should be made.
     *         Null turns off caching, zero means it never expires.
     *         Initialized to null by default.
     */
    public Integer getCacheTimeout() {
        return mCacheTimeout;
    }

    /** @see #getCacheTimeout() */
    public void setCacheTimeout(Integer cacheTimeout) {
        this.mCacheTimeout = cacheTimeout;
    }

    /**
     * Set the parameters to be sent with the request.
     * Should be mutable so that parameters can be added via getParameters().add().
     *
     * @param parameters a LinkedList of BasicNameValuePair objects.
     * @throws IllegalArgumentException If parameters is null.
     */
    public void setParameters(Map<String,String> parameters) {
        this.mParameters = parameters;
    }

    /**
     * Add parameters to the existing collection of parameters.
     *
     * @param parameters Collection of the parameters.
     */

    public void addParameters(Map<String,String> parameters) {
        mParameters.putAll(parameters);
    }

    /**
     * Add a parameter to the existing collection of parameters.
     *
     * @param key   Parameter name.
     * @param value Parameter value.
     */
    public void addParameter(String key, String value) {
        this.mParameters.put(key, value);
    }

    /**
     * Return the parameters as a collection of BasicNameValuePair objects.
     *
     * @return the parameters as a collection of BasicNameValuePair objects
     */
    public Map<String,String> getParameters() {
        return this.mParameters;
    }

    /**
     * Number of retry attempts to make if their was a RESOURCE_ACCESS exception. If there was a HTTP_CLIENT or
     * HTTP_SERVER error then no retry is made as these should fail the second time as well.
     * Note: A value less then 0 indicates that the NetworkManager should use the default value from
     *
     */
    public int getRetryAttempts() {
        return mRetryAttempts;
    }

    /**
     * Sets the number of retry attempts
     * @param retryAttempts Number of times to re-attempt network request on failure
     */
    public void setRetryAttempts(int retryAttempts) {
        this.mRetryAttempts = retryAttempts;
    }


    /**
     * Return the listener interested in this request's response state.
     *
     * @return the NetworkResponseListener implementation interested in getting notified on response events.
     */
    public INetworkResponseListener getResponseListener() {
        return mResponseListener;
    }

    /**
     * Set the listener interested in finding out response events emanating as a result of this request being executed.
     *
     * @param responseListener the NetworkResponseListener listener interested in getting notified
     *                          on response events.
     */
    public void setResponseListener(INetworkResponseListener responseListener) {
        this.mResponseListener = responseListener;
    }

    /**
     * @return Unique ID that is used to identify this request in the data cache.
     *         This implementation uses URL and parameters.
     */
    public String toCacheKey(){
        return new UrlBuilder(mUrl).addParams(mParameters).toString();
    }

    /**
     * @return Thread to call all listener callbacks on. If not set the listener callbacks are made on the ThreadPools
     *         thread. This avoids having to call runOnUiThread in the listeners.
     *         To run on main UI thread set to {@code new PostableHandler(new Handler(Looper.getMainLooper())}.
     */
    public Postable getHandler() {
        return mHandler;
    }

    /** @see #getHandler */
    public void setHandler(Postable handler) {
        this.mHandler = handler;
    }

    /**
     * @return ID that identifies this request such that only at most one network request with this ID will run at a
     *         time, or null if no such limitation is desired.
     *         If a response listener is provided then the request is only considered complete after the listener
     *         callbacks complete.
     *         Default is null.
     */
    public String getSerialRunId() {
        return mSerialRunId;
    }
    /**
     * @see #getSerialRunId
     */
    public void setSerialRunId(final String serialRunId) {
        this.mSerialRunId = serialRunId;
    }


    /**
     * Priority of this request. This is an int rather than an enum so that clients can use their own
     * priority values other than just HIGHEST, HIGH, DEFAULT, or LOW.
     * Default is DEFAULT (200).
     *
     * @return the NetworkPriority enum defining this request's priority.
     */
    public int getPriority() {
        return mPriority;
    }

    /**
     * Set the priority of this NetworkRequest object.
     *
     * @param mPriority am enum field from NetworkPriority enum.
     */
    public void setPriority(int mPriority) {
        this.mPriority = mPriority;
    }

}
