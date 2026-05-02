package com.muort.whitelistvpn;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Config {
    public static final String PREF_NAME = "config";
    public static final String KEY_ENABLED = "enabled";
    public static final String KEY_MODE = "mode";
    public static final String KEY_SELECTED_PACKAGES = "selected_packages";

    public static final String MODE_OFF = "off";
    public static final String MODE_ALL = "all";
    public static final String MODE_SELECTED = "selected";

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled(Context context) {
        return prefs(context).getBoolean(KEY_ENABLED, true);
    }

    public static void setEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply();
    }

    public static String getMode(Context context) {
        return prefs(context).getString(KEY_MODE, MODE_ALL);
    }

    public static void setMode(Context context, String mode) {
        prefs(context).edit().putString(KEY_MODE, mode).apply();
    }

    public static Set<String> getSelectedPackages(Context context) {
        return new HashSet<>(
                prefs(context).getStringSet(KEY_SELECTED_PACKAGES, new HashSet<>())
        );
    }

    public static void setSelectedPackages(Context context, Set<String> packages) {
        prefs(context).edit().putStringSet(KEY_SELECTED_PACKAGES, new HashSet<>(packages)).apply();
    }
}