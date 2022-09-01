package io.slezica.ambientcontrol.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.Nullable;

import io.slezica.ambientcontrol.ambient.Ambient;
import io.slezica.ambientcontrol.ambient.AmbientProvider;
import io.slezica.ambientcontrol.utils.PowerUtils;
import io.slezica.ambientcontrol.utils.TaggedLog;

public class AmbientControlService extends Service {

    private TaggedLog log = new TaggedLog(this);
    private Ambient ambient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ambient = AmbientProvider.getFor(this);

        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        checkPowerState();
        registerReceiver(new PowerStateReceiver(), filter);

        return START_STICKY;
    }

    private void checkPowerState() {
        log.d("Received power Intent");

        if (!ambient.isSupported() || !ambient.hasPermissions()) {
            log.d("Ambient is not supported, or we have no permissions");
            return;
        }

        final Bundle batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            .getExtras();

        final int pluggedFlags = batteryStatus.getInt(BatteryManager.EXTRA_PLUGGED, -1);

        final boolean usbCharge = pluggedFlags == BatteryManager.BATTERY_PLUGGED_USB;
        final boolean acCharge = pluggedFlags == BatteryManager.BATTERY_PLUGGED_AC;
        final boolean wlCharge = pluggedFlags == BatteryManager.BATTERY_PLUGGED_WIRELESS;

        log.d("USB " + usbCharge + ", AC " + acCharge + ", WL " + wlCharge);
        ambient.setAlwaysOn(usbCharge || acCharge || wlCharge);
    }

    public class PowerStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent powerIntent) {
            checkPowerState();
        }
    }
}
