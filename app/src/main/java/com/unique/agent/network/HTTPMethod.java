package com.unique.agent.network;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public enum HTTPMethod {
    /** Idempotent. Safe */
    GET,
    /** Not idempotent. */
    POST,
    /** Idempotent. Not Safe */
    PUT,
    /** Idempotent. Not Safe */
    DELETE;

    /**
     * @return <code>True</code>, if the request method does not include defined semantics for an entity-body.
     *         <code>False</code>, otherwise.
     */
    public boolean hasMessageBody() {
        switch (this) {
            case POST:
            case PUT:
                return true;
            default:
                return false;
        }
    }
}
