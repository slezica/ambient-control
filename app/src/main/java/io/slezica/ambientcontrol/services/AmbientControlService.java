package io.slezica.ambientcontrol.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import io.slezica.ambientcontrol.MainActivity;
import io.slezica.ambientcontrol.R;
import io.slezica.ambientcontrol.ambient.Ambient;
import io.slezica.ambientcontrol.ambient.AmbientProvider;
import io.slezica.ambientcontrol.utils.TaggedLog;

public class AmbientControlService extends Service {

    private static String NOTIFICATION_CHANNEL_ID = "Service";
    private static String NOTIFICATION_CHANNEL_NAME = "Ambient Control";
    private static String NOTIFICATION_TITLE = "Ambient Control";

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            startForeground(1, createNotification());
        }

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification createNotification() {
        Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true);

        Intent intent = new Intent(this, AmbientControlService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel ch = new NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_MIN
        );

        ch.setDescription("Sticky notification to keep service running");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(ch);
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
