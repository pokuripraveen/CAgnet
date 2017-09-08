package com.unique.agent.network.cache;

import java.io.IOException;

/**
 * Created by praveenpokuri on 18/08/17.
 */

class MediaUnavailableException extends IOException {
    private static final long serialVersionUID = 1L;

    public MediaUnavailableException() {
        super();
    }

    public MediaUnavailableException(String msg) {
        super(msg);
    }
}
