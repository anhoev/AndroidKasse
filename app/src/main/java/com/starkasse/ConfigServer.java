package com.starkasse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.posin.device.CustomerDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Consumer;

import static com.starkasse.MainActivity.presentation;
import static com.starkasse.MainActivity.wifiManager;
import static org.chromium.base.ThreadUtils.runOnUiThread;

/**
 * Created by anhtran on 19.12.17.
 */

public class ConfigServer {
    public static void configServer(AsyncHttpServer server, final MainActivity context) {
        server.get("/startWebview", (request, response) -> {
            context.startWebview();
            response.send("OK");
        });

        server.get("/updateAndRestartNode", (request, response) -> {
            runOnUiThread((Runnable) () -> new Handler().postDelayed(
                    () -> {
                        Log.d("server", "updateAndRestartNode");

                        context.utils.stopNode();
                        String s = EnvUtils.isRooted() ? "su" : "sh";
                        try {
                            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/index"});
                            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/version.json"});
                            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/phantomjs"});
                            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/mongod"});
                            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/update/index"});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (new File("/sdcard/starkasse-main.apk").exists()) {
                            context.installApk();
                        } else {
                            context.utils.startNodejs();
                        }
                    },
                    5000));

            response.send("OK");
        });

        server.get("/wifiList", (request, response) -> {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
                wifiManager.setWifiEnabled(true);

            wifiManager.startScan();

            List<ScanResult> results = wifiManager.getScanResults();
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            String ssid = "";
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
            for (ScanResult network : results) {
                if (ssid.equals("\"" + network.SSID + "\"")) {
                    network.BSSID = "Joined";
                }
            }

            Gson gson = new Gson();
            response.send(gson.toJson(results));
        });

        server.get("/reloadCustomerdisplay", (request, response) -> {
            runOnUiThread(() -> {
                if (presentation != null)
                    presentation.startWebview();
            });
            response.send("OK");
        });

        server.get("/isOnline", (request, response) -> response.send(isOnline(context) ? "true" : "false"));

        server.post("/changeUrl", (request, response) -> {
            JSONObject json = ((JSONObjectBody) request.getBody()).get();
            final String address;

            try {
                address = json.getString("url");
                runOnUiThread(() -> context.xWalkWebView.loadUrl(address));
            } catch (JSONException e) {
            }
        });

        server.post("/connectToAp", (request, response) -> {
            JSONObject json = ((JSONObjectBody) request.getBody()).get();

            try {
                String ssid = json.getString("ssid");
                String password = json.getString("password");

                ssid = ssid.replace("\'", "’");

                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    wifiManager.removeNetwork(i.networkId);
                    wifiManager.saveConfiguration();
                }

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + ssid + "\"";
                conf.preSharedKey = "\"" + password + "\"";
                wifiManager.addNetwork(conf);

                List<WifiConfiguration> list2 = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : list2) {
                    if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();

                        break;
                    }
                }

            } catch (JSONException e) {
            }

            response.send("OK");
        });
    }

    public static boolean isOnline(MainActivity context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
