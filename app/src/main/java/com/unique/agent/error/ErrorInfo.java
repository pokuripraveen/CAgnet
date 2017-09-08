package com.unique.agent.error;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by praveenpokuri on 16/08/17.
 */

public class ErrorInfo implements Parcelable {


    private static final java.lang.String AREA_BUNDLE_KEY = "AREA";
    private static final String DEFAULT_ERROR_AREA = "GENERAL";
    private static final String ERROR_CODE_BUNDLE_KEY = "ERROR_CODE";
    private static final String DESCRIPTION_BUNDLE_KEY = "ERROR_DESCRIPTION";
    private static final String THROWABLE_BUNDLE_KEY = "THROWABLE";
    private static final String INTERNAL_ERROR_BUNDLE_KEY = "INTERNAL_ERROR";
    
    final String mArea;

    final int mErrorCode;

    String mErrorDescription;

    Throwable mException;

    ErrorInfo mInternalError;

    protected ErrorInfo(final int errorCode) {
        this(DEFAULT_ERROR_AREA, errorCode);
    }


    protected ErrorInfo(final String area, final int errorCode) {
        mArea = area;
        mErrorCode = errorCode;
    }

    protected ErrorInfo(final GenericErrorBuilder.ErrorCode errorCode) {
        mArea = errorCode.getArea();
        mErrorCode = errorCode.getErrorCode();
    }

    protected ErrorInfo(Parcel in) {
        Bundle bundle = in.readBundle(ErrorInfo.class.getClassLoader());

        String domain = bundle.getString(AREA_BUNDLE_KEY);
        if (domain == null) {
            domain = DEFAULT_ERROR_AREA;
        }
        mArea = domain;
        mErrorCode = bundle.getInt(ERROR_CODE_BUNDLE_KEY);
        mErrorDescription = bundle.getString(DESCRIPTION_BUNDLE_KEY);
        Serializable s = bundle.getSerializable(THROWABLE_BUNDLE_KEY);
        if (s != null) {
            mException = (Throwable) s;
        }
        Parcelable e = bundle.getParcelable(INTERNAL_ERROR_BUNDLE_KEY);
        if (e != null) {
            mInternalError = (ErrorInfo) e;
        }
    }

    public static final Creator<ErrorInfo> CREATOR = new Creator<ErrorInfo>() {
        @Override
        public ErrorInfo createFromParcel(Parcel in) {
            return new ErrorInfo(in);
        }

        @Override
        public ErrorInfo[] newArray(int size) {
            return new ErrorInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final Bundle bundle = new Bundle();

        bundle.putString(AREA_BUNDLE_KEY, mArea);
        bundle.putInt(ERROR_CODE_BUNDLE_KEY, mErrorCode);
        bundle.putString(DESCRIPTION_BUNDLE_KEY, mErrorDescription);
        bundle.putSerializable(THROWABLE_BUNDLE_KEY, mException);
        bundle.putParcelable(INTERNAL_ERROR_BUNDLE_KEY, mInternalError);
        try {
            dest.writeBundle(bundle);
        } catch (final RuntimeException e) {
           Log.i("PRAV", "Could not write bundle:\n" + Log.getStackTraceString(e));
        }
    }

    public final int getErrorCode(){
        return mErrorCode;
    }

    public final String getErrorArea(){
        return mArea;
    }

    public String getErrorDescription(){
        return mErrorDescription;
    }

    /**
     * @return An internal error object if one is provided. Some errors might be built on other errors providing
     *         additional information and context. Note that this method is NOT recommended to be used if trying to get
     *         the 'source' error. Instead it is recommended that you use get root error.
     */
    public ErrorInfo getInternalError() {
        return mInternalError;
    }

    /**
     * Returns a NetworkErrorInfo instance if there was a network error that caused the error.
     * This will return null if there is no Network Error.
     *
     * @return NetworkErrorInfo if there was a network error that triggered this error.
     */
    public NetworkErrorInfo getNetworkError() {
        ErrorInfo errorInfo = mInternalError;
        while (errorInfo != null) {
            if (NetworkErrorCode.AREA_NETWORK.equals(errorInfo.getErrorArea()) ||
                    errorInfo instanceof NetworkErrorInfo) {
                return (NetworkErrorInfo) errorInfo;
            }
            errorInfo = errorInfo.getInternalError();
        }
        return null;
    }


    public static abstract class GenericErrorBuilder<T extends ErrorInfo, GB extends GenericErrorBuilder<T,GB>> {

        protected int mErrorCode;
        protected String mArea;
        protected ErrorCode mErrorCodeObject;
        protected String mErrorDescription;
        protected Throwable mException;
        protected ErrorInfo mInternalError;

        public GenericErrorBuilder(int errorCode) {
            mErrorCode = errorCode;
            mArea = DEFAULT_ERROR_AREA;
        }

        public GenericErrorBuilder(String area, int errorCode) {
            mArea = area;
            mErrorCode = errorCode;
        }

        public GenericErrorBuilder(ErrorCode errorCode) {
            this(errorCode.getArea(), errorCode.getErrorCode());
            mErrorCodeObject = errorCode;
        }

        protected GB setErrorCodeObject(ErrorCode errorCode) {
            mErrorCodeObject = errorCode;
            mErrorCode = errorCode.getErrorCode();
            mArea = errorCode.getArea();
            return (GB)this;
        }

        public GB setErrorDescription(String errorDescription) {
            mErrorDescription = errorDescription;
            return (GB) this;
        }

        public GB appendErrorDescription(String errorDescription) {
            mErrorDescription += ";" + errorDescription;
            return (GB) this;
        }

        public GB setException(Throwable exception) {
            mException = exception;
            return (GB) this;
        }
        public GB setInternalError(ErrorInfo errorInfo) {
            mInternalError = errorInfo;
            return (GB) this;
        }

        public final T build(){
            T errorInfo = createErrorInfo();
            errorInfo.mException =  mException;
            errorInfo.mInternalError = mInternalError;
            errorInfo.mErrorDescription = mErrorDescription;
            return errorInfo;
        }


        public interface ErrorCode {

            int getErrorCode();

            String getArea();
        }

        abstract protected T createErrorInfo();
    }


}
