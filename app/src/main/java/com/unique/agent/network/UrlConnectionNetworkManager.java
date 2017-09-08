package com.unique.agent.network;

/**
 * Created by praveenpokuri on 18/08/17.
 */

import android.content.Context;
import android.os.SystemClock;

import com.unique.agent.concurrent.ThreadPool;
import com.unique.agent.error.NetworkErrorInfo;
import com.unique.agent.network.cache.IDataCache;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Map;

import de.akquinet.android.androlog.Log;

/**
 * INetworkManager that uses HttpUrlConnection for network requests.
 * Uses a file/disk cache via DataCacheManager as the default data cache implementation.
 */

public class UrlConnectionNetworkManager extends AbstractNetworkManager {

    /** maximum chunk of payload body data to send at any 1 time. */
    private static final int MAXIMUM_REQUEST_PAYLOAD_BODY_SIZE = 64 * 1024;
    private static final String LINE_END = "\r\n";
    private UrlConnectionFactory mConnectionFactory = new UrlConnectionFactory();

    public UrlConnectionNetworkManager(final Context context, final ThreadPool threadPool) {
        super(context, threadPool);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If both payloadBytes and payloadInterface are not empty, it throws an
     *             IllegalArgumentException, because we don't know which payload should be sent.
     */
    @Override
    protected NetworkResponse sendHTTPRequest(final NetworkRequest request,
                                              final HTTPMethod method,
                                              final byte[] bodyBytes,
                                              final NetworkUploadInterface uploadInterface) throws Exception {
        final long start = SystemClock.elapsedRealtime();

        verifyPayloadParamOrThrow(method, bodyBytes, uploadInterface);
        final String url = getURLToConnect(request, method, bodyBytes, uploadInterface);
        android.util.Log.i("PRAV"," url "+url);
        final HttpURLConnection conn = mConnectionFactory.openConnection(url);

        try {
            setConnectionTimeouts(request, conn);

            //set the method
            conn.setRequestMethod(method.name());

            String boundary = null;
            if (uploadInterface != null) {
                boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
            }
            final boolean hasBodyBytes = (bodyBytes != null && bodyBytes.length != 0);
            setRequestProperties(conn, request, method, hasBodyBytes, boundary);

            //prepare url http con to receive input
            conn.setDoInput(true);

            /**
             * if we don't disable the cache it will add "if-modified-since" header with a timestamp,
             * it can get a response "304 NOT MODIFIED" and this it is not handled in this manager.
             * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
             */
            if (!request.isETagSupportEnabled()
                    || (request.getCacheTimeout() == null || request.getCacheTimeout() == 0)) {
                conn.setUseCaches(false);
            }

            if (method.hasMessageBody()) {

                conn.setDoOutput(true);

                final DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                //send any bytes[] or parameters if present, in that order.
                if (bodyBytes != null) {
                    dataOutputStream.write(bodyBytes);
                } else if (uploadInterface != null) {
                    dataOutputStream.writeBytes("--" + boundary + LINE_END);

                    byte[] toSend = uploadInterface.getDataChunk(MAXIMUM_REQUEST_PAYLOAD_BODY_SIZE);
                    while (toSend != null) {
                        dataOutputStream.write(toSend, 0, toSend.length);
                        toSend = uploadInterface.getDataChunk(MAXIMUM_REQUEST_PAYLOAD_BODY_SIZE);
                    }
                    dataOutputStream.writeBytes(LINE_END);
                    dataOutputStream.writeBytes("--" + boundary + "--" + LINE_END);
                } else if (request.getParameters() != null) {
                    Log.d("Sending Message Body Parameters: " + request.getParameters());
                    dataOutputStream.writeBytes(new UrlBuilder().addParams(request.getParameters()).toString());
                }
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            // Get the input response, even responses with HTTP error status codes can have a response.
            final byte[] bytes = readStream(conn);
            final int responseCode = conn.getResponseCode();

            // Process whether response is 304 (server data has not been modified)
            if (isResponseNotModified(responseCode)) {
                final IDataCache cache = getDataCache();
                if (cache != null) {
                    // Get cached response and return it
                    return getCacheResponse(cache, request);
                }
            }

            NetworkErrorInfo networkErrorInfo = null;
            if (!isSuccessResponse(responseCode)) {
                // Create Error Info in case of response other then 200 OK and attach it to the Response
                networkErrorInfo = createErrorInfo(request, responseCode, conn.getResponseMessage(), bytes);
            }
            android.util.Log.i("PRAV","responseCode "+responseCode);
            return new NetworkResponse.Builder()
                    .setHeaders(conn.getHeaderFields())
                    .setResponseBytes(bytes)
                    .setRequest(request)
                    .setResponseCode(responseCode)
                    .setNetworkErrorInfo(networkErrorInfo)
                    .setDuration((int) (SystemClock.elapsedRealtime() - start))
                    .build();
        } finally {
            conn.disconnect();
        }
    }

    /**
     * If both bodyBytes and uploadInterface are not empty, it throws a {@link IllegalArgumentException}, because we
     * don't know which one should be sent.
     */
    private void verifyPayloadParamOrThrow(final HTTPMethod method, final byte[] bodyBytes,
                                           final NetworkUploadInterface uploadInterface) {
        if (method.hasMessageBody()) {
            // if given both non-null bodyBytes and uploadInterface, it throws an IllegalArgumentException
            if (bodyBytes != null && uploadInterface != null) {
                throw new IllegalArgumentException("Cannot accept a request with bytes[] AND an upload file");
            }
        }
    }

    /**
     * Creates URL to perform HTTP network connection.
     *
     * @param request          Instance of the {@link NetworkRequest}.
     * @param method           HTTP network request method.
     * @param payloadBytes     Bytes of the payload.
     * @param payloadInterface Reference to the {@link NetworkUploadInterface} implementation.
     *
     * @return URL string to connect.
     */
    private String getURLToConnect(final NetworkRequest request, final HTTPMethod method, final byte[] payloadBytes,
                                   final NetworkUploadInterface payloadInterface) {

        String url = toUTF8EncodedString(request.getUrl());
        if (!method.hasMessageBody() || (payloadBytes != null || payloadInterface != null)) {
            url = new UrlBuilder(url).addParams(request.getParameters()).toString();
        }
        Log.d("PRAV","Opening URL (length: %d) "+url.length()+ url);
        return url;
    }

    /**
     * Given a string to be used for web communications, get the bytes using the most common ISO covering most
     * character sets ISO 8859-1 (8859-2 to 14 cover the remaining), and encode those characters to the WWW standard
     * UTF-8.
     *
     * @param str the string to encode into UTF-8.
     * @return the newly encoded UTF-8 string.
     */
    private String toUTF8EncodedString(final String str) {
        byte[] bytes = str.getBytes(Charset.forName("ISO_8859_1"));
        return new String(bytes, Charset.forName("UTF-8"));
    }

    /**
     * Sets connection timeouts such as connection timeout or read timeout according to the given request.
     */
    private void setConnectionTimeouts(final NetworkRequest request, final HttpURLConnection conn) {
        // set the connect timeout
        long requestConnectionTimeout = request.getConnectionTimeout();
        long requestTimeout = request.getRequestTimeout();
        if (requestConnectionTimeout < 0) {
            requestConnectionTimeout = getNetworkConfiguration().getDefaultNetworkConnectionTimeoutMS();
        }
        if (requestTimeout < 0) {
            requestTimeout = getNetworkConfiguration().getDefaultNetworkRequestTimeoutMS();
        }
        conn.setConnectTimeout((int) requestConnectionTimeout);
        conn.setReadTimeout((int) requestTimeout);
    }

    private void setRequestProperties(final HttpURLConnection conn,
                                      final NetworkRequest request,
                                      final HTTPMethod method,
                                      final boolean hasByteData,
                                      final String uploadBoundary) {
        for (final Map.Entry<String,String> header : request.getRawHeaders().entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }

        if (method.hasMessageBody()) {
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", getContentType(request, hasByteData, uploadBoundary));

            if (uploadBoundary != null) {
                conn.setRequestProperty("Content-Disposition", "form-data; name='file'; filename='file'");
                conn.setRequestProperty("Content-Transfer-Encoding", "binary");
            }
        }
    }

    private String getContentType(final NetworkRequest request, final boolean hasByteData,
                                  final String uploadBoundry) {
        String contentType = request.getRawHeaders().get("Content-Type");
        if (contentType == null) {
            if (uploadBoundry != null) {
                contentType = "multipart/form-data";
            } else if(request.getParameters().size() != 0 && hasByteData) {
                contentType = "application/octet-stream";
            } else {
                contentType = "application/x-www-form-urlencoded";
            }
        }
        if (uploadBoundry != null) {
            contentType += "; boundary=" + uploadBoundry;
        }
        return contentType;
    }

    /**
     * Given an input stream, exhaust the total bytes from it.
     *
     * @param conn Connection to read the input stream of.
     * @return the byte array containing the data of the input stream.
     */
    private byte[] readStream(final HttpURLConnection conn) throws IOException {
        final BufferedInputStream in;
        if (isSuccessResponse(conn.getResponseCode())) {
            in = new BufferedInputStream(conn.getInputStream());
        } else {
            final InputStream errorStream = conn.getErrorStream();
            if (errorStream == null) {
                return new byte[0];
            }
            in = new BufferedInputStream(errorStream);
        }
        try {
            return new HttpEntityConverter().inputStreamToBytes(in);
        } finally {
            in.close();
        }
    }

    /**
     * Whether response is OK
     * .
     * @param responseCode HTTP response code.
     * @return {@code true} if response is OK, {@code false} otherwise.
     */
    private boolean isSuccessResponse(final int responseCode) {
        return (responseCode >= 200 && responseCode <= 299);
    }

    /**
     * Whether response code is 304 - server data has not been modified.
     *
     * @param responseCode HTTP response code.
     * @return {@code true} if server data has not been modified, {@code false} otherwise.
     */
    private boolean isResponseNotModified(final int responseCode) {
        return responseCode == 304;
    }

}
