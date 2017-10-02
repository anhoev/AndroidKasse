package com.starkasse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static com.starkasse.ConnectionChangeReceiver.canReconnect;
import static com.starkasse.ConnectionChangeReceiver.processing;

/**
 * Created by anhtran on 28.08.17.
 */

public class ConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork && processing == false) {
            Log.d("ConnectionReceiver", "auto reconnect!");
            ConnectionChangeReceiver.self.reconnect();
            Toast.makeText(context, "auto reconnect!", Toast.LENGTH_SHORT).show();
        }
    }
}
