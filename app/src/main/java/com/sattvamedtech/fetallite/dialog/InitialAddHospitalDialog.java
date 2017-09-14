package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.AdminRegistrationActivity;
import com.sattvamedtech.fetallite.activity.DeviceRegistrationActivity;
import com.sattvamedtech.fetallite.activity.TutorialsActivity;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;

/**
 * Created by Pavan on 8/9/2017.
 */

public class InitialAddHospitalDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private Toolbar mToolbar;
    private EditText mEtHospitalName, mEtPhoneNumber, mEtEmail, mEtAddress;
    private Button mBCancel, mBNext;

    public Hospital mHospital;
    private boolean isEdit;

    public InitialAddHospitalDialog(Context context) {
        super(context);
        mContext = context;
    }

    public InitialAddHospitalDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected InitialAddHospitalDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_initial_add_hospital);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
//        Dialog dialog=new Dialog(getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        initView();
        initListeners();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(mContext.getString(R.string.label_register_hospital));

        mEtHospitalName = (EditText) findViewById(R.id.etHospitalName);
        mEtPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        mEtEmail = (EditText) findViewById(R.id.etEmail);
        mEtAddress = (EditText) findViewById(R.id.etAddress);

        mBCancel = (Button) findViewById(R.id.bCancel);
        mBNext = (Button) findViewById(R.id.bNext);
    }

    private void initListeners() {
        mBCancel.setOnClickListener(this);
        mBNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bCancel) {
            InitialAddHospitalDialog.this.dismiss();
        } else if (view.getId() == R.id.bNext) {
            validateAndSave();
        }
    }

    private void validateAndSave() {
        if (validParams()) {
            if (isEdit)
                update();
            else
                save();
        }
    }

    private boolean validParams() {
        if (TextUtils.isEmpty(mEtHospitalName.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_hospital_name_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtHospitalName.requestFocus();
                }
            });
            return false;
        }else if (!mEtHospitalName.getText().toString().trim().matches("[a-zA-Z .]+")) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_name), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtHospitalName.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtPhoneNumber.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_phone_number_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPhoneNumber.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtEmail.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_email_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtEmail.requestFocus();
                }
            });
            return false;
        } else if (!TextUtils.isEmpty(mEtEmail.getText().toString().trim()) && !Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText().toString().trim()).matches()) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_invalid_email), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtEmail.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_address_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtAddress.requestFocus();
                }
            });
            return false;
        }
        return true;
    }

    private void save() {
        final Hospital aHospital = new Hospital(mEtHospitalName.getText().toString().trim(),
                mEtPhoneNumber.getText().toString().trim(),
                mEtEmail.getText().toString().trim(),
                mEtAddress.getText().toString().trim());

        DatabaseHelper.getInstance(mContext.getApplicationContext()).addHospital(aHospital);
        ApplicationUtils.mHospitalID = aHospital.hospitalId;
        ApplicationUtils.mHospital = aHospital;
        new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.success_registration), mContext.getString(R.string.success_hospital_add), mContext.getString(R.string.action_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
                isEdit = false;
                Intent aIntent = new Intent(mContext, AdminRegistrationActivity.class);
//                finish();
                //aIntent.putExtra("HospitalID", aHospital.hospitalId);
                mContext.startActivity(aIntent);
//                InitialAddHospitalDialog.this.dismiss();
            }
        });

        Log.e("InitialAddHos ","HospitalID " + aHospital.hospitalId);
    }

    private void update() {
        mHospital.name = mEtHospitalName.getText().toString().trim();
        mHospital.phoneNumber = mEtPhoneNumber.getText().toString().trim();
        mHospital.email = mEtEmail.getText().toString().trim();
        mHospital.address = mEtAddress.getText().toString().trim();

        DatabaseHelper.getInstance(mContext.getApplicationContext()).addHospital(mHospital);

        new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.success_registration), mContext.getString(R.string.success_hospital_edit), mContext.getString(R.string.action_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                isEdit = false;
                InitialAddHospitalDialog.this.dismiss();
            }
        });
    }

    public void clearFields() {
        mEtHospitalName.getText().clear();
        mEtPhoneNumber.getText().clear();
        mEtEmail.getText().clear();
        mEtAddress.getText().clear();
        mEtHospitalName.requestFocus();
    }

    public void showEdit(Hospital iHospital) {
        if (iHospital != null) {
            isEdit = true;
            mHospital = iHospital;
            show();
            setView();
        }
    }

    private void setView() {
        if (mHospital != null) {
            mEtHospitalName.setText(mHospital.name);
            mEtPhoneNumber.setText(mHospital.phoneNumber);
            mEtEmail.setText(mHospital.email);
            mEtAddress.setText(mHospital.address);
        }
    }
}
