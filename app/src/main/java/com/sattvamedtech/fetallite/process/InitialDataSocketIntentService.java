package com.sattvamedtech.fetallite.process;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.sattvamedtech.fetallite.helper.ApplicationUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Pavan on 8/30/2017.
 */

public class InitialDataSocketIntentService extends IntentService
{
    private final IBinder mBinder = new LocalBinder();
    private ServerSocket mServerSocket;
    private Socket mClientConnection;
    public static final int SERVERPORT = 8080;
    private BufferedReader mBufferedInputReader;
    private boolean toReadData;
    private boolean localBatteryFLag = false;

    private Handler handler;


    private static final String RESPONSE_BATTERY_OK_WAIT = "+OK+";

    private static final String CHECK_BATTERY_LOW = "+LOW+";

    private String SEND_DEVICE_ID = null;

    private static final String RESPONSE_READY = "+ready+";
    private static final String GET_BATTERY_VALUES = "+c+";
    private static final String RESPONSE_SYNC_SUCCESFUL = "+sync+";

    private static final String RESPONSE_WRONG_ID = "+incorrect+";
    private String SENT_STRING;

    private DataSocketCallback mDataSocketCallback;

    public InitialDataSocketIntentService() {
        super("DataSocketIntentService");
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

    private void startSocketServer()
    {
        try
        {
            Log.e("DataSocketIntentService", "Starting socket server");
            mServerSocket = new ServerSocket(SERVERPORT);
            Log.e("DataSocketIntentService", "server port = " + mServerSocket.getLocalPort());
            mClientConnection = mServerSocket.accept();
            Log.e("DataSocketIntentService", "server socket accepted");
            mClientConnection.setTcpNoDelay(true);
            InputStreamReader aInputStreamReader = new InputStreamReader(mClientConnection.getInputStream());
            mBufferedInputReader = new BufferedReader(aInputStreamReader);
            Log.e("DataSocketIntentService","End of socket servere buffer reading");

            waitForInitResponse();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void waitForInitResponse() {
        Log.e("DataSocketIntentService", "Waiting for initial response from device");
        toReadData = true;
        new ReadMessageOverSocket(true).execute();
    }

    private void readDeviceID() {
        Log.e("DataSocketIntentService", "Reading Device ID");
        toReadData = true;
        new ReadMessageOverSocket(false).execute();
    }

    private void  readBatteryValues(){
        Log.e("DataSocketIntentService", "Reading Battery values trd" + toReadData + " " + mClientConnection.isConnected() + " ");
        toReadData = true;
        new ReadMessageOverSocket(false).execute();
    }

    public void sendDeviceID() throws InterruptedException {
        Log.e("InitialData", "Sending Device ID");
        SEND_DEVICE_ID = ApplicationUtils.mDeviceID;
        Log.e("DataSocketIntentService", "" + ApplicationUtils.mDeviceID);

        SENT_STRING = SEND_DEVICE_ID.substring(4,6) + SEND_DEVICE_ID.substring(7,12);
        new SendMessageOverSocket(SENT_STRING).execute();

    }
    public void getBatteryValue()
    {
        toReadData = true;
        Log.e("DataSocketIntentService", "Sending +c+");
        new SendMessageOverSocket(GET_BATTERY_VALUES).execute();
    }

    public void breakConnection()
    {
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class ReadMessageOverSocket extends AsyncTask<Void, Void, Void>
    {
        boolean toWaitForDevice;

        public ReadMessageOverSocket(boolean toWaitForDevice)
        {
            this.toWaitForDevice = toWaitForDevice;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {

            Log.e("InitialData", "in background");

            if (mClientConnection != null)
            {
                while ((toWaitForDevice && toReadData) || (toReadData && mClientConnection.isConnected() && !mClientConnection.isClosed()))
                {
                    try
                    {

                        String aMessage = mBufferedInputReader.readLine();
                        Log.e("InitialData", "aMessage = " + aMessage);

                        if (!TextUtils.isEmpty(aMessage))
                        {
                           Log.e("DataSocketIntentService"," " + aMessage);

                            if (toWaitForDevice && !TextUtils.isEmpty(aMessage) && aMessage.equals(RESPONSE_BATTERY_OK_WAIT) && !ApplicationUtils.mSensorUnitReady)
                            {
                                Log.e("InitialData", "waiting: " + aMessage);
                                ApplicationUtils.enableStartSync = true;
                                mDataSocketCallback.onClientConnected();
                                break;
                            }
                            else if (aMessage.equals(CHECK_BATTERY_LOW)) {
                                Log.e("DataSocketIntentService","Battery Low");
                                ApplicationUtils.enableStartSync = true;
                                ApplicationUtils.mSensorUnitBatteryLow = true;
                                mDataSocketCallback.onClientConnected();
                                break;
                            }

                            else if(aMessage.length() == 5 && !ApplicationUtils.batteryValueRead && !localBatteryFLag)
                            {


                                ApplicationUtils.tempBatteryValue = new Double((0.0063*Integer.parseInt(aMessage.substring(1),16))+0.0684);
                                ApplicationUtils.batteryValueRead = true;
                                localBatteryFLag = true;
                                mDataSocketCallback.onClientConnected();
                                Log.e("InitialData", "Got Battery Value = " + ApplicationUtils.tempBatteryValue);
                                break;

                            }

                            else if(aMessage.equals(RESPONSE_READY))
                            {
                                Log.e("DataSocketIntentService","Device ID received at sensor unit, resetting Hotspot: " + aMessage);
                                ApplicationUtils.mSensorUnitReady = true;
                                mDataSocketCallback.onClientConnected();
                                break;

                            }
                            else if(aMessage.equals(RESPONSE_SYNC_SUCCESFUL))
                            {
                                Log.e("InitialData", "SYNC succesful");

                                ApplicationUtils.syncSuccesful = true;
                                mDataSocketCallback.onClientConnected();
                                break;

                            }
                            else if(aMessage.equals(RESPONSE_WRONG_ID))
                            {

                                ApplicationUtils.wrongID = true;
                                mDataSocketCallback.onClientConnected();
                                break;

                            }



                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            toReadData = false;
            Log.e("Hotspot SSID is setup"," Admin registration");
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
            Log.e("DataSocketIntentService", "Sending message to device. Message: " + mMessage);
            try {
                BufferedWriter aBufferedWriter = new BufferedWriter(new OutputStreamWriter(mClientConnection.getOutputStream()));
                aBufferedWriter.write(mMessage);
                aBufferedWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            Log.e("IntitialData", "mMessage = " + mMessage);
            if(mMessage.equals(GET_BATTERY_VALUES)){

                Log.e("InitialData", "calling readbattery");
                readBatteryValues();
                mDataSocketCallback.onDataStreamStarted();
            }

            if (mMessage.equals(SENT_STRING))
            {
                Log.e("InitialData", "calling readDeviceID");
                readDeviceID();
                mDataSocketCallback.onDataStreamStarted();
            }
            else
            {
                Log.e("InitialData", "no mMessage");
                mDataSocketCallback.onDataStreamStopped();
            }
        }
    }

    public void registerCallback(Activity iActivity) {
        mDataSocketCallback = (DataSocketCallback) iActivity;
    }

    public class LocalBinder extends Binder {
        public InitialDataSocketIntentService getSocketIntentService() {
            return InitialDataSocketIntentService.this;
        }
    }



    public interface DataSocketCallback {
        void onClientConnected();

        void onDataStreamStarted();

        void onDataStreamStopped();
    }
}
