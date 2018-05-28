package com.starkasse;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WakeupService extends Service{
    private Thread logThread;
    private Process logcat;
    public Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (Exception e) {
        }
        try {
            logcat = Runtime.getRuntime().exec(new String[]{"logcat"});
        } catch (IOException e) {
            e.printStackTrace();
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        logThread = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 1024);
                String line;
                while ((line = br.readLine()) != null) {
                    //Log.d("WakeupService", line);
                    try {
                        //AP-STA-DISCONNECTED
                        if (line.contains("(pressed) HW keycode = 1")) {
                            if (!pm.isInteractive()) {
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
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        logThread.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
