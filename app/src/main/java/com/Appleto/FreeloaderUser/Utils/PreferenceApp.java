package com.Appleto.FreeloaderUser.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by VR-46 on 1/27/2017.
 */

public class PreferenceApp {
    private Context mContext;
    private SharedPreferences prefs;

    public PreferenceApp(Context mContext) {
        this.mContext = mContext;
        prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, mContext.MODE_PRIVATE);
        //prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void clear() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }

    public void setValue(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public String getValue(String key) {
        return prefs.getString(key, "");
    }

    public void setIntValue(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.commit();
    }

    public int getIntValue(String key) {
        return prefs.getInt(key, 0);
    }

    public void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
        editor.commit();
    }

    public void clearLoginData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.user_id);
        editor.remove(Constants.user_name);
        editor.remove(Constants.user_phone);
        editor.remove(Constants.user_email);
        editor.remove(Constants.user_login_status);
        editor.apply();
        editor.commit();
    }

    public void clearRideDetail() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.pickup_location);
        editor.remove(Constants.pickup_location_lat);
        editor.remove(Constants.pickup_location_long);
        editor.remove(Constants.drop_location);
        editor.remove(Constants.drop_location_lat);
        editor.remove(Constants.drop_location_long);
        editor.remove(Constants.userRequestForRide);
        editor.apply();
        editor.commit();
    }

    public boolean getBooleanValue(String key) {
        return prefs.getBoolean(key, false);
    }

    /*public void setToken(String token){
        SharedPreferences mPrefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(Constants.fcm_token,token);
        editor.apply();
        editor.commit();
    }

    public String getToken(){
        SharedPreferences mPrefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, mContext.MODE_PRIVATE);
        return mPrefs.getString(Constants.fcm_token,"");
    }*/
}
