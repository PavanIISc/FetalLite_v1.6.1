package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.AdminDashboardActivity;
import com.sattvamedtech.fetallite.activity.AdminRegistrationActivity;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.EncryptDecryptHelper;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.helper.SessionHelper;
import com.sattvamedtech.fetallite.model.Admin;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavan on 7/7/2017.
 */

public class ForgotPasswordAdminDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private TextInputLayout mtilAdminConfirmPassword, mtilAdminNewPassword, mtilQuestion, mtilAnswer, mtilAdminPassword;
    private Toolbar mToolbar;
    private EditText mEtAdminID, mEtEmail, mEtPhone, mEtPassword, mEtQuestion, mEtAnswer, mEtNewPassword, mEtConfirmPassword;
    private Button mBCancel, mBSave, mBlogin;
    private TextView mTvAdminForgotPassword;
    //private Hospital mHospital;
    private boolean mChange = false;
    private List<String> mQuestionList = new ArrayList<>();  /*Defect ID: PRO22 is resolved by making question as list and adding spinner */
    private Spinner mSQuestion;


    private Admin mAdmin;
    //private boolean isEdit;

    public ForgotPasswordAdminDialog(Context context){
        super(context);
        mContext = context;
        // mUser = aUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_admin_forgot_password);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        initListeners();
        setViewData();
        initQuestionList();
    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(mContext.getString(R.string.label_forgot_password));
        mtilAdminConfirmPassword = (TextInputLayout)findViewById(R.id.tilAdminConfirmPassword);
        mtilAdminNewPassword = (TextInputLayout)findViewById(R.id.tilAdminNewPassword);
        mtilAnswer = (TextInputLayout)findViewById(R.id.tilAnswer);
        mtilQuestion = (TextInputLayout)findViewById(R.id.tilQuestion);
        mtilAdminPassword = (TextInputLayout)findViewById(R.id.tilAdminPassword);
        //mUser.type = User.TYPE_USER;
        mTvAdminForgotPassword = (TextView) findViewById(R.id.tvAdminForgotPassword);
        mEtAdminID = (EditText) findViewById(R.id.etAdminID);
        mEtEmail = (EditText) findViewById(R.id.etAdminEmail);
        mEtPhone = (EditText) findViewById(R.id.etAdminPhoneNumber);
        mEtPassword = (EditText) findViewById(R.id.etAdminPassword);
        mEtQuestion = (EditText) findViewById(R.id.etQuestion);
        mEtAnswer = (EditText) findViewById(R.id.etAnswer);
        mEtNewPassword = (EditText) findViewById(R.id.etAdminNewPassword);
        mEtConfirmPassword = (EditText) findViewById(R.id.etAdminConfirmPassword);
        mBCancel = (Button) findViewById(R.id.bCancel);
        mBlogin = (Button) findViewById(R.id.bLogin);

        mSQuestion = (Spinner) findViewById(R.id.sAdminQuestion);  /*Defecgt ID resolved by adding spinner */
        mEtQuestion.setVisibility(View.GONE);
        mEtAnswer.setVisibility(View.GONE);
        mEtNewPassword.setVisibility(View.GONE);
        mEtConfirmPassword.setVisibility(View.GONE);
        mSQuestion.setVisibility(View.GONE);
        SpannableString spannableString = new SpannableString(mTvAdminForgotPassword.getText());
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvAdminForgotPassword.setText(spannableString);

    }

    public void setInitViewNextTime(){
        mEtQuestion = (EditText) findViewById(R.id.etQuestion);
        mEtAnswer = (EditText) findViewById(R.id.etAnswer);
        mEtNewPassword = (EditText) findViewById(R.id.etAdminNewPassword);
        mEtConfirmPassword = (EditText) findViewById(R.id.etAdminConfirmPassword);

        mEtQuestion.setVisibility(View.GONE);
        mEtAnswer.setVisibility(View.GONE);
        mEtNewPassword.setVisibility(View.GONE);
        mEtConfirmPassword.setVisibility(View.GONE);
    }

    private void initListeners() {
        mTvAdminForgotPassword.setOnClickListener(this);
        mBCancel.setOnClickListener(this);
        mBlogin.setOnClickListener(this);
    }

    public void setViewData(){
        mAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);


        if (mAdmin != null) {
            //Log.e("Username:", " " + mUser.username);
            mEtAdminID.setText(mAdmin.adminid);
            //mEtAdminID.setEnabled(false);  /* Defect ID: PRO20 is resolved */
            mEtEmail.setText(mAdmin.email);
            mEtPhone.setText(mAdmin.phonenumber);
        }

        Log.e("Reset to ; ","initial view");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tvAdminForgotPassword){
            setView();              /*Defect ID: PRO21 resolved */
            mChange = true;
        }
        if (view.getId() == R.id.bCancel) {
            clearfields();
            ForgotPasswordAdminDialog.this.dismiss();

        }
        else if(view.getId() == R.id.bLogin && !mChange){
            adminLogInandProceed();
            clearfields();
        }
        else if(view.getId() == R.id.bLogin && mChange){
            mEtQuestion.setText(mSQuestion.getSelectedItem().toString().trim());  /* defect ID: PRO22 is resolved by adding spinner */
            validateQuestionandAnswer();

        }


