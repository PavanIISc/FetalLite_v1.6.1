package com.sattvamedtech.fetallite.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.SessionHelper;
import com.sattvamedtech.fetallite.storage.FLPreferences;

public class SplashActivity extends FLBaseActivity {

    private boolean isLogoutFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getExtras();
        if (isLogoutFlow) {
            proceedAfterSystemSync();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO: 10-Sep-17 Chenge below target class to SystemSyncActivity.class
                    Intent aIntent = new Intent(SplashActivity.this, SystemSyncActivity.class);
                    startActivityForResult(aIntent, Constants.RC_SYSTEM_SYNC_ACTIVITY);
                }
            }, 3000);
        }
    }

    private void getExtras() {
        if (getIntent().getExtras() != null) {
            isLogoutFlow = getIntent().getExtras().getBoolean(Constants.EXTRA_LOGOUT_FLOW, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SYSTEM_SYNC_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSmsPermission()) {
                        proceedAfterSystemSync();
                    }
                } else {
                    proceedAfterSystemSync();
                }
            }
        }
    }

    private boolean checkSmsPermission() {
        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.SEND_SMS}, Constants.RC_SEND_SMS);
            }
            return false;
        }
        return true;
    }

    private void proceedAfterSystemSync() {
        Intent aIntent;
        if (TextUtils.isEmpty(FLPreferences.getInstance(FLApplication.getInstance()).getDeviceRegistrationInfo())) {
            aIntent = new Intent(SplashActivity.this, DeviceRegistrationActivity.class);
        }else if (TextUtils.isEmpty(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser())) {
            aIntent = new Intent(SplashActivity.this, AdminRegistrationActivity.class);
        } else if (!FLPreferences.getInstance(FLApplication.getInstance()).getInitialProfile()) {
            aIntent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
        } else if (!FLPreferences.getInstance(FLApplication.getInstance()).getTutorialSeen()) {
            aIntent = new Intent(SplashActivity.this, TutorialsActivity.class);
        } else if (TextUtils.isEmpty(FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson()) || (!TextUtils.isEmpty(FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson()) && !SessionHelper.isLoginSessionValid())) {
            SessionHelper.clearSession();
            aIntent = new Intent(SplashActivity.this, LoginActivity.class);
        } else {
            aIntent = new Intent(SplashActivity.this, HomeActivity.class);
        }
        finish();
        startActivity(aIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.RC_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedAfterSystemSync();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

}
