package com.gigasource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

/**
 * Created by anhtran on 14.11.17.
 */

public class MediaButtonIntentReceiver extends BroadcastReceiver {
    private static final String TAG = MediaButtonIntentReceiver.class.getSimpleName();

    public MediaButtonIntentReceiver() {
    }

    static boolean locked;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
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

                    locked = true;
                    new android.os.Handler().postDelayed(() -> {
                        locked = false;
                    }, 2000);
                } else {
                    if (locked) return;
                    /*MainActivity.self.runOnUiThread(() -> {
                        if (MainActivity.self.screenOn == false) {
                            //MainActivity.self.overlay.setVisibility(View.INVISIBLE);
                        } else {
                            //MainActivity.self.overlay.setVisibility(View.VISIBLE);
                        }
                    });*/

                    if (MainActivity.self.screenOn == false) {
                        MainActivity.self.screenOn = true;
                        MainActivity.self.changeBrightness("high");
                    } else {
                        MainActivity.self.screenOn = false;
                        MainActivity.self.changeBrightness("low");
                    }

                    locked = true;
                    new android.os.Handler().postDelayed(() -> {
                        locked = false;
                    }, 0);

                    MainActivity.self.resetDisconnectTimer();
                }
            }

        }
    }
}
