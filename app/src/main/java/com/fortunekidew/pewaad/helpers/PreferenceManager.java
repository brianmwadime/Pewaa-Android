package com.fortunekidew.pewaad.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class PreferenceManager {
    private static SharedPreferences mSharedPreferences;
    private static final String PREFS_NAME = "MEMBERS_APP";
    private static final String MEMBERS_SELECTED = "MEMBERS_SELECTED";
    private static final String KEY_IS_WAITING_FOR_SMS = "KEY_IS_WAITING_FOR_SMS";
    private static final String KEY_MOBILE_NUMBER = "KEY_MOBILE_NUMBER";
    private static final String USER_PREF = "USER_PREF";

    /**
     * method to set token
     *
     * @param token    this is the first parameter for setToken  method
     * @param mContext this is the second parameter for setToken  method
     * @return return value
     */
    public static boolean setToken(String token, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("token", token);
        return editor.commit();
    }

    /**
     * method to get token
     *
     * @param mContext this is the first parameter for getToken  method
     * @return return value
     */
    public static String getToken(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("token", null);
    }


    /**
     * method to setID
     *
     * @param ID       this is the first parameter for setID  method
     * @param mContext this is the second parameter for setID  method
     * @return return value
     */
    public static boolean setID(String ID, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("id", ID);
        return editor.commit();
    }

    /**
     * method to setNumber
     *
     * @param Number   this is the first parameter for setID  method
     * @param mContext this is the second parameter for setID  method
     * @return return value
     */
    public static boolean setNumber(String Number, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("number", Number);
        return editor.commit();
    }

    /**
     * method to getNumber
     *
     * @param mContext this is  parameter for getNumber  method
     * @return return value
     */
    public static String getNumber(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("number", null);
    }

    /**
     * method to getPhone
     *
     * @return return value
     */
    public static String getPhone(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("phone", null);
    }

    /**
     * method to setPhone
     *
     * @param Phone this is the first parameter for setID  method
     * @return return value
     */
    public static boolean setPhone(Context mContext, String Phone) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("phone", Phone);
        return editor.commit();
    }

    /**
     * method to set user waiting for SMS (code verification)
     *
     * @param isWaiting this is parameter for setIsWaitingForSms  method
     */
    public static boolean setIsWaitingForSms(Context mContext, Boolean isWaiting) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        return editor.commit();
    }

    /**
     * method to check if user is waiting for SMS
     *
     * @return return value
     */
    public static Boolean isWaitingForSms(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    /**
     * method to set mobile phone
     *
     * @param mobileNumber this is parameter for setMobileNumber  method
     */
    public static boolean setMobileNumber(Context mContext, String mobileNumber) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        return editor.commit();
    }

    /**
     * method to get mobile phone
     *
     * @return return value
     */
    public static String getMobileNumber(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_MOBILE_NUMBER, null);
    }

    /**
     * method to getID
     *
     * @param mContext this is  parameter for getID  method
     * @return return value
     */
    public static String getID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString("id", String.valueOf(0));
    }

    /**
     * method to set contacts size
     *
     * @param size     this is the first parameter for setContactSize  method
     * @param mContext this is the second parameter for setContactSize  method
     * @return return value
     */
    public static boolean setContactSize(int size, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("size", size);
        return editor.commit();
    }

    /**
     * method to get contacts size
     *
     * @param mContext this is  parameter for getContactSize  method
     * @return return value
     */
    public static int getContactSize(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("size", 0);

    }

    /**
     * method to clear members
     *
     * @param context this is  parameter for clearMembers  method
     */
    public static void clearMembers(Context context) {


        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString(MEMBERS_SELECTED, null);

        editor.apply();
    }
}
