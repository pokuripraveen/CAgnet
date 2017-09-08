package com.unique.agent.network;

/**
 * Created by praveenpokuri on 18/08/17.
 */

/**
 * Something that has a priority associated with it.
 *
 * @since 2014-03-24
 */
public interface Prioritized {

    /**
     * @return Higher values mean higher priority and should run before lower values.
     */
    int getPriority();

}
