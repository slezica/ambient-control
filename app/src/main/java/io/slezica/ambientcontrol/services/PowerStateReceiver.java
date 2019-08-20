package io.slezica.ambientcontrol.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import io.slezica.ambientcontrol.ambient.Ambient;
import io.slezica.ambientcontrol.ambient.AmbientProvider;
import io.slezica.ambientcontrol.utils.PowerUtils;
import io.slezica.ambientcontrol.utils.TaggedLog;

public class PowerStateReceiver extends BroadcastReceiver {

    private TaggedLog log = new TaggedLog(this);
    private Ambient ambient;

    @Override
    public void onReceive(Context context, Intent powerIntent) {
        ambient = AmbientProvider.getFor(context);
        PowerUtils.requestIgnoreBatteryOptimizations(context);

        if (!ambient.isSupported() || !ambient.hasPermissions()) {
            log.d("Ambient is not supported, or we have no permissions");
            return;
        }

        final Bundle batteryStatus = context
                .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                .getExtras();

        final int pluggedFlags = batteryStatus.getInt(BatteryManager.EXTRA_PLUGGED, -1);

        final boolean usbCharge = pluggedFlags == BatteryManager.BATTERY_PLUGGED_USB;
        final boolean acCharge = pluggedFlags == BatteryManager.BATTERY_PLUGGED_AC;
        final boolean wlCharge = pluggedFlags == BatteryManager.BATTERY_PLUGGED_WIRELESS;

        ambient.setAlwaysOn(usbCharge || acCharge || wlCharge);
    }
}
