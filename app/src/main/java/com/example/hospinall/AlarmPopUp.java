package com.example.hospinall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospinall.ui.home.HomeViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.hospinall.UtilityClass.timeDisplay;
import static com.example.hospinall.UtilityClass.timeDisplayDay;
import static com.example.hospinall.UtilityClass.timeDisplayHours;

public class AlarmPopUp extends AppCompatActivity implements View.OnClickListener {
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
    webdb db_action;
    Button confirmButton,cancellButton;
    TextView alarmMessage;
    EditText alarmComment;
    String alarmType, alarmCommentStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_pop_up);

        alarmasMedic = new AlarmasMedic();
        deviceManager = new DeviceManager();
        batteryWarnings = new BatteryWarnings();
        db_action = new webdb();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);

        alarmType = prefs.getString("alarmCodeColor","");
        confirmButton = findViewById(R.id.confirm);
        cancellButton = findViewById(R.id.cancell);
        alarmComment = findViewById(R.id.alarmComment);
        alarmMessage = findViewById(R.id.alarmMessage);
        alarmMessage.setText(String.format("The %s, has been selected, please confirm your choice.", alarmType));
        confirmButton.setOnClickListener(this);
        cancellButton.setOnClickListener(this);



    }

    /**
     * This onClick method manages the options of the pop-up window when clicking a button.
     * @param v View.
     */

    @Override
    public void onClick(View v) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        alarmCommentStr = alarmComment.getText().toString();
        prefs.edit().putString("alarmComment","DOOP").apply();

        switch (v.getId()){
            case R.id.confirm:
                prefs.edit().putString("alarmComment",alarmCommentStr).apply();
                sendWarningToFirebase(alarmType,alarmCommentStr);
               // prefs.edit().putString("alarmConfirm?","confirmed").apply();
                Toast.makeText(this, alarmType + " sent", Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.cancell:
               //prefs.edit().putString("alarmConfirm?","denied").apply();
                finish();
                break;
        }
    }

    /**
     * Sends the information of the clicked warning to the database.
     * @param typeOfWarning The color of the sent warning.
     * @param alarmCommentStr The optional comment on the reason from said button press or any extra information for the alarm.
     */
    private void sendWarningToFirebase(String typeOfWarning,String alarmCommentStr) {

        SharedPreferences prefs = this.getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String database = sharedPreferences.getString("name_db", "Database");
        int actualBattery = prefs.getInt("percentageBattery", -1);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        tabletName = sharedPreferences.getString("tabletName", "Tablet B1");
        idDevice = sharedPreferences.getString("tabletID", "0");
        username = sharedPreferences.getString("ActiveUser", null);
        reff = FirebaseDatabase.getInstance().getReference().child(database).child(timeDisplayDay()).child("Log " + timeDisplayHours());
        reffActiveAlarms = FirebaseDatabase.getInstance().getReference().child("Active Warnings").child("ID " + idDevice);

        //crea objeto alarma medica
        alarmasMedic.setNom_tablet(tabletName);
        alarmasMedic.setID_tablet(idDevice);
        alarmasMedic.setTipo_Alarma(typeOfWarning);
        alarmasMedic.setTime(timeDisplay());
        alarmasMedic.setNom_user(username);
        alarmasMedic.setDescription(alarmCommentStr);

        //crea objeto device manager
        deviceManager.setDevice_charger(batteryConnected);
        deviceManager.setLast_check(timeDisplay());
        deviceManager.setApp_status("Open App");
        //stopLockTask();

        reffDevicesWar = FirebaseDatabase.getInstance().getReference().child("Other Warnings").child(tabletName);
        reffDevicesWar.setValue(null);
/*
        BatteryManager bm = getParentActivityIntent().getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
*/
        CheckingBattery(60);

        reffDevices = FirebaseDatabase.getInstance().getReference().child("Devices Status").child(tabletName);

        reffDevices.setValue(deviceManager);
        reff.setValue(alarmasMedic);
        reffActiveAlarms.setValue(alarmasMedic);
    }

    /**
     * Checks the battery % of the user's device. If it is below or equal to 30%, a "Low battery" warning will be sent to the database.
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
}
