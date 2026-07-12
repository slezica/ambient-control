package io.slezica.ambientcontrol.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.provider.Settings;

import io.slezica.ambientcontrol.ambient.StatusItem;

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

    public static boolean isIgnoringBatteryOptimizations(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    public static boolean isPlugged(Context context) {
        Intent batteryStatus = context
            .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return batteryStatus != null
            && batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) > 0;
    }

    public static StatusItem getChargerStatus(Context context) {
        return StatusItem.neutral("Charger", isPlugged(context) ? "Connected" : "Disconnected");
    }

    public static StatusItem getBatteryOptimizationStatus(Context context) {
        if (isIgnoringBatteryOptimizations(context)) {
            return StatusItem.ok("Battery optimization", "Unrestricted");
        }

        return StatusItem.warn(
            "Battery optimization", "Restricted",
            "The system may kill the background service, so the charger goes unnoticed.",
            () -> requestIgnoreBatteryOptimizations(context)
        );
    }
}
