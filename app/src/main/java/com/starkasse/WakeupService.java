package com.starkasse;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class WakeupService extends Service {
    private Thread logThread;
    private Process logcat;
    private FileObserver observer;
    private int count = -1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    logcat = Runtime.getRuntime().exec(new String[]{"sh","-c", "cat /proc/irq/148/spurious"});
                    BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 1024);
                    String line = br.readLine();
                    int newCount = Integer.parseInt(line.split(" ")[1]);
                    if (count == -1) count = newCount;
                    Log.d("WakeupService", "trigger");
                    if (newCount != count) {
                        count = newCount;
                        if (!pm.isInteractive()) {
                            //if (line.contains("(pressed) HW keycode = 1") || line.contains("(pressed) HW keycode = 0") || line.contains("SetAnalogGain VoleumType =")) {
                            PowerManager.WakeLock wakeLock = MainActivity.self.wakeLock;
                            if (wakeLock == null) {
                                MainActivity.self.wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
                                wakeLock = MainActivity.self.wakeLock;
                            }

                            if (wakeLock.isHeld()) {
                                wakeLock.release();
                            }

                            wakeLock.acquire();
                            wakeLock.release();

                            Settings.System.putInt(MainActivity.self.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 48 * 60 * 60 * 1000);

                            if (MainActivity.self.screenOn == false) {
                                MainActivity.self.screenOn = true;
                                MainActivity.self.changeBrightness("high");
                            }
                            //}

                        }
                    }
                    logcat.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        runAsForeground();
    }

    private void runAsForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Wakeup")
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MIN)
                .build();

        startForeground(1337, notification);

    }
}
