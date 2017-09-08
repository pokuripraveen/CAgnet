package com.unique.agent.network.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public interface IDataCacheContainer extends Serializable {
    /**
     * @return Data that has been cached (as bytes array).
     */
    byte[] getData();

    /**
     * @return HTTP headers that has been cached.
     */
    Map<String, List<String>> getHeaders();
}
