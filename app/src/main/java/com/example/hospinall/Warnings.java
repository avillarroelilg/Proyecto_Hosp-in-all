package com.example.hospinall;

public class Warnings {

    private int icon;
    private String alarmType;
    private String deviceID;
    private String time;

    public Warnings(int icon, String alarmType, String deviceID, String time) {
        this.icon = icon;
        this.alarmType = alarmType;
        this.deviceID = deviceID;
        this.time = time;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
