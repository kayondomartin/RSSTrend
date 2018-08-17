package com.example.martin.rsstrend;

public class Device {
    private String deviceName;
    private int rssi;

    Device(String name, int rssi){
        this.deviceName = name;
        this.rssi = rssi;
    }

    public String getDeviceName(){
        return deviceName;
    }

    public int getRssi(){
        return rssi;
    }

    public void setDeviceName(String name){
        this.deviceName = name;
    }

    public void setRssi(int rssi){
        this.rssi = rssi;
    }
}
