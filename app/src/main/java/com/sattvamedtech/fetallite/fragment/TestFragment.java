package com.sattvamedtech.fetallite.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.dialog.PatientDetailsDialog;
import com.sattvamedtech.fetallite.dialog.TestAgainMessageDialog;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.CoupleChartGestureListener;
import com.sattvamedtech.fetallite.helper.DateUtils;
import com.sattvamedtech.fetallite.helper.FileLogger;
import com.sattvamedtech.fetallite.helper.Logger;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.helper.SessionHelper;
import com.sattvamedtech.fetallite.interfaces.PlotCallback;
import com.sattvamedtech.fetallite.interfaces.PlotInterface;
import com.sattvamedtech.fetallite.interfaces.PrintCallback;
import com.sattvamedtech.fetallite.model.Patient;
import com.sattvamedtech.fetallite.model.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;

import au.com.bytecode.opencsv.CSVReader;

public class TestFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener, PlotInterface
{

    private Context mContext;
    private static final int SEC_IN_MILLIS = 1000;
    private static final int MIN_IN_SEC = 60;
    private static final int MIN_IN_MILLIS = MIN_IN_SEC * SEC_IN_MILLIS;
    private static final int GRAPH_VISIBLE_RANGE = 10;
    private static final int GRAPH_LABEL_COUNT = 20;
    private static int COLOR_PRIMARY;
    private boolean TEST_STOPPED = false;


    private Patient mPatient;
    private int mTestDuration;
    private Test mTest;
    private String mDoctorName;
    private String mUserName;

    private String mLastPatientName;

	 //06/07/2017: Vibhav added a date variable.
    private long mTestDate;										   
    private LinearLayout mLlFragmentTestRoot, mLlFhr, mLlButtonControls;
    private TextView mTvFhr, mTvFhrLabel, mTvMhr, mTvMhrLabel, mTvGestationalAge, mTvAccelerations, mTvDecelerations, mTvName,mTvDoctor,mTvUser, mTvPatientId, mTvTestTime, mTvMinSecLabel, mTvTestTimeLabel, mTvStopTest, mTvSendSmsLabel, mTvTestDate;
    private Button mBPatientDetails, mBPrint, mBExit, mBTestAgain;
    private ImageButton mIbStopTest, mIbSendSms;
    private DonutProgress mDpTestDuration;
    private ScatterChart mFetalChart;
    private LineChart mUcChart;
    //private LineChart mFetalChart, mUcChart;


    private int mChartLineColor = Color.parseColor("#FF7F7F7F");


    private CountDownTimer mCountDownTimer;

    private static final int FILE_TYPE_HR = 1;
    private static final int FILE_TYPE_UC = 2;

    private boolean isViewDataAvailable = true;
    int countUpSec = 0;
    int countUpMin = 0;






    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getExtraArguments();

        COLOR_PRIMARY = ContextCompat.getColor(getActivity(), R.color.colorAccent);

        initView(view);
        setToolbar();
        initProgress();
        initFetalChart(view);
        initUcChart(view);
        setView();
        initListeners();

        mFetalChart.setOnChartGestureListener(new CoupleChartGestureListener(mFetalChart, new Chart[]{mUcChart}));
        mUcChart.setOnChartGestureListener(new CoupleChartGestureListener(mUcChart, new Chart[]{mFetalChart}));
																										  
        setTheme(false);

