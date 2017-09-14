package com.sattvamedtech.fetallite.adapter;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.sattvamedtech.fetallite.R;

/**
 * Created by pavan on 20-07-2017.
 */

public class DeviceAdminAdapter extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context iContext, Intent iIntent){

    }

    @Override
    public void onDisabled(Context iContext, Intent iIntent){

    }
    @Override
    public CharSequence onDisableRequested(Context iContext, Intent iIntent){
        return "Stopping Kiosk Mode";
    }

    @Override
    public void onLockTaskModeEntering(Context iContext, Intent iIntent, String iPackage){

    }
    @Override
    public void onLockTaskModeExiting(Context iContext, Intent iIntent){

    }
}
