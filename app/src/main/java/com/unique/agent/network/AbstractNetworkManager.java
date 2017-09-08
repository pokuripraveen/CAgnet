package com.unique.agent.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.unique.agent.base.CounterAgentApplication;
import com.unique.agent.concurrent.ThreadPool;
import com.unique.agent.error.NetworkErrorCode;
import com.unique.agent.error.NetworkErrorInfo;
import com.unique.agent.interfaces.ActionExecutor;
import com.unique.agent.interfaces.CancellableActionExecutor;
import com.unique.agent.network.cache.IDataCache;
import com.unique.agent.network.cache.IDataCacheContainer;
import com.unique.agent.utils.AndroidApiLevelUtils;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public abstract class AbstractNetworkManager implements INetworkManager {

    private final Context mContext;
    private final ThreadPool mThreadPool;
    private final NetworkConnectivityListener mConnectivityMonitor;
    private final Map<String, List<WaitingSerialRequest>> mSerialRequests = new HashMap<>();
    private IDataCache mDataCache;
    private NetworkConfiguration mNetworkConfiguration;

    public AbstractNetworkManager(Context context, ThreadPool threadPool) {
        Validate.notNull(context, "context");
        Validate.notNull(threadPool, "threadPool");

        this.mContext = context;
        this.mThreadPool = threadPool;
        this.mConnectivityMonitor = new NetworkConnectivityListener(context);
        this.mNetworkConfiguration = new NetworkConfiguration();
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * <p>
     * DEV NOTE: Be careful on when to use this API. Wifi connectivity can be turned on even if device is set in
     * airplane mode.
     * </p>
     *
     * @return {@code true} if airplane-mode was enabled.
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    protected boolean isAirplaneModeOn() {
        if (!AndroidApiLevelUtils.isApi17()) {
            return Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    @Override
    public NetworkConfiguration getNetworkConfiguration() {
        return mNetworkConfiguration;
    }

    /** {@inheritDoc} */
    @Override
    public NetworkStatus getNetworkStatus() {
        return mConnectivityMonitor.getNetworkStatus();
    }

    /** {@inheritDoc} */
    @Override
    public String getNetworkName() {
        return mConnectivityMonitor.getNetworkName();
    }



    @Override
    public Future<NetworkResponse> post(final NetworkRequest request, final NetworkUploadInterface uploadInterface) {
        return submitTaskOrWaitForSerial(request, HTTPMethod.POST, null, uploadInterface);
    }

    /** {@inheritDoc} */
    @Override
    public Future<NetworkResponse> post(final NetworkRequest request, final byte[] payloadBody) {
        return submitTaskOrWaitForSerial(request, HTTPMethod.POST, payloadBody, null);
    }

    /** {@inheritDoc} */
    @Override
    public Future<NetworkResponse> get(final NetworkRequest request) {
        return submitTaskOrWaitForSerial(request, HTTPMethod.GET, null, null);
    }

    @Override
    public Future<NetworkResponse> put(final NetworkRequest request, final NetworkUploadInterface uploadInterface) {
        return submitTaskOrWaitForSerial(request, HTTPMethod.PUT, null, uploadInterface);
    }

    @Override
    public Future<NetworkResponse> put(final NetworkRequest request, final byte[] payloadBody) {
        return submitTaskOrWaitForSerial(request, HTTPMethod.PUT, payloadBody, null);
    }

    @Override
    public Future<NetworkResponse> delete(final NetworkRequest request) {
        return submitTaskOrWaitForSerial(request, HTTPMethod.DELETE, null, null);
    }

    /** {@inheritDoc} */
    @Override
    public Future<NetworkResponse> execute(ExecutableNetworkRequest request) {
        Validate.notNull(request, "request");
        Validate.notNull(request.getMethod(), "request.method");
        return submitTaskOrWaitForSerial(request, request.getMethod(), request.getPostData(), request.getUploadInterface());
    }

    /**
     * Creates {@link NetworkErrorInfo} object from the HTTP response.
     * Note that HTTP response code must not be equal to 200 OK.
     *
     * @param request       Origin Network Request
     * @param statusCode    Response Code
     * @param statusMessage Response message
     * @param response      Network Response
     *
     * @return {@link NetworkErrorInfo} associated with the provided Network Response.
     */
    protected NetworkErrorInfo createErrorInfo(final NetworkRequest request, final int statusCode,
                                               final String statusMessage, final byte[] response) {
        String convertedResponse;
        try {
            convertedResponse = new String(response, CharEncoding.UTF_8);
        } catch (final Exception e) {
            convertedResponse = null;
        }
        final NetworkErrorInfo.Builder builder;
        if (statusCode != 0) {
            builder = new NetworkErrorInfo.Builder(NetworkErrorCode.HTTP_ERROR)
                    .setErrorDescription("Error with " + request.getUrl() + " and status code " + statusCode)
                    .setHttpStatusCode(statusCode)
                    .setHttpResponseMessage(statusMessage);
        } else {
            builder = new NetworkErrorInfo.Builder(NetworkErrorCode.UNKNOWN_ERROR)
                    .setErrorDescription("Unexpected networking error without status code")
                    .setHttpStatusCode(NetworkErrorInfo.INVALID_HTTP_STATUS_CODE)
                    .setHttpResponseMessage(statusMessage);
        }
        builder.setNetworkRequest(request)
                .setNetworkResponseString(convertedResponse)
                .setResponse(response);
        return builder.build();
    }

    /**
     * Returns NetworkResponse with retrieved data from cache.
     *
     * @param request the NetworkRequest to execute.
     * @return Response from IDataCache. Null if no cache, cache data or cache is expired.
     */
    protected static NetworkResponse getCacheResponse(final IDataCache dataCache,
                                                      final NetworkRequest request) {
        if (dataCache == null) {
            Log.e("getCacheResponse aborted, dataCache is null");
            return null;
        }
        IDataCacheContainer cachedData = null;
        try {
            cachedData = dataCache.retrieveCachedData(request.toCacheKey());
        } catch(Exception ex) {
            final NetworkErrorInfo error = new NetworkErrorInfo.Builder(NetworkErrorCode.UNKNOWN_ERROR)
                    .setErrorDescription("Error retrieving cached data")
                    .setException(ex)
                    .setNetworkRequest(request)
                    .build();
            if (request.getResponseListener() != null) {
                if (request.getHandler() != null) {
                    request.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            request.getResponseListener().onError(request, error);
                        }
                    });
                } else {
                    request.getResponseListener().onError(request, error);
                }
            }
        }
        if (cachedData == null) {
            return null;
        }
        final byte[] data = cachedData.getData();
        if (data == null) {
            return null;
        }
        return new NetworkResponse.Builder()
                .setResponseBytes(data)
                .setHeaders(cachedData.getHeaders())
                .setFromCache(true)
                .setRequest(request)
                .build();

    }
    private Future<NetworkResponse> submitTaskOrWaitForSerial(final NetworkRequest request,
                                                              final HTTPMethod method,
                                                              final byte[] bodyBytes,
                                                              final NetworkUploadInterface uploadInterface) {
        Validate.notNull(request, "request");
        Validate.notEmpty(request.getUrl(), "request.url");

        WaitingSerialRequest serialRequest = createSerialRun(request, method, bodyBytes, uploadInterface);
        if (serialRequest != null) {
            return serialRequest;
        }

        return submitSendHttpTask(request, method, bodyBytes, uploadInterface);
    }

    private Future<NetworkResponse> submitSendHttpTask(final NetworkRequest request,
                                                       final HTTPMethod method,
                                                       final byte[] bodyBytes,
                                                       final NetworkUploadInterface uploadInterface) {

        final PrioritizedCallable<NetworkResponse> task = new PrioritizedNetworkCallable(
                request,
                new Callable<NetworkResponse>() {

                    @Override
                    public NetworkResponse call() throws Exception {
                        android.util.Log.i("PRAV", "HTTP method"+ method + " Url" + request.getUrl()+ " Request "+request);
                        return AbstractNetworkManager.this.sendHTTPRequest(request, method, bodyBytes, uploadInterface);
                    }
                },
                mDataCache,
                this
        );

        return mThreadPool.submit(task);
    }

    private WaitingSerialRequest createSerialRun(final NetworkRequest request,
                                                 final HTTPMethod method,
                                                 final byte[] bodyBytes,
                                                 final NetworkUploadInterface uploadInterface) {

        final String serialRunId = request.getSerialRunId();
        if (serialRunId == null) {
            return null;
        }

        request.setResponseListener(new NetworkResponseListenerModel(request.getResponseListener()) {
            @Override
            public void onFinished(NetworkRequest request, NetworkResponse response) {
                super.onFinished(request, response);
                runNextSerial(serialRunId, request, method, bodyBytes, uploadInterface);
            }

            @Override
            public void onError(NetworkRequest request, NetworkErrorInfo error) {
                try {
                    super.onError(request, error);
                } finally {
                    runNextSerial(serialRunId, request, method, bodyBytes, uploadInterface);
                }
            }

            @Override
            public void onAborted(NetworkRequest request) {
                super.onAborted(request);
                runNextSerial(serialRunId, request, method, bodyBytes, uploadInterface);
            }
        });

        synchronized (mSerialRequests) {
            List<WaitingSerialRequest> requests = mSerialRequests.get(serialRunId);
            if (requests == null) {
                requests = new LinkedList<>();
                mSerialRequests.put(request.getSerialRunId(), requests);
                return null;
            } else {
                Log.d("PRAV", "Already have "+ (requests.size()+1)+ " running with same serialRunId " +serialRunId+
                                "  waiting for other request to finish before submitting "+ request);
                WaitingSerialRequest waitingRequest = new WaitingSerialRequest(request);
                requests.add(waitingRequest);
                return waitingRequest;
            }
        }
    }

    private void runNextSerial(final String serialRunId,
                               final NetworkRequest request,
                               final HTTPMethod method,
                               final byte[] bodyBytes,
                               final NetworkUploadInterface uploadInterface) {

        synchronized (mSerialRequests) {
            List<WaitingSerialRequest> requests = mSerialRequests.get(serialRunId);
            if (request != null && !requests.isEmpty()) {
                WaitingSerialRequest waitingRequest = requests.remove(0);
                NetworkRequest nextRequest = waitingRequest.getRequest();
                Log.d("PRAV","Request with serialRunId %s finished, "+
                        serialRunId+"   now submitting next serial request  "+nextRequest);
                Future<NetworkResponse> future = submitSendHttpTask(nextRequest, method, bodyBytes, uploadInterface);
                waitingRequest.setSubmittedFuture(future);
            } else {
                mSerialRequests.remove(serialRunId);
            }
        }
    }

    private static final class PrioritizedNetworkCallable implements PrioritizedCallable<NetworkResponse>, CancelledListener {

        private final NetworkRequest mRequest;
        private final Callable<NetworkResponse> mTask;
        private int retries = 0;
        private final WeakReference<AbstractNetworkManager> mReference;

        private PrioritizedNetworkCallable(final NetworkRequest request,
                                           final Callable<NetworkResponse> task,
                                           final IDataCache cache,
                                           final AbstractNetworkManager reference) {
            super();

            mReference = new WeakReference<>(reference);
            mRequest = request;

            if (mRequest.isETagSupportEnabled()) {
                mTask = task;

                final NetworkResponse response = getCacheResponse(cache, mRequest);
                if (response == null) {
                    return;
                }

                // When ETag is enabled, prevent passing null values to the next Request
                final String eTag = response.getETag();
                final String lastModified = response.getLastModified();
                if (StringUtils.isEmpty(eTag) || StringUtils.isEmpty(lastModified)) {
                    return;
                }

                mRequest.addRawHeader(NetworkRequest.IF_NONE_MATCH_HEADER_KEY, eTag);
                mRequest.addRawHeader(NetworkRequest.IF_MODIFIED_SINCE_HEADER_KEY, lastModified);
                return;
            }

            if (mRequest.getCacheTimeout() != null) {
                mTask = new CheckCacheTask(mRequest, task, cache);
                return;
            }

            mTask = task;
        }

        @Override
        public NetworkResponse call() throws Exception {
            NetworkResponse response = null;

            final AbstractNetworkManager reference = mReference.get();
            if (reference == null) {
                Log.w("Reference to the enclosed object is null");
                return response;
            }

            try {
                response = mTask.call();
            } catch (Exception ex) {
                int numberOfRetryAttemptsPermitted = mRequest.getRetryAttempts();
                if(numberOfRetryAttemptsPermitted < 0) {
                    numberOfRetryAttemptsPermitted = reference.getNetworkConfiguration().getDefaultNetworkFailureRetries();
                }

                if (retries < numberOfRetryAttemptsPermitted) {
                    retries++;
                    Log.d("Sleeping for a second before we retry "+mRequest+ " again because of error"+ "  Exception"+ ex);
                    reference.mThreadPool.sleep(1000);
                    return call();
                } else {
                    reference.notifyError(mRequest, new NetworkErrorInfo.Builder(NetworkErrorCode.UNKNOWN_ERROR)
                            .setErrorDescription("Networking call has failed " + retries + " times due to exception")
                            .setNetworkRequest(mRequest)
                            .setNetworkResponse(response)
                            .setException(ex)
                            .build());
                    throw new RuntimeException("Error retrieving URL " + mRequest.getUrl() + ":" + mRequest.getParameters(), ex);
                }
            }

            if (response == null) {
                final NetworkConfiguration config = reference.getNetworkConfiguration();
                if (!config.getAllowNetworkRequests()) {
                    reference.notifyError(mRequest, new NetworkErrorInfo.Builder(NetworkErrorCode.INTERNET_UNAVAILABLE)
                            .setErrorDescription("NetworkManager set to not allow networking calls")
                            .build());
                }
            } else {
                final NetworkErrorInfo networkErrorInfo = response.getNetworkErrorInfo();
                if (networkErrorInfo != null) {
                    reference.notifyError(mRequest, networkErrorInfo);
                } else {
                    reference.notifyFinished(mRequest, response);
                }
            }
            return response;
        }

        @Override
        public int getPriority() {
            return mRequest.getPriority();
        }

        @Override
        public void onCancelled() {
            final AbstractNetworkManager reference = mReference.get();
            if (reference == null) {
                return;
            }
            reference.notifyAborted(mRequest);
        }
    }

    private void notifyFinished(final NetworkRequest request, final NetworkResponse response) {
        if (response == null) {
            // http status code error.
            return;
        }
        Log.d("PRAV", "HTTP finished url "+ request.getUrl()+" response "+ response);
        if (request.getResponseListener() != null) {
            if (request.getHandler() != null) {
                boolean posted = request.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        doNotifyFinishedThenCacheResponse(request, response);
                    }
                });
                if (!posted) {
                    Log.d("PRAV", "Posting %"+response+ " to notify thread "+request+" failed, looper must have exited");
                }
            } else {
                doNotifyFinishedThenCacheResponse(request, response);
            }
        } else{
            cacheValidResponse(request, response);
        }
    }

    private void doNotifyFinishedThenCacheResponse(NetworkRequest request, NetworkResponse response) {
        try {
            request.getResponseListener().onFinished(request, response);
        } catch (Throwable ex) {
            request.getResponseListener().onError(request,
                    new NetworkErrorInfo.Builder(NetworkErrorCode.UNKNOWN_ERROR)
                            .setErrorDescription("UserLabor NetworkResponseListener#onFinished threw exception")
                            .setException(ex)
                            .setNetworkRequest(request)
                            .setNetworkResponse(response)
                            .build());
            return;
        }
        cacheValidResponse(request, response);
    }

    /**
     * Caching HTTP response. First check for the {@link IDataCache} implementation, then
     * checking for the "ETag" in the response headers, if there is such, cache response with zero
     * time out and response headers (including "Last-Modified" amd "Etag").
     * Only if there is no "ETag" usage has been specified, check for the response time oyt value
     * and base caching on this one.
     *
     * @param request  HTTP request.
     * @param response HTTP response.
     */
    private void cacheValidResponse(final NetworkRequest request, final NetworkResponse response) {
        // Obtaining data cache impl
        final IDataCache dataCache = getDataCache();
        if (dataCache == null) {
            return;
        }

        // Getting variables
        final boolean isUseETag = request.isETagSupportEnabled();
        final String cacheKey = request.toCacheKey();
        final byte[] responseBytes = response.getResponseBytes();
        Integer timeOut;
        Map<String, List<String>> headers = null;

        // Check for the ETag usage and extract headers in that case.
        if (isUseETag) {
            headers = response.getHeaders();
            // Time out never ends in case of Etag
            timeOut = 0;
        } else {
            timeOut = request.getCacheTimeout();
        }

        if (timeOut == null) {
            // Means no ETag and no time out. Skipp.
            return;
        }

        // Write to cache if there is no handler
        if (request.getHandler() == null) {
            writeToCache(dataCache, cacheKey, headers, responseBytes, timeOut);
            return;
        }

        /*// Increase robustness
        final Core core = aCore();
        if (core == null) {
            return;
        }
        final ThreadPool pool = core.getNoWaitThreadPool();
        if (pool == null) {
            return;
        } */
        CancellableActionExecutor executor = ((CounterAgentApplication)mContext.getApplicationContext()).getComponent().cancelableActionExecutor();
        // Finally, write to cache if there is a handler
        final Integer finalTimeOut = timeOut;
        final Map<String, List<String>> finalHeaders = headers;
        executor.post(new Runnable() {
            @Override
            public void run() {
                writeToCache(dataCache, cacheKey, finalHeaders, responseBytes, finalTimeOut);
            }
        });
        /*executor.submit(new Runnable() {
            @Override
            public void run() {
                writeToCache(dataCache, cacheKey, finalHeaders, responseBytes, finalTimeOut);
            }
        });*/
    }

    /**
     * Writes response data and headers into cache implementation.
     *
     * @param dataCache     Implementation of the {@link IDataCache} interface.
     * @param cacheKey      Cache key, use to associate HTTP request with HTTP response.
     * @param headers       HTTP response headers, can be null in case of no "ETag" usage.
     * @param responseBytes HTTP response data (as bytes array).
     * @param timeOut       HTTP response time out value, in milliseconds.
     *
     * @return {@code true} in case of successful caching, {@code false} otherwise.
     */
    private boolean writeToCache(final IDataCache dataCache, final String cacheKey,
                                 final Map<String, List<String>> headers, final byte[] responseBytes,
                                 final Integer timeOut) {
        // TODO : For the solid robustness implement values check here
        // TODO : Reconsider catching Throwable
        try {
            dataCache.writeToCache(cacheKey, headers, responseBytes, timeOut);
            return true;
        } catch (final Exception e) {
            Log.e("Can not write to cache:" + e.getMessage());
            return false;
        }
    }


    protected void notifyError(final NetworkRequest request, final NetworkErrorInfo error) {
        Log.d("PRAV", "HTTP error "+ error +" for  "+request.getUrl());
        if (request.getResponseListener() != null) {
            if (request.getHandler() != null) {
                request.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        request.getResponseListener().onError(request, error);
                    }
                });
            } else {
                request.getResponseListener().onError(request, error);
            }
        }
    }

    private void notifyAborted(final NetworkRequest request) {
        Log.d("PRAV", "HTTP aborted "+ request.getUrl());
        if (request.getResponseListener() != null) {
            if (request.getHandler() != null) {
                request.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        request.getResponseListener().onAborted(request);
                    }
                });
            } else {
                request.getResponseListener().onAborted(request);
            }
        }
    }


    /**
     *  Checks the cache before making the HTTP call and uses cache response if requested and available.
     */
    private static class CheckCacheTask implements Callable<NetworkResponse> {

        private final NetworkRequest request;
        private final IDataCache dataCache;
        private final Callable<NetworkResponse> httpTask;

        private CheckCacheTask(NetworkRequest request, Callable<NetworkResponse> httpTask, IDataCache dataCache) {
            this.request = request;
            this.dataCache = dataCache;
            this.httpTask = httpTask;
        }

        @Override
        public NetworkResponse call() throws Exception {
            NetworkResponse response = getCacheResponse(dataCache, request);
            if (response != null) {
                return response;
            }
            response = httpTask.call();
            return response;
        }
    }


    @Override
    public void addNetworkConnectivityListener(INetworkConnectivityListener listener) {
        mConnectivityMonitor.addNetworkConnectivityListener(listener);
    }


    @Override
    public void removeNetworkConnectivityListener(INetworkConnectivityListener listener) {
        mConnectivityMonitor.removeNetworkConnectivityListener(listener);
    }


    @Override
    public void setDataCache(IDataCache iDataCache){
        mDataCache = iDataCache;
    }

    @Override
    public IDataCache getDataCache(){
        return mDataCache;
    }


    /** No-op */
    @Override
    public void shutdown() {

    }

    /**
     * Issue a HTTP request.
     *
     * @param request NetworkRequest to execute.
     * @param method HTTP method to use.
     * @param bodyBytes A byte array to send in payload body of the request, or {@code null} if there is nothing to
     *            send.
     * @param uploadInterface NetworkUploadInterface object to send in message body of the request, or {@code null} if
     *            there is nothing to send.
     * @return {@link NetworkResponse} A completed response form the HTTP Server that contains information about it,
     *         such that response status, response data, etc... If the response is not 200 OK, then
     *         {@link NetworkErrorInfo} created and attached to the {@link NetworkResponse#getNetworkErrorInfo()}
     * @throws Exception If there was a resource access error such as a SocketTimeoutException.
     *             request.responseListener.onError() should not be called in this case by sendHTTPRequest.
     */
    protected abstract NetworkResponse sendHTTPRequest(NetworkRequest request,
                                                       HTTPMethod method,
                                                       byte[] bodyBytes,
                                                       NetworkUploadInterface uploadInterface) throws Exception;

}
