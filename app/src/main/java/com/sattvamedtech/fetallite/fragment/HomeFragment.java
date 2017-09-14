package com.sattvamedtech.fetallite.fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.adapter.WifiAdapter;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.SessionHelper;
import com.sattvamedtech.fetallite.model.Patient;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class HomeFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button mBNewTest, mBViewData;
    private Switch mSHeart;
    private TextView mTVWhoseHeart;

    String mLastPatientId = "";
    String mLastPatientFirstName = "";
    String mLastPatientLastName = "";
    Patient mPatient ;
    int mLastPatientAge = 0;
    String mHospitalName;
    static int uniqueInt = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
       // setToolbar();
        initListeners();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setToolbar();

    }

    public void setToolbar() {
        ((HomeActivity) getActivity()).setTitle(null);
        ((HomeActivity) getActivity()).showMenuIcon(true);
    }

    private void initView(View iView) {
        mBNewTest = (Button) iView.findViewById(R.id.bNewTest);
        mBViewData = (Button) iView.findViewById(R.id.bViewData);
        mSHeart = (Switch) iView.findViewById(R.id.sHeartRate);
        mTVWhoseHeart = (TextView) iView.findViewById(R.id.tvHeart);


        initWifiHotspot();


        if(ApplicationUtils.mTestAgain == 1){
            mPatient = (Patient) getArguments().getSerializable("PatientObj");
            //  Log.e("HomeFragment : ", "Name: " + mLastPatientFirstName);
            Calendar aCalendar = Calendar.getInstance();
            NewTestDialog mNewTestDialog = new NewTestDialog(getActivity(), aCalendar);
            mNewTestDialog.populatePatientData(mPatient);
            mNewTestDialog.show();
             ((HomeActivity) getActivity()).startDataStream();

        }

    }


    private void initListeners() {
        mBNewTest.setOnClickListener(this);
        mBViewData.setOnClickListener(this);
        mSHeart.setOnCheckedChangeListener(this);
    }
    private void initWifiHotspot()
   {
       ApplicationUtils.SSID = "FetalLite"; // FLPreferences.getInstance(FLApplication.getInstance()).getWifiSSID();
        ApplicationUtils.wifipassword = "12345678" ; //FLPreferences.getInstance(FLApplication.getInstance()).getWifiPassword();
        Log.e("HomeFragment", " " + ApplicationUtils.SSID + " " + ApplicationUtils.wifipassword);
        WifiAdapter.configApState(getActivity());
    }
    @Override
    public void onClick(View view) {
        if (SessionHelper.isLoginSessionValid()) {
            if (view.getId() == R.id.bNewTest) {
                if (checkReadPermission()) {
                   if(ApplicationUtils.mTestAgain == 1)
                        passTestAgainPatientData();
                    else
                       newTest();

                }
            } else if (view.getId() == R.id.bViewData) {
                ((HomeActivity) getActivity()).addReplaceFragment(new PatientTestDataFragment(), true, null, false);
            }
        } else {
            ((HomeActivity) getActivity()).invalidSession();
        }
    }

    /**
     * Marshmallow permission check
     *
     * @return boolean
     */
    boolean checkReadPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.RC_READ_PERMISSION);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mTVWhoseHeart.setText(b ? getString(R.string.label_fhr) : getString(R.string.label_mhr));
//        FLApplication.isFetalEnabled = b;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case Constants.RC_READ_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    newTest();
                }
                break;
        }
    }

    public void enableNewTest(boolean isConnected) {
        mBNewTest.setEnabled(isConnected);
    }

    private void passTestAgainPatientData(){
//        mLastPatientId = getArguments().getString("Pid");
//        mLastPatientFirstName = getArguments().getString("PatientFirstName");
//        mLastPatientLastName = getArguments().getString("PatientLastName");
//        mLastPatientAge = getArguments().getInt("PatientAge");
        mPatient = (Patient) getArguments().getSerializable("PatientObj");
      //  Log.e("HomeFragment : ", "Name: " + mLastPatientFirstName);

        NewTestDialog mNewTestDialog = new NewTestDialog(getActivity(), null);
        mNewTestDialog.populatePatientData(mPatient);
        mNewTestDialog.show();

//        Bundle passPatientDetails = new Bundle();
//        passPatientDetails.putString("OldPatientName",mLastPatientFirstName);

    }

    private void newTest() {

        ((HomeActivity) getActivity()).resetApplicationUtils();
        Calendar aCalendar = Calendar.getInstance();
        (new NewTestDialog(getActivity(), aCalendar)).show();
//        mHospitalName = getArguments().getString("HospitalName");
        //uniqueInt++;
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
        ((HomeActivity) getActivity()).startDataStream();

    }


}
