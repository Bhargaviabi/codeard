package com.jansen.sander.arduinorgb;

/**
 * Created by Sander on 27/01/2018.
 */

public class BTDevice {
    private String macAddress;
    private String deviceName;

    public BTDevice(String macAddress, String deviceName){
        setMacAddress(macAddress);
        setDeviceName(deviceName);
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
