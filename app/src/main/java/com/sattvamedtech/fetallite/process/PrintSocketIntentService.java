package com.sattvamedtech.fetallite.process;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sattvamedtech.fetallite.helper.ExceptionHandling;
import com.sattvamedtech.fetallite.helper.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PrintSocketIntentService extends IntentService {

    private final IBinder mBinder = new LocalBinder();

    private ServerSocket mServerSocket;
    private Socket mClientConnection;
    public static final int SERVERPORT = 8081;
    private BufferedReader mBufferedInputReader;

    private static final String REQUEST_PRINT_DATA = "+p+";

    private PrintSocketCallback mSocketCallback;

    public PrintSocketIntentService() {
        super("PrintSocketIntentService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startSocketServer();
    }

    private void startSocketServer() {
        try {
            Logger.logDebug("PrintSocketIntentService", "Starting socket server");
            mServerSocket = new ServerSocket(SERVERPORT);
            mClientConnection = mServerSocket.accept();
            mClientConnection.setTcpNoDelay(true);
            InputStreamReader aInputStreamReader = new InputStreamReader(mClientConnection.getInputStream());
            mBufferedInputReader = new BufferedReader(aInputStreamReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printData(String aPrintValuesInString) {
        String aPrintMessage = REQUEST_PRINT_DATA + aPrintValuesInString;
        new SendMessageOverSocket(aPrintMessage).execute();
        new ReadMessageOverSocket().execute();
    }

    public void registerCallback(Activity iActivity) {
        mSocketCallback = (PrintSocketCallback) iActivity;
    }

    @Override
    public void onDestroy() {
        try {
            Logger.logInfo("PrintSocketIntentService", "close mBufferedInputReader");
            if (mBufferedInputReader != null)
                mBufferedInputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            Logger.logInfo("PrintSocketIntentService", "close mOutput");
//            mOutput.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            Logger.logInfo("PrintSocketIntentService", "close mClientConnection");
            if (mClientConnection != null)
                mClientConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Logger.logInfo("PrintSocketIntentService", "close mServerSocket");
            if (mServerSocket != null)
                mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public class ReadMessageOverSocket extends AsyncTask<Void, Void, Void> {

        public ReadMessageOverSocket() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String aMessage = mBufferedInputReader.readLine();
                if (!TextUtils.isEmpty(aMessage)) {
                    Logger.logInfo("PrintSocketIntentService", "message: " + aMessage);
                    if (aMessage.equals("Print Success."))
                        mSocketCallback.onDataPrintSuccess();
                    else if (aMessage.contains("Invalid Points"))
                        mSocketCallback.onDataFailure();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class SendMessageOverSocket extends AsyncTask<Void, Void, Void> {

        String mMessage;

        public SendMessageOverSocket(String iMessage) {
            mMessage = iMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Logger.logDebug("PrintSocketIntentService", "Sending message to device. Message: " + mMessage);
            try {
                BufferedWriter aBufferedWriter = new BufferedWriter(new OutputStreamWriter(mClientConnection.getOutputStream()));
                aBufferedWriter.write(mMessage);
                aBufferedWriter.newLine();
                aBufferedWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
                if (ExceptionHandling.getInstance().getExceptionListener() != null) {
                    ExceptionHandling.getInstance().getExceptionListener().onException(new Exception("Issue with printer connection. Please restart app."));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class LocalBinder extends Binder {
        public PrintSocketIntentService getSocketIntentService() {
            return PrintSocketIntentService.this;
        }
    }

    public interface PrintSocketCallback {
        void onDataPrintSuccess();

        void onDataFailure();
    }
}
