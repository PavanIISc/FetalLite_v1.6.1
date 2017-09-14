package com.sattvamedtech.fetallite.helper;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.FLPreferences;

public class SessionHelper {

    private static final long ONE_HOUR_IN_MILLIS = 3600000;

    public static boolean isLoginSessionValid() {
        return (System.currentTimeMillis() - FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionTimestamp()) < ONE_HOUR_IN_MILLIS;
    }

    public static void clearSession() {
        FLPreferences.getInstance(FLApplication.getInstance()).setLoginSessionUser("");
        FLPreferences.getInstance(FLApplication.getInstance()).setLoginSessionTimestamp(0);
    }

    public static void resetSession()
    {
        User aCurrentUser  = FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserObject();
        FLPreferences.getInstance(FLApplication.getInstance()).setLoginSessionTimestamp(System.currentTimeMillis());
        FLPreferences.getInstance(FLApplication.getInstance()).setLoginSessionUser(GsonHelper.toUserJson(aCurrentUser));
    }

}
