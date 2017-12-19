package com.starkasse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.starkasse.MainActivity.presentation;
import static com.starkasse.MainActivity.wifiManager;
import static org.chromium.base.ThreadUtils.runOnUiThread;

/**
 * Created by anhtran on 19.12.17.
 */

public class ConfigServer {
    public static void configServer(AsyncHttpServer server, final MainActivity context) {
        server.get("/startWebview", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                context.startWebview();
                response.send("OK");
            }
        });

        server.get("/updateAndRestartNode", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        Log.d("server", "updateAndRestartNode");

                                        context.utils.stopNode();
                                        String s = EnvUtils.isRooted() ? "su" : "sh";
                                        try {
                                            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse"});
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (new File("/sdcard/starkasse-main.apk").exists()) {
                                            context.installApk();
                                        } else {
                                            context.utils.startNodejs();
                                        }
                                    }
                                },
                                5000);
                    }
                });

                response.send("OK");
            }
        });

        server.get("/wifiList", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
                    wifiManager.setWifiEnabled(true);

                wifiManager.startScan();

                List<ScanResult> results = wifiManager.getScanResults();
                Gson gson = new Gson();
                response.send(gson.toJson(results));
            }
        });

        server.get("/reloadCustomerdisplay", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (presentation != null)
                            presentation.startWebview();
                    }
                });
                response.send("OK");
            }
        });

        server.get("/isOnline", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send(isOnline(context) ? "true" : "false");
            }
        });

        server.post("/changeUrl", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                JSONObject json = ((JSONObjectBody) request.getBody()).get();
                final String address;

                try {
                    address = json.getString("url");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.xWalkWebView.loadUrl(address);
                        }
                    });
                } catch (JSONException e) {
                }
            }
        });

        server.post("/connectToAp", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                JSONObject json = ((JSONObjectBody) request.getBody()).get();

                try {
                    final String ssid = json.getString("ssid");
                    final String password = json.getString("password");

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
            }
        });
    }

    public static boolean isOnline(MainActivity context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
