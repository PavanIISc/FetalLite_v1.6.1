package com.sattvamedtech.fetallite.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.adapter.DeviceAdminAdapter;
import com.sattvamedtech.fetallite.dialog.CustomerCareDialog;
import com.sattvamedtech.fetallite.dialog.ForgotPasswordAdminDialog;
import com.sattvamedtech.fetallite.fragment.DrawerListFragment;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.EncryptDecryptHelper;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.helper.SMSHelper;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.KioskModeApp;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtUsername, mEtPassword;

    private TextView mTvForgotPassword;
    private Button mBLogin;
    private LinearLayout mMenuIcon;
    private LinearLayout mLlToolbar, mLlMenu;
    private Spinner mSHospital;
    private ForgotPasswordAdminDialog forgotPasswordAdminDialog;
    HomeActivity homeActivity;
    private Toolbar mToolbar;

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminRcr;
    protected View mDecorView;


    private FrameLayout mFlNavigationContainer;
    private ActionBarDrawerToggle mToggle;
    private DrawerListFragment mNavigationFragment;
    private boolean hasBrightnessPermission;
    private DrawerLayout mDrawerLayout;
    private Switch mKiosk;

    private List<Hospital> mHospitalList = new ArrayList<>();
    private boolean isSmsForCustomerCare;
    private String mMessageString, mTicketNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        homeActivity = new HomeActivity();
        initView();
        initHospitalList();
        initDrawer();
        initListeners();
        hasBrightnessPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(this);

        if (!hasBrightnessPermission)
            checkBrightnessPermission();
    }



    private void initView() {
        findViewById(R.id.viewUnderline).setVisibility(View.GONE);

        mEtUsername = (EditText) findViewById(R.id.etUsername);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mTvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        mBLogin = (Button) findViewById(R.id.bLogin);
        mSHospital = (Spinner) findViewById(R.id.sHospitalList);
        mKiosk = (Switch) findViewById(R.id.sScreenPin);
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(getApplicationContext().getString(R.string.label_login_page));
        mLlMenu = (LinearLayout) findViewById(R.id.llMenu);

        SpannableString spannableString = new SpannableString(mTvForgotPassword.getText());
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvForgotPassword.setText(spannableString);

        forgotPasswordAdminDialog = new ForgotPasswordAdminDialog(this);
        forgotPasswordAdminDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EditText mPwd = (EditText) forgotPasswordAdminDialog.findViewById(R.id.etAdminPassword);
                TextView mTvForgotPassword = (TextView) forgotPasswordAdminDialog.findViewById(R.id.tvAdminForgotPassword);
                EditText mEtQ = (EditText) forgotPasswordAdminDialog.findViewById(R.id.etQuestion);
                EditText mAns = (EditText) forgotPasswordAdminDialog.findViewById(R.id.etAnswer);
                Button  mBlogin = (Button) forgotPasswordAdminDialog.findViewById(R.id.bLogin);
                TextInputLayout mtilAdminNewPassword = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilAdminNewPassword);
                TextInputLayout mtilAnswer = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilAnswer);
                TextInputLayout mtilQuestion = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilQuestion);
                TextInputLayout mtilAdminPassword = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilAdminPassword);
                TextInputLayout mtilAdminConfirmPassword = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilAdminConfirmPassword);

                mtilAdminPassword.setVisibility(View.VISIBLE);
                mPwd.setVisibility(View.VISIBLE);
                mPwd.setText("");
                mTvForgotPassword.setVisibility(View.VISIBLE);
                mtilAdminNewPassword.setVisibility(View.GONE);
                mtilAnswer.setVisibility(View.GONE);
                mtilQuestion.setVisibility(View.GONE);
                mtilAdminConfirmPassword.setVisibility(View.GONE);
                mEtQ.setVisibility(View.GONE);
                mAns.setVisibility(View.GONE);
                mBlogin.setText("LOGIN");



            }
        });

        mLlMenu.setVisibility(View.VISIBLE);

    }


    private void initDrawer() {
        mFlNavigationContainer = (FrameLayout) findViewById(R.id.flNavigationContainer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.logindrawerLayout);
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
        mNavigationFragment = new DrawerListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flNavigationContainer, mNavigationFragment).commitAllowingStateLoss();

    }

    private void initHospitalList(){
        mHospitalList.addAll(DatabaseHelper.getInstance(getApplicationContext()).getAllHospital());
        if(mHospitalList.size() > 1){
            mHospitalList.add(0,new Hospital("Choose Hospital","","",""));
            changeView();
        }

        ArrayAdapter<Hospital> HospitalAdapter = new ArrayAdapter<>(this,R.layout.item_spinner,mHospitalList);
        mSHospital.setAdapter(HospitalAdapter);
        mSHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) {
                    enableFields();
                    ApplicationUtils.mHospital = mHospitalList.get(position);
                    Log.e("ChooseHospital","" + mHospitalList.get(position).name);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
    }



    private void enableFields(){
        mEtPassword.setEnabled(true);
        mEtUsername.setEnabled(true);
    }

    private void changeView(){
        mEtPassword.setEnabled(false);
        mEtUsername.setEnabled(false);
    }

    private void initListeners() {
        mTvForgotPassword.setOnClickListener(this);
        mBLogin.setOnClickListener(this);

        mLlMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        mKiosk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    confirmKioskStart();
                }
                else
                    confirmKioskStop();
            }
        });
    }

    private void openDrawer() {

        mDrawerLayout.openDrawer(GravityCompat.END);
    }

    public void closeDrawer() {

        mDrawerLayout.closeDrawer(GravityCompat.END);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvForgotPassword) {

            forgotPasswordAdminDialog.show();

        } else if (view.getId() == R.id.bLogin) {
            if (validParams()) {
                loginAndProceed();
            }
        }
    }

    public boolean hasBrightnessPermission() {

        return hasBrightnessPermission;
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.flFragmentContainer);
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

    private void confirmKioskStart(){
        new MessageHelper(LoginActivity.this).showTitleAlertOkCancel(getString(R.string.label_kiosk), getString(R.string.confim_kiosk_start), "", "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startKioskMode();
            }

        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private void confirmKioskStop(){
        new MessageHelper(LoginActivity.this).showTitleAlertOkCancel(getString(R.string.label_kiosk), getString(R.string.confim_kiosk_stop), "", "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                stopKioskMode();
            }

        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private void startKioskMode(){
        Toast.makeText(getApplication(), "Starting Kiosk Mode ....", Toast.LENGTH_SHORT).show();
        turnOnKiosk();
    }

    private void stopKioskMode(){
        Toast.makeText(getApplication(), "Kiosk Mode Stopped ....", Toast.LENGTH_SHORT).show();
        enableKioskMode(false);
    }

    public void turnOnKiosk(){
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminRcr = new ComponentName(this, DeviceAdminAdapter.class);
        if (!mDPM.isAdminActive(mDeviceAdminRcr)) {
            Log.e("Kiosk Mode Error", "not_device_admin");
        }

        if (mDPM.isDeviceOwnerApp(getPackageName())) {
            mDPM.setLockTaskPackages(mDeviceAdminRcr, new String[]{getPackageName()});
        } else {
            Log.e("Kiosk Mode Error", "not_device_owner");
        }

        enableKioskMode(true);
        mDecorView = getWindow().getDecorView();
        // hideSystemUI();
    }

    public void enableKioskMode(boolean enabled) {
        try {
            if (enabled) {
                if (mDPM.isLockTaskPermitted(this.getPackageName())) {
                    KioskModeApp.setIsInLockMode(true);
                    startLockTask();
                } else {
                    KioskModeApp.setIsInLockMode(false);
                    Log.e("Kiosk Mode Error", "kiosk is not permitted");
                }
            } else {
                KioskModeApp.setIsInLockMode(false);
                stopLockTask();
            }
        } catch (Exception e) {
            KioskModeApp.setIsInLockMode(false);
            Log.e("Kiosk Mode Error", e.getMessage());
        }
    }

    private boolean validParams() {
        if (TextUtils.isEmpty(mEtUsername.getText().toString().trim())) {
            new MessageHelper(LoginActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_userid_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtUsername.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            new MessageHelper(LoginActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_password_required, Constants.MIN_PASSWD_LENGTH), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPassword.requestFocus();
                }
            });
            return false;
        }
        return true;
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
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.SEND_SMS}, Constants.RC_SEND_SMS);
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
        new CustomerCareDialog(LoginActivity.this, mTicketNumber).show();
        SMSHelper.sendSMS(LoginActivity.this, Constants.CC_PHONE_NUMBER, mMessageString, false);
    }

    private void sendSmsForTest() {
        SMSHelper.sendSMS(LoginActivity.this, Constants.SMS_PHONE_NUMBER, mMessageString, true);
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

    private void loginAndProceed() {
        String aEncryptedPassword = EncryptDecryptHelper.encryptIt(mEtPassword.getText().toString().trim());
        User aUser = DatabaseHelper.getInstance(FLApplication.getInstance()).validUserCredentials(mEtUsername.getText().toString().trim().toLowerCase(), aEncryptedPassword,(Hospital)mSHospital.getSelectedItem());

        if (aUser == null) {
            new MessageHelper(LoginActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_invalid_credentials), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtUsername.requestFocus();
                }
            });
        } else {
            FLPreferences.getInstance(FLApplication.getInstance()).setLoginSessionTimestamp(System.currentTimeMillis());
            FLPreferences.getInstance(FLApplication.getInstance()).setLoginSessionUser(GsonHelper.toUserJson(aUser));
            Intent aIntent = new Intent(LoginActivity.this, HomeActivity.class);
            finish();
            startActivity(aIntent);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
           //do nothing
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //do nothing
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_MOVE_HOME){
            //do nothing
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
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
