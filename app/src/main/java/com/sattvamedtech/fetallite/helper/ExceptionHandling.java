package com.sattvamedtech.fetallite.helper;

import com.sattvamedtech.fetallite.interfaces.ExceptionCallback;

/**
 * Created by riteshdubey on 2/24/17.
 */

public class ExceptionHandling {
    private static ExceptionHandling mInstance;
    private ExceptionCallback mCallback;

    public static ExceptionHandling getInstance(){
        if(mInstance == null)
            mInstance = new ExceptionHandling();
        return mInstance;
    }

    public void setExceptionListener(ExceptionCallback iCallback){
        mCallback = iCallback;
    }

    public void removeExceptionListener(){
        mCallback = null;
    }

    public ExceptionCallback getExceptionListener(){
        return mCallback;
    }
}
