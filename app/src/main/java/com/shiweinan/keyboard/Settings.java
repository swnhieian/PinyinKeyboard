package com.shiweinan.keyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Weinan on 2017/12/28.
 */

public class Settings {
    //private static boolean useBigram = true;
    //private static boolean showTouchPoints = false;
    private static Context context = null;
    public static void setContext(Context context) {
        Settings.context = context;
    }
    public static boolean getUseBigram() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_key_use_bigram), false);
        //return useBigram;
    }
//    public static void setUseBigram(boolean v) {
//        if (useBigram == v) return;
//        useBigram = v;
//    }
    public static boolean getShowTouchPoints() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_key_show_points), false);
        //return showTouchPoints;
    }
    public static boolean getShowPinyinSegmentation() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_key_show_pinyinseg), false);
    }
//    public static void setShowTouchPoints(boolean v) {
//        if (showTouchPoints == v) return;
//        showTouchPoints = v;
//    }
}
