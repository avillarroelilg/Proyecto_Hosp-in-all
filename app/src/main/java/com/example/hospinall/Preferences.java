package com.example.hospinall;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.hospinall.R;

public class Preferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
