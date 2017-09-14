package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.adapter.TestMiniAdapter;
import com.sattvamedtech.fetallite.helper.DateUtils;
import com.sattvamedtech.fetallite.model.Patient;
import com.sattvamedtech.fetallite.model.Test;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class PatientDetailsDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private Toolbar mToolbar;
    private TextView mTvGestationalAge, mTvGravidity, mTvPatientAge, mTvMedicalIndications;
    private Button mBClose;
    private Patient mPatient;
    private RecyclerView mRvTestHistory;
    private TestMiniAdapter mAdapter;
    private ArrayList<Test> mTestList = new ArrayList<>();

    public PatientDetailsDialog(Context context, Patient iPatient) {
        super(context);
        mContext = context;
        mPatient = iPatient;
    }

    public PatientDetailsDialog(Context context, int themeResId, Patient iPatient) {
        super(context, themeResId);
        mContext = context;
        mPatient = iPatient;
    }

    protected PatientDetailsDialog(Context context, boolean cancelable, OnCancelListener cancelListener, Patient iPatient) {
        super(context, cancelable, cancelListener);
        mContext = context;
        mPatient = iPatient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_patient_details);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        fetchTestList();
        setView();
        initListeners();

        // 06/07/2017: Implemented pop-up timeout
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                PatientDetailsDialog.this.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 120000);


    }



    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(mPatient.firstName + " " + mPatient.lastName + " | " + mPatient.id);

        mTvGestationalAge = (TextView) findViewById(R.id.tvGestationalAge);
        mTvGravidity = (TextView) findViewById(R.id.tvGravidity);
        mTvPatientAge = (TextView) findViewById(R.id.tvPatientAge);
        mTvMedicalIndications = (TextView) findViewById(R.id.tvMedicalIndications);

        mRvTestHistory = (RecyclerView) findViewById(R.id.rvTestHistory);
        mRvTestHistory.setLayoutManager(new LinearLayoutManager(mContext));
        mRvTestHistory.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mAdapter = new TestMiniAdapter(mContext, mTestList);
        mRvTestHistory.setAdapter(mAdapter);

        mBClose = (Button) findViewById(R.id.bClose);

    }

    private void fetchTestList() {
        mTestList.clear();
        mTestList.addAll(DatabaseHelper.getInstance(FLApplication.getInstance()).getAllTestByPatient(mPatient, FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital()));
    }

    private void setView() {
        if (mPatient != null) {
            mTvGestationalAge.setText(mContext.getString(R.string.label_gestational_age_value, mPatient.gestationalWeeks));
            mTvGravidity.setText(mContext.getString(R.string.label_gravidity_value, String.valueOf(mPatient.gravidity)));
            mTvPatientAge.setText(mContext.getString(R.string.label_patient_age_value, mPatient.age));
            mTvMedicalIndications.setText(mContext.getString(R.string.label_medical_indications_value, mPatient.riskFactor));
            findViewById(R.id.llTestHistory).setVisibility(mTestList.size() > 0 ? View.VISIBLE : View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initListeners() {
        mBClose.setOnClickListener(this);
        //mBStartTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bClose) {
            PatientDetailsDialog.this.dismiss();
        }
//        if(view.getId() == R.id.bStartTest){
//            if()
//        }
    }


}
