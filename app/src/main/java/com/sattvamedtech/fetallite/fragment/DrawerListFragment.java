package com.sattvamedtech.fetallite.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.DeviceRegistrationActivity;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.activity.LoginActivity;
import com.sattvamedtech.fetallite.activity.SystemSyncActivity;
import com.sattvamedtech.fetallite.activity.TutorialsActivity;
import com.sattvamedtech.fetallite.adapter.DrawerListAdapter;
import com.sattvamedtech.fetallite.dialog.AboutDialog;
import com.sattvamedtech.fetallite.dialog.AddHospitalAdminLogInDialog;
import com.sattvamedtech.fetallite.dialog.AdminDetailsAdminLoginDialog;
import com.sattvamedtech.fetallite.dialog.AdminLoginDialog;
import com.sattvamedtech.fetallite.dialog.ChooseHospitalDialog;
import com.sattvamedtech.fetallite.helper.DateUtils;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.model.DeviceRegistration;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.NavigationMenuItem;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Pavan on 8/1/2017.
 */

public class DrawerListFragment extends Fragment implements View.OnClickListener {

    private HomeActivity mHomeActivity = new HomeActivity();
    private TextView mTvHospitalName, mTvDate, mTvUserId, mTvVersion;
    private ImageButton mIbClose;
    private ArrayList<NavigationMenuItem> mNavigationMenuItems = new ArrayList<>();
    private RecyclerView mRvNavigationList;
    private DrawerListAdapter mAdapter;
    private ImageView mIvSystemSync;
    private long lastClickTime = 0;
    private int systemSyncCount = 0;
    private Handler mSystemSyncHandler = new Handler();
    private Runnable mSystemSyncRunnable = new Runnable() {
        @Override
        public void run() {
            lastClickTime = 0;
            systemSyncCount = 0;
        }
    };

    private static final int BRIGHTNESS = 0;
    private static final int THEME = 1;
    private static final int VIEW_PATIENT_DATA = 2;
    private static final int DOCTOR_DIR = 3;
    private static final int HOSPITAL_SETTINGS = 4;
    private static final int ADD_NEW_HOSPITAL = 5;
    private static final int ADMIN_DETAILS = 6;
    private static final int ADMIN_DASH = 7;
    private static final int TUTORIAL = 8;
    private static final int CUSTOMER = 9;
    private static final int ABOUT = 10;
    private static final int LOGOUT = 11;

    private static String mVersionName = "";

    private DrawerListAdapter.NavigationMenuItemClick mNavigationMenuItemClick = new DrawerListAdapter.NavigationMenuItemClick() {
        @Override
        public void onNavigationMenuItemClick(int iPosition) {
            if (mNavigationMenuItems.get(iPosition).enable || !new HomeActivity().isTestInProgress()) {
                //new HomeActivity().closeDrawer();
                    if (mNavigationMenuItems.get(iPosition).id == VIEW_PATIENT_DATA && !(new HomeActivity().getCurrentFragment() instanceof PatientTestDataFragment)) {
                        //new HomeActivity().addReplaceFragment(new PatientTestDataFragment(), true, null, false);
                    } else if (mNavigationMenuItems.get(iPosition).id == DOCTOR_DIR) {
                        //new DoctorDirectoryDialog(getActivity(), R.style.full_screen_dialog).show();
                    }else if (mNavigationMenuItems.get(iPosition).id == HOSPITAL_SETTINGS) {
                            new ChooseHospitalDialog(getActivity()).show();
                    }else if (mNavigationMenuItems.get(iPosition).id == ADD_NEW_HOSPITAL) {
                        new AddHospitalAdminLogInDialog(getActivity()).show();
                    }else if (mNavigationMenuItems.get(iPosition).id == ADMIN_DETAILS) {
                        new AdminDetailsAdminLoginDialog(getActivity()).show();
                    }else if (mNavigationMenuItems.get(iPosition).id == ADMIN_DASH) {
                        new AdminLoginDialog(getActivity()).show();
                    } else if (mNavigationMenuItems.get(iPosition).id == TUTORIAL) {
                        Intent aIntent = new Intent(getActivity(), TutorialsActivity.class);
                        startActivity(aIntent);
                    } else if (mNavigationMenuItems.get(iPosition).id == CUSTOMER) {
                        if (getActivity().getClass() == HomeActivity.class) {
                            ((HomeActivity) getActivity()).prepareForSms(true, "");
                        } else if (getActivity().getClass() == LoginActivity.class) {
                            // TODO: 04-Sep-17 Check validity of below pasted code
                            ((LoginActivity) getActivity()).prepareForSms(true, "");
                        }
                    } else if (mNavigationMenuItems.get(iPosition).id == ABOUT) {
                        new AboutDialog(getActivity()).show();
                    } else if (mNavigationMenuItems.get(iPosition).id == LOGOUT) {
                        ((HomeActivity) getActivity()).confirmLogout();
                    }
                } else {
                    //new HomeActivity().invalidSession();
                }
            }

    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListeners();
        initMenuItems();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setView();
    }

