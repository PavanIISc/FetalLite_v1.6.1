package com.sattvamedtech.fetallite;

import android.support.v7.app.AppCompatActivity;

public class FLBaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        if (!isTaskRoot()) {
            super.onBackPressed();
        }
    }
}
