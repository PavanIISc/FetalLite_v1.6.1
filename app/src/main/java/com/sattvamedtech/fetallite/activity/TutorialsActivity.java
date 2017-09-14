package com.sattvamedtech.fetallite.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.FLBaseActivity;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.adapter.TutorialsAdapter;
import com.sattvamedtech.fetallite.dialog.CustomerCareDialog;
import com.sattvamedtech.fetallite.dialog.InitialAddHospitalDialog;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.SMSHelper;
import com.sattvamedtech.fetallite.model.Tutorial;
import com.sattvamedtech.fetallite.process.InitialDataSocketIntentService;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.io.File;
import java.util.ArrayList;

public class TutorialsActivity extends FLBaseActivity implements View.OnClickListener, InitialDataSocketIntentService.DataSocketCallback {

    private Button mBPlayALl, mBContactCare, mBNext;
    private RecyclerView mRvVideos, mRvGuides;
    private ArrayList<Tutorial> mVideoList = new ArrayList<>();
    private ArrayList<Tutorial> mGuideList = new ArrayList<>();
    private TutorialsAdapter mVideosAdapter, mGuidesAdapter;

    private InitialAddHospitalDialog mInitialAddHospitalDialog;
    private AdminDashboardActivity mAdminDashboardActivity;

    private Intent mDataSocketIntent;
    private boolean isDataServiceConnected;
    private InitialDataSocketIntentService mDataSocketIntentService;

    private TutorialsAdapter.TutorialItemClickListener mVideoTutorialItemClick = new TutorialsAdapter.TutorialItemClickListener() {
        @Override
        public void onTutorialItemClick(int iPosition) {
            openVideo(mVideoList.get(iPosition));
        }
    };

    private TutorialsAdapter.TutorialItemClickListener mGuideTutorialItemClick = new TutorialsAdapter.TutorialItemClickListener() {
        @Override
        public void onTutorialItemClick(int iPosition) {
            openPdf(mGuideList.get(iPosition));
        }
    };


    private ServiceConnection mDataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isDataServiceConnected = true;
            InitialDataSocketIntentService.LocalBinder aBinder = (InitialDataSocketIntentService.LocalBinder) iBinder;
            mDataSocketIntentService = aBinder.getSocketIntentService();
            mDataSocketIntentService.registerCallback(TutorialsActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isDataServiceConnected = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);

