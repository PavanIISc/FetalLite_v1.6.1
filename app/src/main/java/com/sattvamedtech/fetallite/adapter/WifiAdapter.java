package com.sattvamedtech.fetallite.adapter;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.sattvamedtech.fetallite.helper.ApplicationUtils;

import java.lang.reflect.Method;

/**
 * Created by vijeth on 04-07-2017.
 */

public class WifiAdapter {

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context)
    {


        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }




    // toggle wifi hotspot on
    public static boolean configApState(Context context)
    {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);



        /***************************6-7-2017********************/
        WifiConfiguration wificonfiguration = new WifiConfiguration();

        //Hotsspot name
        wificonfiguration.SSID = ApplicationUtils.SSID;
        //Hotspit Password
        wificonfiguration.preSharedKey = ApplicationUtils.wifipassword;



        Log.e("WifiAdapter", "SSID & Password are: " + wificonfiguration.SSID + " " + wificonfiguration.preSharedKey);


        wificonfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wificonfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//        /*******************************************************/
        try {
            // if WiFi is off then turn it on
            if(!isApOn(context)) {
                Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.invoke(wifimanager, wificonfiguration, !isApOn(context));
                return true;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}
