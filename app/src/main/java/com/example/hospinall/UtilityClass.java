package com.example.hospinall;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UtilityClass {

    /**
     * Displays the time.
     * @return Time.
     */
    public static String timeDisplay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyy");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    /**
     * Displays the day.
     * @return Day.
     */
    public static String timeDisplayDay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    /**
     * Displays the hour.
     * @return Hour.
     */

    public static String timeDisplayHours() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }


}
