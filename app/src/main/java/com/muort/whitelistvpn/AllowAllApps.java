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

    private static boolean sHooked = false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (sHooked) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                    "android.net.VpnService$Builder",
                    lpparam.classLoader,
                    "establish",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object builder = param.thisObject;

                            if (AndroidAppHelper.currentApplication() == null) {
                                return;
                            }

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
                                    // 忽略单个应用失败
                                }
                            }
                        }
                    }
            );

            sHooked = true;
            XposedBridge.log("AllWhitelist: hooked VpnService.Builder.establish");
        } catch (Throwable t) {
            XposedBridge.log("AllWhitelist hook failed: " + t);
        }
    }
}