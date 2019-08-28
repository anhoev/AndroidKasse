package com.gigasource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * Created by anhtran on 28.08.17.
 */

public class ConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) wifiManager.setWifiEnabled(true);
    }
}
