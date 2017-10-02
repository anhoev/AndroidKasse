package com.starkasse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * Created by anhtran on 28.08.17.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {
    private WifiManager wifiManager;
    private Context context;
    private WifiConfiguration i;
    static Boolean canReconnect = true;
    static Boolean processing = false;
    static ConnectionChangeReceiver self;
    static boolean connected = false;
    static boolean shouldOnResume = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int dbm = wifiInfo.getRssi();
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        i = list.get(0);

        //Toast.makeText(context, wifiInfo.getBSSID() + " dbm :" + dbm, Toast.LENGTH_SHORT).show();

        if (dbm > -85 && dbm != 0 && shouldOnResume) {
            shouldOnResume = false;
            try {
                MainActivity.self.onResumeCb.call();
            } catch (Exception e) {
            }
            //reconnect();
        }
    }

    public void reconnect() {
        if (true) return;
        if (processing) return;
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) canReconnect = true;

        if (!canReconnect) return;


        if (activeNetwork != null) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int dbm = wifiInfo.getRssi();

            if (dbm > -87) {
                return;
            }
        }


        Toast.makeText(context, "reconnect", Toast.LENGTH_SHORT).show();

        RunnableFuture future = new FutureTask<>(
                new Callable<Void>() {
                    @Override
                    public Void call() throws InterruptedException {
                        processing = true;
                        while (!connected) {
                            if (activeNetwork != null) wifiManager.disconnect();
                            wifiManager.enableNetwork(i.networkId, true);
                            wifiManager.reconnect();
                            if (cm.getActiveNetworkInfo() != null) {
                                connected = true;
                            }
                        }

                        MainActivity.self.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                                shouldOnResume = true;
                            }
                        });


                        canReconnect = false;

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        canReconnect = true;
                                        processing = false;
                                    }
                                },
                                10000);

                        return null;
                    }

                });

        Thread t = new Thread(future);
        t.start();
    }
}
