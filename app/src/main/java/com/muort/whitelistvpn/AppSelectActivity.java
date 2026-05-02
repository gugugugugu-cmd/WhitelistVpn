package com.muort.whitelistvpn;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppSelectActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnSave;
    private AppListAdapter adapter;
    private final List<AppInfo> appList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);

        listView = findViewById(R.id.listViewApps);
        btnSave = findViewById(R.id.btnSaveApps);

        loadApps();

        adapter = new AppListAdapter(this, appList);
        listView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveSelection());
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        Set<String> selected = Config.getSelectedPackages(this);

        List<ApplicationInfo> installed = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo ai : installed) {
            String label = String.valueOf(pm.getApplicationLabel(ai));
            String pkg = ai.packageName;

            appList.add(new AppInfo(
                    label,
                    pkg,
                    pm.getApplicationIcon(ai),
                    selected.contains(pkg)
            ));
        }

        appList.sort((a, b) -> a.appName.compareToIgnoreCase(b.appName));
    }

    private void saveSelection() {
        Set<String> selected = new HashSet<>();
        for (AppInfo app : appList) {
            if (app.checked) {
                selected.add(app.packageName);
            }
        }

        Config.setSelectedPackages(this, selected);
        finish();
    }
}