package com.sattvamedtech.fetallite.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.adapter.RiskFactorAdapter;
import com.sattvamedtech.fetallite.dialog.ConfirmEnteredDetailsDialog;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.Doctor;
import com.sattvamedtech.fetallite.model.Patient;
import com.sattvamedtech.fetallite.model.RiskFactor;
import com.sattvamedtech.fetallite.model.Test;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class NewTestDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Context mContext;

    private Toolbar mToolbar;
    private AutoCompleteTextView mEtPatientId;
    private EditText mEtFirstName, mEtLastName, mEtAge;
    private Spinner mSRiskFactor, mSGravidity, mSParity, mSDoctor;
    private RiskFactorAdapter mRskFactorAdapter;
    private ConfirmEnteredDetailsDialog mConfirmDialog;

  //  private ArrayAdapter<CharSequence> mRskFactorAdapter;
    private ArrayAdapter<CharSequence>  mGravidityAdapter, mParityAdapter;
    private ArrayList<Doctor> mDoctorList = new ArrayList<>();
    private ArrayList<RiskFactor> mRiskFactorList = new ArrayList<>();
    private ArrayAdapter<Doctor> mDoctorAdapter;
    private TextView mTvTestDuration;
    private SeekBar mSbTestDuration;
    private Button mBCancel;
    private Button mBStartTest;
    private NumberPicker mNpWeek, mNpDay;
    private DatePickerDialog mDatePicker;
    private Calendar mFileTimestampCalendar;
    private Calendar mDoBCalendar = Calendar.getInstance();
    private Patient mPatient;
    private Test mTest;

    String mLastPatientId = "";
    String mLastPatientFirstName = "";
    String mLastPatientLastName = "";
    int mLastPatientAge = 0;



    private static final int STEP_SIZE = 15;



    public NewTestDialog(Context context, Calendar iCalendar) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_test);
        mFileTimestampCalendar = iCalendar;
        if (getWindow() != null)
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContext = context;
        initView();
        setSpinnerData();
        //populateLastPatientData();
        setNumberPickers();
        initListeners();

    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mToolbar.setTitle(mContext.getString(R.string.label_new_test));
        Log.e("I am in init view","NewTestDialog");
        mEtPatientId = (AutoCompleteTextView) findViewById(R.id.etPatientId);
        mEtAge = (EditText) findViewById(R.id.etAge);
        mEtFirstName = (EditText) findViewById(R.id.etFirstName);
        mEtLastName = (EditText) findViewById(R.id.etLastName);

        mSRiskFactor = (Spinner) findViewById(R.id.sRiskFactor);
        mSGravidity = (Spinner) findViewById(R.id.sGravidity);
        mSParity = (Spinner) findViewById(R.id.sParity);
        mSDoctor = (Spinner) findViewById(R.id.sDoctor);

        mTvTestDuration = (TextView) findViewById(R.id.tvTestDuration);
        mSbTestDuration = (SeekBar) findViewById(R.id.sbTestDuration);
         //mSbTestDuration.setProgress(15);
        // 06/07/2017: Have mapped 0 ->15, 15-> 30 and so on as seekBar has default min as 0 and qwe don't want that.
        mTvTestDuration.setText(mContext.getString(R.string.label_test_duration, mSbTestDuration.getProgress()+15, 0));

        mNpWeek = (NumberPicker) findViewById(R.id.npWeek);
        mNpDay = (NumberPicker) findViewById(R.id.npDay);

        mBCancel = (Button) findViewById(R.id.bCancel);
        mBStartTest = (Button) findViewById(R.id.bStartTest);


        resetCalendarTime(null);
      //  mDatePicker = new DatePickerDialog(mContext, mDateSetListener, mDoBCalendar.get(Calendar.YEAR), mDoBCalendar.get(Calendar.MONTH), mDoBCalendar.get(Calendar.DATE));
			 mTvTestDuration.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });		 
    }


    public void populatePatientData(Patient iPatient){
        //Log.e("populatePatient",iPatient.firstName);

        if(ApplicationUtils.mTestAgain == 1) {
            mEtPatientId.setText(iPatient.id);
        }
        mEtFirstName.setText(iPatient.firstName);
        mEtLastName.setText(iPatient.lastName);
        mEtAge.setText(""+iPatient.age);
        Log.e("NewTestDialog","Gravity :"+mGravidityAdapter.getPosition(iPatient.gravidity+""));
        mSGravidity.setSelection(mGravidityAdapter.getPosition(iPatient.gravidity+""));
        Log.e("NewTestDialog","Parity :"+mParityAdapter.getPosition(iPatient.parity+""));
        mSParity.setSelection(mParityAdapter.getPosition(iPatient.parity+""));
        mRskFactorAdapter.setSelectedItems(iPatient.riskFactor);
    }

    private void setSpinnerData() {
        String[] stringArray = mContext.getResources().getStringArray(R.array.list_risk_factors);
        for(int i = 0 ;i <  stringArray.length ;i++ ){
            RiskFactor aRiskFactor = new RiskFactor();
            aRiskFactor.setTitle(stringArray[i]);
            aRiskFactor.setSelected(false);
            mRiskFactorList.add(aRiskFactor);
        }

         mRskFactorAdapter = new RiskFactorAdapter(mContext, 0, mRiskFactorList);
      //  mRskFactorAdapter = ArrayAdapter.createFromResource(mContext, R.array.list_risk_factors, R.layout.item_spinner);
        mSRiskFactor.setAdapter(mRskFactorAdapter);

        mGravidityAdapter = ArrayAdapter.createFromResource(mContext, R.array.list_gravidity, R.layout.item_spinner);
        mSGravidity.setAdapter(mGravidityAdapter);

        mParityAdapter = ArrayAdapter.createFromResource(mContext, R.array.list_parity, R.layout.item_spinner);
        mSParity.setAdapter(mParityAdapter);

        mDoctorList.clear();
        mDoctorList.add(new Doctor("Doctor*", "", "", true));
        mDoctorList.addAll(DatabaseHelper.getInstance(FLApplication.getInstance()).getAllDoctors(FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital()));
        mDoctorAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mDoctorList);
        mSDoctor.setAdapter(mDoctorAdapter);

        ArrayList<Patient> aPatientList  = DatabaseHelper.getInstance(FLApplication.getInstance()).getAllPatient();
        final ArrayAdapter<Patient> aPatientAdapter = new ArrayAdapter<Patient>(mContext,R.layout.item_spinner,aPatientList);
        mEtPatientId.setThreshold(1);
        mEtPatientId.setAdapter(aPatientAdapter);
        mEtPatientId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                populatePatientData(aPatientAdapter.getItem(position));
            }
        });
    }

    private void setNumberPickers() {
        mNpWeek.setMinValue(35);
        mNpWeek.setMaxValue(40);
        mNpWeek.setWrapSelectorWheel(true);

        mNpDay.setMinValue(0);
        mNpDay.setMaxValue(6);
        mNpDay.setWrapSelectorWheel(true);
    }

    private void initListeners() {
        mBCancel.setOnClickListener(this);
        mBStartTest.setOnClickListener(this);
        mSbTestDuration.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.bCancel) {
            //Logger.logInfo("NewTestDialog", "Cancel test dialog");
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.label_info), mContext.getString(R.string.prompt_stop_sensor), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    NewTestDialog.this.dismiss();
                    ((HomeActivity) mContext).stopDataStream();
                }
            });

        } else if (view.getId() == R.id.bStartTest) {


            if (validParams()) {


                // 06/07/2017: Have added a check for parity position should be lesser than gravidity position.
                if(mSParity.getSelectedItemPosition() > mSGravidity.getSelectedItemPosition())
                {
                    Toast.makeText(mSParity.getContext(), "Please enter valid value for Gravidity and Parity", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    confirmEnteredDetails();
                        // checkPatientInDbAndProceed();
                }

            }
//
        }

    }


    private void confirmEnteredDetails(){

        /************ commit DB: patient details are saved in data base **********************************/
        mPatient = DatabaseHelper.getInstance(FLApplication.getInstance()).getPatientByIdNameAge(mEtPatientId.getText().toString().trim(), TextUtils.isEmpty(mEtAge.getText().toString().trim()) ? 0 : Integer.parseInt(mEtAge.getText().toString().trim()), mEtFirstName.getText().toString().trim(), mEtLastName.getText().toString().trim());

        if (mPatient == null) {
            mPatient = new Patient();
            mPatient.id = TextUtils.isEmpty(mEtPatientId.getText().toString().trim()) ? UUID.randomUUID().toString() : mEtPatientId.getText().toString().trim();
        }
        mPatient.firstName = TextUtils.isEmpty(mEtFirstName.getText().toString().trim()) ? (TextUtils.isEmpty(mPatient.firstName) ? "" : mPatient.firstName) : mEtFirstName.getText().toString().trim();
        mPatient.lastName = TextUtils.isEmpty(mEtLastName.getText().toString().trim()) ? (TextUtils.isEmpty(mPatient.lastName) ? "" : mPatient.lastName) : mEtLastName.getText().toString().trim();
        mPatient.age = TextUtils.isEmpty(mEtAge.getText().toString().trim()) ? 0 : Integer.parseInt(mEtAge.getText().toString().trim());
        mPatient.gestationalWeeks = mNpWeek.getValue();
        mPatient.gestationalDays = mNpDay.getValue();
        mPatient.riskFactor = mRskFactorAdapter.getSelectedItems();
        // Log.e("NewTestDialog :",""+aPatient.riskFactor+";");
//        aPatient.riskFactor = mSRiskFactor.getSelectedItemPosition() > 0 ? mSRiskFactor.getSelectedItem().toString() : "";
        mPatient.gravidity = mSGravidity.getSelectedItemPosition() > 0 ? Integer.parseInt(mSGravidity.getSelectedItem().toString()) : 0;
        mPatient.parity = mSParity.getSelectedItemPosition() > 0 ? Integer.parseInt(mSParity.getSelectedItem().toString()): 0;
        mPatient.user = FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserObject();
        mPatient.doctor = mSDoctor.getSelectedItemPosition() > 0 ? (Doctor) mSDoctor.getSelectedItem() : null;
        DatabaseHelper.getInstance(FLApplication.getInstance()).addPatient(mPatient);
        FLApplication.mPatientId = mPatient.id;
        /**********************************************addning patient to data base finish *************************/


        /************** commit DB: test data ***************************************************/


        Test aTest = new Test();
        aTest.id = FLApplication.mFileTimeStamp;
        aTest.testDurationInMinutes = mSbTestDuration.getProgress()+15;
        aTest.doctor =  mSDoctor.getSelectedItemPosition() > 0 ? (Doctor) mSDoctor.getSelectedItem() : null;
        aTest.testTime = mFileTimestampCalendar.getTimeInMillis();
        aTest.testDate = new SimpleDateFormat("dd/MM/yyyy").format(mFileTimestampCalendar.getInstance().getTimeInMillis());
        aTest.inputFilePath = FLApplication.mFileTimeStamp;  //+aTest.id
        aTest.patient = mPatient;
        aTest.user = FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserObject();
        aTest.hospital = FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital();
        DatabaseHelper.getInstance(FLApplication.getInstance()).addTest(aTest);
        /***********************************finish adding Test data to DB ******************************/

        mTest = aTest;

        mConfirmDialog = new ConfirmEnteredDetailsDialog(mContext,mPatient,mTest);
        mConfirmDialog.show();
        mConfirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(mConfirmDialog.isConfirmed()){
                    checkPatientInDbAndProceed(mPatient, mTest, mSbTestDuration);
                }
                mConfirmDialog.clearFields();

            }
        });


    }

    private boolean validParams() {
        //&& TextUtils.isEmpty(mEtDob.getText().toString().trim()) && TextUtils.isEmpty(mEtFirstName.getText().toString().trim()) && TextUtils.isEmpty(mEtLastName.getText().toString().trim())
        if (TextUtils.isEmpty(mEtFirstName.getText().toString().trim()) ) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_patient_name), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            return false;
        }else if (!mEtPatientId.getText().toString().trim().matches("[a-zA-Z0-9]+") ) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_Pid_invalid), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            return false;
        }else if (!mEtFirstName.getText().toString().trim().matches("[a-zA-Z .]+") ) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_fname), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            return false;
        }else if (!TextUtils.isEmpty(mEtLastName.getText().toString().trim()) && !mEtLastName.getText().toString().trim().matches("[a-zA-Z .]+") ) {
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_lname), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            return false;
        }else if(TextUtils.isEmpty(mEtAge.getText().toString().trim()) || Integer.parseInt(mEtAge.getText().toString().trim()) < 10){
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_age), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            return false;
        }else if(mSDoctor.getSelectedItemPosition() <= 0){
            new MessageHelper(mContext).showTitleAlertOk(mContext.getString(R.string.error_invalid_data), mContext.getString(R.string.error_doctor_required), "", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            return false;
        }
        return true;
    }



    public void checkPatientInDbAndProceed(Patient mPatient, Test mTest, SeekBar mSbTestDuration) {


        ApplicationUtils.algoProcessEndCount = -1;
        ApplicationUtils.lastFetalPlotXValue = 0;
       ((HomeActivity) mContext).openTestScreen(mPatient,mTest, mSbTestDuration.getProgress());
        //ApplicationUtils.mTestAgain = 0;
        NewTestDialog.this.dismiss();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        i = Math.round((float) i / STEP_SIZE) * STEP_SIZE;
        setSeekBarProgress(seekBar, i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setSeekBarProgress(SeekBar iSeekBar, int iProgress) {
        iSeekBar.setOnSeekBarChangeListener(null);
        iSeekBar.setProgress(iProgress);
        mTvTestDuration.setText(mContext.getString(R.string.label_test_duration, iProgress + 15, 0));
        iSeekBar.setOnSeekBarChangeListener(this);
        //Log.e("NewTestDialog","Duration: " + mTvTestDuration.getText().toString());
    }

    private void resetCalendarTime(Calendar iCalendar) {
        if (iCalendar != null) {
            iCalendar.set(Calendar.HOUR_OF_DAY, 0);
            iCalendar.set(Calendar.MINUTE, 0);
            iCalendar.set(Calendar.SECOND, 0);
            iCalendar.set(Calendar.MILLISECOND, 0);
        } else {
            mDoBCalendar.set(Calendar.HOUR_OF_DAY, 0);
            mDoBCalendar.set(Calendar.MINUTE, 0);
            mDoBCalendar.set(Calendar.SECOND, 0);
            mDoBCalendar.set(Calendar.MILLISECOND, 0);
        }
    }


    public void populateLastPatientData(String pid, String Fname, String Lname, int age,Patient iPatient){
        mLastPatientId = pid;
        mLastPatientFirstName = Fname;
        mLastPatientLastName = Lname;
        mLastPatientAge = age;

        mEtPatientId.setText(mLastPatientId);
        mEtFirstName.setText(mLastPatientFirstName);
        mEtLastName.setText(mLastPatientLastName);
        mEtAge.setText(Integer.toString(mLastPatientAge));


       // Log.e("NewTestDialog","From Patient Obj"+iPatient.id);
       // Toast.makeText(getContext(), "From patient OBj :Name: " + iPatient.id, Toast.LENGTH_LONG).show();


    }
}
