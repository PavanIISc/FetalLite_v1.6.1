package com.sattvamedtech.fetallite.storage;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sattvamedtech.fetallite.R;

/**
 * Created by Pavan on 7/10/2017.
 */

public class PreferencesFragement extends Fragment {

    public PreferencesFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }
}
