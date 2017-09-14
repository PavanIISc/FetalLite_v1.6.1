package com.sattvamedtech.fetallite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.DateUtils;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.model.Admin;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.FLPreferences;

public class AboutDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private Toolbar mToolbar;
    private TextView mTvDeviceId, mTvAdmin, mTvRegistrationDate, mTvLastServiceDate;

    public AboutDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public AboutDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected AboutDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_about);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        initListeners();
        setView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTvDeviceId = (TextView) findViewById(R.id.tvDeviceId);
        mTvAdmin = (TextView) findViewById(R.id.tvAdmin);
        mTvRegistrationDate = (TextView) findViewById(R.id.tvRegistrationDate);
        mTvLastServiceDate = (TextView) findViewById(R.id.tvLastServiceDate);
    }

    private void initListeners() {
        findViewById(R.id.bClose).setOnClickListener(this);
    }

    private void setView() {
        mToolbar.setTitle(mContext.getString(R.string.label_about_fl));
        mTvDeviceId.setText(mContext.getString(R.string.label_device_id, FLPreferences.getInstance(FLApplication.getInstance()).getDeviceId()));
        Admin aAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);
        mTvAdmin.setText(mContext.getString(R.string.label_admin, aAdmin.adminid));
        String aRegistrationDate = DateUtils.convertDateToLongHumanReadable(FLPreferences.getInstance(FLApplication.getInstance()).getRegistrationDate());
        mTvRegistrationDate.setText(mContext.getString(R.string.label_registration_date, aRegistrationDate));
        String aLastServiceDate = DateUtils.convertDateToLongHumanReadable(FLPreferences.getInstance(FLApplication.getInstance()).getLastServiceDate());
        mTvLastServiceDate.setText(mContext.getString(R.string.label_last_service_date, aLastServiceDate));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bClose) {
            AboutDialog.this.dismiss();
        }
    }
}
