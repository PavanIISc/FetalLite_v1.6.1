package com.sattvamedtech.fetallite.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiConfiguration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.adapter.WifiAdapter;
import com.sattvamedtech.fetallite.adapter.WifiApManager;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.DeviceRegistration;
import com.sattvamedtech.fetallite.process.InitialDataSocketIntentService;
import com.sattvamedtech.fetallite.storage.FLPreferences;

/**
 * Created by Pavan on 8/8/2017.
 */

public class DeviceRegistrationActivity extends FLBaseActivity implements View.OnClickListener, InitialDataSocketIntentService.DataSocketCallback {

    //initial hotspot ID and password
    private String mInitSSID = "FetalLite";
    private String mInitPassword = "12345678";

    Button mBSync, mBNext;
    EditText mEtDeviceID;
    TextView mTvDeviceID, mTvPressToSync;
    ImageView mIvTurnOnSensor, mIvPressToSync;
    int mTabBatteryLevel;
    private String mDeviceID = "";
    private double minSensorUnitBatteryLevel = 3.2;
    private int minTabletBatteryLevel = 5;

    private ImageView mIdFormat;
    private Intent mDataSocketIntent;
    private boolean isDataServiceConnected;
    private InitialDataSocketIntentService mDataSocketIntentService;


    public ProgressDialog syncProgressDialog;

    private String SSID1; //variable to store first substring(0,2) from deviceID
    private String SSID2; //variable to store second substring(4,5) from deviceID
    private String SSID3;  // variable to store third substring(13,14) from deviceID

    private String password1; //variable to store first substring(7,11) from deviceID
    private String password2; //variable to store second substring(4,5) from deviceID

