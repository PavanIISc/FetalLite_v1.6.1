package com.sattvamedtech.fetallite.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.dialog.CustomerCareDialog;
import com.sattvamedtech.fetallite.dialog.TestAgainMessageDialog;
import com.sattvamedtech.fetallite.fragment.DrawerListFragment;
import com.sattvamedtech.fetallite.fragment.HomeFragment;
import com.sattvamedtech.fetallite.fragment.NavigationFragment;
import com.sattvamedtech.fetallite.fragment.PatientTestDataFragment;
import com.sattvamedtech.fetallite.fragment.TestFragment;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.ExceptionHandling;
import com.sattvamedtech.fetallite.helper.FileLogger;
import com.sattvamedtech.fetallite.helper.FileUtils;
import com.sattvamedtech.fetallite.helper.Logger;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.helper.SMSHelper;
import com.sattvamedtech.fetallite.helper.SessionHelper;
import com.sattvamedtech.fetallite.interfaces.ExceptionCallback;
import com.sattvamedtech.fetallite.interfaces.PrintCallback;
import com.sattvamedtech.fetallite.interfaces.PrintInterface;
import com.sattvamedtech.fetallite.job.CompressionJobService;
import com.sattvamedtech.fetallite.model.Patient;
import com.sattvamedtech.fetallite.model.Test;
import com.sattvamedtech.fetallite.process.DataSocketIntentService;
import com.sattvamedtech.fetallite.process.PrintSocketIntentService;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HomeActivity extends FLBaseActivity implements DataSocketIntentService.DataSocketCallback, PrintSocketIntentService.PrintSocketCallback, ExceptionCallback, FragmentManager.OnBackStackChangedListener, PrintInterface {

    private Toolbar mToolbar;
    private LinearLayout mLlToolbar, mLlMenu;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationFragment mNavigationFragment;
    private DrawerListFragment mDrawerListFragment;
    private Intent mDataSocketIntent, mPrintSocketIntent;
    private FrameLayout mFlNavigationContainer;
    private boolean isTestInProgress;
    private boolean isDataServiceConnected, isPrintServiceConnected;
    private boolean isPrintPending = false;
    private ArrayList<String> mPrintData = new ArrayList<>();
    private int mCurrentPrintCount = 0;

    TestAgainMessageDialog mTestAgainDialog;

    private  String mHospitalName;

    public ProgressDialog mProgressDialog;

    private DataSocketIntentService mDataSocketIntentService;
    private PrintSocketIntentService mPrintSocketIntentService;

    private ServiceConnection mDataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isDataServiceConnected = true;
            DataSocketIntentService.LocalBinder aBinder = (DataSocketIntentService.LocalBinder) iBinder;
            mDataSocketIntentService = aBinder.getSocketIntentService();
            mDataSocketIntentService.registerCallback(HomeActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isDataServiceConnected = false;
        }
    };

    private ServiceConnection mPrintServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isPrintServiceConnected = true;
            PrintSocketIntentService.LocalBinder aBinder = (PrintSocketIntentService.LocalBinder) iBinder;
            mPrintSocketIntentService = aBinder.getSocketIntentService();
            mPrintSocketIntentService.registerCallback(HomeActivity.this);
            if (isPrintPending)
                HomeActivity.this.printData();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isPrintServiceConnected = false;
        }
    };

    private boolean hasBrightnessPermission;
    private boolean isSmsForCustomerCare;
    private String mMessageString, mTicketNumber;

    private String iLastPatientID;
    private String iLastPatientFirstName;
    private String iLastPatientLastName;
    private int iLastPatientAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        hasBrightnessPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(this);

        if (!hasBrightnessPermission)
            checkBrightnessPermission();
        initView();
        initDrawer();
        initListeners();
        startDataSocketService();
        startPrintSocketService();
        PrintCallback.getInstance().addPlotInterface(this);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mLlToolbar = (LinearLayout) findViewById(R.id.llToolbar);
        mLlMenu = (LinearLayout) findViewById(R.id.llMenu);
        mProgressDialog = new MessageHelper().getProgressDialog(HomeActivity.this);
        addReplaceFragment(new HomeFragment(), false, null, false);
    }

    private void initDrawer() {
        mFlNavigationContainer = (FrameLayout) findViewById(R.id.flNavigationContainer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mNavigationFragment.setDate();
                mNavigationFragment.notifyDataSetChanged();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.setDrawerIndicatorEnabled(false);
        mToggle.syncState();
        int width = getResources().getDisplayMetrics().widthPixels / 3;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mFlNavigationContainer.getLayoutParams();
        params.width = width;
        mFlNavigationContainer.setLayoutParams(params);
        mNavigationFragment = new NavigationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flNavigationContainer, mNavigationFragment).commitAllowingStateLoss();
    }

    public void showToolbar(boolean toShow) {
        mLlToolbar.setVisibility(toShow ? View.VISIBLE : View.GONE);
    }

    public void showMenuIcon(boolean toShow) {
        mLlMenu.setVisibility(toShow ? View.VISIBLE : View.GONE);
    }

    public void setTitle(String iTitle) {
        if (TextUtils.isEmpty(iTitle)) {
            showToolbar(false);
        } else {
            showToolbar(true);
            mToolbar.setTitle(iTitle);
        }
    }

    private void initListeners() {
        mLlMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });
        ExceptionHandling.getInstance().setExceptionListener(this);
    }

    private void openDrawer() {

        mDrawerLayout.openDrawer(GravityCompat.END);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.END);
    }

    public void addReplaceFragment(Fragment iFragment, boolean toAdd, Patient iPatient, boolean iViewData) {
        HomeFragment iHomeFragment = new HomeFragment();

        FragmentTransaction aTransaction = getSupportFragmentManager().beginTransaction();
        if(iPatient != null) {
            iLastPatientID = iPatient.id;
            iLastPatientFirstName = iPatient.firstName;
            iLastPatientLastName = iPatient.lastName;
            iLastPatientAge = iPatient.age;

            final Bundle bdl = new Bundle();
            bdl.putSerializable("PatientObj",iPatient);
            bdl.putInt(Constants.EXTRA_TEST_DURATION, 15);
            iHomeFragment.setArguments(bdl);
        }

        if (toAdd)
        {
           aTransaction.add(R.id.flFragmentContainer, iFragment);
           aTransaction.addToBackStack(null);
        }
        else
        {
            aTransaction.replace(R.id.flFragmentContainer, iFragment);
        }
        aTransaction.commitAllowingStateLoss();
    }

    public void popFragment() {
        getSupportFragmentManager().popBackStack();
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.flFragmentContainer);
    }

    public void openTestScreen(Patient iPatient, Test iTest, int iTestDuration) {
        initJobSchedule();
        TestFragment aTestFragment = new TestFragment();
        Bundle aBundle = new Bundle();
        aBundle.putSerializable(Constants.EXTRA_PATIENT, iPatient);
        aBundle.putInt(Constants.EXTRA_TEST_DURATION, iTestDuration);
        aBundle.putString(Constants.DOCTOR,""+iTest.doctor);
        aBundle.putString(Constants.USER,""+iTest.user);
        aBundle.putSerializable("PatientObj",iPatient);
        aTestFragment.setArguments(aBundle);
        addReplaceFragment(aTestFragment, true, iPatient, false);
    }

    public void confirmLogout() {
        new MessageHelper(HomeActivity.this).showTitleAlertOkCancel(getString(R.string.label_logout), getString(R.string.confirm_logout), "", "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                logout();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    public void invalidSession() {
        new MessageHelper(HomeActivity.this).showTitleAlertOk(getString(R.string.label_info), getString(R.string.alert_invalid_session), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                logout();
            }
        }, false);
    }

    private void logout() {
        SessionHelper.clearSession();
        Intent aIntent = new Intent(HomeActivity.this, SplashActivity.class);
        aIntent.putExtra(Constants.EXTRA_LOGOUT_FLOW, true);
        HomeActivity.this.finish();
        startActivity(aIntent);
    }

    @Override
    public void onBackPressed() {
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            closeDrawer();
//        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            if (getCurrentFragment() instanceof TestFragment) {
//
//            } else {
//                popFragment();
//            }
//        } else {
//            super.onBackPressed();
//        }
    }

    public void initJobSchedule() {
        if (!FLPreferences.getInstance(FLApplication.getInstance()).getJobScheduleInitialised()) {
            ComponentName aServiceComponent = new ComponentName(this, CompressionJobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(0, aServiceComponent);
            builder.setPeriodic(FileUtils.ONE_DAY);
            builder.setRequiresDeviceIdle(false); // device should be idle
            builder.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            int aJobScheduleResult = jobScheduler.schedule(builder.build());
            //Logger.logInfo("HomeActivity", "aJobScheduleResult: " + (aJobScheduleResult == JobScheduler.RESULT_SUCCESS));
            FLPreferences.getInstance(FLApplication.getInstance()).setJobScheduleInitialised(aJobScheduleResult == JobScheduler.RESULT_SUCCESS);
        }
    }

    private void startDataSocketService() {
        mDataSocketIntent = new Intent(HomeActivity.this, DataSocketIntentService.class);
        Logger.logInfo("MainActivity", "startDataSocketService");
        startService(mDataSocketIntent);
        bindService(mDataSocketIntent, mDataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void startDataSocketServiceAgain(Context context) {
        mDataSocketIntent = new Intent(HomeActivity.this, DataSocketIntentService.class);
        Logger.logInfo("MainActivity", "startDataSocketService");
        context.startService(mDataSocketIntent);
        context.bindService(mDataSocketIntent, mDataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopDataSocketService() {
        try {
            unbindService(mDataServiceConnection);
            stopService(mDataSocketIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartDataSocketService() {
        if (isDataServiceConnected)
            stopDataSocketService();
        startDataSocketService();
    }

    public void restartDataSocketServiceTestAgain(Context context) {
        if (isDataServiceConnected)
            stopDataSocketService();
        startDataSocketServiceAgain(context);
    }
    public void startDataStream() {
        Log.e("HomeFrag", "start Data Stream");
        isTestInProgress = true;
        resetApplicationUtils();
        mDataSocketIntentService.startDataStream();
    }

    public void stopDataStream() {
        Logger.logInfo("HomeActivity", "stop data stream");
        mDataSocketIntentService.stopDataStream();
        isTestInProgress = false;
        Logger.logInfo("DataSocketIntentService", "Total Line count:  " + ApplicationUtils.lineCount);
        ApplicationUtils.lineCount = 0;
    }

    public boolean isTestInProgress() {
        return isTestInProgress;
    }

    public boolean hasBrightnessPermission() {
        return hasBrightnessPermission;
    }

    private void startPrintSocketService() {
        mPrintSocketIntent = new Intent(HomeActivity.this, PrintSocketIntentService.class);
        Logger.logInfo("HomeActivity", "startPrintSocketService");
        startService(mPrintSocketIntent);
        bindService(mPrintSocketIntent, mPrintServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopPrintSocketService() {
        try {
            unbindService(mPrintServiceConnection);
            stopService(mPrintSocketIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartPrintSocketService() {
        if (isPrintServiceConnected)
            stopPrintSocketService();
        startPrintSocketService();
    }

    public void resetApplicationUtils() {
        FileLogger.logData(new SimpleDateFormat("MM-dd-HH-mm-ss").format(FLApplication.mCalendar.getTimeInMillis())+" : HomeActivity : Reset Flags on Click start Test", "ExecutionLogs", FLApplication.mFileTimeStamp);
        Logger.logInfo("HomeActivity", "Reset application utils");
        ApplicationUtils.mSampleMasterList.clear();
        ApplicationUtils.mFqrsMasterList.clear();
        ApplicationUtils.mMaternalMasterList.clear();
        ApplicationUtils.algoProcessStartCount = -1;
        ApplicationUtils.algoProcessEndCount = -1;
        ApplicationUtils.bufferLength = 15000;
        ApplicationUtils.lastFetalPlotIndex = ApplicationUtils.SKIP_COUNT_FOR_PLOT;
        ApplicationUtils.lastMaternalPlotIndex = ApplicationUtils.SKIP_COUNT_FOR_PLOT;
        ApplicationUtils.lastFetalPlotXValue = 0;
        ApplicationUtils.lastMaternalPlotXValue = 0;
        ApplicationUtils.lastUcPlotXValue = 0;
        ApplicationUtils.mConversionFlag = ApplicationUtils.IDLE;
        ApplicationUtils.mHrPlottingFlag = ApplicationUtils.IDLE;
        ApplicationUtils.mUcPlottingFlag = ApplicationUtils.IDLE;
        ApplicationUtils.mStartMS = 0;
        //ApplicationUtils.mConversionFlag = ApplicationUtils.IDLE;
        //ApplicationUtils.mHrPlottingFlag = ApplicationUtils.IDLE;
        //ApplicationUtils.mUcPlottingFlag = ApplicationUtils.IDLE;
        com.sattvamedtech.fetallite.signalproc.Constants.HR_FETAL.clear();
        com.sattvamedtech.fetallite.signalproc.Constants.QRS_FETAL_LOCATION.clear();
        com.sattvamedtech.fetallite.signalproc.Constants.HR_MATERNAL.clear();
        com.sattvamedtech.fetallite.signalproc.Constants.QRS_MATERNAL_LOCATION.clear();
        clearPrintData();
        com.sattvamedtech.fetallite.signalproc.Constants.reset();
    }

    public void clearPrintData() {
        mPrintData.clear();
        mCurrentPrintCount = 0;
    }

    public void printData() {
        mProgressDialog.show();
        if (isPrintServiceConnected) {
            if (mCurrentPrintCount < mPrintData.size()) {
                mPrintSocketIntentService.printData(mPrintData.get(mCurrentPrintCount));
                mCurrentPrintCount++;
            } else {
                mCurrentPrintCount = 0;
                mProgressDialog.dismiss();
                isPrintPending = false;
            }
        } else {
            isPrintPending = true;
            restartPrintSocketService();
        }
    }

    private void stopPrint() {
        isPrintPending = false;
    }

    @Override
    public void onClientConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getCurrentFragment() instanceof HomeFragment) {
                    ((HomeFragment) getCurrentFragment()).enableNewTest(true);
                }
                else if(ApplicationUtils.mTestAgain == 1){
                  new TestAgainMessageDialog(getApplicationContext(), ApplicationUtils.mPatient).enableTestAgain(true);

                }
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
    public void onDataStreamStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Logger.logInfo("HomeActivity", "on data stream stopped");
                if (getCurrentFragment() instanceof HomeFragment) {
                    ((HomeFragment) getCurrentFragment()).enableNewTest(false);
                }
                restartDataSocketService();
            }
        });
    }

    @Override
    public void onInvalidData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopDataStream();
                if (getCurrentFragment() instanceof TestFragment)
                    ((TestFragment) getCurrentFragment()).stopTest(false);
                new MessageHelper(HomeActivity.this).showTitleAlertOk(getString(R.string.label_info), getString(R.string.error_device_placing_incorrect), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (getCurrentFragment() instanceof TestFragment) {
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            getSupportFragmentManager().popBackStack();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExceptionHandling.getInstance().removeExceptionListener();
        PrintCallback.getInstance().removePlotInterface(this);
        stopDataSocketService();
        stopPrintSocketService();
    }

    @Override
    public void onException(final Exception e)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                new MessageHelper(HomeActivity.this).showTitleAlertOk("SENSOR UNIT PLACEMENT ERROR", "Please check eletrode contacts and/or sensor unit orientation and position before starting a new test", "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (getCurrentFragment() instanceof TestFragment) {
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            getSupportFragmentManager().popBackStack();
                        }
                    }
                });
                e.printStackTrace();
                if (e.getMessage().contains("printer") && !isPrintPending)
                    return;
                if (getCurrentFragment() instanceof TestFragment) {
                    ((TestFragment) getCurrentFragment()).stopTest(true);
                    stopPrint();
                }
            }
        });
        e.printStackTrace();
    }

    @Override
    public void onBackStackChanged() {
        if (getCurrentFragment() instanceof HomeFragment) {
            ((HomeFragment) getCurrentFragment()).setToolbar();
            ((HomeFragment) getCurrentFragment()).enableNewTest(false);
        } else if (getCurrentFragment() instanceof PatientTestDataFragment) {
            ((PatientTestDataFragment) getCurrentFragment()).setToolbar();
        }
    }

    private void checkBrightnessPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, Constants.RC_WRITE_SETTINGS);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, Constants.RC_WRITE_SETTINGS);
        }
    }

    public void prepareForSms(boolean isSmsForCustomerCare, String iMessage) {
        this.isSmsForCustomerCare = isSmsForCustomerCare;
        mTicketNumber = String.valueOf(System.currentTimeMillis());
        mMessageString = TextUtils.isEmpty(iMessage) ? mTicketNumber : iMessage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSmsPermission()) {
                sendSms();
            }
        } else {
            sendSms();
        }
    }

    private boolean checkSmsPermission() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.SEND_SMS}, Constants.RC_SEND_SMS);
            }
            return false;
        }
        return true;
    }

    private void sendSms() {
        if (isSmsForCustomerCare) {
            sendSmsForCustomerCare();
        } else {
            sendSmsForTest();
        }
    }

    private void sendSmsForCustomerCare() {
        new CustomerCareDialog(HomeActivity.this, mTicketNumber).show();
        SMSHelper.sendSMS(HomeActivity.this, Constants.CC_PHONE_NUMBER, mMessageString, false);
    }

    private void sendSmsForTest() {
        SMSHelper.sendSMS(HomeActivity.this, Constants.SMS_PHONE_NUMBER, mMessageString, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_WRITE_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
            hasBrightnessPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.RC_WRITE_SETTINGS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasBrightnessPermission = true;
        } else if (requestCode == Constants.RC_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms();
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onDataPrintSuccess() {
        printData();
    }

    @Override
    public void onDataFailure() {
        mProgressDialog.dismiss();
    }

    @Override
    public void savePrintData(String iPrintData) {
        mPrintData.add(iPrintData);
        FileLogger.logData(iPrintData, "print", FLApplication.mFileTimeStamp);


    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }



}
