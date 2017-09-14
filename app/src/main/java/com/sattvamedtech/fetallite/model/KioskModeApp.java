package com.sattvamedtech.fetallite.model;

import android.app.Application;

/**
 * Created by pavan on 20-07-2017.
 */

public class KioskModeApp extends Application {
    public static boolean isInLockMode;

    public static boolean isInLockMode() {
        return isInLockMode;
    }

    public static void setIsInLockMode(boolean isInLockMode) {
        KioskModeApp.isInLockMode = isInLockMode;
    }
}
