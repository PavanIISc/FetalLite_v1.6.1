package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.fragment.HospitalDetailsFragment;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;
import com.sattvamedtech.fetallite.helper.EncryptDecryptHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.User;

public class AddUserDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private Toolbar mToolbar;
    private EditText mEtUserid, mEtFname, mEtLname, mEtEmail,mEtPhoneNumber, mEtPassword, mEtConfirmPassword;
    private Button mBCancel, mBSave;
    private Hospital mHospital;


    private User mUser;
    private boolean isEdit;

    public AddUserDialog(Context context, Hospital iHospital) {
        super(context);
        mContext = context;
        mHospital = iHospital;
    }


    public AddUserDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AddUserDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_user);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        initListeners();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(mContext.getString(R.string.label_add_user));

        mEtUserid = (EditText) findViewById(R.id.etUserId);
        mEtFname = (EditText) findViewById(R.id.etUserFname);
        mEtLname = (EditText) findViewById(R.id.etUserLname);
        mEtEmail = (EditText) findViewById(R.id.etUserEmail);
        mEtPhoneNumber = (EditText) findViewById(R.id.etUserPhoneNumber);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mEtConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        mBCancel = (Button) findViewById(R.id.bCancel);
        mBSave = (Button) findViewById(R.id.bSave);

    }

    private void initListeners() {
        mBCancel.setOnClickListener(this);
        mBSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bCancel) {
            AddUserDialog.this.dismiss();
        } else if (view.getId() == R.id.bSave) {
            validateAndSave();
        }
    }

    private void validateAndSave() {
        if (validParams()) {
            if(isEdit)
                update();
            else
                save();
        }
    }


    private boolean validParams() {
        if (TextUtils.isEmpty(mEtUserid.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_userid_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtUserid.requestFocus();
                }
            });
            return false;
        }else if (!mEtUserid.getText().toString().trim().matches("[a-zA-Z0-9]+")) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_userid_invalid), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtUserid.requestFocus();
                }
            });
            return false;
        }else if (TextUtils.isEmpty(mEtFname.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_user_fname_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtUserid.requestFocus();
                }
            });
            return false;
        } else if (!isEdit && DatabaseHelper.getInstance(FLApplication.getInstance()).usernameExists(mEtUserid.getText().toString().trim().toLowerCase(),mHospital)) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_username_exists), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtUserid.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_required), ""+Constants.MIN_PASSWD_LENGTH, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPassword.requestFocus();
                }
            });
            return false;
        } else if (mEtPassword.getText().toString().trim().length() < Constants.MIN_PASSWD_LENGTH) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_min_length, Constants.MIN_PASSWD_LENGTH), "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPassword.requestFocus();
                }
            });
            return false;
        } else if (TextUtils.isEmpty(mEtConfirmPassword.getText().toString().trim())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_email_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtConfirmPassword.requestFocus();
                }
            });
            return false;
        }else if (!mEtFname.getText().toString().trim().matches("[a-zA-Z .]*")) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_fname), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtConfirmPassword.requestFocus();
                }
            });
            return false;
        }else if (!TextUtils.isEmpty(mEtLname.getText().toString().trim()) && !mEtLname.getText().toString().trim().matches("[a-zA-Z .]*")) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_lname), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtConfirmPassword.requestFocus();
                }
            });
            return false;
        }
//        else if (mEtConfirmPassword.getText().toString().trim().length() < Constants.MIN_PASSWD_LENGTH) {
////            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_confirm_password_min_length, Constants.MIN_PASSWD_LENGTH), "", new DialogInterface.OnClickListener() {
////                @Override
////                public void onClick(DialogInterface dialogInterface, int i) {
////                    dialogInterface.dismiss();
////                    mEtConfirmPassword.requestFocus();
////                }
////            });
//            return false;
//        }
        else if (!mEtPassword.getText().toString().equals(mEtConfirmPassword.getText().toString())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_mismatch), "", new DialogInterface.OnClickListener() {
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

    private void save() {
        String aPassword = EncryptDecryptHelper.encryptIt(mEtPassword.getText().toString().trim());
        if (mUser == null) {
//            mUser = new User(mEtUsername.getText().toString().trim(),
//                    aPassword,
//                    null,
//                    null,
//                    User.TYPE_USER,
//                    true,
//                    mHospital);
            Log.e("AddUserDialog : ","Inside Save");
            mUser = new User (mEtUserid.getText().toString().trim(),
                    mEtFname.getText().toString().trim(),
                    mEtLname.getText().toString().trim(),
                    aPassword,
                    mEtEmail.getText().toString().trim(),
                    mEtPhoneNumber.getText().toString().toString(),
                    true,
                    mHospital);
        }
        Log.e("AddUserDialog : ","Before Database call");
        DatabaseHelper.getInstance(mContext.getApplicationContext()).addUser(mUser);

        new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.success_registration),  mContext.getString(R.string.success_user_add), mContext.getString(R.string.action_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                AddUserDialog.this.dismiss();
            }
        });
    }


    private void update()
    {
        mUser.fname = mEtFname.getText().toString().trim();
        mUser.lname = mEtLname.getText().toString().trim();
        mUser.email = mEtEmail.getText().toString().trim();
        mUser.phoneNumber = mEtPhoneNumber.getText().toString().trim();

        mUser.password = EncryptDecryptHelper.encryptIt(mEtPassword.getText().toString().trim());
        DatabaseHelper.getInstance(mContext.getApplicationContext()).addUser(mUser);

        new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.complete_update),  mContext.getString(R.string.success_user_edit), mContext.getString(R.string.action_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                AddUserDialog.this.dismiss();
            }
        });
    }

    public void clearFields() {
        mEtUserid.getText().clear();
        mToolbar.setTitle(mContext.getString(R.string.label_add_user));
        mEtUserid.setEnabled(true);
        mEtPassword.getText().clear();
        mEtConfirmPassword.getText().clear();
        mEtFname.getText().clear();
        mEtLname.getText().clear();
        mEtEmail.getText().clear();
        mEtPhoneNumber.getText().clear();
        mEtUserid.requestFocus();
        mUser = null;
        isEdit = false;
    }

    public void showEdit(User iUser) {
        if (iUser != null) {
            isEdit = true;
            mUser = iUser;
            show();
         mToolbar.setTitle(mContext.getString(R.string.label_edit_user));
            setView();
        }
    }

    private void setView() {
        if (mUser != null) {
            mEtUserid.setText(mUser.userid);
            mEtUserid.setEnabled(!isEdit);
            mEtFname.setText(mUser.fname);
            mEtLname.setText(mUser.lname);
            mEtEmail.setText(mUser.email);
            mEtPhoneNumber.setText(mUser.phoneNumber);
            mEtPassword.setText(EncryptDecryptHelper.decryptIt(mUser.password.toString().trim()));
            mEtConfirmPassword.setText(EncryptDecryptHelper.decryptIt(mUser.password.toString().trim()));
        }
    }
}
