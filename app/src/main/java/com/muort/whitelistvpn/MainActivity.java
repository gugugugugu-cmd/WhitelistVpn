package com.muort.whitelistvpn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Switch switchEnable;
    private RadioButton rbOff;
    private RadioButton rbAll;
    private RadioButton rbSelected;
    private Button btnSelectApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchEnable = findViewById(R.id.switchEnable);
        rbOff = findViewById(R.id.rbOff);
        rbAll = findViewById(R.id.rbAll);
        rbSelected = findViewById(R.id.rbSelected);
        btnSelectApps = findViewById(R.id.btnSelectApps);

        loadState();

        switchEnable.setOnCheckedChangeListener((buttonView, isChecked) ->
                Config.setEnabled(this, isChecked));

        rbOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Config.setMode(this, Config.MODE_OFF);
                btnSelectApps.setEnabled(false);
            }
        });

        rbAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Config.setMode(this, Config.MODE_ALL);
                btnSelectApps.setEnabled(false);
            }
        });

        rbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Config.setMode(this, Config.MODE_SELECTED);
                btnSelectApps.setEnabled(true);
            }
        });

        btnSelectApps.setOnClickListener(v ->
                startActivity(new Intent(this, AppSelectActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadState();
    }

    private void loadState() {
        switchEnable.setChecked(Config.isEnabled(this));

        String mode = Config.getMode(this);
        rbOff.setChecked(Config.MODE_OFF.equals(mode));
        rbAll.setChecked(Config.MODE_ALL.equals(mode));
        rbSelected.setChecked(Config.MODE_SELECTED.equals(mode));

        btnSelectApps.setEnabled(Config.MODE_SELECTED.equals(mode));
    }
}