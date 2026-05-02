package com.muort.whitelistvpn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private final Context context;
    private final List<AppInfo> apps;

    public AppListAdapter(Context context, List<AppInfo> apps) {
        this.context = context;
        this.apps = apps;
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        AppInfo app = apps.get(position);

        icon.setImageDrawable(app.icon);
        name.setText(app.appName);
        pkg.setText(app.packageName);

        check.setOnCheckedChangeListener(null);
        check.setChecked(app.checked);
        check.setOnCheckedChangeListener((buttonView, isChecked) -> app.checked = isChecked);

        view.setOnClickListener(v -> {
            app.checked = !app.checked;
            check.setChecked(app.checked);
        });

        return view;
    }
}