package com.example.whitelistvpn;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.util.List;

public class VpnHookModule implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        // 你可以根据包名过滤，只 Hook 特定的加速器，或者 Hook 所有开启 VPN 的应用
        XposedHelpers.findAndHookMethod(
            "android.net.VpnService$Builder", 
            lpparam.classLoader, 
            "establish", 
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object builder = param.thisObject;
                    XposedBridge.log("WhitelistVpn: 正在强制拦截 establish()");

                    // 1. 获取 Context 以访问 PackageManager
                    Object currentApp = XposedHelpers.callStaticMethod(
                        XposedHelpers.findClass("android.app.ActivityThread", null), 
                        "currentApplication"
                    );

                    if (currentApp != null) {
                        PackageManager pm = (PackageManager) XposedHelpers.callMethod(currentApp, "getPackageManager");
                        // 获取所有已安装的应用
                        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
                        
                        for (ApplicationInfo app : apps) {
                            // 2. 强制调用 addAllowedApplication 将其加入白名单
                            XposedHelpers.callMethod(builder, "addAllowedApplication", app.packageName);
                        }
                        XposedBridge.log("WhitelistVpn: 已成功注入 " + apps.size() + " 个应用");
                    }
                }
            }
        );
    }
}
