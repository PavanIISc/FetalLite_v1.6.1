package com.sattvamedtech.fetallite.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.sattvamedtech.fetallite.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Pavan on 9/5/2017.
 */

public class VideoPlayerActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl{
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;

    private MediaController mediaController;
    private Handler handler = new Handler();
    private Button mTutorialClose;

    String videoSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_video_player);
        mTutorialClose = (Button) findViewById(R.id.bVideoClose);
        videoSource = Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + "lorem_ipsum.mp4";
        surfaceView = (SurfaceView)findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mediaController != null){
                    mediaController.show();
                }
                return false;
            }
        });

        mTutorialClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.bVideoClose) {
            mediaPlayer.stop();
            Intent iIntent = new Intent(this, TutorialsActivity.class);
            startActivity(iIntent);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaController = new MediaController(this);
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            public void onClick(View v) {

                videoSource = Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + "lorem_ipsum2.mp4";


            }
        }, new View.OnClickListener() {
            public void onClick(View v) {

                videoSource = Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + "lorem_ipsum.mp4";

            }
        });
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);

        try {
            mediaPlayer.setDataSource(videoSource);
            mediaPlayer.prepare();



        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(VideoPlayerActivity.this,
                    "something wrong!\n" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        Toast.makeText(VideoPlayerActivity.this,
                "onPrepared()", Toast.LENGTH_LONG).show();


        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(surfaceView);
        handler.post(new Runnable() {

            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });


    }

    private void releaseMediaPlayer() {
        if (mediaPlayer!= null) {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer= null;
        }
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    public void onBackPressed() {
        releaseMediaPlayer();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        { //Back key pressed
            //Things to Do
            if(mediaPlayer!= null)
            {
                mediaPlayer.stop();
                mediaPlayer=null;
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }



}

