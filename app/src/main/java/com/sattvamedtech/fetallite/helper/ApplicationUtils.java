package com.sattvamedtech.fetallite.helper;

import android.content.Context;

import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.Patient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Vibhav on 16/11/16.
 */
public class ApplicationUtils {

    public static final int IDLE = 0;
    public static final int PROCESSING = 1;

    public static Queue<String> mDynamicDataStore = new LinkedList<String>();
    public static ArrayList<String> mSampleMasterList = new ArrayList<>();
    public static ArrayList<Integer> mFqrsMasterList = new ArrayList<>();
    public static ArrayList<Integer> mMaternalMasterList = new ArrayList<>();
    public static int algoProcessStartCount = -1;
    public static int algoProcessEndCount = -1;
    public static int bufferLength = 15000;
    //    public static int lastBufferIndex = 0;
    public static final int SKIP_COUNT_FOR_PLOT = 1;
    public static int lastFetalPlotIndex = SKIP_COUNT_FOR_PLOT;
    public static int lastMaternalPlotIndex = SKIP_COUNT_FOR_PLOT;
    public static int lastFetalPlotXValue = 0;
    public static int lastMaternalPlotXValue = 0;
    public static int  lastUcPlotXValue = 0;
    public static int chan_select = 0;
    public static int mConversionFlag = IDLE;
    public static int mPlottingFlag = IDLE;
    public static int mHrPlottingFlag = IDLE;
    public static int mUcPlottingFlag = IDLE;
    public static double[][] mInputArray = new double[15000][4];
    public static double[] mInputArrayUc = new double[15000];
    public static final double[][] mTestInputArray = new double[15000][4];
    public static long mStartMS;
    public static int[] FQRS;
    public static int[] MQRS;
    public static int test_printer_flag = 1;
    public static int mTestDurationMin = 0;
    public static int mTestDurationSec = 0;

    public static float mXEntryDiff = 0;
    public static int sample_set = 0;

    public static int dummyFlag = ApplicationUtils.REAL_DATA;

    public static int REAL_DATA = 0;
    public static int DUMMY_DATA = 1;

    public static long mFetalNextPlotTime = 0;
    public static long mMaternalNextPlotTime = 0;
    public static long mUCNextPlotTime = 0;

    public static int lineCount = 0;
    public static boolean connectionLost = false;

    public static boolean isForgotAdminPassword = true;

    public static String mTestID;

    public static int mTestAgain = 0;

    public static int mHospitalID = 0;

    /*temp: variable to take sensor unit battery values*/
    public static String temp;
    /*batteryLow: Boolean, set if sensor battery is low */
    public static boolean mSensorUnitBatteryLow;
    /*batteryLow: Boolean, set if Tab battery is low */
    public static boolean mTabBatteryLow;
    public static String mDeviceID;

    //variable to send deciveID to sensor unit
    public static boolean mSendDeviceID = false;
    public static boolean mSensorUnitReady = false;
    public static boolean wifipasswordReset = false;
    public static boolean syncSuccesful = false;
    public static boolean wrongID = false;
    public static boolean batteryValueRead = false;
    public static boolean enableStartSync = false;

    public static Double tempBatteryValue;

    //Wifi SSID and password
    public static String SSID;
    public static String wifipassword;

    //getting hospital from menu
    public static Hospital mHospital;

    public static Patient mPatient;

    public static boolean mIsAdminAsUser;
    public static boolean mIsAdminAsDoctor;

    public static int enableTestAgainOnWindow;

    public static Context mTestAgainDialogContext;

    public static int mFromMenu;

    public static String getCurrentTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}
