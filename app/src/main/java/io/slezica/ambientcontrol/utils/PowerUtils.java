package io.slezica.ambientcontrol.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

import static android.content.Context.POWER_SERVICE;

public class PowerUtils {

    public static void requestIgnoreBatteryOptimizations(Context context) {
        Intent intent = new Intent();
        String packageName = context.getPackageName();

        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);

        if (! pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));

            context.startActivity(intent);
        }
    }
}
