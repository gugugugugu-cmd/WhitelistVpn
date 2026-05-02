package com.muort.whitelistvpn;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String appName;
    public String packageName;
    public Drawable icon;
    public boolean checked;

    public AppInfo(String appName, String packageName, Drawable icon, boolean checked) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.checked = checked;
    }
}