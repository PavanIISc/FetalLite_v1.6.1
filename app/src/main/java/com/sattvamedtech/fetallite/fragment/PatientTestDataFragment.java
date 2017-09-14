package com.sattvamedtech.fetallite.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.activity.LoginActivity;
import com.sattvamedtech.fetallite.adapter.TestAdapter;
import com.sattvamedtech.fetallite.helper.CompressionHelper;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.DateUtils;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.helper.SessionHelper;
import com.sattvamedtech.fetallite.model.Patient;
import com.sattvamedtech.fetallite.model.Test;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PatientTestDataFragment extends Fragment implements View.OnClickListener {

    private EditText mEtPatientDetails, mEtTestDate;
    private Button mBBack, mBSearch;
    private RecyclerView mRvTestList;
    private ArrayList<Test> mTestList = new ArrayList<>();
    private TestAdapter mAdapter;
    private DatePickerDialog mDatePicker;
    private Calendar mCalendar = Calendar.getInstance();
    private Patient iPatient;

    private int searchResetFlag = 0;

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
            mCalendar.set(year, month, date);
            resetCalendarTime(null);
            mEtTestDate.setText(DateUtils.convertDateToShortHumanReadable(mCalendar.getTimeInMillis()));
        }
    };

    private TestAdapter.TestClickListener mTestClickListener = new TestAdapter.TestClickListener() {
        @Override
        public void onTestClick(int iPosition) {
            if (SessionHelper.isLoginSessionValid()) {
                ((HomeActivity) getActivity()).mProgressDialog.show();
                File aZip = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTestList.get(iPosition).inputFilePath + ".zip");
                File aDir = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTestList.get(iPosition).inputFilePath);
                if (!aDir.isDirectory()) {
                    if (aZip.exists()) {
                        CompressionHelper.unzipFile(aZip, aDir);
                    } else {
                        new MessageHelper(getActivity()).showTitleAlertOk(getString(R.string.label_info), getString(R.string.error_empty_invalid_files), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        return;
                    }
                }
                TestFragment aTestFragment = new TestFragment();
                Bundle aBundle = new Bundle();
                aBundle.putSerializable(Constants.EXTRA_TEST, mTestList.get(iPosition));
                aTestFragment.setArguments(aBundle);
                iPatient = mTestList.get(iPosition).patient;
                ((HomeActivity) getActivity()).addReplaceFragment(aTestFragment, true, iPatient, true);
            } else {
                ((HomeActivity) getActivity()).invalidSession();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_test_data, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setToolbar();
        search();
        reset();
    }

    public void setToolbar() {
        ((HomeActivity) getActivity()).setTitle(getString(R.string.label_patient_test_data));
        ((HomeActivity) getActivity()).showMenuIcon(true);
    }

    private void initView(View iView) {
        mEtPatientDetails = (EditText) iView.findViewById(R.id.etPatientDetails);
        mEtTestDate = (EditText) iView.findViewById(R.id.etTestDate);
        mBBack = (Button) iView.findViewById(R.id.bPatientTestDataBack);
        mBSearch = (Button) iView.findViewById(R.id.bSearch);
        mRvTestList = (RecyclerView) iView.findViewById(R.id.rvTestList);
        mAdapter = new TestAdapter(getActivity(), mTestList, mTestClickListener);
        mRvTestList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvTestList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRvTestList.setAdapter(mAdapter);

        mDatePicker = new DatePickerDialog(getActivity(), mDateSetListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));
    }

    private void initListeners() {
        mEtTestDate.setOnClickListener(this);
        mBBack.setOnClickListener(this);
        mBSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (SessionHelper.isLoginSessionValid()) {
            if (v.getId() == R.id.etTestDate) {
                mDatePicker.show();
            } else if (v.getId() == R.id.bPatientTestDataBack) {
//                getActivity().onBackPressed();
                Intent aIntent = new Intent(getActivity(), HomeActivity.class);
                startActivity(aIntent);
            } else if (v.getId() == R.id.bSearch) {
                //search();

                // Added a reset functionality so that user does not have to go back and click on view data again.
                if(searchResetFlag == 0)
                {
                    search();

                }
                else
                {
                    reset();
                }

            }
        } else {
            ((HomeActivity) getActivity()).invalidSession();
        }
    }

    private void search() {

        mTestList.clear();

        if (TextUtils.isEmpty(mEtPatientDetails.getText().toString().trim()) && TextUtils.isEmpty(mEtTestDate.getText().toString().trim()))
            mTestList.addAll(DatabaseHelper.getInstance(FLApplication.getInstance()).getAllTestByHospital(FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital()));

        else if(!TextUtils.isEmpty(mEtTestDate.getText().toString().trim()) && TextUtils.isEmpty(mEtPatientDetails.getText().toString().trim())){
                mTestList.addAll(DatabaseHelper.getInstance(FLApplication.getInstance()).getAllTestByTestDate(FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital(),mEtTestDate.getText().toString().trim()));
                mBSearch.setText("Reset");
                searchResetFlag = 1;
        }
        else if(!TextUtils.isEmpty(mEtTestDate.getText().toString().trim()) && !TextUtils.isEmpty(mEtPatientDetails.getText().toString().trim())){
            mTestList.addAll(DatabaseHelper.getInstance(FLApplication.getInstance()).getAllTestByTestDateAndPatient(FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital(),mEtTestDate.getText().toString().trim(),mEtPatientDetails.getText().toString().trim()));
            mBSearch.setText("Reset");
            searchResetFlag = 1;
        } else
        {
            Log.e("EnteredSearch", "Searched time is" + mCalendar.getTimeInMillis());
            Log.e("EnteredSearch", "Current time is" + Calendar.getInstance().getTimeInMillis());
            mBSearch.setText("Reset");
            searchResetFlag = 1;
            mTestList.addAll(DatabaseHelper.getInstance(FLApplication.getInstance()).getAllTestByPatient(mEtPatientDetails.getText().toString().trim(), !TextUtils.isEmpty(mEtTestDate.getText().toString().trim()) ? mCalendar.getTimeInMillis() : 0, FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital()));

        }
        mAdapter.notifyDataSetChanged();

        //clearFields();

        if (mTestList.size() == 0) {
            handleEmptySearchResults();
           // reset();
        }


    }


    private void clearFields(){
        mEtTestDate.getText().clear();
        mEtPatientDetails.getText().clear();

    }

    private void reset()
    {
        searchResetFlag = 0;
        mEtTestDate.getText().clear();
        mEtPatientDetails.getText().clear();
        mBSearch.setText("Search");
        search();

    }
    private void handleEmptySearchResults() {
        new MessageHelper(getActivity()).showTitleAlertOk(getString(R.string.label_info), getString(R.string.error_empty_test_results), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private void resetCalendarTime(Calendar iCalendar) {
        if (iCalendar != null) {
            iCalendar.set(Calendar.HOUR_OF_DAY, 0);
            iCalendar.set(Calendar.MINUTE, 0);
            iCalendar.set(Calendar.SECOND, 0);
            iCalendar.set(Calendar.MILLISECOND, 0);
        } else {
            mCalendar.set(Calendar.HOUR_OF_DAY, 0);
            mCalendar.set(Calendar.MINUTE, 0);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);
        }
    }
}
