package com.unique.agent.network;

import com.unique.agent.network.cache.IDataCache;

import java.util.concurrent.Future;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public interface INetworkManager {
    /**
     * Returns access to some common configuration values for network requests.
     * @return The NetworkConfiguration object.
     */
    NetworkConfiguration getNetworkConfiguration();


    /**
     * Issue a POST request at the specified NetworkRequest location with the specified data in NetworkUploadInterface.
     * To cancel/abort a task use Future.cancel()
     *
     * @param request the NetworkRequest object containing network location information.
     * @param uploadInterface the implementation object from which data to be uploaded can be fetched in chunks,
     *                        along with information such as the total size.
     * @return Future that can be used cancel the given request, or to wait for the async task to complete.
     *         Most of the time you will add a listener on the request and get the response asynchronously, however on
     *         rare occasions you may need to wait synchronously for the task to complete.
     * @throws IllegalArgumentException If request is @{code null} or request.url is empty.
     */
    Future<NetworkResponse> post(NetworkRequest request, NetworkUploadInterface uploadInterface);

    /**
     * Issue a POST request at the specified NetworkRequest location with the specified byte array to be sent.
     * To cancel/abort a task use Future.cancel()
     *
     * @param request NetworkRequest object containing network location information.
     * @param payloadBody A payload body to send with the request.
     * @return Future that can be used cancel the given request, or to wait for the async task to complete.
     *         Most of the time you will add a listener on the request and get the response asynchronously, however on
     *         rare occasions you may need to wait synchronously for the task to complete.
     * @throws IllegalArgumentException If request is @{code null} or request.url is empty.
     */
    Future<NetworkResponse> post(NetworkRequest request, byte[] payloadBody);

    /**
     * Issue a GET request at the specified NetworkRequest location.
     * To cancel/abort a task use Future.cancel()
     *
     * @param request the NetworkRequest object containing network location information.
     * @return Future that can be used cancel the given request, or to wait for the async task to complete.
     *         Most of the time you will add a listener on the request and get the response asynchronously, however on
     *         rare occasions you may need to wait synchronously for the task to complete.
     * @throws IllegalArgumentException If request is @{code null} or request.url is empty.
     */
    Future<NetworkResponse> get(NetworkRequest request);


    /**
     * Adds listener for this network managers connectivity state events.
     */
    void addNetworkConnectivityListener(INetworkConnectivityListener listener);

    /**
     * Removes listener if it was previously added using addNetworkConnectivityListener.
     */
    void removeNetworkConnectivityListener(INetworkConnectivityListener listener);

    /**
     * Issue a PUT request at the specified NetworkRequest location with the specified data in NetworkUploadInterface.
     * To cancel/abort a task use Future.cancel()
     *
     * @param request the NetworkRequest object containing network location information.
     * @param uploadInterface the implementation object from which data to be uploaded can be fetched in chunks,
     *                        along with information such as the total size.
     * @return Future that can be used cancel the given request, or to wait for the async task to complete.
     *         Most of the time you will add a listener on the request and get the response asynchronously, however on
     *         rare occasions you may need to wait synchronously for the task to complete.
     * @throws IllegalArgumentException If request is @{code null} or request.url is empty.
     */
    Future<NetworkResponse> put(NetworkRequest request, NetworkUploadInterface uploadInterface);

    /**
     * Issue a PUT request at the specified NetworkRequest location with the specified byte array to be sent.
     * To cancel/abort a task use Future.cancel()
     *
     * @param request the NetworkRequest object containing network location information.
     * @param payloadBody A payload body to send with the request.
     * @return Future that can be used cancel the given request, or to wait for the async task to complete.
     *         Most of the time you will add a listener on the request and get the response asynchronously, however on
     *         rare occasions you may need to wait synchronously for the task to complete.
     * @throws IllegalArgumentException If request is @{code null} or request.url is empty.
     */
    Future<NetworkResponse> put(NetworkRequest request, byte[] payloadBody);

    /**
     * Issue a DELETE request at the specified NetworkRequest location.
     * To cancel/abort a task use Future.cancel()
     *
     * @param request the NetworkRequest object containing network location information.
     * @return Future that can be used cancel the given request, or to wait for the async task to complete.
     *         Most of the time you will add a listener on the request and get the response asynchronously, however on
     *         rare occasions you may need to wait synchronously for the task to complete.
     * @throws IllegalArgumentException If request is @{code null} or request.url is empty.
     */
    Future<NetworkResponse> delete(NetworkRequest request);


    /**
     * Issue a HTTP request at the specified NetworkRequest location, depending on NetworkRequest.method.
     * To cancel/abort a task use Future.cancel()
     *
     * @param request the NetworkRequest object containing network location information.
     * @return Future that can be used cancel the given request, or to wait for the async task to complete.
     *         Most of the time you will add a listener on the request and get the response asynchronously, however on
     *         rare occasions you may need to wait synchronously for the task to complete.
     * @throws IllegalArgumentException If request.method is not set, or request is null or request.url is empty.
     */
    Future<NetworkResponse> execute(ExecutableNetworkRequest request);


    /**
     * Return the status of the Network Manager.
     * @return the NetworkStatus enum object containing the current status of the Network Manager implementation.
     */
    NetworkStatus getNetworkStatus();

    /**
     * Returns the name of the current network. This should not be relied upon, but could be used for
     * things such as event reporting
     * @return Name of the current network - if network is note known, will return "Unknown"
     */
    String getNetworkName();

    /**
     * Replace current data cache.
     *
     * @param dataCache with new cache information
     * @see NetworkRequest#setCacheTimeout
     */
    void setDataCache(IDataCache dataCache);

    /**
     * @return current data cache being used, or null if no cache is being used.
     */
    IDataCache getDataCache();

    /**
     * Cleanup process, release variables, empty lists... .
     */
    void shutdown();
}
