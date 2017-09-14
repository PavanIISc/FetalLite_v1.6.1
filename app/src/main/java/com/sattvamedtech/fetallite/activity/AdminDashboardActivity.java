package com.sattvamedtech.fetallite.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.dialog.AddHospitalDialog;
import com.sattvamedtech.fetallite.fragment.AdminDashboardFragment;
import com.sattvamedtech.fetallite.fragment.HospitalDetailsFragment;
import com.sattvamedtech.fetallite.model.Hospital;

public class AdminDashboardActivity extends FLBaseActivity implements FragmentManager.OnBackStackChangedListener {

    private AddHospitalDialog mAddHospitalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        initToolbar();
        mAddHospitalDialog = new AddHospitalDialog(this);
        mAddHospitalDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mAddHospitalDialog.clearFields();
                refreshAdminDashboardFragment();
                refreshHospitalDetailsFragment();
//                if (getCurrentFragment() instanceof AdminDashboardFragment)
//                    ((AdminDashboardFragment) getCurrentFragment()).openLatestHospitalDetails();
            }
        });
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        addFragment(new AdminDashboardFragment(), false);
    }

    public void initToolbar() {
        Toolbar aToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        TextView aHeading = (TextView)  findViewById(R.id.app_toolbar_text);
        aHeading.setVisibility(View.VISIBLE);
        aHeading.setText(R.string.label_hospital_dashboard);
        aHeading.setTypeface(null, Typeface.BOLD);
        setSupportActionBar(aToolbar);
    }

    public void changeToolbar(){
        TextView aHeading = (TextView)  findViewById(R.id.app_toolbar_text);
        aHeading.setVisibility(View.VISIBLE);
        aHeading.setText(R.string.label_hospital_dashboard);
        aHeading.setTypeface(null, Typeface.BOLD);
    }

    public void addFragment(Fragment iFragment, boolean iAddToBackStack) {
        FragmentTransaction aTransaction = getSupportFragmentManager().beginTransaction();
        if (iAddToBackStack) {
            aTransaction.add(R.id.flFragmentContainer, iFragment);
            aTransaction.addToBackStack(null);
        } else {
            aTransaction.replace(R.id.flFragmentContainer, iFragment);
        }
        aTransaction.commit();
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.flFragmentContainer);
    }
    /*****************************************28-06-17*************************/
    public void showAddHospitalDialog() {
            mAddHospitalDialog.show();

    }
    /******************************************************************************/

    public void showEditHospitalDialog(Hospital iHospital) {
        mAddHospitalDialog.showEdit(iHospital);
    }

    @Override
    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
//        } else {
//            super.onBackPressed();
//        }
    }



    private void refreshAdminDashboardFragment() {
        if (getCurrentFragment() instanceof AdminDashboardFragment) {
            ((AdminDashboardFragment) getCurrentFragment()).fetchHospitals();
        }
    }

    private void refreshHospitalDetailsFragment() {
        if (getCurrentFragment() instanceof HospitalDetailsFragment) {
            ((HospitalDetailsFragment) getCurrentFragment()).setHospital();
        }
    }

    @Override
    public void onBackStackChanged() {
        refreshAdminDashboardFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }



}
