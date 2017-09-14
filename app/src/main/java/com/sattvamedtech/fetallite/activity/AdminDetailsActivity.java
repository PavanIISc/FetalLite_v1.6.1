package com.sattvamedtech.fetallite.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.EncryptDecryptHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.Doctor;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Pavan on 8/9/2017.
 */

public class AdminDetailsActivity extends FLBaseActivity implements View.OnClickListener {

    Button mBNext, mBBack;
    private User mUser, mUpdateUser;
    private Doctor mDoctor;
    private Hospital mHospital;
    String mAdminID, mAdminFName, mAdminPassword, mAdminEmail, mAdminPhone;
    private LinearLayout mLlConductTest, mLlDoctor;
    private TextView mTvConductTestYes, mTvConductTestNo, mTvDoctorYes, mTvDoctorNo;
    Intent intent;
    private Boolean conductTest, doctor;
    private int mMenuValue = 0;
    private boolean mDoNotResetAdmin = false, mAdminExist;

    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<Hospital> mHospitalList = new ArrayList<>();
    private ArrayList<Doctor> mDoctorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_details);
        initToolbar();
        setHospital();

        Log.d("test", "onCreate: is user  " + ApplicationUtils.mIsAdminAsUser);
        Log.d("test", "onCreate: is doctor " + ApplicationUtils.mIsAdminAsDoctor);

       // checkIfNonAdminEnabledUserDoctorExists();

        initView();
        initListeners();
        checkAdminToggleStatus();
        setAndListenToAdminToggle();


    }

    private void initToolbar() {
        Toolbar aToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(aToolbar);
    }

    private void setHospital(){
        mHospital = DatabaseHelper.getInstance(FLApplication.getInstance()).getHospitalById(ApplicationUtils.mHospitalID);
    }
    private void initView()
    {
        intent = getIntent();
        mLlConductTest = (LinearLayout) findViewById(R.id.ll_conduct_test);
        mTvConductTestYes = (TextView) mLlConductTest.findViewById(R.id.tvYes);
        mTvConductTestNo = (TextView) mLlConductTest.findViewById(R.id.tvNo);
        mLlDoctor = (LinearLayout) findViewById(R.id.ll_doctor);
        mTvDoctorYes = (TextView) mLlDoctor.findViewById(R.id.tvYes);
        mTvDoctorNo = (TextView) mLlDoctor.findViewById(R.id.tvNo);
        mBNext = (Button) findViewById(R.id.bNext);
        mBBack = (Button) findViewById(R.id.bBack);
        mAdminID = intent.getStringExtra("AdminID");
        mAdminFName = intent.getStringExtra("AdminFirstName");
        mAdminPassword = intent.getStringExtra("AdminPassword");
        mAdminEmail = intent.getStringExtra("AdminEmail");
        mAdminPhone = intent.getStringExtra("AdminPhone");
        mMenuValue = intent.getIntExtra("FromMenu", -1);

        Log.e("AdminDetails","" + mMenuValue);
        Log.e("AdminDetails","Users :" + mUserList.size());
        //newly added
       }

    private void initListeners() {
        mBBack.setOnClickListener(this);
        mBNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String aPassword = EncryptDecryptHelper.encryptIt(mAdminPassword.toString().trim());

        if (view.getId() == R.id.bBack) {
            //DO Something
        } else if (view.getId() == R.id.bNext) {
            /*******************************new code for admin to conduct test************************/

            if (mMenuValue != 1)  //From installation
            {
                if (conductTest) {
                    //create admin model
                    if (mUser == null)
                    {
                        mUser = new User(mAdminID.toString().trim(),
                                mAdminFName.toString().trim(),
                                null,
                                aPassword,
                                mAdminEmail.toString().trim(),
                                mAdminPhone.toString().toString(),
                                true,
                                mHospital);
                        DatabaseHelper.getInstance(getApplicationContext()).addUser(mUser);
                        //Toast.makeText(getApplicationContext(), "Admin has selected to conduct test", Toast.LENGTH_LONG).show();
                        Log.e("AdminDetails", "FromInst: added user model");
                    }
                }
                else {
                    //do not create admin model
                    Log.e("AdminDetails", "FromInst: didn't add user model");
                    Toast.makeText(getApplicationContext(), "Admin has selected not to conduct test", Toast.LENGTH_LONG).show();
                }

                if(doctor)
                {
                    if (mDoctor == null)
                    {
                        mDoctor = new Doctor(mAdminFName.toString().trim().toLowerCase(),
                                mAdminEmail.toString().trim(),
                                mAdminPhone.toString().trim(),
                                true,
                                mHospital
                        );
                        DatabaseHelper.getInstance(getApplicationContext()).addDoctor(mDoctor);
                        Toast.makeText(getApplicationContext(), "Admin as Doctor", Toast.LENGTH_LONG).show();
                        Log.e("AdminDetails", "FromInst: added doctor model");
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Admin has not selected to be a doctor", Toast.LENGTH_LONG).show();
                    Log.e("AdminDetails", "FromInst: didn't add doctor model");
                }

            }
            else if (mMenuValue == 1)  // from Menu
            {
                if (conductTest)
                {
                    if (checkAdminAlreadyExistAsUser()) // returns true if admin model is already created
                    {
                        //if admin already exist, update the admin in  user table
                        mUserList = DatabaseHelper.getInstance(FLApplication.getInstance()).getAllUsers();
                        Log.e("AdminDetails", "AdminID " + mAdminID);
                        for (int i = 0; i < mUserList.size(); i++) {
                            Log.e("AdminDetails", "user " + mUserList.get(i).userid);

                            if (mUserList.get(i).userid.matches(mAdminID.toString())) {
                                Log.e("AdminDetails", "Admin" + mUserList.get(i).fname);
                                mUserList.get(i).userid = mAdminID;
                                mUserList.get(i).fname = mAdminFName;
                                mUserList.get(i).lname = null;
                                mUserList.get(i).password = aPassword;
                                mUserList.get(i).email = mAdminEmail;
                                mUserList.get(i).phoneNumber = mAdminPhone;
                                mUserList.get(i).enable = true;
                                mUserList.get(i).hospital = mHospital;
                                DatabaseHelper.getInstance(getApplicationContext()).addUser(mUserList.get(i));
                                Log.e("AdminDetails", "From Menu: AdminAsUser update");
                            }
                        }
                    }
                    else  //if admin does not exist, create a new model for admin
                    {
                        if (mUser == null) {
                            mUser = new User(mAdminID.toString().trim(),
                                    mAdminFName.toString().trim(),
                                    null,
                                    aPassword,
                                    mAdminEmail.toString().trim(),
                                    mAdminPhone.toString().toString(),
                                    true,
                                    mHospital);
                            DatabaseHelper.getInstance(getApplicationContext()).addUser(mUser);
                            Toast.makeText(getApplicationContext(), "From Menu: created admin model", Toast.LENGTH_LONG).show();
                            Log.e("AdminDetails", "From Menu: Admin model created");
                        }
                    }
                }
                else
                {
                    Log.e("AdminDetails", "From Menu: user diselected");
                    checkIfNonAdminEnabledUserDoctorExists();
                }

                if(doctor)
                {

                    if (checkAdminAlreadyExistAsDoctor())
                    {
                        mDoctorList = DatabaseHelper.getInstance(FLApplication.getInstance()).getAllDoctors();
                        Log.e("AdminDetails", "AdminID " + mAdminID);
                        for (int i = 0; i < mDoctorList.size(); i++) {
                            Log.e("AdminDetails", "Doctor " + mDoctorList.get(i).email);

                            if (mDoctorList.get(i).email.matches(mAdminEmail.toString())) {
                                Log.e("AdminDetails", "Admin" + mDoctorList.get(i).name);
                                mDoctorList.get(i).name = mAdminFName;
                                mDoctorList.get(i).email = mAdminEmail;
                                mDoctorList.get(i).phoneNumber = mAdminPhone;
                                mDoctorList.get(i).enable = true;
                                mDoctorList.get(i).hospital = mHospital;
                                DatabaseHelper.getInstance(getApplicationContext()).addDoctor(mDoctorList.get(i));
                                Log.e("AdminDetails", "From Menu: AdminAsDoctor update");

                            }
                        }
                    }
                    else
                    {
                        if (mDoctor == null)
                        {
                            mDoctor = new Doctor(mAdminFName.toString().trim().toLowerCase(),
                                    mAdminEmail.toString().trim(),
                                    mAdminPhone.toString().trim(),
                                    true,
                                    mHospital
                            );
                            DatabaseHelper.getInstance(getApplicationContext()).addDoctor(mDoctor);
                            Toast.makeText(getApplicationContext(), "Added Admin as Doctor", Toast.LENGTH_LONG).show();
                            Log.e("AdminDetails", "From Menu: Doctor model created");
                        }
                    }


                }
//                else
//                {
//                    Log.e("AdminDetails", "From Menu: Doctor diselected");
//                    checkIfNonAdminEnabledUserDoctorExists();
//                }

                /*****************************************end of new code*********************************************/


            }
            showRegistrationConfirmation();
        }
    }

    private boolean checkAdminAlreadyExistAsUser(){
        mUserList = DatabaseHelper.getInstance(FLApplication.getInstance()).getAllUsers();
        for (int i = 0; i < mUserList.size(); i++)
        {
            Log.e("AdminDetails", "user " + mUserList.get(i).userid);

            if (mUserList.get(i).userid.matches(mAdminID.toString()))
            {
                Log.e("AdminDetails", "admin exist as user");
               return true;

            }

        }
        Log.e("AdminDetails", "admin doesn't exist as user");
        return false;
    }

    private boolean checkAdminAlreadyExistAsDoctor(){
        mDoctorList = DatabaseHelper.getInstance(FLApplication.getInstance()).getAllDoctors();
        Log.e("AdminDetails", "AdminID " + mAdminID);
        for (int i = 0; i < mDoctorList.size(); i++) {
            Log.e("AdminDetails", "Doctor " + mDoctorList.get(i).email);

            if (mDoctorList.get(i).email.matches(mAdminEmail.toString())) {
                Log.e("AdminDetails", "admin exist as doctor");
                return true;
            }

        }
        Log.e("AdminDetails", "admin doesn't exist as doctor");
        return false;
    }

    private void showRegistrationConfirmation() {
        //checkIfNonAdminEnabledUserDoctorExists();
        if(mDoNotResetAdmin && (!doctor || !conductTest) ){
            new MessageHelper(AdminDetailsActivity.this).showTitleAlertOk("DO NOT RESET", getString(R.string.error_donot_reset), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
        }
        else {
            new MessageHelper(AdminDetailsActivity.this).showTitleAlertOk(getString(R.string.success_registration), getString(R.string.success_thank_you), getString(R.string.action_next), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (mMenuValue == 1) {
                        openLogInActivity();

                    } else {
                        openAdminDashboard(dialogInterface);
                    }

                }
            });
        }
    }

    public void setAndListenToAdminToggle()
    {
        mTvConductTestYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvConductTestYes.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, R.color.colorPrimary));
                mTvConductTestNo.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, android.R.color.white));
                new MessageHelper(AdminDetailsActivity.this).showTitleAlertOk(getString(R.string.label_disable_admin), getString(R.string.string_admin), "CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                ApplicationUtils.mIsAdminAsUser = true;
                conductTest = true;
            }
        });

        mTvConductTestNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvConductTestYes.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, android.R.color.white));
                mTvConductTestNo.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, R.color.colorPrimary));
                new MessageHelper(AdminDetailsActivity.this).showTitleAlertOk(getString(R.string.label_disable_admin), getString(R.string.string_admin), "CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                ApplicationUtils.mIsAdminAsUser = false;
                conductTest = false;
            }
        });

        mTvDoctorYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvDoctorYes.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, R.color.colorPrimary));
                mTvDoctorNo.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, android.R.color.white));
                new MessageHelper(AdminDetailsActivity.this).showTitleAlertOk(getString(R.string.label_disable_admin), getString(R.string.string_admin), "CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                ApplicationUtils.mIsAdminAsDoctor = true;
                doctor = true;
            }
        });

        mTvDoctorNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvDoctorYes.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, android.R.color.white));
                mTvDoctorNo.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, R.color.colorPrimary));
                new MessageHelper(AdminDetailsActivity.this).showTitleAlertOk(getString(R.string.label_disable_admin), getString(R.string.string_admin), "CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                ApplicationUtils.mIsAdminAsDoctor = false;
                doctor = false;
            }
        });

    }

    public void checkAdminToggleStatus()
    {
        if(ApplicationUtils.mIsAdminAsDoctor)
        {
            mTvDoctorYes.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, R.color.colorPrimary));
            mTvDoctorNo.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, android.R.color.white));
            doctor = true;
        }
        else
        {
            mTvDoctorYes.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, android.R.color.white));
            mTvDoctorNo.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, R.color.colorPrimary));
            doctor = false;
        }

        if(ApplicationUtils.mIsAdminAsUser)
        {
            mTvConductTestYes.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, R.color.colorPrimary));
            mTvConductTestNo.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, android.R.color.white));
            conductTest = true;
        }
        else
        {
            mTvConductTestYes.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, android.R.color.white));
            mTvConductTestNo.setBackgroundColor(ContextCompat.getColor(AdminDetailsActivity.this, R.color.colorPrimary));
            conductTest = false;
        }
    }


    public void checkIfNonAdminEnabledUserDoctorExists()
    {
        mHospitalList.addAll(DatabaseHelper.getInstance(getApplicationContext()).getAllHospital()); //newly added
        Log.e("AdminDetails","Hospitals :" + mHospitalList.size());

        for (Hospital aHospital : mHospitalList)
        {
            mUserList.addAll(DatabaseHelper.getInstance(getApplicationContext()).getAllUsers(aHospital));
            Log.e("AdminDetails","Hospital Name :" + aHospital.name);
            Log.e("AdminDetails","User Name :" + mUserList.size());
            if(mMenuValue == 1 && mUserList.size() < 1){
                mDoNotResetAdmin = true;
            }
            mUserList.clear();
        }
        mHospitalList.clear();
    }


    private void openLogInActivity(){
        Intent aIntent = new Intent(AdminDetailsActivity.this, LoginActivity.class);
        startActivity(aIntent);
    }
    private void openAdminDashboard(DialogInterface iDialogInterface) {
        iDialogInterface.dismiss();
        Intent aIntent = new Intent(AdminDetailsActivity.this, AdminDashboardActivity.class);
        finish();
        startActivity(aIntent);
    }



}
