package com.sattvamedtech.fetallite.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.EncryptDecryptHelper;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.Admin;
import com.sattvamedtech.fetallite.model.Doctor;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;
import com.sattvamedtech.fetallite.storage.FLPreferences;

public class HospitalRegistrationActivity extends FLBaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTvInfo;
    private EditText mEtHospitalName, mEtPhoneNumber, mEtEmail, mEtAddress;
    private Button mBNext;

    private Hospital mHospital;
    private boolean isEdit, mIsAdminAsDoctor, mIsAdminAsUser;

    private User mUser;
    private Doctor mDoctor;
    Admin aAdmin;

    String mAdminID, mAdminFName, mAdminPassword, mAdminEmail, mAdminPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_registration);

        mIsAdminAsDoctor = FLPreferences.getInstance(FLApplication.getInstance()).getAdminAsDoctor();
        mIsAdminAsUser = FLPreferences.getInstance(FLApplication.getInstance()).getAdminAsUser();

        initToolbar();
        initView();
        initListeners();
    }

    private void initToolbar() {
        Toolbar aToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(aToolbar);
    }

    private void initView() {
        mTvInfo = (TextView) findViewById(R.id.tvInfo);
        mEtHospitalName = (EditText) findViewById(R.id.etHospitalName);
        mEtPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        mEtEmail = (EditText) findViewById(R.id.etEmail);
        mEtAddress = (EditText) findViewById(R.id.etAddress);

        mBNext = (Button) findViewById(R.id.bNext);

        SpannableString spannableString = new SpannableString(mTvInfo.getText());

        int startIndex = mTvInfo.getText().toString().indexOf("Hospital");
        int endIndex = mTvInfo.getText().toString().indexOf("in the Menu");

        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.black)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvInfo.setText(spannableString);
    }

    private void initListeners() {
        mBNext.setOnClickListener(this);
    }




    @Override
    public void onClick(View view) {
        validateAndSave();
    }

    private void validateAndSave() {
        if (validParams()) {
            save();
        }
    }

    private boolean validParams() {
        if (TextUtils.isEmpty(mEtHospitalName.getText().toString().trim())) {
            new MessageHelper(this).showTitleAlertOk(this.getString(R.string.error_invalid_data), this.getString(R.string.error_hospital_name_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtHospitalName.requestFocus();
                }
            });
            return false;
        } else if (!mEtHospitalName.getText().toString().trim().matches("[a-zA-Z .]+")) {
            new MessageHelper(this).showTitleAlertOk(this.getString(R.string.error_invalid_data), this.getString(R.string.error_name), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtHospitalName.requestFocus();
                }
            });
            return false;
        }else if (TextUtils.isEmpty(mEtPhoneNumber.getText().toString().trim())) {
            new MessageHelper(this).showTitleAlertOk(this.getString(R.string.error_invalid_data), this.getString(R.string.error_phone_number_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPhoneNumber.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtEmail.getText().toString().trim())) {
            new MessageHelper(this).showTitleAlertOk(this.getString(R.string.error_invalid_data), this.getString(R.string.error_email_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtEmail.requestFocus();
                }
            });
            return false;
        } else if (!TextUtils.isEmpty(mEtEmail.getText().toString().trim()) && !Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText().toString().trim()).matches()) {
            new MessageHelper(this).showTitleAlertOk(this.getString(R.string.error_invalid_data), this.getString(R.string.error_invalid_email), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtEmail.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
            new MessageHelper(this).showTitleAlertOk(this.getString(R.string.error_invalid_data), this.getString(R.string.error_address_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtAddress.requestFocus();
                }
            });
            return false;
        }else if (mEtPhoneNumber.getText().toString().trim().length() != 10 && mEtPhoneNumber.getText().toString().trim().length() != 11) {
            new MessageHelper(this).showTitleAlertOk(this.getString(R.string.error_invalid_data), this.getString(R.string.error_phone_length), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPhoneNumber.requestFocus();
                }
            });
            return false;
        }

        return true;
    }

    private void save() {
        /*Storing admin as User if Admin has selected to conduct test
         */
        Hospital aHospital = new Hospital(mEtHospitalName.getText().toString().trim().toLowerCase(),
                mEtPhoneNumber.getText().toString().trim(),
                mEtEmail.getText().toString().trim(),
                mEtAddress.getText().toString().trim().toLowerCase());

        DatabaseHelper.getInstance(getApplicationContext()).addHospital(aHospital);
        ApplicationUtils.mHospitalID = aHospital.hospitalId;
        ApplicationUtils.mHospital = aHospital;

        new MessageHelper(this).showTitleAlertOk(getString(R.string.success_registration), getString(R.string.success_hospital_add), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();

                Intent newIntent = new Intent(HospitalRegistrationActivity.this, AdminRegistrationActivity.class);
                startActivity(newIntent);
            }
        });

        if(mIsAdminAsUser){
            Log.e("AddHospital","AdminAsAUser");
            mHospital = aHospital;
            aAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);
            if (aAdmin != null) {
                mAdminID = aAdmin.adminid;
                mAdminFName = aAdmin.name;
                mAdminEmail = aAdmin.email;
                mAdminPassword = aAdmin.password;
                mAdminPhone = aAdmin.phonenumber;
                Log.e("AddHospital","ID" + mAdminID);
            }
            String aPassword = EncryptDecryptHelper.encryptIt(mAdminPassword.toString().trim());
            if (mUser == null) {
                mUser = new User(mAdminID.toString().trim(),
                        mAdminFName.toString().trim(),
                        null,
                        aPassword,
                        mAdminEmail.toString().trim(),
                        mAdminPhone.toString().toString(),
                        true,
                        mHospital);
                DatabaseHelper.getInstance(this).addUser(mUser);
                Log.e("AddHospital","User Added to db");
            }
        }
        if(mIsAdminAsDoctor){
            Log.e("AddHospital","AdminAsADoctor");
            //mHospital = ApplicationUtils.mHospital;
            aAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);
            Log.e("AddHospital","Admin " + aAdmin.toString());
            if (aAdmin != null) {
                mAdminFName = aAdmin.name;
                mAdminEmail = aAdmin.email;
                mAdminPhone = aAdmin.phonenumber;
                Log.e("AddHospital","name" + mAdminFName);
            }
            if (mDoctor == null) {
                mDoctor = new Doctor(mAdminFName.toString().trim().toLowerCase(),
                        mAdminEmail.toString().trim(),
                        mAdminPhone.toString().trim(),
                        true,
                        mHospital
                );
                DatabaseHelper.getInstance(this).addDoctor(mDoctor);
                Log.e("AddHospital","Doctor Added to db");
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
