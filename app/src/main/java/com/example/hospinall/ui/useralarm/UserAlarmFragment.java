package com.example.hospinall.ui.useralarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hospinall.AlarmPopUp;
import com.example.hospinall.R;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserAlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserAlarmFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserAlarmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserAlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserAlarmFragment newInstance(String param1, String param2) {
        UserAlarmFragment fragment = new UserAlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_alarm, container, false);
        root.findViewById(R.id.btn_doct_req).setOnClickListener(this);
        root.findViewById(R.id.btn_bathroom).setOnClickListener(this);
        root.findViewById(R.id.btn_med_rem).setOnClickListener(this);
        root.findViewById(R.id.btn_emergency).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), AlarmPopUp.class));
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        switch (v.getId()) {
            case R.id.btn_doct_req:
                Log.i("click", "Blue test");
                prefs.edit().putString("alarmCodeColor", "Doctor Request Alarm").apply();
                prefs.edit().putString("alarmType", "Patient").apply();
                break;

            case R.id.btn_emergency:
                Log.i("click", "Green test");
                prefs.edit().putString("alarmCodeColor", "Emergency Alarm").apply();
                prefs.edit().putString("alarmType", "Patient").apply();
                break;

            case R.id.btn_med_rem:

                Log.i("click", "Yellow test");
                prefs.edit().putString("alarmCodeColor", "Medicine Reminder Alarm").apply();
                prefs.edit().putString("alarmType", "Patient").apply();
                break;

            case R.id.btn_bathroom:

                Log.i("click", "Red test");
                prefs.edit().putString("alarmCodeColor", "Bathroom Alarm").apply();
                prefs.edit().putString("alarmType", "Patient").apply();
                break;

            default:
                Log.i("click", "Unregistered ID");


        }
    }
}
