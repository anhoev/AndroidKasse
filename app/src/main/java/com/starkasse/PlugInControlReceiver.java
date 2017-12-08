package com.starkasse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by anhtran on 02.11.17.
 */

public class PlugInControlReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
            MainActivity.self.logout();
            MainActivity.self.resetDisconnectTimer();

            MainActivity.self.updateTime = true;

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            MainActivity.self.updateTime = false;
                        }
                    },
                    10000);
        } else {

        }
    }
}
