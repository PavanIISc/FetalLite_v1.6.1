package com.sattvamedtech.fetallite.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.EncryptDecryptHelper;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.helper.SMSHelper;
import com.sattvamedtech.fetallite.model.Admin;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.util.ArrayList;
import java.util.List;

public class AdminRegistrationActivity extends FLBaseActivity implements View.OnClickListener {

    private TextInputEditText mEtLoginId, mEtPassword, mEtConfirmPassword, mEtPhoneNumber, mEtEmail,mEtAdminName, mEtAdminLastName, mEtAnswer;
    private Spinner mSQuestion;
    private Button mBNext;
    private String mSmsMessage;
    private String mHospitalID;
    private int mNavigateFromMenu = 0;
    Admin mAdmin;
    Intent adminIntent;
    String mMenuStringMatch = "FromMenu";
    private List<String> mQuestionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);

        initToolbar();
        initView();
        initListeners();
        initQuestionList();
    }

    private void initQuestionList(){
        mQuestionList.add("What was your Mother's maiden name ?");
        mQuestionList.add("What is your home town ?");
        mQuestionList.add("How many languages do you speak ?");

        ArrayAdapter<String>QuestionAdapter = new ArrayAdapter<>(this,R.layout.item_spinner,mQuestionList);
        mSQuestion.setAdapter(QuestionAdapter);
    }

    private void initToolbar() {
        Toolbar aToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(aToolbar);
    }

    private void initView() {
        mEtLoginId = (TextInputEditText) findViewById(R.id.etLoginId);
        mEtAdminName = (TextInputEditText) findViewById(R.id.etAdminName);
        mEtAdminLastName = (TextInputEditText) findViewById(R.id.etAdminLastName);
        mEtPassword = (TextInputEditText) findViewById(R.id.etPassword);
        mEtConfirmPassword = (TextInputEditText) findViewById(R.id.etConfirmPassword);
        mEtPhoneNumber = (TextInputEditText) findViewById(R.id.etPhoneNumber);
        mEtEmail = (TextInputEditText) findViewById(R.id.etEmail);
        mSQuestion = (Spinner) findViewById(R.id.sQuestion);
        mEtAnswer = (TextInputEditText) findViewById(R.id.etAnswer);
        mBNext = (Button) findViewById(R.id.bNext);

        adminIntent = getIntent();
        mNavigateFromMenu = adminIntent.getIntExtra("FromMenu", -1);
        Log.e("AdminRegistration","" + mNavigateFromMenu);
        if(mMenuStringMatch != null && mNavigateFromMenu == 1){
            Log.e("AdminRegistration","Inside if");
            mAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);
            if (mAdmin != null) {
                String aDecryptedPassword = EncryptDecryptHelper.decryptIt(mAdmin.password.toString().trim());
                mEtLoginId.setText(mAdmin.adminid);
                mEtAdminName.setText(mAdmin.name);
                mEtAdminLastName.setText(mAdmin.lname);
                mEtPassword.setText(aDecryptedPassword);
                mEtConfirmPassword.setText(aDecryptedPassword);
                mEtPhoneNumber.setText(mAdmin.phonenumber);
                mEtEmail.setText(mAdmin.email);
                mEtAnswer.setText(mAdmin.answer);
                Log.e("AdminRegistration","" + mAdmin.name.toString());
                Log.e("AdminRegistration","" + aDecryptedPassword);
            }
            ApplicationUtils.mFromMenu = mNavigateFromMenu;
        }
        else{
            ApplicationUtils.mFromMenu = -1;
        }
          //Toast.makeText(getApplicationContext(), "ID " + ApplicationUtils.mHospitalID, Toast.LENGTH_LONG).show();

    }

    private void initListeners() {
        mBNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bNext) {
            if (validParams()) {
                String aEncryptedPassword = EncryptDecryptHelper.encryptIt(mEtPassword.getText().toString().trim());
                Admin aAdmin = new Admin(mEtLoginId.getText().toString().trim().toLowerCase(),mEtAdminName.getText().toString().trim(), mEtAdminLastName.getText().toString().trim(), aEncryptedPassword, mEtPhoneNumber.getText().toString().trim(), mEtEmail.getText().toString().trim(), mSQuestion.getSelectedItem().toString().trim(), mEtAnswer.getText().toString().trim());
                FLPreferences.getInstance(FLApplication.getInstance()).setAdminUser(GsonHelper.toAdminJson(aAdmin));
                FLPreferences.getInstance(FLApplication.getInstance()).setAdminPassword(aEncryptedPassword);
                String aDeviceId = "deviceId-" + "SFL0XX"; //+ UUID.randomUUID().toString();
                FLPreferences.getInstance(FLApplication.getInstance()).setDeviceId(aDeviceId);
                long aTimeStamp = System.currentTimeMillis();
                FLPreferences.getInstance(FLApplication.getInstance()).setRegistrationDate(aTimeStamp);
                FLPreferences.getInstance(FLApplication.getInstance()).setLastServiceDate(aTimeStamp);
                prepareForSms();
                Intent aIntent = new Intent(AdminRegistrationActivity.this, AdminDetailsActivity.class);
//                finish();
                //aIntent.putExtra("HospitalID", ApplicationUtils.mHospitalID);
                aIntent.putExtra("AdminID",mEtLoginId.getText().toString());
                aIntent.putExtra("AdminPassword",mEtPassword.getText().toString());
                aIntent.putExtra("AdminFirstName", mEtAdminName.getText().toString() + " " + mEtAdminLastName.getText().toString());
                aIntent.putExtra("AdminEmail", mEtEmail.getText().toString());
                aIntent.putExtra("AdminPhone", mEtPhoneNumber.getText().toString());

                if(mNavigateFromMenu == 1){
                    aIntent.putExtra("FromMenu",mNavigateFromMenu);
                }
                startActivity(aIntent);

                //showRegistrationConfirmation();
            }

        }
    }

    private boolean validParams() {
        if (TextUtils.isEmpty(mEtLoginId.getText().toString().trim())) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_login_id_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtLoginId.requestFocus();
                }
            });
            return false;
        }else if (!mEtLoginId.getText().toString().trim().matches("[a-zA-Z0-9]+")) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_login_id_invalid), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtLoginId.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtAdminName.getText().toString().trim())) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_admin_name_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtLoginId.requestFocus();
                }
            });
            return false;
        } else if (!mEtAdminName.getText().toString().trim().matches("[a-zA-Z .]+")) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_admin_name_invalid), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtLoginId.requestFocus();
                }
            });
            return false;
        }else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_password_required,Constants.MIN_PASSWD_LENGTH), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPassword.requestFocus();
                }
            });
            return false;
        } else if (mEtPassword.getText().toString().trim().length() < Constants.MIN_PASSWD_LENGTH) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_password_min_length, Constants.MIN_PASSWD_LENGTH), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPassword.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtConfirmPassword.getText().toString().trim())) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_confirm_password_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtConfirmPassword.requestFocus();
                }
            });
            return false;
       }
