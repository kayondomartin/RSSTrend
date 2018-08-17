package com.example.martin.rsstrend;

import java.util.UUID;

public class Device {
    private String deviceName;
    private int rssi;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public UUID getProximityUUID() {
        return proximityUUID;
    }

    public void setProximityUUID(UUID proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

    public void setPower(int power){
        this.power = power;
    }

    public int getPower(){
        return power;
    }

    private String macAddress;
    private UUID proximityUUID;
    private int power;

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
