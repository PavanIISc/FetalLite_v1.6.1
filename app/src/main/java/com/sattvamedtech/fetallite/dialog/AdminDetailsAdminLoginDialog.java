package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.AdminDashboardActivity;
import com.sattvamedtech.fetallite.activity.AdminRegistrationActivity;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.EncryptDecryptHelper;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.helper.SessionHelper;
import com.sattvamedtech.fetallite.model.Admin;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.storage.FLPreferences;

/**
 * Created by Pavan on 9/3/2017.
 */

public class AdminDetailsAdminLoginDialog extends Dialog implements View.OnClickListener{
    private Context mContext;

    private Toolbar mToolbar;
    private EditText mEtLoginId, mEtPassword;
    private Button mBCancel, mBLogin;
    private Hospital mHospital;

    public AdminDetailsAdminLoginDialog(Context context) {
        super(context);
        mContext = context;
    }

    public AdminDetailsAdminLoginDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected AdminDetailsAdminLoginDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_admin_login);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        initListeners();
//        Bundle aBundle = new Bundle();
//        String HName = aBundle.getString(Constants.EXTRA_HOSPITAL);
//        Log.e("AdminLogin","hospital" + HName);
//        Intent iIntent = new Intent();
//        int id = iIntent.getIntExtra(Constants.EXTRA_HOSPITAL, -1);
//        Log.e("AdminLogin","hospital" + id);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(mContext.getString(R.string.action_login));

        mEtLoginId = (EditText) findViewById(R.id.etLoginId);
        mEtPassword = (EditText) findViewById(R.id.etPassword);

        mBCancel = (Button) findViewById(R.id.bCancel);
        mBLogin = (Button) findViewById(R.id.bLogin);
    }

    private void initListeners() {
        mBCancel.setOnClickListener(this);
        mBLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bCancel) {
            AdminDetailsAdminLoginDialog.this.dismiss();
        } else if (view.getId() == R.id.bLogin) {
            validateAndLogin();
        }
    }

    private void validateAndLogin() {
        if (validParams()) {
            login();
        }
    }

    private boolean validParams() {
        if (TextUtils.isEmpty(mEtLoginId.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_login_id_required), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtLoginId.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_required, Constants.MIN_PASSWD_LENGTH), "", new DialogInterface.OnClickListener() {
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

    private void login() {
        String aEncryptedPassword = EncryptDecryptHelper.encryptIt(mEtPassword.getText().toString().trim());
        Admin aAdminUser = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);

        if (mEtLoginId.getText().toString().trim().toLowerCase().equals(aAdminUser.adminid.toLowerCase()) && aEncryptedPassword.equals(aAdminUser.password)) {
            SessionHelper.clearSession();
            AdminDetailsAdminLoginDialog.this.dismiss();

            Intent aIntent = new Intent(mContext, AdminRegistrationActivity.class);
            aIntent.putExtra("FromMenu", 1);
            mContext.startActivity(aIntent);
        } else {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_invalid_credentials), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtLoginId.requestFocus();
                }
            });
        }

    }
}
