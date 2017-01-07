package com.hezhujun.csu_chinanet_logger;

import android.content.SharedPreferences;

import java.util.Properties;

import login.util.PropertyUtil;

/**
 * Created by hezhujun on 2016/12/25 0025.
 */

public class PropertyUtilAndroidImpl implements PropertyUtil {

    private SharedPreferences sp;

    public PropertyUtilAndroidImpl(SharedPreferences sp) {
        this.sp = sp;
    }

    @Override
    public void saveProperties(Properties properties) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("accountID", properties.getProperty("accountID"));
        editor.putString("password", properties.getProperty("password"));
        editor.putString("brasAddress", properties.getProperty("brasAddress"));
        editor.putString("userIntranetAddress", properties.getProperty("userIntranetAddress"));
        editor.apply();
    }

    @Override
    public Properties readProperties() {
        Properties prop = new Properties();
        prop.put("accountID", sp.getString("accountID", null));
        prop.put("password", sp.getString("password", null));
        prop.put("brasAddress", sp.getString("brasAddress", null));
        prop.put("userIntranetAddress", sp.getString("userIntranetAddress", null));
        return prop;
    }
}
