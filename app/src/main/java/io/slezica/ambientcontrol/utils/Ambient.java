package io.slezica.ambientcontrol.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class Ambient {

    private final TaggedLog log = new TaggedLog(this);

    private final String DOZE_ALWAYS_ON = "doze_always_on";

    private Context context;

    public Ambient(Context context) {
        this.context = context;
    }

    public boolean hasPermissions() {
        try {
            final int state = ContextCompat
                    .checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS);

            return (state == PackageManager.PERMISSION_GRANTED);

        } catch (Exception e) {
            return false;
        }
    }

    public void requestPermissions() {
        if (! (context instanceof Activity)) {
            throw new RuntimeException();
        }

        final String permission = Manifest.permission.WRITE_SECURE_SETTINGS;

        log.d("Requesting permission " + permission);
        ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, 1);
    }

    public boolean isSupported() {
        try {
            Settings.Secure.getInt(context.getContentResolver(), DOZE_ALWAYS_ON);
            return true;

        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }

    public void setAlwaysOn( boolean alwaysOn) {
        log.d("Setting ambient always on: " + alwaysOn);

        if (alwaysOn != isAlwaysOn()) {
            Settings.Secure.putInt(context.getContentResolver(), DOZE_ALWAYS_ON, alwaysOn ? 1 : 0);
        }
    }

    public boolean isAlwaysOn() {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), DOZE_ALWAYS_ON) != 0;

        } catch (Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
