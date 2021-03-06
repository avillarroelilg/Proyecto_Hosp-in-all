package com.example.hospinall;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;


import com.example.hospinall.ui.home.HomeViewModel;

import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.processphoenix.ProcessPhoenix;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import static com.example.hospinall.ui.home.HomeFragment.isPlugged;
import static com.example.hospinall.ui.home.HomeFragment.timeDisplay;

public class MainActivity extends AppCompatActivity {

    static ImageView imageView;
    static TextView headerText;
    static TextView subheaderText;
    static SharedPreferences prefs;
    Integer userID;
    private HomeViewModel homeViewModel;
    DatabaseReference reff;
    DatabaseReference reffDevices;
    DatabaseReference reffDevicesWar;

    AlarmasMedic alarmasMedic;
    DeviceManager deviceManager;
    BatteryWarnings batteryWarnings;
    NavigationView navigationView;

    Context context;

    String tabletName;
    String username;
    String idDevice;
    Integer logComplete;


    private AppBarConfiguration mAppBarConfiguration;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alarmasMedic = new AlarmasMedic();
        deviceManager = new DeviceManager();
        batteryWarnings = new BatteryWarnings();

        Context context = getApplicationContext();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send, R.id.userAlarmFragment, R.id.listaAlarmasPacientes)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        imageView = (ImageView) header.findViewById(R.id.imageLog);
        headerText = (TextView) header.findViewById(R.id.navHeader);
        subheaderText = (TextView) header.findViewById(R.id.navHeaderSub);
        prefs = this.getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        Boolean darkmode = prefs.getBoolean("app_theme", false);
        if (!darkmode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if (savedInstanceState == null) {
            prefs.edit().putString("loggedIn", "notLogged").apply();
            removeImageView();
        }
    }

    /**
     * Changes the device's status between connected and disconnected, depending if the device is charging or not.
     *
     * @param status The device's status.
     */

    public void changeStatus(String status) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        if (isPlugged(this)) {
            prefs.edit().putString("chargerConnected", "Conectado").apply();
        } else if (!isPlugged(this)) {
            prefs.edit().putString("chargerConnected", "Desconectado").apply();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tabletName = sharedPreferences.getString("tabletName", "Tablet B1");
        idDevice = sharedPreferences.getString("tabletID", "0");

        String latestAction = sharedPreferences.getString("latestAction", null);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        assert bm != null;
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

    @Override
    public void onStart() {
        super.onStart();
        changeStatus("Open App");
        Menu menu = navigationView.getMenu();
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        String user = prefs.getString("loggedIn", "notLogged");
        String username = prefs.getString("ActiveUser", "notLogged");
        if (user.equals("notLogged")) {
            menu.findItem(R.id.nav_home).setVisible(false);
            menu.findItem(R.id.nav_send).setVisible(false);
            menu.findItem(R.id.nav_share).setVisible(false);
            menu.findItem(R.id.nav_tools).setVisible(false);
            menu.findItem(R.id.userAlarmFragment).setVisible(false);
            menu.findItem(R.id.listaAlarmasPacientes).setVisible(false);
        } else if (username.contains("Admin")) {
            menu.findItem(R.id.nav_home).setVisible(true);
            menu.findItem(R.id.nav_send).setVisible(true);
            menu.findItem(R.id.nav_share).setVisible(true);
            menu.findItem(R.id.nav_tools).setVisible(true);
            menu.findItem(R.id.userAlarmFragment).setVisible(true);
            menu.findItem(R.id.listaAlarmasPacientes).setVisible(true);
        } else if (username.contains("Tech")) {
            menu.findItem(R.id.nav_home).setVisible(false);
            menu.findItem(R.id.nav_send).setVisible(false);
            menu.findItem(R.id.nav_share).setVisible(true);
            menu.findItem(R.id.nav_tools).setVisible(true);
            menu.findItem(R.id.userAlarmFragment).setVisible(false);
            menu.findItem(R.id.listaAlarmasPacientes).setVisible(false);
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        changeStatus("Paused App");


    }

    @Override
    protected void onDestroy() {
        prefs = this.getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("app_theme", false).apply();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAppInLockTaskMode()) {
            startLockTask();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // If the screen is off then the device has been locked
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = powerManager.isInteractive();
        } else {
            isScreenOn = powerManager.isScreenOn();
        }
        if (!isScreenOn) {

            ProcessPhoenix.triggerRebirth(context);

        }
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
     * Sets the user's profile picture.
     */

    public void setImageView() {
        String mDrawableName = prefs.getString("ActiveUser", "def");
        int resID = Resources.getSystem().getIdentifier(mDrawableName, "drawable", "android");
        assert mDrawableName != null;
        if (mDrawableName.contains("Admin")) {
            imageView.setImageResource(R.drawable.admin_icon);
        } else if (mDrawableName.contains("Tech")) {
            imageView.setImageResource(R.drawable.tech_icon);
        } else if (mDrawableName.contains("Patient")) {
            imageView.setImageResource(R.drawable.patient_icon);
        } else if (mDrawableName.contains("Doct")) {
            imageView.setImageResource(R.drawable.doctor_icon);
        }
        headerText.setText(mDrawableName);
        subheaderText.setText(String.format("%s@gmail.com", mDrawableName.toLowerCase()));
    }

    /**
     * Eliminates the user's profile picture when logging out.
     */
    public static void removeImageView() {
        imageView.setImageResource(R.mipmap.ic_launcher);
        headerText.setText(prefs.getString("ActiveUser", "def"));
        subheaderText.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }


    /**
     * Checks if the app is in Lock Task mode
     *
     * @return
     */
    public boolean isAppInLockTaskMode() {
        ActivityManager activityManager;

        activityManager = (ActivityManager)
                this.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For SDK version 23 and above.
            return activityManager.getLockTaskModeState()
                    != ActivityManager.LOCK_TASK_MODE_NONE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK version >= 21. This API is deprecated in 23.
            return activityManager.isInLockTaskMode();
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String user = prefs.getString("loggedIn", "notLogged");
        outState.putString("isUserLogged", user);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
