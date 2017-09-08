package com.unique.agent.error;

import com.unique.agent.error.ErrorInfo.GenericErrorBuilder.ErrorCode;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public enum NetworkErrorCode implements ErrorCode {

    UNKNOWN_ERROR(1000),
    TIMEOUT_ERROR(1001),
    HTTP_ERROR(1002),
    INTERNET_UNAVAILABLE(10003);

    public static final String AREA_NETWORK = "Network";
    private final int mErrorCode;

    NetworkErrorCode(int errorCode){
        mErrorCode = errorCode;
    }

    @Override
    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public String getArea() {
        return AREA_NETWORK;
    }
}