//          else if (mEtConfirmPassword.getText().toString().trim().length() < Constants.MIN_PASSWD_LENGTH) {
//            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_confirm_password_min_length, Constants.MIN_PASSWD_LENGTH), "", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                    mEtConfirmPassword.requestFocus();
//                }
//            });
//            return false;
//        }
        else if (!mEtPassword.getText().toString().equals(mEtConfirmPassword.getText().toString())) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_password_mismatch), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPassword.requestFocus();
                }
            });
            return false;
        }else if (TextUtils.isEmpty(mEtEmail.getText().toString().trim()) ) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_invalid_email), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtEmail.requestFocus();
                }
            });
            return false;
        } else if ( !Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText().toString().trim()).matches()) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_invalid_email), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtEmail.requestFocus();
                }
            });
            return false;
        }else if (mEtPhoneNumber.getText().toString().trim().length() != 10 && mEtPhoneNumber.getText().toString().trim().length() != 11 && mEtPhoneNumber.getText().toString().trim().length() != 0) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_phone_length), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPhoneNumber.requestFocus();
                }
            });
            return false;
        }

        else if( !Patterns.PHONE.matcher(mEtPhoneNumber.getText().toString().trim()).matches()) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_phone_length), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPhoneNumber.requestFocus();
                }
            });
            return false;
        }else if (TextUtils.isEmpty(mEtAnswer.getText().toString().trim())) {
            new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_answer_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtLoginId.requestFocus();
                }
            });
            return false;
        }
        return true;
    }

    private void prepareForSms() {
        mSmsMessage = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSmsPermission()) {
                SMSHelper.sendSMS(AdminRegistrationActivity.this, Constants.SMS_PHONE_NUMBER, mSmsMessage, false);
            }
        } else {
            SMSHelper.sendSMS(AdminRegistrationActivity.this, Constants.SMS_PHONE_NUMBER, mSmsMessage, false);
        }
    }

    private boolean checkSmsPermission() {
        if (ActivityCompat.checkSelfPermission(AdminRegistrationActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(AdminRegistrationActivity.this, new String[]{Manifest.permission.SEND_SMS}, Constants.RC_SEND_SMS);
            }
            return false;
        }
        return true;
    }

    private void showRegistrationConfirmation() {
        new MessageHelper(AdminRegistrationActivity.this).showTitleAlertOk(getString(R.string.success_registration), getString(R.string.success_thank_you), getString(R.string.action_next), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openAdminDashboard(dialogInterface);
            }
        });
    }

    private void openAdminDashboard(DialogInterface iDialogInterface) {
        iDialogInterface.dismiss();
        Intent aIntent = new Intent(AdminRegistrationActivity.this, AdminDashboardActivity.class);

        finish();
        startActivity(aIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.RC_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SMSHelper.sendSMS(AdminRegistrationActivity.this, Constants.SMS_PHONE_NUMBER, mSmsMessage, false);
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
