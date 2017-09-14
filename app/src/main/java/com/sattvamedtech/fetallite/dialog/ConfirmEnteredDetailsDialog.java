package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.Patient;
import com.sattvamedtech.fetallite.model.Test;

/**
 * Created by Pavan on 7/21/2017.
 */

public class ConfirmEnteredDetailsDialog  extends Dialog implements View.OnClickListener{

    private Context mContext;
    private TextView mtvPatientName, mtvPatientAge, mtvPatientID, mtvDoctor, mtvRiskFactor, mtvGestationalAge, mtvGravidity, mtvParity, mtvTestDuration;
    private Button mbEdit, mbConfirm;
    private Toolbar mToolbar;
    private Patient mPatient;
    private Test mTest;
    private Boolean mIsConfirmed = false;

    public ConfirmEnteredDetailsDialog(Context context,Patient iPatient,Test iTest) {
        super(context);
        mContext = context;
        mPatient = iPatient;
        mTest = iTest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_entered_details);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        initListeners();
        setView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(mContext.getString(R.string.label_confirm_details));

        mtvPatientName = (TextView) findViewById(R.id.tvPatientName);
        mtvPatientAge = (TextView) findViewById(R.id.tvPatientAge);
        mtvPatientID = (TextView) findViewById(R.id.tvPatientID);
        mtvDoctor = (TextView) findViewById(R.id.tvDoctor);
        mtvRiskFactor = (TextView) findViewById(R.id.tvRiskFactor);
        mtvGestationalAge = (TextView) findViewById(R.id.tvGestationalAge);
        mtvGravidity = (TextView) findViewById(R.id.tvGravidity);
        mtvParity = (TextView) findViewById(R.id.tvParity);
        mtvTestDuration = (TextView) findViewById(R.id.tvTestDuration);


        mbEdit = (Button) findViewById(R.id.bEdit);
        mbConfirm = (Button) findViewById(R.id.bConfirm);

    }

    private void initListeners() {
        mbEdit.setOnClickListener(this);
        mbConfirm.setOnClickListener(this);
    }

    private void setView(){
        mtvPatientName.setText(mContext.getString(R.string.label_confirm_patient_name, mPatient.firstName+" "+mPatient.lastName));
        mtvPatientAge.setText(mContext.getString(R.string.label_patient_age_value,mPatient.age));
        if(TextUtils.isEmpty(mPatient.id))
            mtvPatientID.setText(mContext.getString(R.string.label_confirm_patient_ID, "--"));
        else
            mtvPatientID.setText(mContext.getString(R.string.label_confirm_patient_ID, mPatient.id));
        mtvGestationalAge.setText(mContext.getString(R.string.label_gestational_age_value, mPatient.gestationalWeeks));
        if(TextUtils.isEmpty(mPatient.riskFactor))
            mtvRiskFactor.setText(mContext.getString(R.string.label_confirm_patient_riskfactor,"--"));
        else
            mtvRiskFactor.setText(mContext.getString(R.string.label_confirm_patient_riskfactor, mPatient.riskFactor));
        if(mPatient.gravidity == 0)
            mtvGravidity.setText(mContext.getString(R.string.label_gravidity_value, "--"));
        else
            mtvGravidity.setText(mContext.getString(R.string.label_gravidity_value, mPatient.gravidity+""));
        if(mPatient.parity == 0)
             mtvParity.setText(mContext.getString(R.string.label_parity_value,"--"));
        else
            mtvParity.setText(mContext.getString(R.string.label_parity_value,mPatient.parity+""));
        mtvTestDuration.setText(mContext.getString(R.string.label_test_duration_value,mTest.testDurationInMinutes));
        mtvDoctor.setText(mContext.getString(R.string.label_test_doctor_name,mTest.doctor));
    }

    public boolean isConfirmed(){
        return mIsConfirmed;
    }

    public void clearFields(){
        mIsConfirmed = false;
        mPatient = null;
        mTest = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bEdit) {
            ConfirmEnteredDetailsDialog.this.dismiss();
        } else if (view.getId() == R.id.bConfirm) {
            mIsConfirmed = true;
            ConfirmEnteredDetailsDialog.this.dismiss();
        }
    }
}
