package com.sattvamedtech.fetallite.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.DeviceRegistrationActivity;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.activity.SystemSyncActivity;
import com.sattvamedtech.fetallite.activity.TutorialsActivity;
import com.sattvamedtech.fetallite.adapter.NavigationAdapter;
import com.sattvamedtech.fetallite.dialog.AboutDialog;
import com.sattvamedtech.fetallite.dialog.AddHospitalAdminLogInDialog;
import com.sattvamedtech.fetallite.dialog.AddHospitalDialog;
import com.sattvamedtech.fetallite.dialog.AdminDetailsAdminLoginDialog;
import com.sattvamedtech.fetallite.dialog.AdminLoginDialog;
import com.sattvamedtech.fetallite.dialog.ChooseHospitalDialog;
import com.sattvamedtech.fetallite.dialog.DoctorDirectoryDialog;
import com.sattvamedtech.fetallite.dialog.ForgotPasswordAdminDialog;
import com.sattvamedtech.fetallite.helper.DateUtils;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.SessionHelper;
import com.sattvamedtech.fetallite.model.DeviceRegistration;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.NavigationMenuItem;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.util.ArrayList;
import java.util.Calendar;

public class NavigationFragment extends Fragment implements View.OnClickListener {

    private TextView mTvHospitalName, mTvDate, mTvUserId, mTvVersion;
    private ImageButton mIbClose;
    private ArrayList<NavigationMenuItem> mNavigationMenuItems = new ArrayList<>();
    private RecyclerView mRvNavigationList;
    private NavigationAdapter mAdapter;
    private ImageView mIvSystemSync;
    private long lastClickTime = 0;
    private int systemSyncCount = 0;
    private ForgotPasswordAdminDialog forgotPasswordAdminDialog;
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

    private NavigationAdapter.NavigationMenuItemClick mNavigationMenuItemClick = new NavigationAdapter.NavigationMenuItemClick() {
        @Override
        public void onNavigationMenuItemClick(int iPosition) {
            if (mNavigationMenuItems.get(iPosition).enable || !((HomeActivity) getActivity()).isTestInProgress()) {
                ((HomeActivity) getActivity()).closeDrawer();
                if (SessionHelper.isLoginSessionValid() || ((HomeActivity) getActivity()).isTestInProgress()) {
                    if (mNavigationMenuItems.get(iPosition).id == VIEW_PATIENT_DATA && !(((HomeActivity) getActivity()).getCurrentFragment() instanceof PatientTestDataFragment)) {
                        ((HomeActivity) getActivity()).addReplaceFragment(new PatientTestDataFragment(), true, null, false);
                    } else if (mNavigationMenuItems.get(iPosition).id == DOCTOR_DIR) {
                        new DoctorDirectoryDialog(getActivity(), R.style.full_screen_dialog).show();
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
                        ((HomeActivity) getActivity()).prepareForSms(true, "");
                    } else if (mNavigationMenuItems.get(iPosition).id == ABOUT) {
                        new AboutDialog(getActivity()).show();
                    } else if (mNavigationMenuItems.get(iPosition).id == LOGOUT) {
                        ((HomeActivity) getActivity()).confirmLogout();
                    }
                } else {
                    ((HomeActivity) getActivity()).invalidSession();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
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
        mAdapter = new NavigationAdapter(getActivity(), mNavigationMenuItems, mNavigationMenuItemClick);
        mRvNavigationList.setAdapter(mAdapter);
        mIvSystemSync = (ImageView) iView.findViewById(R.id.ivSystemSync);
        mTvVersion = (TextView) iView.findViewById(R.id.tvVersion);

        forgotPasswordAdminDialog = new ForgotPasswordAdminDialog(getContext());
        forgotPasswordAdminDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EditText mPwd = (EditText) forgotPasswordAdminDialog.findViewById(R.id.etAdminPassword);
                TextView mTvForgotPassword = (TextView) forgotPasswordAdminDialog.findViewById(R.id.tvAdminForgotPassword);
                EditText mEtQ = (EditText) forgotPasswordAdminDialog.findViewById(R.id.etQuestion);
                EditText mAns = (EditText) forgotPasswordAdminDialog.findViewById(R.id.etAnswer);
                EditText mNwPwd = (EditText) forgotPasswordAdminDialog.findViewById(R.id.etAdminNewPassword);
                EditText mCfPwd = (EditText) forgotPasswordAdminDialog.findViewById(R.id.etAdminConfirmPassword);
                Button  mBlogin = (Button) forgotPasswordAdminDialog.findViewById(R.id.bLogin);
                TextInputLayout mtilAdminNewPassword = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilAdminNewPassword);
                TextInputLayout mtilAnswer = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilAnswer);
                TextInputLayout mtilQuestion = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilQuestion);
                TextInputLayout mtilAdminPassword = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilAdminPassword);
                TextInputLayout mtilAdminConfirmPassword = (TextInputLayout)forgotPasswordAdminDialog.findViewById(R.id.tilAdminConfirmPassword);

