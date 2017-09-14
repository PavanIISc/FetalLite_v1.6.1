package com.sattvamedtech.fetallite.helper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;

public class MessageHelper {
    static Context mContext;

    private static MessageHelper mMessage;

    private static Toast mSingleToast;

    public MessageHelper(Context iContext) {
        mContext = iContext;
    }

    public MessageHelper() {
    }

    public static MessageHelper get(Context iContext) {
        mContext = iContext;
        if (mMessage == null) {
            mMessage = new MessageHelper();
        }
        return mMessage;
    }

    public static MessageHelper getInstance() {
        if (mMessage == null) {
            mMessage = new MessageHelper();
        }
        if (mSingleToast == null) {
            mSingleToast = Toast.makeText(FLApplication.getInstance().getApplicationContext(), "", Toast.LENGTH_LONG);
        }
        return mMessage;
    }

    public void showToast(String iMessage) {
        if (mSingleToast != null) {
            mSingleToast.setText(iMessage);
            mSingleToast.show();
        }
    }

    public ProgressDialog getProgressDialog(Context iContext) {
        ProgressDialog aProgressDialog = new ProgressDialog(iContext);
        aProgressDialog.setMessage(iContext.getString(R.string.label_please_wait));
        aProgressDialog.setCancelable(false);
        aProgressDialog.setCanceledOnTouchOutside(false);
        return aProgressDialog;
    }

    public void showTitleAlertOk(String iTitle, String iMessage, String iPositiveString, final DialogInterface.OnClickListener iPositiveListener) {
        showTitleAlertOk(iTitle, iMessage, iPositiveString, iPositiveListener, false);
    }

    public void showTitleAlertOk(String iTitle, String iMessage, String iPositiveString, final DialogInterface.OnClickListener iPositiveListener, boolean isCancellable) {
        AlertDialog.Builder aDialogBuilder = new AlertDialog.Builder(mContext);

        View aDialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_view_ok, null);

        ((TextView) aDialogView.findViewById(R.id.tvTitle)).setText(iTitle);
        ((TextView) aDialogView.findViewById(R.id.tvContent)).setText(iMessage);
        Button aButton = (Button) aDialogView.findViewById(R.id.bDismiss);
        if (!TextUtils.isEmpty(iPositiveString))
            aButton.setText(iPositiveString);

        aDialogBuilder.setView(aDialogView);
        final AlertDialog aDialog = aDialogBuilder.create();
        aDialog.setCancelable(isCancellable);
        aDialog.setCanceledOnTouchOutside(isCancellable);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPositiveListener.onClick(aDialog, 0);
            }
        });

        aDialog.show();
    }

    public void showTitleAlertOkCustomView(String iTitle, int iResource) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View aView = inflater.inflate(iResource, null);

        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle(iTitle);
        dialog.setView(aView);

        dialog.getWindow().setGravity(Gravity.TOP);

        dialog.show();
    }

    public void showTitleAlertOkCancel(String iTitle, String iMessage, String iPositiveString, String iNegativeString, final DialogInterface.OnClickListener iPositiveListener, final DialogInterface.OnClickListener iNegativeListener) {
        AlertDialog.Builder aDialogBuilder = new AlertDialog.Builder(mContext);

        View aDialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_view_ok_cancel, null);

        ((TextView) aDialogView.findViewById(R.id.tvTitle)).setText(iTitle);
        ((TextView) aDialogView.findViewById(R.id.tvContent)).setText(iMessage);
        Button aPositiveButton = (Button) aDialogView.findViewById(R.id.bPositive);
        Button aNegativeButton = (Button) aDialogView.findViewById(R.id.bNegative);

        if (!TextUtils.isEmpty(iPositiveString))
            aPositiveButton.setText(iPositiveString);
        if (!TextUtils.isEmpty(iNegativeString))
            aPositiveButton.setText(iNegativeString);

        aDialogBuilder.setView(aDialogView);
        final AlertDialog aDialog = aDialogBuilder.create();
        aDialog.setCancelable(false);
        aDialog.setCanceledOnTouchOutside(false);
        aPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPositiveListener.onClick(aDialog, 0);
            }
        });

        aNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNegativeListener.onClick(aDialog, 0);
            }
        });

        aDialog.show();
    }

    public void showTitleAlertOkCancelDelete(String iTitle, String iMessage, String iPositiveString, String iNegativeString, DialogInterface.OnClickListener iPositiveListener, DialogInterface.OnClickListener iNegativeListener) {
        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle(iTitle);
        dialog.setMessage(iMessage);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                iPositiveString,
                iPositiveListener);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                iNegativeString,
                iNegativeListener);

        dialog.show();
    }
}
