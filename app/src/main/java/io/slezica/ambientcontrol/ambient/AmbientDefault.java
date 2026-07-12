package io.slezica.ambientcontrol.ambient;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import io.slezica.ambientcontrol.utils.PowerUtils;
import io.slezica.ambientcontrol.utils.TaggedLog;

public class AmbientDefault implements Ambient {

    private final TaggedLog log = new TaggedLog(this);

    private final String DOZE_ALWAYS_ON = "doze_always_on";

    private Context context;

    public AmbientDefault(Context context) {
        this.context = context;
    }

    @Override
    public List<StatusItem> getStatus() {
        List<StatusItem> items = new ArrayList<>();

        if (!isSupported()) {
            items.add(StatusItem.warn(
                "Ambient display", "Not detected",
                "This device does not expose the doze_always_on setting.",
                null
            ));
            return items;
        }

        if (hasPermissions()) {
            items.add(StatusItem.ok("Permission", "Granted"));
        } else {
            items.add(StatusItem.warn(
                "Permission", "Missing",
                "Grant WRITE_SECURE_SETTINGS through ADB:\n"
                    + "adb shell pm grant io.slezica.ambientcontrol"
                    + " android.permission.WRITE_SECURE_SETTINGS",
                null
            ));
            return items;
        }

        boolean alwaysOn = isAlwaysOn();
        boolean plugged = PowerUtils.isPlugged(context);

        if (alwaysOn == plugged) {
            items.add(StatusItem.ok("Ambient display", alwaysOn ? "On" : "Off"));
        } else {
            items.add(StatusItem.warn(
                "Ambient display", alwaysOn ? "On" : "Off",
                "Doesn't match the charger state. The background service may not be running.",
                null
            ));
        }

        return items;
    }

    @Override
    public boolean hasPermissions() {
        try {
            final int state = ContextCompat
                    .checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS);

            return (state == PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void requestPermissions() {
        if (! (context instanceof Activity)) {
            throw new RuntimeException();
        }

        final String permission = Manifest.permission.WRITE_SECURE_SETTINGS;

        log.d("Requesting permission " + permission);
        ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, 1);
    }

    @Override
    public boolean isSupported() {
        try {
            Settings.Secure.getInt(context.getContentResolver(), DOZE_ALWAYS_ON);
            return true;

        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }

    @Override
    public void setAlwaysOn(boolean alwaysOn) {
        log.d("Setting ambient always on: " + alwaysOn);

        if (alwaysOn != isAlwaysOn()) {
            Settings.Secure.putInt(context.getContentResolver(), DOZE_ALWAYS_ON, alwaysOn ? 1 : 0);
        }
    }

    @Override
    public boolean isAlwaysOn() {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), DOZE_ALWAYS_ON) != 0;

        } catch (Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
