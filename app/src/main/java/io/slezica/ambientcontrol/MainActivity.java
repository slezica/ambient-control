package io.slezica.ambientcontrol;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.slezica.ambientcontrol.ambient.Ambient;
import io.slezica.ambientcontrol.ambient.AmbientProvider;
import io.slezica.ambientcontrol.inspection.SettingsReader;
import io.slezica.ambientcontrol.services.AmbientControlService;
import io.slezica.ambientcontrol.utils.PowerUtils;

public class MainActivity extends AppCompatActivity {

    private Ambient ambient;
    private TextView explanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ambient = AmbientProvider.getFor(this);
        explanation = (TextView) findViewById(R.id.explanation);

        startControlService();

        // Only for development, as Android changes how settings are managed:
        // startWatchingSettingChanges();
    }

    private void startControlService() {
        PowerUtils.requestIgnoreBatteryOptimizations(this);

        Intent intent = new Intent(this, AmbientControlService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void startWatchingSettingChanges() {
        new SettingsReader(getContentResolver()).startWatchingChanges(this::onSettingChange);
    }

    private Void onSettingChange(String name, String oldValue, String newValue) {
        Log.d("SettingsReader", "Settings: " + name + " changed from '" + oldValue + "' to '" + newValue + "'");
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!ambient.isSupported()) {
            explanation.setText(getString(R.string.not_supported));

        } else if (!ambient.hasPermissions()) {
            explanation.setText(getString(R.string.no_permissions));

        } else {
            explanation.setText(getString(
                    R.string.ambient_state,
                    ambient.isAlwaysOn() ? "ON" : "OFF"
            ));
        }
    }
}
