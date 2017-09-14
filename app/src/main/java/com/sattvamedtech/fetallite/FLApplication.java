package com.sattvamedtech.fetallite;

import android.app.Application;

import java.util.Calendar;

public class FLApplication extends Application {

    private static FLApplication mInstance;
    public static boolean isFetalEnabled = true;
    public static String mPatientId = "";
    public static String mTestId = "";
    public static String mFileTimeStamp = "";
    public static Calendar mCalendar = Calendar.getInstance();
    //public static String mPatientName="";
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static FLApplication getInstance() {
        return mInstance;
    }
}