                mtilAdminPassword.setVisibility(View.VISIBLE);
                mPwd.setVisibility(View.VISIBLE);
                mPwd.setText("");
                mTvForgotPassword.setVisibility(View.VISIBLE);
                mtilAdminNewPassword.setVisibility(View.GONE);
                mtilAnswer.setVisibility(View.GONE);
                mtilQuestion.setVisibility(View.GONE);
                mtilAdminConfirmPassword.setVisibility(View.GONE);
                mEtQ.setVisibility(View.GONE);
                mAns.setVisibility(View.GONE);
                mNwPwd.setVisibility(View.GONE);
                mCfPwd.setVisibility(View.GONE);
                mBlogin.setText("LOGIN");



            }
        });
    }

    private void initListeners() {
        mIbClose.setOnClickListener(this);
        mIvSystemSync.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibClose) {
            ((HomeActivity) getActivity()).closeDrawer();
        } else if (v.getId() == R.id.ivSystemSync) {
            openSystemSycn();
        }
    }

    private void setView() {
        Hospital aHospital = FLPreferences.getInstance(FLApplication.getInstance()).getSessionHospital();
        if (aHospital != null)
            mTvHospitalName.setText(aHospital.name);
        String aUserJson = FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson();
        if (!TextUtils.isEmpty(aUserJson)) {
            User aUser = (User) GsonHelper.getGson(aUserJson, User.class);
            mTvUserId.setText(aUser.userid);
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
        mNavigationMenuItems.add(new NavigationMenuItem(VIEW_PATIENT_DATA, getString(R.string.item_view_patient_data), false));
        mNavigationMenuItems.add(new NavigationMenuItem(DOCTOR_DIR, getString(R.string.item_doctor_dir), false));
        mNavigationMenuItems.add(new NavigationMenuItem(HOSPITAL_SETTINGS, getString(R.string.item_hospital_details), false));
        mNavigationMenuItems.add(new NavigationMenuItem(ADD_NEW_HOSPITAL, getString(R.string.item_add_new_hospital), false));
        mNavigationMenuItems.add(new NavigationMenuItem(ADMIN_DETAILS, getString(R.string.item_admin_details), false));
        //mNavigationMenuItems.add(new NavigationMenuItem(ADMIN_DASH, getString(R.string.item_admin_dash), false));
        mNavigationMenuItems.add(new NavigationMenuItem(TUTORIAL, getString(R.string.item_tutorial), false));
        mNavigationMenuItems.add(new NavigationMenuItem(CUSTOMER, getString(R.string.item_customer), true));
        mNavigationMenuItems.add(new NavigationMenuItem(ABOUT, getString(R.string.item_about), true));
        mNavigationMenuItems.add(new NavigationMenuItem(LOGOUT, getString(R.string.item_logout), false));
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
