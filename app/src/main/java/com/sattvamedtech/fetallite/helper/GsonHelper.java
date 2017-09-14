package com.sattvamedtech.fetallite.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sattvamedtech.fetallite.model.Admin;
import com.sattvamedtech.fetallite.model.DeviceRegistration;
import com.sattvamedtech.fetallite.model.User;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonHelper {
    public static Gson mGson = new Gson();

    static Type type = new TypeToken<Map<String, String>>() {
    }.getType();

    public static Object getGson(String json, Class<?> class1) {

        return mGson.fromJson(json, class1);
    }

    public static String toUserJson(User iUserDetail) {
        return mGson.toJson(iUserDetail);
    }

    public static String toAdminJson(Admin iAdminDetail) {
        return mGson.toJson(iAdminDetail);
    }

    public static String toDeviceRegJson(DeviceRegistration iDeviceRegistrationDetails){
        return mGson.toJson(iDeviceRegistrationDetails);
    }
}
