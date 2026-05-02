package com.muort.whitelistvpn;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppSelectActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnSave;
    private EditText editSearch;
    private AppListAdapter adapter;
    private final List<AppInfo> appList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);

        listView = findViewById(R.id.listViewApps);
        btnSave = findViewById(R.id.btnSaveApps);
        editSearch = findViewById(R.id.editSearch);

        loadApps();

        adapter = new AppListAdapter(this, appList);
        listView.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s == null ? "" : s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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
        for (AppInfo app : adapter.getOriginalApps()) {
            if (app.checked) {
                selected.add(app.packageName);
            }
        }

        Config.setSelectedPackages(this, selected);
        finish();
    }
}