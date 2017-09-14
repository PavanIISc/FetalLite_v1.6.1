package com.sattvamedtech.fetallite.helper;

import android.util.Log;

/**
 * Created by riteshdubey on 12/30/16.
 */

public class Logger {

    private static boolean DEBUG = true;

    public static void logVerbose(String iTitle, String iLogMessage) {
        if (DEBUG)
            Log.v(Constants.TAG, iTitle + " : " + iLogMessage);
    }

    public static void logInfo(String iTitle, String iLogMessage) {
        if (DEBUG)
            Log.i(Constants.TAG, iTitle + " : " + iLogMessage);
    }

    public static void logError(String iTitle, String iLogMessage, Exception iException) {
        if (DEBUG)
            Log.e(Constants.TAG, iTitle + " : " + iLogMessage, iException);

    }

    public static void logWarn(String iTitle, String iLogMessage) {
        if (DEBUG)
            Log.w(Constants.TAG, iTitle + " : " + iLogMessage);

    }

    public static void logWarn(String iLogMessage) {
        if (DEBUG)
            Log.w(Constants.TAG, iLogMessage);

    }

    public static void logDebug(String iTitle, String iLogMessage) {
        if (DEBUG)
            Log.d(Constants.TAG, iTitle + " : " + iLogMessage);

    }
}