        initToolbar();
        initView();
        initListeners();
        initTutorialLists();
    }

    private void initToolbar() {
        Toolbar aToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(aToolbar);
    }

    private void initView() {

        mInitialAddHospitalDialog = new InitialAddHospitalDialog(this);
        mBPlayALl = (Button) findViewById(R.id.bPlayAll);

        mRvVideos = (RecyclerView) findViewById(R.id.rvVideos);
        mRvVideos.setLayoutManager(new LinearLayoutManager(TutorialsActivity.this));
        mRvVideos.addItemDecoration(new DividerItemDecoration(TutorialsActivity.this, DividerItemDecoration.VERTICAL));
        mVideosAdapter = new TutorialsAdapter(TutorialsActivity.this, mVideoList, mVideoTutorialItemClick);
        mRvVideos.setAdapter(mVideosAdapter);

        mRvGuides = (RecyclerView) findViewById(R.id.rvGuides);
        mRvGuides.setLayoutManager(new LinearLayoutManager(TutorialsActivity.this));
        mRvGuides.addItemDecoration(new DividerItemDecoration(TutorialsActivity.this, DividerItemDecoration.VERTICAL));
        mGuidesAdapter = new TutorialsAdapter(TutorialsActivity.this, mGuideList, mGuideTutorialItemClick);
        mRvGuides.setAdapter(mGuidesAdapter);

        mBContactCare = (Button) findViewById(R.id.bContactCare);
        mBNext = (Button) findViewById(R.id.bGoToHome);

        if (!FLPreferences.getInstance(FLApplication.getInstance()).getTutorialSeen())
        {
            mBNext.setText("Next");
        }


    }

    private void initListeners() {
        mBPlayALl.setOnClickListener(this);
        mBContactCare.setOnClickListener(this);
        mBNext.setOnClickListener(this);
    }

    private void initTutorialLists() {
        initVideoTutorialList();
        initGuideTutorialList();
    }

    private void initVideoTutorialList() {
        mVideoList.clear();
        mVideoList.add(new Tutorial(1, "Setting up Sattva Fetal Lite", "lorem_ipsum.mp4", true));
        mVideoList.add(new Tutorial(2, "Attaching the electrodes", "lorem_ipsum.mp4", true));
        mVideoList.add(new Tutorial(3, "Placing the sensor unit on the mother", "lorem_ipsum.mp4", true));
        mVideosAdapter.notifyDataSetChanged();
    }

    private void initGuideTutorialList() {
        mGuideList.clear();
        mGuideList.add(new Tutorial(1, "Example guide topic one", "lorem_ipsum.pdf", false));
        mGuideList.add(new Tutorial(2, "Example guide topic two", "lorem_ipsum.pdf", false));
        mGuideList.add(new Tutorial(3, "Example guide topic three", "lorem_ipsum.pdf", false));
        mGuidesAdapter.notifyDataSetChanged();
    }

    private void openVideo(Tutorial iVideo) {
//        File aVideo = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + iVideo.fileName);
//        Intent aIntent = new Intent(Intent.ACTION_VIEW);
//        aIntent.setDataAndType(Uri.fromFile(aVideo), "video/*");
//        aIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(aIntent);

        Intent aVideoIntent = new Intent(this, VideoPlayerActivity.class);
        aVideoIntent.putExtra("lorem_ipsum","lorem_ipsum");
        startActivity(aVideoIntent);
//
//
////        VideoView mVideoView;
////        final Dialog dialog = new Dialog(TutorialsActivity.this);// add here your class name
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////        dialog.setContentView(R.layout.dialog_video_player);//add your own xml with defied with and height of videoview
////        dialog.show();
////        mVideoView = (VideoView) dialog.findViewById(R.id.vid123);
////        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
////                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        lp.copyFrom(dialog.getWindow().getAttributes());
////        dialog.getWindow().setAttributes(lp);
////        String uriPath = "android.resource://" + getPackageName() + "/" + R.raw.lorem_ipsum;
////
////        getWindow().setFormat(PixelFormat.TRANSLUCENT);
////        Log.e("Vidoe-URI", uriPath+ "");
////        MediaController mediaController = new MediaController(this);
////        mediaController.setAnchorView(mVideoView);
////        mVideoView.setMediaController(mediaController);
////        mVideoView.setVideoURI(Uri.parse(uriPath));
////        mVideoView.start();
////
////        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
////        // if button is clicked, close the custom dialog
////        dialogButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                dialog.dismiss();
////            }
////        });
    }

    private void openPdf(Tutorial iGuide) {
        File aPdf = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + iGuide.fileName);
        Intent aIntent = new Intent(Intent.ACTION_VIEW);
        aIntent.setDataAndType(Uri.fromFile(aPdf), "application/pdf");
        aIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(aIntent);
    }




    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bContactCare)
        {
            sendSmsForCustomerCare();
        }
        else if (view.getId() == R.id.bGoToHome)
        {
            if (!FLPreferences.getInstance(FLApplication.getInstance()).getTutorialSeen())
            {
                FLPreferences.getInstance(FLApplication.getInstance()).setTutorialSeen(true);
                Intent aIntent;
                if (TextUtils.isEmpty(FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson()))
                {
//                    aIntent = new Intent(TutorialsActivity.this, LoginActivity.class);

                    aIntent = new Intent(TutorialsActivity.this, HospitalRegistrationActivity.class);
                    finish();
                    startActivity(aIntent);
                }

            }
            else
            {
                finish();
            }

        }
    }

    private void sendSmsForCustomerCare() {
        String aTicketNumber = String.valueOf(System.currentTimeMillis());
        new CustomerCareDialog(TutorialsActivity.this, aTicketNumber).show();
        SMSHelper.sendSMS(TutorialsActivity.this, Constants.CC_PHONE_NUMBER, aTicketNumber, false);
    }

    @Override
    public void onClientConnected() {

    }

    @Override
    public void onDataStreamStarted() {

    }

    @Override
    public void onDataStreamStopped() {

    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

}
