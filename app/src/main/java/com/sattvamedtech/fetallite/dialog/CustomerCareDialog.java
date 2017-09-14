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

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.SMSHelper;

import java.util.Timer;
import java.util.TimerTask;


public class CustomerCareDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private Toolbar mToolbar;
    private TextView mTvTicketNo, mTvCallUs, mTvEmail;
    private String mTicketNumber;

    public CustomerCareDialog(@NonNull Context context, String iTicketNumber) {
        super(context);
        mContext = context;
        mTicketNumber = iTicketNumber;
    }

    public CustomerCareDialog(@NonNull Context context, @StyleRes int themeResId, String iTicketNumber) {
        super(context, themeResId);
        mContext = context;
        mTicketNumber = iTicketNumber;
    }

    protected CustomerCareDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, String iTicketNumber) {
        super(context, cancelable, cancelListener);
        mContext = context;
        mTicketNumber = iTicketNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_customer_care);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        initView();
        initListeners();
        setView();
        SMSHelper.sendSMS(mContext, Constants.CC_PHONE_NUMBER, String.valueOf(mTicketNumber), false);
// 06/07/2017: Implemented pop-up timeout

        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                CustomerCareDialog.this.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 120000);


    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTvTicketNo = (TextView) findViewById(R.id.tvTicketNo);
        mTvCallUs = (TextView) findViewById(R.id.tvCallUs);
        mTvEmail = (TextView) findViewById(R.id.tvEmail);
    }

    private void initListeners() {
        findViewById(R.id.bExit).setOnClickListener(this);
    }

    private void setView() {
        mToolbar.setTitle(mContext.getString(R.string.item_customer));
        mTvTicketNo.setText(mContext.getString(R.string.label_ticket_no, mTicketNumber));
        mTvCallUs.setText(mContext.getString(R.string.label_cc_phone, Constants.CC_PHONE_NUMBER));
        mTvEmail.setText(mContext.getString(R.string.label_cc_email, Constants.CC_EMAIL));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bExit) {
            CustomerCareDialog.this.dismiss();
        }
    }
}
