package com.sattvamedtech.fetallite.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.Constants;

/**
 * Created by Pavan on 8/9/2017.
 */

public class InitialSplashActivity extends FLBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent aIntent = new Intent(InitialSplashActivity.this, DeviceRegistrationActivity.class);
                startActivity(aIntent);
            }
        }, 3000);


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
