package com.muort.whitelistvpn;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AllowAllApps implements IXposedHookLoadPackage {

    private static final String MODULE_PACKAGE = "com.muort.whitelistvpn";
    private static final String PREF_NAME = "config";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            XposedHelpers.findAndHookMethod(
                    "android.net.VpnService$Builder",
                    lpparam.classLoader,
                    "establish",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Application app = AndroidAppHelper.currentApplication();
                            if (app == null) return;

                            Object builder = param.thisObject;

                            XSharedPreferences prefs = new XSharedPreferences(MODULE_PACKAGE, PREF_NAME);
                            prefs.reload();

                            boolean enabled = prefs.getBoolean(Config.KEY_ENABLED, true);
                            if (!enabled) return;

                            String mode = prefs.getString(Config.KEY_MODE, Config.MODE_ALL);
                            PackageManager pm = app.getPackageManager();

                            if (Config.MODE_OFF.equals(mode)) {
                                return;
                            }

                            if (Config.MODE_ALL.equals(mode)) {
                                List<PackageInfo> packages =
                                        pm.getInstalledPackages(PackageManager.GET_META_DATA);

                                for (PackageInfo info : packages) {
                                    if (info == null || info.packageName == null) continue;
                                    try {
                                        XposedHelpers.callMethod(
                                                builder,
                                                "addAllowedApplication",
                                                info.packageName
                                        );
                                    } catch (Throwable ignored) {
                                    }
                                }
                            } else if (Config.MODE_SELECTED.equals(mode)) {
                                Set<String> selected =
                                        prefs.getStringSet(Config.KEY_SELECTED_PACKAGES, null);

                                if (selected != null) {
                                    for (String pkg : selected) {
                                        if (pkg == null || pkg.trim().isEmpty()) continue;
                                        try {
                                            XposedHelpers.callMethod(
                                                    builder,
                                                    "addAllowedApplication",
                                                    pkg
                                            );
                                        } catch (Throwable ignored) {
                                        }
                                    }
                                }
                            }
                        }
                    }
            );

            XposedBridge.log("WhitelistVpn: hook installed");
        } catch (Throwable t) {
            XposedBridge.log("WhitelistVpn hook failed: " + t);
        }
    }
}