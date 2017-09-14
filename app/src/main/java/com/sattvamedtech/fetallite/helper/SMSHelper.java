package com.sattvamedtech.fetallite.helper;

import android.content.Context;
import android.telephony.SmsManager;

import com.sattvamedtech.fetallite.R;

public class SMSHelper {

    public static void sendSMS(Context iContext, String phoneNo, String msg, boolean toShowConfirmation) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            if (toShowConfirmation)
                MessageHelper.getInstance().showToast(iContext.getString(R.string.alert_sms_success));
        } catch (Exception e) {
            e.printStackTrace();
            if (toShowConfirmation)
                MessageHelper.getInstance().showToast(iContext.getString(R.string.alert_sms_success));
        }
    }

}
