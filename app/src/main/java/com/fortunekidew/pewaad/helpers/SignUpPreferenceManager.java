package com.fortunekidew.pewaad.helpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Abderrahim El imame on 23/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SignUpPreferenceManager {

    // Shared Preferences
    private SharedPreferences sharedPreferences;
    // Editor for Shared preferences
    private Editor editor;
    // Shared preferences file name
    private static final String PREF_NAME = "Pewaa";
    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "MobileNumber";

    private static final String KEY_DEVICE_SAVED = "DeviceSaved";
    private static final String KEY_DEVICE_TOKEN = "DeviceToken";

    public SignUpPreferenceManager(Context mContext) {
        int PRIVATE_MODE = 0;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    /**
     * method to set mobile phone
     * @param deviceToken this is parameter for setMobileNumber  method
     */
    public void setDeviceToken(String deviceToken) {
        editor.putString(KEY_DEVICE_TOKEN, deviceToken);
        editor.commit();
    }

    /**
     * method to get mobile phone
     * @return return value
     */
    public String getDeviceToken() {
        return sharedPreferences.getString(KEY_DEVICE_TOKEN, null);
    }

    /**
     * method to set Device Token Saved
     * @param isSaved this is parameter for setIsDeviceSaved  method
     */
    public void setIsDeviceSaved(boolean isSaved) {
        editor.putBoolean(KEY_DEVICE_SAVED, isSaved);
        editor.commit();
    }

    /**
     * method to check if user is waiting for SMS
     * @return  return value
     */
    public boolean isDeviceSaved() {
        return sharedPreferences.getBoolean(KEY_DEVICE_SAVED, false);
    }

    /**
     * method to set user waiting for SMS (code verification)
     * @param isWaiting this is parameter for setIsWaitingForSms  method
     */
    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    /**
     * method to check if user is waiting for SMS
     * @return  return value
     */
    public boolean isWaitingForSms() {
        return sharedPreferences.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    /**
     * method to set mobile phone
     * @param mobileNumber this is parameter for setMobileNumber  method
     */
    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    /**
     * method to get mobile phone
     * @return return value
     */
    public String getMobileNumber() {
        return sharedPreferences.getString(KEY_MOBILE_NUMBER, null);
    }

}
