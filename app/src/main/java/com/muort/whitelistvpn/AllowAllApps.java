package com.muort.whitelistvpn;

import android.app.AndroidAppHelper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AllowAllApps implements IXposedHookLoadPackage {

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
                            try {
                                if (AndroidAppHelper.currentApplication() == null) {
                                    return;
                                }

                                Object builder = param.thisObject;
                                PackageManager packageManager =
                                        AndroidAppHelper.currentApplication().getPackageManager();

                                List<PackageInfo> packages =
                                        packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

                                for (PackageInfo info : packages) {
                                    if (info == null || info.packageName == null) {
                                        continue;
                                    }

                                    try {
                                        XposedHelpers.callMethod(
                                                builder,
                                                "addAllowedApplication",
                                                info.packageName
                                        );
                                    } catch (Throwable ignored) {
                                        // 忽略单个应用失败，避免影响 establish()
                                    }
                                }
                            } catch (Throwable t) {
                                XposedBridge.log("WhitelistVpn beforeHookedMethod error in "
                                        + lpparam.packageName + ": " + t);
                            }
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("WhitelistVpn hook failed in " + lpparam.packageName + ": " + t);
        }
    }
}