//        else if (view.getId() == R.id.bSave) {
//            validateAndSave();
//        }
    }

    public void setView(){
        mEtQuestion.setEnabled(true);          /*Defect ID: PRO22 resolved  */
        mTvAdminForgotPassword.setVisibility(View.GONE);
        mEtPassword.setVisibility(View.GONE);
        mtilAdminPassword.setVisibility(View.GONE);
        mtilAdminNewPassword.setVisibility(View.VISIBLE);
        mtilAdminConfirmPassword.setVisibility(View.VISIBLE);
        mtilQuestion.setVisibility(View.VISIBLE);
        mtilAnswer.setVisibility(View.VISIBLE);

        mSQuestion.setVisibility(View.VISIBLE);
        //mEtQuestion.setVisibility(View.VISIBLE);  /* Defect ID: PRO20 is resolved */
        mEtAnswer.setVisibility(View.VISIBLE);    /* Defect ID: PRO20 is resolved */
        mEtNewPassword.setVisibility(View.GONE);
        mEtConfirmPassword.setVisibility(View.GONE);
        mAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);
        //mEtQuestion.setText(mAdmin.question);
        //mEtQuestion.setText(mSQuestion.getSelectedItem().toString().trim());
        mBlogin.setText("LOGIN");
    }

    private void initQuestionList(){
        mQuestionList.add("What was your Mother's maiden name ?");
        mQuestionList.add("What is your home town ?");
        mQuestionList.add("How many languages do you speak ?");

        ArrayAdapter<String> QuestionAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner,mQuestionList);
        mSQuestion.setAdapter(QuestionAdapter);
    }
    private void validateQuestionandAnswer(){
        /* Defect ID: PRO22 is resolved
         * Checked the answer against the answer in */

        if(!mEtAnswer.getText().toString().trim().toLowerCase().equals(mAdmin.answer.toLowerCase())){
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_invaild_question_answer), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtAnswer.requestFocus();
                }
            });
        }
        else{
//            if(validateAdminNewPassword()) {
//                resetAdminPassword();
//                proceedToAdminDashboard();
//            }
            /* uncomment below three lines if you want to get into admin details activity */
            ForgotPasswordAdminDialog.this.dismiss();
            Intent aIntent = new Intent(mContext, AdminRegistrationActivity.class);
            aIntent.putExtra("FromMenu", 1);
            mContext.startActivity(aIntent);

        }

    }

    private boolean validateAdminNewPassword(){
        if (mChange && mEtNewPassword.getText().toString().trim().length() < Constants.MIN_PASSWD_LENGTH) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_min_length, Constants.MIN_PASSWD_LENGTH), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtNewPassword.requestFocus();
                }
            });
            return false;
        }
        else if (mChange && !mEtNewPassword.getText().toString().equals(mEtConfirmPassword.getText().toString())) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_password_mismatch), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtNewPassword.requestFocus();
                }
            });
            return false;
        }


        return true;

    }
    private void resetAdminPassword(){
        //Log.e("AdminID:", " " + mEtAdminID.getText());
        String aPassword;
        aPassword = mAdmin.password;
        if(mChange) {
            aPassword = EncryptDecryptHelper.encryptIt(mEtNewPassword.getText().toString().trim());
            mAdmin.password = aPassword;

            //Log.e("EditAdminDialog", "Inside Save: password");
        }


        FLPreferences.getInstance(FLApplication.getInstance()).setAdminUser(GsonHelper.toAdminJson(mAdmin));
        FLPreferences.getInstance(FLApplication.getInstance()).setAdminPassword(aPassword);
//        //DatabaseHelper.getInstance(mContext.getApplicationContext()).updateAdmin(mUser);
//        new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.complete_update),  mContext.getString(R.string.complete_admin_update), mContext.getString(R.string.action_ok), new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                ForgotPasswordAdminDialog.this.dismiss();
//            }
//        });

    }

    private void proceedToAdminDashboard(){
        ForgotPasswordAdminDialog.this.dismiss();
        Intent aIntent = new Intent(mContext, AdminDashboardActivity.class);
        mContext.startActivity(aIntent);
        clearfields();
    }


    private void adminLogInandProceed(){
        String aEncryptedPassword = EncryptDecryptHelper.encryptIt(mEtPassword.getText().toString().trim());
        Admin aAdminUser = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);

        if (mEtAdminID.getText().toString().trim().toLowerCase().equals(aAdminUser.adminid.toLowerCase()) && aEncryptedPassword.equals(aAdminUser.password)) {
            SessionHelper.clearSession();
            ForgotPasswordAdminDialog.this.dismiss();
            Intent aIntent = new Intent(mContext, AdminDashboardActivity.class);
            mContext.startActivity(aIntent);
            //new ChooseHospitalDialog(getContext()).show();
        } else {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_invalid_credentials), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtAdminID.requestFocus();
                }
            });
        }

    }



    private boolean validParams() {
        String aEncryptedPassword = EncryptDecryptHelper.encryptIt(mEtPassword.getText().toString().trim());
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
        } else if (mChange && !mAdmin.password.equals(aEncryptedPassword) ){ //DatabaseHelper.getInstance(FLApplication.getInstance()).checkAdminOldPassword(mUser.username, mEtOldPassword.getText().toString().trim()) == null) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_old_password_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mEtPassword.requestFocus();
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

    public void save(){
        Log.e("AdminID:", " " + mEtAdminID.getText());
        String aPassword;
        mAdmin.email = mEtEmail.getText().toString().trim();
        mAdmin.phonenumber = mEtPhone.getText().toString().trim();
        aPassword = mAdmin.password;
        if(mChange) {
            aPassword = EncryptDecryptHelper.encryptIt(mEtNewPassword.getText().toString().trim());
            mAdmin.password = aPassword;
            Log.e("EditAdminDialog", "Inside Save: password");
        }

        FLPreferences.getInstance(FLApplication.getInstance()).setAdminUser(GsonHelper.toAdminJson(mAdmin));
        FLPreferences.getInstance(FLApplication.getInstance()).setAdminPassword(aPassword);
        //DatabaseHelper.getInstance(mContext.getApplicationContext()).updateAdmin(mUser);
        new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.complete_update),  mContext.getString(R.string.complete_admin_update), mContext.getString(R.string.action_ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ForgotPasswordAdminDialog.this.dismiss();
            }
        });
        clearfields();
    }

    private  void clearfields(){
        mChange = false;
        mBlogin.setText("LOGIN");

        mEtConfirmPassword.getText().clear();
        mEtAnswer.getText().clear();
        mEtNewPassword.getText().clear();
        mEtPassword.getText().clear();
        mEtEmail.getText().clear();
        mEtAdminID.requestFocus();
        mAdmin = null;
    }



}
