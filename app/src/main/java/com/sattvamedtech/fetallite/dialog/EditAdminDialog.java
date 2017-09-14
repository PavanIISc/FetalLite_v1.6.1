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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.AdminRegistrationActivity;
import com.sattvamedtech.fetallite.fragment.AdminDashboardFragment;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.EncryptDecryptHelper;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.Admin;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;
import com.sattvamedtech.fetallite.storage.FLPreferences;

/**
 * Created by Pavan on 6/29/2017.
 */

public class EditAdminDialog extends Dialog implements OnClickListener {

    private Context mContext;

    private Toolbar mToolbar;
    private EditText mEtAdminID, mEtEmail, mEtPhone, mEtOldPassword, mEtNewPassword, mEtConfirmPassword;
    private Button mBChangePwd, mBCancel, mBSave, mBReset;
    //private Hospital mHospital;
    private boolean mChange = false;


    private Admin mAdmin;
    //private boolean isEdit;

    public EditAdminDialog(Context context){
        super(context);
        mContext = context;
       // mUser = aUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_admin_edit);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        initListeners();
        setViewData();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(mContext.getString(R.string.label_edit_admin_details));

        //mUser.type = User.TYPE_USER;

        mEtAdminID = (EditText) findViewById(R.id.etAdminID);
        mEtEmail = (EditText) findViewById(R.id.etAdminEmail);
        mEtPhone = (EditText) findViewById(R.id.etAdminPhoneNumber);
        mEtOldPassword = (EditText) findViewById(R.id.etAdminOldPassword);
        mEtNewPassword = (EditText) findViewById(R.id.etAdminNewPassword);
        mEtConfirmPassword = (EditText) findViewById(R.id.etAdminConfirmPassword);
        mBReset = (Button) findViewById(R.id.bReset);

        mBChangePwd = (Button) findViewById(R.id.bChangePwd);
        mBCancel = (Button) findViewById(R.id.bCancel);
        mBSave = (Button) findViewById(R.id.bSave);

        mEtOldPassword.setVisibility(View.GONE);
        mEtNewPassword.setVisibility(View.GONE);
        mEtConfirmPassword.setVisibility(View.GONE);

    }

    private void initListeners() {
        mBChangePwd.setOnClickListener(this);
        mBCancel.setOnClickListener(this);
        mBSave.setOnClickListener(this);
        mBReset.setOnClickListener(this);
    }

    public void setViewData(){
        mAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);
        if (mAdmin != null) {
            //Log.e("Username:", " " + mUser.username);
            mEtAdminID.setText(mAdmin.adminid);
            mEtAdminID.setEnabled(false);
            mEtEmail.setText(mAdmin.email);
            mEtPhone.setText(mAdmin.phonenumber);
       }
    }

    public void setInitView(){

        mBChangePwd.setVisibility(View.VISIBLE);
        mEtOldPassword.setVisibility(View.GONE);
        mEtNewPassword.setVisibility(View.GONE);
        mEtConfirmPassword.setVisibility(View.GONE);
        setViewData();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bChangePwd){
            setView();
            mChange = true;
        }
        if (view.getId() == R.id.bCancel) {
            EditAdminDialog.this.dismiss();
        } else if (view.getId() == R.id.bSave) {
            validateAndSave();
        }else if(view.getId() == R.id.bReset){

            Intent aIntent = new Intent(mContext, AdminRegistrationActivity.class);
            EditAdminDialog.this.dismiss();
            mContext.startActivity(aIntent);
        }
    }

    public void setView(){
        mBChangePwd.setVisibility(View.GONE);
        mEtOldPassword.setVisibility(View.VISIBLE);
        mEtNewPassword.setVisibility(View.VISIBLE);
        mEtConfirmPassword.setVisibility(View.VISIBLE);
    }

    public void validateAndSave(){
        if (validParams()) {
           save();
        }
    }

    private boolean validParams() {
        String aEncryptedOldPassword = EncryptDecryptHelper.encryptIt(mEtOldPassword.getText().toString().trim());
        //String aDecryptedPassword = EncryptDecryptHelper.decryptIt(mUser.password);

        String aEncryptedNewPassword = EncryptDecryptHelper.encryptIt(mEtNewPassword.getText().toString().trim());
        String aEncryptedConfirmPassword = EncryptDecryptHelper.encryptIt(mEtConfirmPassword.getText().toString().trim());
        if (TextUtils.isEmpty(mEtAdminID.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_login_id_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtAdminID.requestFocus();
                }
            });
            return false;
        } else if (mChange && !mAdmin.password.equals(aEncryptedOldPassword) ){ //DatabaseHelper.getInstance(FLApplication.getInstance()).checkAdminOldPassword(mUser.username, mEtOldPassword.getText().toString().trim()) == null) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_old_password_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtOldPassword.requestFocus();
                }
            });
            return false;
        }else if (mChange && TextUtils.isEmpty(mEtNewPassword.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtNewPassword.requestFocus();
                }
            });
            return false;
        } else if (mChange && mEtNewPassword.getText().toString().trim().length() < Constants.MIN_PASSWD_LENGTH) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_min_length, Constants.MIN_PASSWD_LENGTH), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtNewPassword.requestFocus();
                }
            });
            return false;
        } else if (mChange && TextUtils.isEmpty(mEtConfirmPassword.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_confirm_password_required), "", new OnClickListener() {
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
        else if (mChange && !mEtNewPassword.getText().toString().equals(mEtConfirmPassword.getText().toString())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_mismatch), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtNewPassword.requestFocus();
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
        }else if (mEtPhone.getText().toString().trim().length() != 10 && mEtPhone.getText().toString().trim().length() != 11) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_phone_length), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPhone.requestFocus();
                }
            });
            return false;
        }

        else if( !Patterns.PHONE.matcher(mEtPhone.getText().toString().trim()).matches()) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_phone_length), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPhone.requestFocus();
                }
            });
            return false;
        }
        return true;
    }

    public void clearFields() {

        mEtAdminID.requestFocus();
        mAdmin = null;
        mChange = false;

    }


    public void save(){
        //Log.e("AdminID:", " " + mEtAdminID.getText());
        String aPassword;
        mAdmin.email = mEtEmail.getText().toString().trim();
        mAdmin.phonenumber = mEtPhone.getText().toString().trim();
        aPassword = mAdmin.password;
        if(mChange) {
            aPassword = EncryptDecryptHelper.encryptIt(mEtNewPassword.getText().toString().trim());
            mAdmin.password = aPassword;
            //Log.e("EditAdminDialog", "Inside Save: password");
         }
        FLPreferences.getInstance(FLApplication.getInstance()).setAdminUser(GsonHelper.toAdminJson(mAdmin));
        FLPreferences.getInstance(FLApplication.getInstance()).setAdminPassword(aPassword);
        //DatabaseHelper.getInstance(mContext.getApplicationContext()).updateAdmin(mUser);
        new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.complete_update),  mContext.getString(R.string.complete_admin_update), mContext.getString(R.string.action_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                EditAdminDialog.this.dismiss();
            }
        });
    }


}
