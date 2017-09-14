package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.fragment.HomeFragment;
import com.sattvamedtech.fetallite.fragment.NewTestDialog;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.model.Patient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Pavan on 9/5/2017.
 */

public class TestAgainMessageDialog extends Dialog implements View.OnClickListener{
    Context mContext;
    public static Button mBTestAgain, mBCancel;
    Patient mPatient ;

    public TestAgainMessageDialog(Context context, Patient patient) {
        super(context);
        mContext = context;
        mPatient = patient;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_test_again);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        //initHospitalList();
        initListeners();
        //setViewData();
    }

    private void initView() {
        mBTestAgain = (Button) findViewById(R.id.bDailogTestAgain);
        mBCancel = (Button) findViewById(R.id.bCancel);

        mBTestAgain.setEnabled(false);

        waitForSensorUnitToRestart();
        ApplicationUtils.mPatient = mPatient;
        ApplicationUtils.mTestAgainDialogContext = getContext();

    }

    private void initListeners() {
        mBTestAgain.setOnClickListener(this);
        mBCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bDailogTestAgain) {
            //get the patient data and pass it to new test details
            //start intent
            //open new test details dialog
            Log.e("DialogTestAgain","open New Test details");
            Log.e("DialogTestAgain","start data stream");
            newTest();

        }
        if(view.getId() == R.id.bCancel){
            //previous screen should open
        }
    }

    public static void enableTestAgain(boolean isConnected){
        mBTestAgain.setEnabled(isConnected);
    }

    private void waitForSensorUnitToRestart(){
        new HomeActivity().restartDataSocketServiceTestAgain(getContext());
    }

    private void newTest() {
        dismiss();
        new HomeActivity().resetApplicationUtils();
        Calendar aCalendar = Calendar.getInstance();
        FLApplication.mFileTimeStamp = "sattva-" + new SimpleDateFormat("MM-dd-HH-mm-ss").format(aCalendar.getTimeInMillis());
        FLApplication.mTestId = "test-" + UUID.randomUUID().toString();
        try
        {
            File aRootDir = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva");
            File aTestDir = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + FLApplication.mFileTimeStamp);

            aRootDir.mkdir();
            aTestDir.mkdir();
            Calendar logCalendar = Calendar.getInstance();

            String log_time = "sattva-" + new SimpleDateFormat("MM-dd-HH-mm-ss").format(logCalendar.getTimeInMillis());
            Process process = Runtime.getRuntime().exec("logcat -d");
            process = Runtime.getRuntime().exec( "logcat -f " + "/storage/emulated/0/sattva/" + FLApplication.mFileTimeStamp + File.separator + "Logging" + log_time + ".txt");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        HomeFragment aHomeFragment = new HomeFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable("PatientObj",mPatient);
        aHomeFragment.setArguments(bundle);
        ((HomeActivity) mContext).addReplaceFragment(aHomeFragment, false, mPatient, false);

    }

}
