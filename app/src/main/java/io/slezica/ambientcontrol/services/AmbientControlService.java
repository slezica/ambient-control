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

        Intent intent = new Intent(this, MainActivity.class);
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
        log.d("Checking initial power state");

        final Bundle batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            .getExtras();

        // Any non-zero flag (AC, USB, wireless, dock) counts as plugged:
        final int pluggedFlags = batteryStatus.getInt(BatteryManager.EXTRA_PLUGGED, 0);

        applyPowerState(pluggedFlags > 0);
    }

    private void applyPowerState(boolean plugged) {
        if (!ambient.isSupported() || !ambient.hasPermissions()) {
            log.d("Ambient is not supported, or we have no permissions");
            return;
        }

        log.d("Plugged: " + plugged);

        try {
            ambient.setAlwaysOn(plugged);
        } catch (Exception e) {
            // A rejected settings write must not crash-loop the sticky service:
            log.d("Failed to apply power state: " + e);
        }
    }

    public class PowerStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent powerIntent) {
            // The sticky ACTION_BATTERY_CHANGED intent can lag behind this
            // broadcast, so the action itself is the source of truth here:
            applyPowerState(Intent.ACTION_POWER_CONNECTED.equals(powerIntent.getAction()));
        }
    }
}
