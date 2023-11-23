package com.news.gamersky.util;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class NightModeUtil {
    public static final int UI_MODE_NIGHT_NO = 0;
    public static final int UI_MODE_NIGHT_YES = 1;
    public static final int UI_MODE_NIGHT_FOLLOW_SYSTEM = 2;

    public static boolean isNightMode(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode != Configuration.UI_MODE_NIGHT_NO;
    }

    public static void changeNightMode(int nightMode) {
        if (nightMode == UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
        if (nightMode == UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        }
        if (nightMode == UI_MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
