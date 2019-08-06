package com.starkasse;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

import static com.starkasse.MainActivity.presentation;
import static com.starkasse.MainActivity.wifiManager;
import static com.starkasse.Utils.shell;
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
                        Log.d("node", "updateAndRestartNode");

                        context.utils.stopNode();
                        shell("chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/index");
                        shell("chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/version.json");
                        shell("chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/phantomjs");
                        shell("chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/mongod");
                        shell("chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/update/index");

                        if (new File("/sdcard/starkasse-main.apk").exists()) {
                            context.installApk();
                        } else {
                            (new Handler()).postDelayed(context.utils::startNodejs, 500);
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

        server.get("/disableWifi", (request, response) -> {
            wifiManager.disconnect();
            response.send("ok");
        });

        server.get("/enableWifi", (request, response) -> {
            wifiManager.enableNetwork(wifiManager.getConfiguredNetworks().get(0).networkId, true);
            wifiManager.reconnect();
            response.send("ok");
        });

        server.post("/connectToAp", (request, response) -> {
            JSONObject json = ((JSONObjectBody) request.getBody()).get();

            try {
                String ssid = json.getString("ssid");
                String password = json.getString("password");

                ssid = ssid.replace("\'", "’");

                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    if (i.SSID.equals("\"" + ssid + "\"")) wifiManager.removeNetwork(i.networkId);
                    wifiManager.saveConfiguration();
                }

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + ssid + "\"";
                if (!TextUtils.isEmpty(password)) {
                    conf.preSharedKey = "\"" + password + "\"";
                } else {
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }

                int id = wifiManager.addNetwork(conf);

                wifiManager.disconnect();
                wifiManager.enableNetwork(id, true);
                wifiManager.reconnect();

            } catch (JSONException e) {
            }

            response.send("OK");
        });

        server.post("/writeToDsp", (request, response) -> {
            JSONObject json = ((JSONObjectBody) request.getBody()).get();

            try {
                String text = json.getString("text");
                context.dsp.write(text);
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