    private ServiceConnection mDataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isDataServiceConnected = true;
            InitialDataSocketIntentService.LocalBinder aBinder = (InitialDataSocketIntentService.LocalBinder) iBinder;
            mDataSocketIntentService = aBinder.getSocketIntentService();
            mDataSocketIntentService.registerCallback(DeviceRegistrationActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isDataServiceConnected = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_registration);
        initToolbar();
        initView();
        initListeners();

    }

    private void initToolbar() {
        Toolbar aToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(aToolbar);
    }

    private void initView() {
        mIvTurnOnSensor = (ImageView) findViewById(R.id.ivTurnOnSensor);
        mTvPressToSync = (TextView) findViewById(R.id.tvPressToSync);
        mIvPressToSync = (ImageView) findViewById(R.id.ivPressToSync);
        mBSync = (Button) findViewById(R.id.bStartSync);
        mBNext = (Button) findViewById(R.id.bNext);
        mEtDeviceID = (EditText) findViewById(R.id.etDeviceID);
        mTvDeviceID = (TextView) findViewById(R.id.tvDeviceID);
        mIdFormat = (ImageView) findViewById(R.id.ivIdFormat);


        ApplicationUtils.mSendDeviceID = false;
        ApplicationUtils.mSensorUnitReady = false;
        ApplicationUtils.wifipasswordReset = false;
        ApplicationUtils.syncSuccesful = false;
        ApplicationUtils.wrongID = false;
        ApplicationUtils.batteryValueRead = false;
        ApplicationUtils.enableStartSync = false;

        Log.e("DeviceReg", "" + mInitSSID + " " + mInitPassword);



        if(mDataSocketIntent != null)
        {

            //If called from battery check
            mDataSocketIntentService.breakConnection();
            unbindService(mDataServiceConnection);
        }

        disableWifiAp();
        initWifiHotspot(mInitSSID, mInitPassword);
        startDataSocketService();

        mBSync.setVisibility(View.GONE);
        mBSync.setEnabled(false);
        mTvDeviceID.setVisibility(View.GONE);
        mEtDeviceID.setVisibility(View.GONE);
        mTvPressToSync.setVisibility(View.GONE);
        mIvTurnOnSensor.setVisibility(View.GONE);
        mIvPressToSync.setVisibility(View.GONE);

        mBNext.setVisibility(View.GONE);
        mBNext.setEnabled(false);

    }


    private void initListeners()
    {
        mBNext.setOnClickListener(this);
        mBSync.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {

        if (view.getId() == R.id.bStartSync)
        {
            mDataSocketIntentService.getBatteryValue();
        }


        if (view.getId() == R.id.bNext)
        {
            mDeviceID = mEtDeviceID.getText().toString();
            if (!mDeviceID.matches("[A-Z]{3}\\-[0-9]{1,2}\\-[0-9]{1,5}\\-[0-9]{1,2}"))
            {

                new MessageHelper(DeviceRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.label_invalid_device_id), "", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                        mEtDeviceID.setText("");
                    }
                });
            }
            else if (TextUtils.isEmpty(mEtDeviceID.getText().toString()))
            {
                new MessageHelper(DeviceRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_deviceID_empty), "", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
            }
            else
            {
                ApplicationUtils.mDeviceID = mEtDeviceID.getText().toString();
                ApplicationUtils.mSendDeviceID = true;


                try
                {
                    mDataSocketIntentService.sendDeviceID();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }


                syncProgressDialog = new MessageHelper().getProgressDialog(DeviceRegistrationActivity.this);
                syncProgressDialog.show();


            }


        }

    }


    // Callback from IntentService

    @Override
    public void onClientConnected()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("MainActivity", "onClientConnected()");




                if (ApplicationUtils.enableStartSync)
                {

                    mBSync.setVisibility(View.VISIBLE);
                    mBSync.setEnabled(true);
                    mIvTurnOnSensor.setVisibility(View.VISIBLE);
                    mTvPressToSync.setVisibility(View.VISIBLE);
                    ApplicationUtils.enableStartSync = false;

                }


                if (ApplicationUtils.batteryValueRead)
                {

                    Log.e("DeviceReg", "checking battery value");
                    checkSensorTabletBattery();
                    ApplicationUtils.batteryValueRead = false;
                }





                if (ApplicationUtils.mSensorUnitReady && !ApplicationUtils.syncSuccesful)
                {

                    Log.e("DataReg", "started Data Socket again");
                    registerSensorUnit();

                }

                if (ApplicationUtils.wrongID && !ApplicationUtils.syncSuccesful)
                {

                    syncProgressDialog.dismiss();

                    new MessageHelper(DeviceRegistrationActivity.this).showTitleAlertOk("Connection Lost", "Try syncing again", "", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            initView();
                            dialog.dismiss();
                        }
                    });
                }

                if(ApplicationUtils.syncSuccesful)
                {
                    Log.e("DevReg", "this is to test branch");
                    syncProgressDialog.dismiss();
                    showDeviceRegistrationConfirmation();
                }





            }
        });
    }


    // Functions called in this activity.

    private void showDeviceRegistrationConfirmation() {
        new MessageHelper(DeviceRegistrationActivity.this).showTitleAlertOk(getString(R.string.success_device_registration), getString(R.string.success_turn_off_device), getString(R.string.action_next), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openTutorialActivity(dialogInterface);
            }
        });
    }



    private void openTutorialActivity(DialogInterface iDialogInterface)
    {
        iDialogInterface.dismiss();
        Intent aIntent = new Intent(DeviceRegistrationActivity.this, TutorialsActivity.class);
        finish();
        mDataSocketIntentService.breakConnection();
        unbindService(mDataServiceConnection);
        startActivity(aIntent);
    }

    private void initWifiHotspot(String mSSID, String mPassword)
    {
        ApplicationUtils.SSID = mSSID;
        ApplicationUtils.wifipassword = mPassword;
        WifiAdapter.configApState(getApplicationContext());

    }

    private void disableWifiAp()
    {
        WifiApManager wifiMan = new WifiApManager(getApplicationContext());
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        wifiConfiguration.SSID = ApplicationUtils.SSID;
        wifiConfiguration.preSharedKey = ApplicationUtils.wifipassword;


        wifiMan.setWifiApEnabled(wifiConfiguration, false);
    }


    private void startDataSocketService() {
        mDataSocketIntent = new Intent(DeviceRegistrationActivity.this, InitialDataSocketIntentService.class);
        Log.e("MainActivity", "startDataSocketService");
        startService(mDataSocketIntent);
        bindService(mDataSocketIntent, mDataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void startDataSocketServiceSync() {
        mDataSocketIntent = new Intent(DeviceRegistrationActivity.this, InitialDataSocketIntentService.class);
        Log.e("DeviceRegistration", "startDataSocketService for Sync");
        startService(mDataSocketIntent);
        bindService(mDataSocketIntent, mDataServiceConnection, Context.BIND_AUTO_CREATE);
    }


    public void checkSensorTabletBattery()
    {
        checkTabletBattery();
        if(ApplicationUtils.tempBatteryValue > minSensorUnitBatteryLevel && mTabBatteryLevel > minTabletBatteryLevel)
        {
            enableReadForDeviceID();
        }
        else if(ApplicationUtils.tempBatteryValue < minSensorUnitBatteryLevel && mTabBatteryLevel > minTabletBatteryLevel)
        {

            new MessageHelper(DeviceRegistrationActivity.this).showTitleAlertOk("Low Battery", "Please turn off the sensor unit & charge the Sensor Unit for 1 hour and then try again ", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    initView();
                    dialog.dismiss();
                }
            });
        }
        else if(ApplicationUtils.tempBatteryValue > minSensorUnitBatteryLevel && mTabBatteryLevel < minTabletBatteryLevel)
        {

            new MessageHelper(DeviceRegistrationActivity.this).showTitleAlertOk("Low Battery", "Please charge the the Tablet for 1 hour and then try again ", "", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    initView();
                    dialog.dismiss();
                }
            });

        }
        else if(ApplicationUtils.tempBatteryValue < minSensorUnitBatteryLevel && mTabBatteryLevel < minTabletBatteryLevel)
        {

            new MessageHelper(DeviceRegistrationActivity.this).showTitleAlertOk("Low Battery", "Please turn off the sensor unit and charge the sensor unit and the tablet for 1 hour. Then try again. ", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    initView();
                    dialog.dismiss();


                }
            });

        }
    }


    public void registerSensorUnit()
    {



        mDataSocketIntentService.breakConnection();
        unbindService(mDataServiceConnection);

        SSID1 = ApplicationUtils.mDeviceID.substring(0, 3);
        SSID2 = ApplicationUtils.mDeviceID.substring(4, 6);
        SSID3 = ApplicationUtils.mDeviceID.substring(13, 15);
        ApplicationUtils.SSID = SSID1.concat(SSID2).concat(SSID3);
        password1 = ApplicationUtils.mDeviceID.substring(7, 12);
        password2 = ApplicationUtils.mDeviceID.substring(4, 6);
        ApplicationUtils.wifipassword = "S" + password1.concat(password2);

        disableWifiAp();

        initWifiHotspot(ApplicationUtils.SSID, ApplicationUtils.wifipassword);
        ApplicationUtils.wifipasswordReset = true;


        FLPreferences.getInstance(FLApplication.getInstance()).setWifiSSID(ApplicationUtils.SSID);
        FLPreferences.getInstance(FLApplication.getInstance()).setWifiPassword(ApplicationUtils.wifipassword);

        DeviceRegistration aDeviceRegistration = new DeviceRegistration(mEtDeviceID.getText().toString());
        FLPreferences.getInstance(FLApplication.getInstance()).setDeviceRegistrationInfo(GsonHelper.toDeviceRegJson(aDeviceRegistration));

        startDataSocketServiceSync();


    }

    public void stopDataSocketService() {
        try {
            unbindService(mDataServiceConnection);
            stopService(mDataSocketIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkTabletBattery()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        mTabBatteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);


    }

    public void enableReadForDeviceID()
    {
        mIvPressToSync.setVisibility(View.VISIBLE);
        mTvDeviceID.setVisibility(View.VISIBLE);
        mEtDeviceID.setVisibility(View.VISIBLE);
        mBNext.setVisibility(View.VISIBLE);
        mBNext.setEnabled(true);

        // TODO: 04-Sep-17 remove below hard-coded line
        mEtDeviceID.setText("SFL-17-12345-01");
    }

    @Override
    public void onDataStreamStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("HomeActivity", "on data stream stopped");
            }
        });
    }


    @Override
    public void onDataStreamStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopDataSocketService();


    }
}
