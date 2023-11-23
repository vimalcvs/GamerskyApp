package com.news.gamersky.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.news.gamersky.R;

public class UserMsgUtil {
    private static final String userName = "user_name";
    private static final String userAvatar = "user_avatar";
    private static SharedPreferences userNameSP;
    private static SharedPreferences userAvatarSP;

    public static void putUserName(Context context, String value) {
        if (userNameSP == null) {
            userNameSP = context.getSharedPreferences(userName, MODE_PRIVATE);
        }
        userNameSP.edit().putString(userName, value).apply();
    }

    public static String getUserName(Context context) {
        if (userNameSP == null) {
            userNameSP = context.getSharedPreferences(userName, MODE_PRIVATE);
        }
        return userNameSP.getString(userName, context.getString(R.string.user_name));
    }

    public static void putUserAvatar(Context context, String key, String value) {
        if (userAvatarSP == null) {
            userAvatarSP = context.getSharedPreferences(UserMsgUtil.userAvatar, MODE_PRIVATE);
        }
        userAvatarSP.edit().putString(key, value).apply();
    }

    public static String getUserAvatar(Context context, String key) {
        if (userAvatarSP == null) {
            userAvatarSP = context.getSharedPreferences(UserMsgUtil.userAvatar, MODE_PRIVATE);
        }
        return userAvatarSP.getString(key, "");
    }
}
