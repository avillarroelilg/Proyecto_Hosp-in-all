package com.example.hospinall.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hospinall.AlarmPopUp;
import com.example.hospinall.AlarmasMedic;
import com.example.hospinall.BatteryWarnings;
import com.example.hospinall.DeviceManager;
import com.example.hospinall.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.BATTERY_SERVICE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    DatabaseReference reff;
    DatabaseReference reffActiveAlarms;
    DatabaseReference reffDevices;
    DatabaseReference reffDevicesWar;

    AlarmasMedic alarmasMedic;
    DeviceManager deviceManager;
    BatteryWarnings batteryWarnings;

    String tabletName;
    String username;
    String idDevice;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.findViewById(R.id.btn_blue).setOnClickListener(this);
        root.findViewById(R.id.btn_green).setOnClickListener(this);
        root.findViewById(R.id.btn_yellow).setOnClickListener(this);
        root.findViewById(R.id.btn_red).setOnClickListener(this);
        return root;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmasMedic = new AlarmasMedic();
        deviceManager = new DeviceManager();
        batteryWarnings = new BatteryWarnings();

    }

    @Override
    public void onStart() {
        super.onStart();
        changeStatus("Open App");


    }

    @Override
    public void onStop() {
        super.onStop();

        changeStatus("Open App");

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), AlarmPopUp.class));
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        switch (v.getId()) {
            case R.id.btn_blue:
                Log.i("click", "Blue test");
                prefs.edit().putString("alarmCodeColor", "Blue Alarm").apply();
                prefs.edit().putString("alarmType", "Doctor").apply();
                break;

            case R.id.btn_green:
                Log.i("click", "Green test");
                prefs.edit().putString("alarmCodeColor", "Green Alarm").apply();
                prefs.edit().putString("alarmType", "Doctor").apply();

                break;

            case R.id.btn_yellow:

                Log.i("click", "Yellow test");
                prefs.edit().putString("alarmCodeColor", "Yellow Alarm").apply();
                prefs.edit().putString("alarmType", "Doctor").apply();

                break;

            case R.id.btn_red:

                Log.i("click", "Red test");
                prefs.edit().putString("alarmCodeColor", "Red Alarm").apply();
                prefs.edit().putString("alarmType", "Doctor").apply();

                break;

            default:
                Log.i("click", "Unregistered ID");


        }
    }

    public static String timeDisplay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyy");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    public static String timeDisplayDay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    public static String timeDisplayHours() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    /**
     * Checks the battery % of the user's device. If it is below or equal to 30%, a "Low battery" warning will be sent to the database.
     *
     * @param battPercentage The battery % of the device.
     */

    public void CheckingBattery(int battPercentage) {
        if (battPercentage <= 30) {
            reffDevicesWar = FirebaseDatabase.getInstance().getReference().child("Other Warnings").child(tabletName);
            batteryWarnings.setBattery_lvl(battPercentage);
            batteryWarnings.setId_tablet(idDevice);
            batteryWarnings.setLast_check(timeDisplay());
            batteryWarnings.setNom_tablet(tabletName);
            batteryWarnings.setWarning_type("Low Battery");
            reffDevicesWar.setValue(batteryWarnings);
        } else {
            reffDevicesWar = FirebaseDatabase.getInstance().getReference().child("Other Warnings").child(tabletName);
            reffDevicesWar.setValue(null);
        }
    }

    /**
     * Checks if the device is plugged.
     *
     * @param context Context
     * @return If the device is plugged or not.
     */

    public static boolean isPlugged(Context context) {
        boolean isPlugged = false;
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        return isPlugged;
    }

    /**
     * Changes the device's status between connected and disconnected, depending if the device is charging or not.
     *
     * @param status The device's status.
     */

    public void changeStatus(String status) {
        SharedPreferences prefs = getContext().getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        if (isPlugged(getContext())) {
            prefs.edit().putString("chargerConnected", "Conectado").apply();
        } else if (!isPlugged(getContext())) {
            prefs.edit().putString("chargerConnected", "Desconectado").apply();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        tabletName = sharedPreferences.getString("tabletName", "Tablet B1");
        idDevice = sharedPreferences.getString("tabletID", "0");

        String latestAction = sharedPreferences.getString("latestAction", null);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        BatteryManager bm = (BatteryManager) getActivity().getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        reffDevices = FirebaseDatabase.getInstance().getReference().child("Devices Status").child(tabletName);
        deviceManager.setNom_tablet(tabletName);
        deviceManager.setID_tablet(idDevice);
        deviceManager.setUltima_Accion(latestAction);
        deviceManager.setApp_status(status);
        deviceManager.setLast_check(timeDisplay());
        deviceManager.setBattery_lvl(percentage);
        deviceManager.setDevice_charger(batteryConnected);
        CheckingBattery(percentage);


        reffDevices.setValue(deviceManager);
    }

}