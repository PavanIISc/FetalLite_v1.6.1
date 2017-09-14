package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavan on 8/22/2017.
 */

public class ChooseHospitalDialog extends Dialog implements View.OnClickListener  {

    private Context mContext;
    private Spinner mSHospital;
    HomeActivity mHomeActivity = new HomeActivity();

    private List<Hospital> mHospitalList = new ArrayList<>();
    private Button mBOk, mBCancel;

    public ChooseHospitalDialog(Context context) {
        super(context);
        mContext = context;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choose_hospital);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        initHospitalList();
        initListeners();
        //setViewData();
    }
    private void initView() {
        mSHospital = (Spinner) findViewById(R.id.sHospitalList);
        mBOk = (Button) findViewById(R.id.bOk);
        mBCancel = (Button) findViewById(R.id.bCancel);
    }

    private void initListeners() {
        mBOk.setOnClickListener(this);
        mBCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bOk) {
            //Hospital settings screen should open
            //mHomeActivity.addReplaceFragment(new HospitalDetailsFragment(), true, null, false);
//            ((HomeActivity) getContext()).addReplaceFragment(new HospitalDetailsFragment(), true, null, false);
            new AdminLoginDialog(getContext()).show();
        }
        if(view.getId() == R.id.bCancel){
            //previous screen should open
            this.dismiss();
        }
    }

    private void initHospitalList(){
        mHospitalList.addAll(DatabaseHelper.getInstance(getContext()).getAllHospital());
        if(mHospitalList.size() > 1){
            mHospitalList.add(0,new Hospital("Choose Hospital","","",""));

        }

        ArrayAdapter<Hospital> HospitalAdapter = new ArrayAdapter<>(mContext,R.layout.item_spinner,mHospitalList);
        mSHospital.setAdapter(HospitalAdapter);
        mSHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if(position > 0)
                   // enableFields();
                //HospitalDetailsFragment aHospitalDetailsFragment = new HospitalDetailsFragment();
                ApplicationUtils.mHospital = mHospitalList.get(position);
                 ApplicationUtils.mHospitalID = mHospitalList.get(position).hospitalId;
//                Bundle aBundle = new Bundle();
//                aBundle.putSerializable(Constants.EXTRA_HOSPITAL, mHospitalList.get(position));
                Log.e("ChooseHospital","" + mHospitalList.get(position).name);
                //aHospitalDetailsFragment.setArguments(aBundle);
                //((AdminDashboardActivity) getActivity()).addFragment(aHospitalDetailsFragment, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
    }

}
