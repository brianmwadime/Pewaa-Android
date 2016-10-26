package com.fortunekidew.pewaa.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.fortunekidew.pewaa.models.groups.MembersGroupModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Abderrahim El imame on 20/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class PreferenceManager {


    private static SharedPreferences mSharedPreferences;
    private static final String PREFS_NAME = "MEMBERS_APP";
    private static final String MEMBERS_SELECTED = "MEMBERS_SELECTED";
    private static final String USER_PREF = "USER_PREF";
    private static final String ADS_PREF = "ADS_PREF";

    /**
     * method to set token
     *
     * @param token    this is the first parameter for setToken  method
     * @param mContext this is the second parameter for setToken  method
     * @return return value
     */
    public static boolean setToken(String token, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, 0);
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
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, 0);
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
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("id", ID);
        return editor.commit();
    }

    /**
     * method to getID
     *
     * @param mContext this is  parameter for getID  method
     * @return return value
     */
    public static String getID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, 0);
        return mSharedPreferences.getString("id", String.valueOf(0));
    }


    /**
     * method to setSocketID
     *
     * @param ID       this is the first parameter for setID  method
     * @param mContext this is the second parameter for setID  method
     * @return return value
     */
    public static boolean setSocketID(String ID, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("socketId", ID);
        return editor.commit();
    }

    /**
     * method to getID
     *
     * @param mContext this is  parameter for getSocketID  method
     * @return return value
     */
    public static String getSocketID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, 0);
        return mSharedPreferences.getString("socketId", null);
    }

    /**
     * method to set contacts size
     *
     * @param size     this is the first parameter for setContactSize  method
     * @param mContext this is the second parameter for setContactSize  method
     * @return return value
     */
    public static boolean setContactSize(int size, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, 0);
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
        mSharedPreferences = mContext.getSharedPreferences(USER_PREF, 0);
        return mSharedPreferences.getInt("size", 0);

    }


    /**
     * method to save new members to group
     *
     * @param context            this is the first parameter for saveMembers  method
     * @param membersGroupModels this is the second parameter for saveMembers  method
     */
    private static void saveMembers(Context context, List<MembersGroupModel> membersGroupModels) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonMembers = gson.toJson(membersGroupModels);

        editor.putString(MEMBERS_SELECTED, jsonMembers);

        editor.apply();
    }

    /**
     * method to add member
     *
     * @param context           this is the first parameter for addMember  method
     * @param membersGroupModel this is the second parameter for addMember  method
     */
    public static void addMember(Context context, MembersGroupModel membersGroupModel) {
        List<MembersGroupModel> membersGroupModelArrayList = getMembers(context);
        if (membersGroupModelArrayList == null)
            membersGroupModelArrayList = new ArrayList<MembersGroupModel>();
        membersGroupModelArrayList.add(membersGroupModel);
        saveMembers(context, membersGroupModelArrayList);
    }

    /**
     * method to remove member
     *
     * @param context           this is the first parameter for removeMember  method
     * @param membersGroupModel this is the second parameter for removeMember  method
     */
    public static void removeMember(Context context, MembersGroupModel membersGroupModel) {
        ArrayList<MembersGroupModel> membersGroupModelArrayList = getMembers(context);
        if (membersGroupModelArrayList != null) {
            membersGroupModelArrayList.remove(membersGroupModel);
            saveMembers(context, membersGroupModelArrayList);
        }
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

    /**
     * method to get all members
     *
     * @param context this is parameter for getMembers  method
     * @return return value
     */
    public static ArrayList<MembersGroupModel> getMembers(Context context) {
        try {
            SharedPreferences settings;
            List<MembersGroupModel> membersGroupModels;

            settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            if (settings.contains(MEMBERS_SELECTED)) {
                String jsonMembers = settings.getString(MEMBERS_SELECTED, null);
                Gson gson = new Gson();
                MembersGroupModel[] membersItems = gson.fromJson(jsonMembers, MembersGroupModel[].class);
                membersGroupModels = Arrays.asList(membersItems);
                return new ArrayList<>(membersGroupModels);
            } else {
                return null;
            }

        } catch (Exception e) {
            AppHelper.LogCat("getMembers Exception " + e.getMessage());
            return null;
        }
    }


    /**
     * method to setUnitInterstitialAdID
     *
     * @param UnitId       this is the first parameter for setUnitInterstitialAdID  method
     * @param mContext this is the second parameter for setUnitInterstitialAdID  method
     * @return return value
     */
    public static boolean setUnitInterstitialAdID(String UnitId, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(ADS_PREF, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("InterstitialUnitId", UnitId);
        return editor.commit();
    }

    /**
     * method to getUnitInterstitialAdID
     *
     * @param mContext this is  parameter for getUnitInterstitialAdID  method
     * @return return value
     */
    public static String getUnitInterstitialAdID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(ADS_PREF, 0);
        return mSharedPreferences.getString("InterstitialUnitId", null);
    }

    /**
     * method to setShowInterstitialAds
     *
     * @param UnitId       this is the first parameter for setShowInterstitialAds  method
     * @param mContext this is the second parameter for setShowInterstitialAds  method
     * @return return value
     */
    public static boolean setShowInterstitialAds(Boolean UnitId, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(ADS_PREF, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ShowInterstitialAds", UnitId);
        return editor.commit();
    }

    /**
     * method to ShowInterstitialrAds
     *
     * @param mContext this is  parameter for ShowInterstitialrAds  method
     * @return return value
     */
    public static Boolean ShowInterstitialrAds(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(ADS_PREF, 0);
        return mSharedPreferences.getBoolean("ShowInterstitialAds", false);
    }
    /**
     * method to setUnitBannerAdsID
     *
     * @param UnitId       this is the first parameter for setUnitBannerAdsID  method
     * @param mContext this is the second parameter for setUnitBannerAdsID  method
     * @return return value
     */
    public static boolean setUnitBannerAdsID(String UnitId, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(ADS_PREF, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("BannerUnitId", UnitId);
        return editor.commit();
    }

    /**
     * method to getUnitBannerAdsID
     *
     * @param mContext this is  parameter for getUnitBannerAdsID  method
     * @return return value
     */
    public static String getUnitBannerAdsID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(ADS_PREF, 0);
        return mSharedPreferences.getString("BannerUnitId", null);
    }


    /**
     * method to setShowBannerAds
     *
     * @param UnitId       this is the first parameter for setShowBannerAds  method
     * @param mContext this is the second parameter for setShowBannerAds  method
     * @return return value
     */
    public static boolean setShowBannerAds(Boolean UnitId, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(ADS_PREF, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ShowBannerAds", UnitId);
        return editor.commit();
    }

    /**
     * method to ShowBannerAds
     *
     * @param mContext this is  parameter for ShowBannerAds  method
     * @return return value
     */
    public static Boolean ShowBannerAds(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(ADS_PREF, 0);
        return mSharedPreferences.getBoolean("ShowBannerAds", false);
    }
}