    private void initView(View iView) {
        mTvHospitalName = (TextView) iView.findViewById(R.id.tvHospitalName);
        mTvDate = (TextView) iView.findViewById(R.id.tvDate);
        mTvUserId = (TextView) iView.findViewById(R.id.tvUserId);
        mIbClose = (ImageButton) iView.findViewById(R.id.ibClose);
        mRvNavigationList = (RecyclerView) iView.findViewById(R.id.rvNavigationList);
        mRvNavigationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DrawerListAdapter(getActivity(), mNavigationMenuItems, mNavigationMenuItemClick);
        mRvNavigationList.setAdapter(mAdapter);
        mIvSystemSync = (ImageView) iView.findViewById(R.id.ivSystemSync);
        mTvVersion = (TextView) iView.findViewById(R.id.tvVersion);
    }

    private void initListeners() {
        mIbClose.setOnClickListener(this);
        mIvSystemSync.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibClose) {
            ((LoginActivity) getActivity()).closeDrawer();
        } else if (v.getId() == R.id.ivSystemSync) {
            openSystemSycn();
        }
    }

    private void setView() {

        Hospital aHospital = FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital();
        if (aHospital != null)
            mTvHospitalName.setText(aHospital.name);
        else
            mTvHospitalName.setText("");
        String aUserJson = FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson();
        if (!TextUtils.isEmpty(aUserJson)) {
            User aUser = (User) GsonHelper.getGson(aUserJson, User.class);
            mTvUserId.setText(aUser.userid);
        }
        else{
            mTvUserId.setText("");
        }
        setDate();
        mTvVersion.setText("Version : " + getResources().getString(R.string.version).toString());



    }

    public void setDate() {
        Calendar aCalendar = Calendar.getInstance();
        mTvDate.setText(DateUtils.convertDateToLongHumanReadable(aCalendar.getTimeInMillis()));
    }

    private void initMenuItems() {
        mNavigationMenuItems.clear();
        mNavigationMenuItems.add(new NavigationMenuItem(BRIGHTNESS, getString(R.string.item_brightness), true, NavigationMenuItem.VIEW_BRIGHTNESS));
        mNavigationMenuItems.add(new NavigationMenuItem(THEME, getString(R.string.item_theme), true, NavigationMenuItem.VIEW_THEME));
        //mNavigationMenuItems.add(new NavigationMenuItem(VIEW_PATIENT_DATA, getString(R.string.item_view_patient_data), false));
        //mNavigationMenuItems.add(new NavigationMenuItem(DOCTOR_DIR, getString(R.string.item_doctor_dir), false));
        mNavigationMenuItems.add(new NavigationMenuItem(HOSPITAL_SETTINGS, getString(R.string.item_hospital_details), false));
        mNavigationMenuItems.add(new NavigationMenuItem(ADD_NEW_HOSPITAL, getString(R.string.item_add_new_hospital), false));
        mNavigationMenuItems.add(new NavigationMenuItem(ADMIN_DETAILS, getString(R.string.item_admin_details), false));
        //mNavigationMenuItems.add(new NavigationMenuItem(ADMIN_DASH, getString(R.string.item_admin_dash), false));
        mNavigationMenuItems.add(new NavigationMenuItem(TUTORIAL, getString(R.string.item_tutorial), false));
        mNavigationMenuItems.add(new NavigationMenuItem(CUSTOMER, getString(R.string.item_customer), true));
        mNavigationMenuItems.add(new NavigationMenuItem(ABOUT, getString(R.string.item_about), true));
        //mNavigationMenuItems.add(new NavigationMenuItem(LOGOUT, getString(R.string.item_logout), false));
    }

    private void openSystemSycn() {
        if (lastClickTime == 0 || System.currentTimeMillis() - lastClickTime < 1000) {
            mSystemSyncHandler.removeCallbacksAndMessages(null);
            lastClickTime = System.currentTimeMillis();
            systemSyncCount++;
            if (systemSyncCount == 7) {
                Intent aIntent = new Intent(getActivity(), DeviceRegistrationActivity.class);
                startActivity(aIntent);
                lastClickTime = 0;
                systemSyncCount = 0;
            }
            mSystemSyncHandler.postDelayed(mSystemSyncRunnable, 1000);
        }
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }
}