        if (mTest == null)
        {
            PlotCallback.getInstance().addPlotInterface(this);
            ((HomeActivity) getActivity()).mProgressDialog.show();
        } else {
            ((HomeActivity) getActivity()).clearPrintData();
            stoppedStateView(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    readFile(FILE_TYPE_HR);
                    readFile(FILE_TYPE_UC);
                    // TODO: 10-Sep-17 Uncomment below line and put that into background thread
                    //readPrintFile();
                    checkEmptyData();
                    ((HomeActivity) getActivity()).mProgressDialog.dismiss();
                }
            }, 500);
        }
    }

    private void getExtraArguments()
    {

        if (getArguments() != null)
        {
            Log.e("TestFragment","GetArguments=True");
            mTest = (Test) getArguments().getSerializable(Constants.EXTRA_TEST);
            mDoctorName = getArguments().getString(Constants.DOCTOR);
            mUserName = getArguments().getString(Constants.USER);

            if (mTest != null)
            {
                mPatient = mTest.patient;
                mTestDuration = mTest.testDurationInMinutes + 15;
				mTestDate = mTest.testTime;
                Log.e("TestFragment","Test Duration : " + mTestDuration);
            }
            else
            {

                mPatient = (Patient) getArguments().getSerializable("PatientObj");
                mTestDuration = getArguments().getInt(Constants.EXTRA_TEST_DURATION) + 15;

            }

        }
        //startTest();
        //mCountDownTimer.start();
    }

    private void initProgress()
    {
//        new MessageHelper(getContext()).showTitleAlertOk("SENSOR UNIT PLACEMENT ERROR", "Please check eletrode contacts and/or sensor unit orientation and position before starting a new test", "CLOSE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i)
//            {
//                dialogInterface.dismiss();
//                //stopDataStream();
//                //mEtDeviceID.setText("");
//            }
//        });

        mDpTestDuration.setMax(mTestDuration * MIN_IN_MILLIS);
        //mCountDownTimer = new Co
        mCountDownTimer = new CountDownTimer(mTestDuration * MIN_IN_MILLIS, SEC_IN_MILLIS) {
            @Override
            public void onTick(long l) {
                setTestTime(l);
                countUpSec++;

                if(countUpSec == 60) {
                    countUpMin++;
                    countUpSec = 0;
                }

                if(countUpSec%2 == 0)
                {
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150);


                }

            }

            @Override
            public void onFinish() {
                //setTestTime(0);
                confirmStopTest(getString(R.string.label_duration_complete), getString(R.string.confirm_stop_test_duration));
            }
        };
    }

    private void initView(View iView)
    {
        mLlFragmentTestRoot = (LinearLayout) iView.findViewById(R.id.fragment_test_root);
        mLlFhr = (LinearLayout) iView.findViewById(R.id.llFhr);
        mTvFhr = (TextView) iView.findViewById(R.id.tvFhr);
        mTvFhrLabel = (TextView) iView.findViewById(R.id.tvFhrLabel);
        mTvMhr = (TextView) iView.findViewById(R.id.tvMhr);
        mTvMhrLabel = (TextView) iView.findViewById(R.id.tvMhrLabel);
        mTvGestationalAge = (TextView) iView.findViewById(R.id.tvGestationalAge);
        mTvAccelerations = (TextView) iView.findViewById(R.id.tvAccelerations);
        mTvDecelerations = (TextView) iView.findViewById(R.id.tvDecelerations);
        mTvName = (TextView) iView.findViewById(R.id.tvName);
        mTvDoctor = (TextView) iView.findViewById(R.id.tvDoctor);
        mTvUser = (TextView) iView.findViewById(R.id.tvUser);
        mTvPatientId = (TextView) iView.findViewById(R.id.tvPatientId);
        mTvTestTime = (TextView) iView.findViewById(R.id.tvTestTime);
        mTvMinSecLabel = (TextView) iView.findViewById(R.id.tvMinSecLabel);
        mTvTestDate = (TextView)iView.findViewById(R.id.tvTestDate);
        mTvTestTimeLabel = (TextView) iView.findViewById(R.id.tvTestTimeLabel);
        mBPatientDetails = (Button) iView.findViewById(R.id.bPatientDetails);
        mIbStopTest = (ImageButton) iView.findViewById(R.id.ibStopTest);
        mTvStopTest = (TextView) iView.findViewById(R.id.tvStopTest);
        mIbSendSms = (ImageButton) iView.findViewById(R.id.ibSendSms);
        mTvSendSmsLabel = (TextView) iView.findViewById(R.id.tvSendSmsLabel);
        mDpTestDuration = (DonutProgress) iView.findViewById(R.id.dpTestDuration);
        mLlButtonControls = (LinearLayout) iView.findViewById(R.id.llButtonControls);
        mBPrint = (Button) iView.findViewById(R.id.bPrint);
        mBExit = (Button) iView.findViewById(R.id.bExit);
        mBTestAgain = (Button) iView.findViewById(R.id.bTestAgain);
    }

    public void setToolbar() {
        ((HomeActivity) getActivity()).setTitle(null);
        ((HomeActivity) getActivity()).showMenuIcon(true);
    }

    public void setTheme(boolean isThemeDark) {
        mLlFragmentTestRoot.setBackgroundColor(isThemeDark ? Color.BLACK : Color.WHITE);
        mTvFhr.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvFhrLabel.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvMhr.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvMhrLabel.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvGestationalAge.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvAccelerations.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvDecelerations.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvName.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvDoctor.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvUser.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvPatientId.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvTestTime.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvMinSecLabel.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvTestTimeLabel.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvStopTest.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
        mTvSendSmsLabel.setTextColor(isThemeDark ? Color.WHITE : Color.BLACK);
    }

    private void initFetalChart(View iView) {
        mFetalChart = (ScatterChart) iView.findViewById(R.id.chartFetal);
        //mFetalChart = (LineChart) iView.findViewById(R.id.chartFetal);
        mFetalChart.setOnChartValueSelectedListener(this);
        // enable description text
        mFetalChart.getDescription().setEnabled(false);
        // enable touch gestures
        mFetalChart.setTouchEnabled(true);
        // enable scaling and dragging
        mFetalChart.setDragEnabled(true);
        mFetalChart.setScaleEnabled(true);
        mFetalChart.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        mFetalChart.setPinchZoom(true);
        // set an alternative background color
        mFetalChart.setBackgroundColor(Color.TRANSPARENT);
//        LineData aLineData = new LineData();
//        aLineData.setValueTextColor(Color.BLACK);
//        // add empty data
//        mFetalChart.setData(aLineData);
        ScatterData aScatterData = new ScatterData();

        aScatterData.setValueTextColor(Color.BLACK);
        aScatterData.setValueTextSize(50);
        // add empty data
        mFetalChart.setData(aScatterData);

        // get the legend (only possible after setting data)
        mFetalChart.getLegend().setEnabled(false);
        XAxis aXAxis = mFetalChart.getXAxis();
        aXAxis.setTextColor(COLOR_PRIMARY);
        aXAxis.setDrawGridLines(true);
        aXAxis.setAvoidFirstLastClipping(true);
        aXAxis.setEnabled(true);
        aXAxis.setDrawLabels(true);
        aXAxis.setLabelCount(GRAPH_LABEL_COUNT);
        aXAxis.setAxisMinimum(0f);

        YAxis aLeftYAxis = mFetalChart.getAxisLeft();
        aLeftYAxis.setTextColor(COLOR_PRIMARY);
        if (FLApplication.isFetalEnabled) {
            aLeftYAxis.setAxisMaximum(210f);
            aLeftYAxis.setAxisMinimum(50f);
        } else {
            aLeftYAxis.setAxisMaximum(130f);
            aLeftYAxis.setAxisMinimum(30f);
        }
        aLeftYAxis.setDrawGridLines(true);

        YAxis aRightYAxis = mFetalChart.getAxisRight();
        aRightYAxis.setEnabled(false);

        addDummyFetalData();
    }

    private void addDummyFetalData() {
        addFetalEntry(10 * MIN_IN_MILLIS, 0);
        addFetalEntry(9 * MIN_IN_MILLIS, 0);
        addFetalEntry(8 * MIN_IN_MILLIS, 0);
        addFetalEntry(7 * MIN_IN_MILLIS, 0);
        addFetalEntry(6 * MIN_IN_MILLIS, 0);
        addFetalEntry(5 * MIN_IN_MILLIS, 0);
        addFetalEntry(4 * MIN_IN_MILLIS, 0);
        addFetalEntry(3 * MIN_IN_MILLIS, 0);
        addFetalEntry(2 * MIN_IN_MILLIS, 0);
        addFetalEntry(MIN_IN_MILLIS, 0);
        addFetalEntry(0, 0);
        addFetalEntry(-1 * MIN_IN_MILLIS, 0);
        addFetalEntry(-2 * MIN_IN_MILLIS, 0);
        addFetalEntry(-3 * MIN_IN_MILLIS, 0);
        addFetalEntry(-4 * MIN_IN_MILLIS, 0);
        addFetalEntry(-5 * MIN_IN_MILLIS, 0);
    }

    private void initUcChart(View iView) {
        mUcChart = (LineChart) iView.findViewById(R.id.chartMaternal);
        mUcChart.setOnChartValueSelectedListener(this);
        // enable description text
        mUcChart.getDescription().setEnabled(false);
        // enable touch gestures
        mUcChart.setTouchEnabled(true);
        // enable scaling and dragging
        mUcChart.setDragEnabled(true);
        mUcChart.setScaleEnabled(true);
        mUcChart.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        mUcChart.setPinchZoom(true);
        // set an alternative background color
        mUcChart.setBackgroundColor(Color.TRANSPARENT);
        LineData aLineData = new LineData();
        aLineData.setValueTextColor(Color.BLACK);
        // add empty data
        mUcChart.setData(aLineData);
        // get the legend (only possible after setting data)
        mUcChart.getLegend().setEnabled(false);
        XAxis aXAxis = mUcChart.getXAxis();
        aXAxis.setTextColor(COLOR_PRIMARY);
        aXAxis.setDrawGridLines(true);
        aXAxis.setAvoidFirstLastClipping(true);
        aXAxis.setEnabled(true);
        aXAxis.setDrawLabels(true);
        aXAxis.setLabelCount(GRAPH_LABEL_COUNT);
        aXAxis.setAxisMinimum(0f);

        YAxis aLeftYAxis = mUcChart.getAxisLeft();
        aLeftYAxis.setTextColor(COLOR_PRIMARY);
        aLeftYAxis.setDrawGridLines(true);
        aLeftYAxis.setAxisMaximum(100f);
        aLeftYAxis.setAxisMinimum(0f);

        YAxis aRightYAxis = mUcChart.getAxisRight();
        aRightYAxis.setEnabled(false);

        addDummyUcData();
    }

    private void addDummyUcData() {
        addUcEntry(10 * MIN_IN_MILLIS, -1);
        addUcEntry(9 * MIN_IN_MILLIS, -1);
        addUcEntry(8 * MIN_IN_MILLIS, -1);
        addUcEntry(7 * MIN_IN_MILLIS, -1);
        addUcEntry(6 * MIN_IN_MILLIS, -1);
        addUcEntry(5 * MIN_IN_MILLIS, -1);
        addUcEntry(4 * MIN_IN_MILLIS, -1);
        addUcEntry(3 * MIN_IN_MILLIS, -1);
        addUcEntry(2 * MIN_IN_MILLIS, -1);
        addUcEntry(MIN_IN_MILLIS, -1);
        addUcEntry(0, -1);
        addUcEntry(-1 * MIN_IN_MILLIS, -1);
        addUcEntry(-2 * MIN_IN_MILLIS, -1);
        addUcEntry(-3 * MIN_IN_MILLIS, -1);
        addUcEntry(-4 * MIN_IN_MILLIS, -1);
        addUcEntry(-5 * MIN_IN_MILLIS, -1);
    }

    private void setView() {
        mIbStopTest.setEnabled(true);
        mTvGestationalAge.setText(getString(R.string.label_gestational_weeks_value, mPatient.gestationalWeeks));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mTvName.setText(Html.fromHtml(getString(R.string.label_name_value, mPatient.firstName, mPatient.lastName)));
            mTvPatientId.setText(Html.fromHtml(getString(R.string.label_patient_id_value, mPatient.id)));
            mTvDoctor.setText(Html.fromHtml(getString(R.string.label_test_doctor, mPatient.doctor.name)));
            mTvUser.setText(Html.fromHtml(getString(R.string.label_test_user, mPatient.user.fname)));

        } else {
            mTvName.setText(Html.fromHtml(getString(R.string.label_name_value, mPatient.firstName, mPatient.lastName), Html.FROM_HTML_MODE_COMPACT));
            mTvPatientId.setText(Html.fromHtml(getString(R.string.label_patient_id_value, mPatient.id), Html.FROM_HTML_MODE_COMPACT));
            mTvDoctor.setText(Html.fromHtml(getString(R.string.label_test_doctor,mPatient.doctor.name), Html.FROM_HTML_MODE_COMPACT));
            mTvUser.setText(Html.fromHtml(getString(R.string.label_test_user,mPatient.user.fname),Html.FROM_HTML_MODE_COMPACT));

        }
        setTestTime(mTestDuration * MIN_IN_MILLIS);
        //startTest();
    }

    private void initListeners() {
        mIbStopTest.setOnClickListener(this);
        mIbSendSms.setOnClickListener(this);
        mBPatientDetails.setOnClickListener(this);
        mBPrint.setOnClickListener(this);
        mBExit.setOnClickListener(this);
        mBTestAgain.setOnClickListener(this);
    }

    private void setTestTime(long iTimeInMillis) {
        try {
            mDpTestDuration.setProgress((mTestDuration * MIN_IN_MILLIS) - iTimeInMillis);
            //mTvTestTime.setText(getString(R.string.label_test_duration, iTimeInMillis / MIN_IN_MILLIS, (iTimeInMillis % MIN_IN_MILLIS) / SEC_IN_MILLIS));
            mTvTestTime.setText(getString(R.string.label_test_duration, countUpMin, countUpSec));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTest() {
        mCountDownTimer.start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bPatientDetails) {
            new PatientDetailsDialog(getActivity(), mPatient).show();
        } else if (view.getId() == R.id.ibSendSms) {
            ((HomeActivity) getActivity()).prepareForSms(false, "Test Details");
        } else if (view.getId() == R.id.ibStopTest) {
            toneGen1.stopTone();
            confirmStopTest(getString(R.string.label_stop_test), getString(R.string.confirm_stop_test_manual));
        } else if (view.getId() == R.id.bPrint) {
            ((HomeActivity) getActivity()).printData();
        } else if (view.getId() == R.id.bExit) {
            if (SessionHelper.isLoginSessionValid()) {
                if (((HomeActivity) getActivity()).mProgressDialog != null && ((HomeActivity) getActivity()).mProgressDialog.isShowing())
                    ((HomeActivity) getActivity()).mProgressDialog.dismiss();
                ((HomeActivity) getActivity()).restartDataSocketService();
                ((HomeActivity) getActivity()).restartPrintSocketService();
                ((HomeActivity) getActivity()).popFragment();
//                Logger.logInfo("Test fragment: ", "bExit button click");
            }
            else {
                ((HomeActivity) getActivity()).invalidSession();
            }
        }
        else if(view.getId() == R.id.bTestAgain){
            ApplicationUtils.mTestAgain = 1;
            new TestAgainMessageDialog(getContext(), mPatient).show(); //showing a confirmation pop up

        }
    }

    private void confirmStopTest(String iTitle, String iMessage) {
        new MessageHelper(getActivity()).showTitleAlertOkCancel(iTitle, iMessage, "", "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                stopTest(true);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Log.e("Test Fragment ","pressed cancel");
            }
        });
    }

    public void stopTest(boolean isFromTestFragment) {
        if (isFromTestFragment)
            ((HomeActivity) getActivity()).stopDataStream();
        mCountDownTimer.cancel();

        ApplicationUtils.mTestDurationMin = countUpMin;
        ApplicationUtils.mTestDurationSec = countUpSec;
        countUpMin = 0;
        countUpSec = 0;
        toneGen1.stopTone();
        stoppedStateView(1);

    }

    public void connectionLost() {
        mCountDownTimer.cancel();
        countUpMin = 0;
        countUpSec = 0;
        toneGen1.stopTone();
        stoppedStateView(1);

    }


    private void stoppedStateView(int inputTypeFlag) {
        TEST_STOPPED = true;
        mUcChart.setPinchZoom(true);
        mFetalChart.setPinchZoom(true);
        mUcChart.setTouchEnabled(true);
        mFetalChart.setTouchEnabled(true);
        SessionHelper.resetSession();
        mIbStopTest.setEnabled(false);
        mIbStopTest.setImageResource(R.drawable.stopped);
        mTvStopTest.setText(getString(R.string.action_stopped));
        mLlButtonControls.setVisibility(View.VISIBLE);
        mTvTestDate.setVisibility(View.VISIBLE);

        if(inputTypeFlag == 0)
        {
            mTvTestDate.setText("Date: "+ DateUtils.convertDateToLongHumanReadable(mTestDate));
        }
        else
        {
            mTvTestDate.setText("Date: "+ DateUtils.convertDateToLongHumanReadable(Calendar.getInstance().getTimeInMillis()));
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(getActivity(), String.valueOf(e.getY()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void plotFetalHeartRate(final float iXValue, final float iYValue) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (((HomeActivity) getActivity()).mProgressDialog.isShowing()) {
                        ((HomeActivity) getActivity()).mProgressDialog.dismiss();

                        startTest();
                    }

                    if (FLApplication.isFetalEnabled) {
                        setFetalHeartText(iYValue);
                    } else {
                        setMaternalHeartText(iYValue);
                    }
                    addFetalEntry(iXValue, iYValue);
					mUcChart.moveViewToX(mFetalChart.getLowestVisibleX());													  
                    FileLogger.logData(String.valueOf(iXValue) + "," + String.valueOf(iYValue), "fhr", FLApplication.mFileTimeStamp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void plotMaternalHeartRate(final float iXValue, final float iMhr) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (((HomeActivity) getActivity()).mProgressDialog.isShowing()) {
                        ((HomeActivity) getActivity()).mProgressDialog.dismiss();
                        startTest();
                    }
                    setMaternalHeartText(iMhr);
                    FileLogger.logData(String.valueOf(iXValue) + "," + String.valueOf(iMhr), "mhr", FLApplication.mFileTimeStamp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void plotUc(final float iXValue, final float iYValue) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (((HomeActivity) getActivity()).mProgressDialog.isShowing()) {
                        ((HomeActivity) getActivity()).mProgressDialog.dismiss();
                        startTest();
                    }
                    addUcEntry(iXValue, iYValue);
                    FileLogger.logData(String.valueOf(iXValue) + "," + String.valueOf(iYValue), "UC", FLApplication.mFileTimeStamp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setMaternalHeartText(float iMhr) {
        if(ApplicationUtils.connectionLost){
            new MessageHelper(getActivity()).showTitleAlertOk(getString(R.string.label_info), getString(R.string.error_test_was_improper), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    connectionLost();
                }
            });
        }
        if(iMhr == 1 || TEST_STOPPED){
            mTvMhr.setText("-");
        }
        else{
            mTvMhr.setText(String.valueOf((int) iMhr));
        }

    }

    private void setFetalHeartText(float iFhr) {
        if(ApplicationUtils.connectionLost){
            new MessageHelper(getActivity()).showTitleAlertOk(getString(R.string.label_info), getString(R.string.error_test_was_improper), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    connectionLost();
                }
            });
        }
        if(iFhr == 1 || TEST_STOPPED){
            mTvFhr.setText("-");
        }
        else{
            mTvFhr.setText(String.valueOf((int) iFhr));
        }

    }

    private void addFetalEntry(float iXEntry, float iYEntry) {
        ScatterData aScatterData = mFetalChart.getData();
        //LineData aLineData = mFetalChart.getData();
        if (aScatterData != null) {
            IScatterDataSet aIScatterDataSet = aScatterData.getDataSetByIndex(0);
            // set.addFetalEntry(...); // can be called as well
            if (aIScatterDataSet == null) {
                aIScatterDataSet = fetalHeartRate();
                aScatterData.addDataSet(aIScatterDataSet);
            }
            Logger.logInfo("MainActivity: Fetal", "x_entry = " + iXEntry + ", y_entry = " + iYEntry);
            aScatterData.addEntry(new Entry((iXEntry / MIN_IN_MILLIS), iYEntry), 0);
            Logger.logInfo("TestFragment", "Iteration " + ApplicationUtils.algoProcessStartCount + " fetalEntryCount: " + aScatterData.getEntryCount());
            aScatterData.notifyDataChanged();
            mFetalChart.notifyDataSetChanged();
            mFetalChart.setVisibleXRangeMaximum(GRAPH_VISIBLE_RANGE);
            mFetalChart.setVisibleXRangeMinimum(0);
            mFetalChart.moveViewToX(aScatterData.getEntryCount() - GRAPH_VISIBLE_RANGE);
        }
//        if (aLineData != null) {
//            ILineDataSet aILineDataSet = aLineData.getDataSetByIndex(0);
//            // set.addFetalEntry(...); // can be called as well
//            if (aILineDataSet == null) {
//                aILineDataSet = fetalHeartRate();
//                aLineData.addDataSet(aILineDataSet);
//            }
//           Logger.logInfo("MainActivity: Fetal", "x_entry = " + iXEntry + ", y_entry = " + iYEntry);
//            aLineData.addEntry(new Entry((iXEntry / MIN_IN_MILLIS), iYEntry), 0);
//           Logger.logInfo("TestFragment", "Iteration " + ApplicationUtils.algoProcessStartCount + " fetalEntryCount: " + aLineData.getEntryCount());
//            aLineData.notifyDataChanged();
//            mFetalChart.notifyDataSetChanged();
//            mFetalChart.setVisibleXRangeMaximum(GRAPH_VISIBLE_RANGE);
//            mFetalChart.setVisibleXRangeMinimum(0);
//            mFetalChart.moveViewToX(aLineData.getEntryCount() - GRAPH_VISIBLE_RANGE);
//        }
    }

    private void addUcEntry(float iXEntry, float iYEntry) {
        LineData aLineData = mUcChart.getData();
        if (aLineData != null) {
            ILineDataSet aILineDataSet = aLineData.getDataSetByIndex(0);
            // set.addFetalEntry(...); // can be called as well
            if (aILineDataSet == null) {
                aILineDataSet = ucRate();
                aLineData.addDataSet(aILineDataSet);
            }
            Logger.logInfo("MainActivity: UC", "x_entry = " + iXEntry + ", y_entry = " + iYEntry);
            aLineData.addEntry(new Entry((iXEntry / MIN_IN_MILLIS), iYEntry), 0);
            aLineData.notifyDataChanged();
            mUcChart.notifyDataSetChanged();
            mUcChart.setVisibleXRangeMaximum(GRAPH_VISIBLE_RANGE);
            mUcChart.setVisibleXRangeMinimum(0);
            mUcChart.moveViewToX(aLineData.getEntryCount() - GRAPH_VISIBLE_RANGE);
        }
    }

    private ScatterDataSet fetalHeartRate() {
        ScatterDataSet aScatterDataSet = new ScatterDataSet(null, "Fetal Heart Rate");
        aScatterDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        aScatterDataSet.setColor(mChartLineColor);
        aScatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        //aScatterDataSet.setLineWidth(2f);
        aScatterDataSet.setScatterShapeSize(3f);
        //aLineDataSet.setFillAlpha(65);
        //aScatterDataSet.setFillColor(ColorTemplate.getHoloBlue());
        aScatterDataSet.setHighLightColor(Color.TRANSPARENT);
        aScatterDataSet.setDrawValues(false);
        return aScatterDataSet;
    }

//    private LineDataSet fetalHeartRate() {
//        LineDataSet aLineDataSet = new LineDataSet(null, "Fetal Heart Rate");
//        aLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//        aLineDataSet.setColor(mChartLineColor);
//        aLineDataSet.setCircleColor(mChartLineColor);
//        aLineDataSet.setLineWidth(2f);
//        aLineDataSet.setCircleRadius(1f);
//        //aLineDataSet.setFillAlpha(65);
//        aLineDataSet.setFillColor(ColorTemplate.getHoloBlue());
//        aLineDataSet.setHighLightColor(Color.TRANSPARENT);
//        aLineDataSet.setDrawValues(false);
//        return aLineDataSet;
//    }

    private LineDataSet ucRate() {
        LineDataSet aLineDataSet = new LineDataSet(null, "Uterine Contraction (Kpa)");
        aLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        aLineDataSet.setColor(Color.BLUE);
        aLineDataSet.setCircleColor(Color.BLUE);
        aLineDataSet.setLineWidth(2f);
        aLineDataSet.setCircleRadius(1f);
        //aLineDataSet.setFillAlpha(65);
        aLineDataSet.setFillColor(ColorTemplate.getHoloBlue());
        aLineDataSet.setHighLightColor(Color.TRANSPARENT);
        aLineDataSet.setDrawValues(false);
        return aLineDataSet;
    }

    private void readFile(int iFileType) {
        String aFileType = iFileType == FILE_TYPE_HR ? "fhr" : "UC";
        aFileType += "-";
        String[] aNextLine, aMhr;
        try {
            CSVReader aReader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTest.inputFilePath + File.separator + aFileType + mTest.inputFilePath.substring(mTest.inputFilePath.indexOf("sattva-")) + ".txt"));
            //CSVReader aReader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTest.inputFilePath + File.separator + aFileType + mTest.id + mTest.inputFilePath.substring(mTest.inputFilePath.indexOf("-sattva-")) + ".txt"));
            //CSVReader aReader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTest.inputFileName + File.separator + aFileType + mTest.id + mTest.inputFileName.substring(mTest.inputFileName.indexOf("-sattva-")) + ".txt"));
            CSVReader aMhrReader = null;
            if (iFileType == FILE_TYPE_HR) {
                aMhrReader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTest.inputFilePath + File.separator + "mhr-" + mTest.inputFilePath.substring(mTest.inputFilePath.indexOf("sattva-")) + ".txt"));
                //aMhrReader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTest.inputFilePath + File.separator + "mhr-" + mTest.id + mTest.inputFilePath.substring(mTest.inputFilePath.indexOf("-sattva-")) + ".txt"));
            }
            while (((aNextLine = aReader.readNext()) != null)) {
                // nextLine[] is an array of values from the line
                if (iFileType == FILE_TYPE_HR) {
                    addFetalEntry(Float.parseFloat(aNextLine[0]), Float.parseFloat(aNextLine[1]));
                    setFetalHeartText(Float.parseFloat(aNextLine[1]));
                    if (((aMhr = aMhrReader.readNext()) != null))
                        setMaternalHeartText(Float.parseFloat(aMhr[1]));
                    setTestTime((long) Float.parseFloat(aNextLine[0]));
                } else {
                    addUcEntry(Float.parseFloat(aNextLine[0]), Float.parseFloat(aNextLine[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isViewDataAvailable = false;
        }
    }

    // TODO: 10-Sep-17 Optimize below method to avoid ANR Exception
    private void readPrintFile() {
        String aFileType = "print-";
        String[] aNextLine;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTest.inputFilePath + File.separator + aFileType + mTest.inputFilePath.substring(mTest.inputFilePath.indexOf("sattva-")) + ".txt");
            Log.d("TestFragment", "readPrintFile: " + file.length());

            CSVReader aReader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTest.inputFilePath + File.separator + aFileType + mTest.inputFilePath.substring(mTest.inputFilePath.indexOf("sattva-")) + ".txt"));
            //CSVReader aReader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + mTest.inputFilePath + File.separator + aFileType + mTest.id + mTest.inputFilePath.substring(mTest.inputFilePath.indexOf("-sattva-")) + ".txt"));
            while (((aNextLine = aReader.readNext()) != null)) {
                // nextLine[] is an array of values from the line
                PrintCallback.getInstance().savePrintData(aNextLine[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new MessageHelper(getActivity()).showTitleAlertOk(getString(R.string.label_info), getString(R.string.error_no_print_data), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mBPrint.setVisibility(View.GONE);
                    dialogInterface.dismiss();
                }
            });
        }
    }

    private void checkEmptyData() {
        if (mFetalChart.getData().getEntryCount() == 0 || mUcChart.getData().getEntryCount() == 0 || !isViewDataAvailable) {
            new MessageHelper(getActivity()).showTitleAlertOk(getString(R.string.label_info), getString(R.string.error_test_was_improper), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        PlotCallback.getInstance().removePlotInterface(this);
    }

    private void newTest() {

        Log.e("Patient Name: ", "" + mPatient.firstName);
        Bundle bundle = new Bundle();
//        bundle.putString("Pid",mPatient.id);
//        bundle.putString("PatientFirstName", mPatient.firstName);
//        bundle.putString("PatientLastName", mPatient.lastName);
//        bundle.putInt("PatientAge", mPatient.age);
        bundle.putSerializable("PatientObj",mPatient);

        HomeFragment aHomeFragment = new HomeFragment();
        aHomeFragment.setArguments(bundle);
        ((HomeActivity) getActivity()).addReplaceFragment(aHomeFragment, false, mPatient, false);
    }
}
