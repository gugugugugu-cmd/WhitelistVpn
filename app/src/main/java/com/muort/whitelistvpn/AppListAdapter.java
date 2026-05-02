package com.muort.whitelistvpn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppListAdapter extends BaseAdapter {

    private final Context context;
    private final List<AppInfo> originalApps;
    private final List<AppInfo> filteredApps;

    public AppListAdapter(Context context, List<AppInfo> apps) {
        this.context = context;
        this.originalApps = apps;
        this.filteredApps = new ArrayList<>(apps);
    }

    @Override
    public int getCount() {
        return filteredApps.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void filter(String keyword) {
        filteredApps.clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            filteredApps.addAll(originalApps);
        } else {
            String lower = keyword.toLowerCase(Locale.ROOT).trim();

            for (AppInfo app : originalApps) {
                String name = app.appName == null ? "" : app.appName.toLowerCase(Locale.ROOT);
                String pkg = app.packageName == null ? "" : app.packageName.toLowerCase(Locale.ROOT);

                if (name.contains(lower) || pkg.contains(lower)) {
                    filteredApps.add(app);
                }
            }
        }

        notifyDataSetChanged();
    }

    public List<AppInfo> getOriginalApps() {
        return originalApps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        }

        ImageView icon = view.findViewById(R.id.appIcon);
        TextView name = view.findViewById(R.id.appName);
        TextView pkg = view.findViewById(R.id.appPackage);
        CheckBox check = view.findViewById(R.id.appCheck);

        AppInfo app = filteredApps.get(position);

        icon.setImageDrawable(app.icon);
        name.setText(app.appName);
        pkg.setText(app.packageName);

        check.setOnCheckedChangeListener(null);
        check.setChecked.setOnCheckedChangeListener((buttonView, isChecked) -> app.checked = isChecked);

        view.setOnClickListener(v -> {
            app.checked = !app.checked;
            check.setChecked(app.checked);
        });

        return view;
    }
}