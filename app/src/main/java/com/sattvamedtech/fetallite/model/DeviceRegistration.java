package com.sattvamedtech.fetallite.model;

/**
 * Created by Pavan on 8/11/2017.
 */

public class DeviceRegistration {

    public String deviceid;

    public DeviceRegistration(String deviceid){
        this.deviceid = deviceid;
    }

    @Override
    public String toString() {
        return this.deviceid;
    }
}
