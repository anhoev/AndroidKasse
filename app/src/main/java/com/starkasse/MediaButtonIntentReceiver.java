package com.starkasse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;

/**
 * Created by anhtran on 14.11.17.
 */

public class MediaButtonIntentReceiver extends BroadcastReceiver {
    private static final String TAG = MediaButtonIntentReceiver.class.getSimpleName();

    public MediaButtonIntentReceiver() {
    }

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

                    MainActivity.self.runOnUiThread(() -> {
                        if (MainActivity.self.screenOn == false) {
                            MainActivity.self.screenOn = true;
                            MainActivity.self.overlay.setVisibility(View.INVISIBLE);
                            new android.os.Handler().postDelayed(
                                    () -> Settings.System.putInt(MainActivity.self.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255)
                                    , 30);

                        }
                    });
                } else {
                    MainActivity.self.runOnUiThread(() -> {
                        if (MainActivity.self.screenOn == false) {

                            MainActivity.self.overlay.setVisibility(View.INVISIBLE);
                        } else {

                            MainActivity.self.overlay.setVisibility(View.VISIBLE);
                        }
                    });
                    new android.os.Handler().postDelayed(
                            () -> {
                                if (MainActivity.self.screenOn == false) {
                                    MainActivity.self.screenOn = true;
                                    Settings.System.putInt(MainActivity.self.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
                                } else {
                                    MainActivity.self.screenOn = false;
                                    Settings.System.putInt(MainActivity.self.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                                }
                            }
                            , 30);

                    MainActivity.self.resetDisconnectTimer();
                }
            }

        }
    }
}
