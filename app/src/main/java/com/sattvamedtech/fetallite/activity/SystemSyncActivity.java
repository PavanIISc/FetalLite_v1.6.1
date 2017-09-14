package com.sattvamedtech.fetallite.activity;

import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.adapter.WifiAdapter;

public class SystemSyncActivity extends FLBaseActivity {

    private ProgressBar mPbSetupSync;
    private TextView mTvProbeTab, mTvDeviceConn, mTvSetupSoftware, mTvPrinter, mTvBattery;
    private ImageView mIvProbeTab, mIvDeviceConn, mIvSetupSoftware, mIvPrinter, mIvBattery;
    private int mSetupStepsCompleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_sync);
        initToolbar();
        initView();
        simulateSetup();
       // initWifiHotspot();
    }

    private void initToolbar() {
        Toolbar aToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(aToolbar);
    }




    private void initView() {
        mPbSetupSync = (ProgressBar) findViewById(R.id.pbSetupSync);

        mTvProbeTab = (TextView) findViewById(R.id.tvProbeTab);
        mTvDeviceConn = (TextView) findViewById(R.id.tvDeviceConn);
        mTvSetupSoftware = (TextView) findViewById(R.id.tvSetupSoftware);
        mTvPrinter = (TextView) findViewById(R.id.tvPrinter);
        mTvBattery = (TextView) findViewById(R.id.tvBattery);

        mIvProbeTab = (ImageView) findViewById(R.id.ivProbeTab);
        mIvDeviceConn = (ImageView) findViewById(R.id.ivDeviceConn);
        mIvSetupSoftware = (ImageView) findViewById(R.id.ivSetupSoftware);
        mIvPrinter = (ImageView) findViewById(R.id.ivPrinter);
        mIvBattery = (ImageView) findViewById(R.id.ivBattery);
    }

//    private void initWifiHotspot()
//    {
//        WifiAdapter.configApState(this);
//    }

    private void simulateSetup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSetupStepsCompleted++;
                updateProgress();
            }
        }, 500);
    }

    private void updateProgress() {
        switch (mSetupStepsCompleted) {
            case 1:
                progressAnimate(20);
                mIvProbeTab.setVisibility(View.VISIBLE);
                mTvProbeTab.setTypeface(null, Typeface.NORMAL);
                mTvDeviceConn.setTypeface(null, Typeface.BOLD);
                simulateSetup();
                break;
            case 2:
                progressAnimate(40);
                mIvDeviceConn.setVisibility(View.VISIBLE);
                mTvDeviceConn.setTypeface(null, Typeface.NORMAL);
                mTvSetupSoftware.setTypeface(null, Typeface.BOLD);
                simulateSetup();
                break;
            case 3:
                progressAnimate(60);
                mIvSetupSoftware.setVisibility(View.VISIBLE);
                mTvSetupSoftware.setTypeface(null, Typeface.NORMAL);
                mTvPrinter.setTypeface(null, Typeface.BOLD);
                simulateSetup();
                break;
            case 4:
                progressAnimate(80);
                mIvPrinter.setVisibility(View.VISIBLE);
                mTvPrinter.setTypeface(null, Typeface.NORMAL);
                mTvBattery.setTypeface(null, Typeface.BOLD);
                simulateSetup();
                break;
            case 5:
                progressAnimate(100);
                mIvBattery.setVisibility(View.VISIBLE);
                setupCompleted();
                break;
        }
    }

    private void progressAnimate(int iNewProgress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(mPbSetupSync, "progress", mPbSetupSync.getProgress(), iNewProgress);
        animation.setDuration(100);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void setupCompleted() {
        setResult(RESULT_OK);
        finish();
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

    }
}
