package com.sattvamedtech.fetallite.process;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Pavan on 8/15/2017.
 */

public class HotspotSocketIntentService extends IntentService {

    private final IBinder mBinder = new LocalBinder();
    private ServerSocket mServerSocket;
    private Socket mClientConnection;
    public static final int SERVERPORT = 8080;
    private BufferedReader mBufferedInputReader;
    private boolean toReadData;

    private static final String RESPONSE_BATTERY_OK_WAIT = "+OK+";
    private static final String RECIEVE_BATTERY_VALUES =  "+c+";
    private static final String CHECK_BATTERY_LOW = "+LOW+";
    private static final String SEND_DEVICE_ID = "SMPL";
    private static final String REQUEST_START_DATA_STREAM = "+b+";
    private static final String REQUEST_STOP_DATA_STREAM = "+s+";
    private static final String RESPONSE_READY = "ready";

    int deviceIDSent = 0;

    private DataSocketCallback mDataSocketCallback;

    public HotspotSocketIntentService() {
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

    private void startSocketServer() {
        try {
            Log.e("DataSocketIntentService", "Starting socket server");
            mServerSocket = new ServerSocket(SERVERPORT);
            Log.e("DataSocketIntentService", "server port");
            mClientConnection = mServerSocket.accept();
            Log.e("DataSocketIntentService", "server socket accepted");
            mClientConnection.setTcpNoDelay(true);
            InputStreamReader aInputStreamReader = new InputStreamReader(mClientConnection.getInputStream());
            mBufferedInputReader = new BufferedReader(aInputStreamReader);
            Log.e("DataSocketIntentService","End of socket servere buffer reading");
            //sendOK();
            waitForInitResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    private void sendOK(){
//        Log.e("DataSocketIntentService","Sending OK");
//        new SendMessageOverSocket(RESPONSE_BATTERY_OK_WAIT).execute();
//    }
    private void waitForInitResponse() {
        Log.e("DataSocketIntentService", "Waiting for initial response from device");
        toReadData = true;
        new ReadMessageOverSocket(true).execute();
    }

    private void readDeviceID() {
        Log.e("DataSocketIntentService", "Reading Device ID");
        toReadData = true;
        new ReadMessageOverSocket(true).execute();
    }

    public void sendDeviceID() {
        Log.e("DataSocketIntentService", "Sending Device ID");
        new SendMessageOverSocket(SEND_DEVICE_ID).execute();
    }


    public void sendResponseReady(){
        Log.e("DataSocketIntentService","Sending ready");
        new SendMessageOverSocket(RESPONSE_READY).execute();
    }

    public class ReadMessageOverSocket extends AsyncTask<Void, Void, Void> {

        boolean toWaitForDevice;

        public ReadMessageOverSocket(boolean toWaitForDevice) {
            this.toWaitForDevice = toWaitForDevice;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mClientConnection != null) {
                while ((toWaitForDevice && toReadData) || (toReadData && mClientConnection.isConnected() && !mClientConnection.isClosed())) {
                    try {
                        String aMessage = mBufferedInputReader.readLine();
                        if (!TextUtils.isEmpty(aMessage)) {
                            if (toWaitForDevice && !TextUtils.isEmpty(aMessage) && aMessage.equals(RESPONSE_BATTERY_OK_WAIT)) {
                                Log.e("DataSocketIntentService", "waiting: " + aMessage);
                                mDataSocketCallback.onClientConnected();
                                break;
                            } else if (aMessage.equals(CHECK_BATTERY_LOW)) {
                                Log.e("DataSocketIntentService","Battery Low");
                                break;
                            }
//                            else if (aMessage.equals(RECIEVE_BATTERY_VALUES)) {
//                                Log.e("DataSocketIntentService","Battery values are: ");
//                                break;
//                            }
                            else if(aMessage.equals(RESPONSE_READY)){
                                Log.e("DataSocketIntentService","Device ID recieved at sensor unit, resetting Hotspot: " + aMessage);
                                break;
                                //sendResponseReady();
                            }
//                            else if(!aMessage.equals(RESPONSE_BATTERY_OK_WAIT)){
//                                Log.e("DataSocketIntentService","Device ID recieved at sensor unit, resetting Hotspot at MS");
//                                break;
//                            }
                            /*****************************Server side *****************************/
                            else if(!aMessage.equals(CHECK_BATTERY_LOW) && !aMessage.equals(RESPONSE_BATTERY_OK_WAIT)){
                                sendDeviceID();

                                break;
                                //deviceIDSent = 1;
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
                aBufferedWriter.newLine();
                aBufferedWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mMessage.equals(SEND_DEVICE_ID)) {
                readDeviceID();
                mDataSocketCallback.onDataStreamStarted();
            } else {
                mDataSocketCallback.onDataStreamStopped();
            }
        }
    }

    public void registerCallback(Activity iActivity) {
        mDataSocketCallback = (DataSocketCallback) iActivity;
    }

    public class LocalBinder extends Binder {
        public HotspotSocketIntentService getSocketIntentService() {
            return HotspotSocketIntentService.this;
        }
    }

    public interface DataSocketCallback {
        void onClientConnected();

        void onDataStreamStarted();

        void onDataStreamStopped();

        void onInvalidData();
    }
}
