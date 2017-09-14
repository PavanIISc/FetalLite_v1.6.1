package com.sattvamedtech.fetallite.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.User;

/**
 * Helper class to deal with preferences i.e. saving data in preferences
 */
public class FLPreferences {

    private static String PREFS_FILE_NAME = "flPref";

    Context mContext;

    private static FLPreferences mInstance = null;

    public FLPreferences(Context iContext) {

        this.mContext = iContext;

    }

    public static final synchronized FLPreferences getInstance(Context iContext) {
        if (null == mInstance) {
            mInstance = new FLPreferences(iContext);
        }
        return mInstance;
    }

    public void clearSavedData() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.clear().commit();
    }
    public void setDeviceRegistrationInfo(String iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putString("deviceRegistrationID", iData);
        prefsEditor.commit();
    }

    public void setAdminUser(String iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putString("adminUser", iData);
        prefsEditor.commit();
    }

    public String getDeviceRegistrationInfo(){
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getString("deviceRegistrationID", "");
    }
    public String getAdminUser() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getString("adminUser", "");
    }

    public void setAdminPassword(String iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putString("adminPassword", iData);
        prefsEditor.commit();
    }

    public String getAdminPassword() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getString("adminPassword", "");
    }

    public void setDeviceId(String iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putString("deviceId", iData);
        prefsEditor.commit();
    }

    public String getDeviceId() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getString("deviceId", "");
    }

    public void setRegistrationDate(long iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putLong("registrationDate", iData);
        prefsEditor.commit();
    }

    public long getRegistrationDate() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getLong("registrationDate", 0);
    }

    public void setLastServiceDate(long iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putLong("lastServiceDate", iData);
        prefsEditor.commit();
    }

    public long getLastServiceDate() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getLong("lastServiceDate", 0);
    }

    public void setInitialProfile(boolean iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putBoolean("initialProfile", iData);
        prefsEditor.commit();
    }

    public boolean getInitialProfile() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getBoolean("initialProfile", false);
    }

    public void setTutorialSeen(boolean iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putBoolean("tutorialSeen", iData);
        prefsEditor.commit();
    }

    public boolean getTutorialSeen() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getBoolean("tutorialSeen", false);
    }

    public void setLoginSessionTimestamp(long iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putLong("loginTimestamp", iData);
        prefsEditor.commit();
    }

    public long getLoginSessionTimestamp() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getLong("loginTimestamp", 0);
    }

    public void setLoginSessionUser(String iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putString("loggedIn", iData);
        prefsEditor.commit();
    }

    public String getLoginSessionUserJson() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getString("loggedIn", null);
    }

    public User getLoginSessionUserObject() {
        String aUserJson = getLoginSessionUserJson();
        return (User) GsonHelper.getGson(aUserJson, User.class);
    }

    public Hospital getSessionHospital() {
        String aUserJson = getLoginSessionUserJson();
        User aUser = (User) GsonHelper.getGson(aUserJson, User.class);
        if(aUser != null)
            return aUser.hospital;
        else
            return null;
    }

    public void setJobScheduleInitialised(boolean iData) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putBoolean("jobScheduleInitialised", iData);
        prefsEditor.commit();
    }

    public boolean getJobScheduleInitialised() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getBoolean("jobScheduleInitialised", false);
    }


    public void setWifiSSID(String mSSID)
    {

        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME,  Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putString("SSID", mSSID);
        prefsEditor.commit();
    }

    public void setWifiPassword(String mWifiPassword)
    {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME,  Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = aMyPrefs.edit();
        prefsEditor.putString("wifipassword", mWifiPassword);
        prefsEditor.commit();
    }


    public String getWifiSSID() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getString("SSID", "FetalLite");
    }

    public String getWifiPassword() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getString("wifipassword", "12345678");
    }



    public Boolean getAdminAsUser() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getBoolean("adminAsUser", false);
    }


    public void setAdminAsUser(Boolean isAdminAsUser) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        aMyPrefs.edit().putBoolean("adminAsUser", isAdminAsUser).commit();
    }



    public Boolean getAdminAsDoctor() {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return aMyPrefs.getBoolean("adminAsDoctor", false);
    }



    public void setAdminAsDoctor(Boolean isAdminAsDoctor) {
        SharedPreferences aMyPrefs = mContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        aMyPrefs.edit().putBoolean("adminAsDoctor", isAdminAsDoctor).apply();
    }
}