package com.starkasse;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaRouter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.teamviewer.sdk.screensharing.api.TVConfigurationID;
import com.teamviewer.sdk.screensharing.api.TVCreationError;
import com.teamviewer.sdk.screensharing.api.TVSession;
import com.teamviewer.sdk.screensharing.api.TVSessionConfiguration;
import com.teamviewer.sdk.screensharing.api.TVSessionCreationCallback;
import com.teamviewer.sdk.screensharing.api.TVSessionFactory;

import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;

import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.starkasse.BluetoothServer.binder;
import static com.starkasse.BluetoothServer.conn;

public class MainActivity extends XWalkActivity {

    @InjectView(R.id.xwalkWebView)
    XWalkView xWalkWebView;

    final int uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    SharedPreferences sharedPref;

    static AsyncHttpServer server = new AsyncHttpServer();
    static AsyncHttpServer serverGateway = new AsyncHttpServer();

    @InjectView(R.id.secondaryDevice)
    CheckBox secondaryDeviceCheckbox;

    @InjectView(R.id.ipAddressForSecondaryDevice)
    EditText ipAddressForSecondaryDeviceInput;

    @InjectView(R.id.reloadBtn)
    Button reloadButton;

    @InjectView(R.id.disableSystemUi)
    Button disableSystemUiButton;

    @InjectView(R.id.enableSystemUi)
    Button enableSystemUiButton;

    @InjectView(R.id.main)
    View main;

    @InjectView(R.id.setting)
    Button settingBtn;

    @InjectView(R.id.domain)
    EditText domain;

    @InjectView(R.id.setSoftetherDomain)
    Button setSoftetherDomainBtn;

    @InjectView(R.id.brightnessDown)
    Button brightnessDownBtn;

    @InjectView(R.id.brightnessUp)
    Button brightnessUpBtn;

    @InjectView(R.id.wifiDebug)
    Button wifiDebugBtn;

    @InjectView(R.id.wifiDebugDeactive)
    Button wifiDebugDeactiveBtn;

    @InjectView(R.id.loading)
    View loadingView;

    static CustomerPresentation presentation;
    private BluetoothServer bluetoothServer;
    private WifiManager wifiManager;
    private TVSessionConfiguration config;
    private boolean autoReloadValue;

    @InjectView(R.id.autoReload)
    CheckBox autoReload;
    private int loadingAnzahl;
    boolean secondaryDeviceActive;
    String ipAddressForSecondaryDevice;
    private PowerManager.WakeLock wakeLock;

    void showLoading() {
        xWalkWebView.setVisibility(View.INVISIBLE);
        main.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);
    }

    void hideLoading() {
        main.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothServer.uninit();
        server.stop();

        if (binder != null) {
            binder.disconnectCurrentPort(new UiExecute() {
                @Override
                public void onsucess() {

                }

                @Override
                public void onfailed() {

                }
            });
        }
        unbindService(conn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configServer();
        server.listen(5000);
        serverGateway.listen(44818);

        Intent intent = new Intent(this, PosprinterService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        if (sharedPref.getBoolean("NotFirstTime", true)) {
            EnvUtils.updateEnv(this);
            sharedPref.edit().putBoolean("NotFirstTime", false).commit();
        }

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        autoReloadInit();

        findViewById(R.id.startLinux).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EnvUtils.cli(MainActivity.this, "-p linux start", "-m");
                stop();
                startProgram();
            }
        });

        findViewById(R.id.stopLinux).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                            getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
                        }
                    }
                });

        // prevent lockscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWebview();
            }
        });

        bluetoothServer = new BluetoothServer();
        bluetoothServer.init(this);


        //customerdisplay

        MediaRouter mediaRouter = (MediaRouter) this.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(0);
        if (route != null) {
            Display presentationDisplay = route.getPresentationDisplay();
            if (presentationDisplay != null) {
                presentation = new CustomerPresentation(this, presentationDisplay);
            }
        }

        //server

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        loadingAnzahl = 0;
        loadingView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (loadingAnzahl == 3) {
                    loadingAnzahl = 0;
                    hideLoading();
                } else {
                    loadingAnzahl++;
                }
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        setSoftetherDomainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = domain.getText().toString();
                if (TextUtils.isEmpty(name)) return;

                String data = "{\"name\":\"" + name + "\"}"; //data to post

                OutputStream out = null;
                try {

                    URL url = new URL("localhost:8888/api/install-softether");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    out = new BufferedOutputStream(urlConnection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    out.close();
                    urlConnection.connect();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        brightnessDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int current = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                    if (current > 10)
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, current - 5);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        brightnessUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int current = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                    if (current < 250)
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, current + 5);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        wifiDebugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "setprop persist.adb.tcp.port 5555"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        wifiDebugDeactiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "setprop persist.adb.tcp.port \"\""});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        disableSystemUiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSystemUIEnabled(false);
            }
        });

        enableSystemUiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSystemUIEnabled(true);
            }
        });

        secondaryDeviceCheckboxInit();
        //reconnect();

        if (autoReloadValue) loadingView.setVisibility(View.VISIBLE);

        /*try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "service call SurfaceFlinger 1008 i32 1"});
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        setWakelock();
    }


    public void setWakelock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakelock");
        }

        if (wakeLock.isHeld()) {
            wakeLock.release(); // release old wake lock
        }

        wakeLock.acquire();
        //wakeLock.release();
    }

    public void setSystemUIEnabled(boolean enabled) {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("pm " + (enabled ? "enable" : "disable") + " com.android.systemui\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void reconnect() {
        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) return;

        wifiManager.setWifiEnabled(false);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        wifiManager.setWifiEnabled(true);
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                                        for (WifiConfiguration i : list) {
                                            wifiManager.disconnect();
                                            wifiManager.enableNetwork(i.networkId, true);
                                            wifiManager.reconnect();
                                            break;
                                        }
                                    }
                                },
                                5000);
                    }
                },
                3000);
    }

    private void stop() {
        EnvUtils.cli(MainActivity.this, "-p linux stop", "-u");
    }

    class JsInterface {
        Context mContext;

        public JsInterface(Context mContext) {
            this.mContext = mContext;
        }

        @JavascriptInterface
        public void restart() {
            MainActivity.this.restart();
        }

        @JavascriptInterface
        public void shutdown() {
            MainActivity.this.stop();
            MainActivity.this.shutdown();
        }

        @JavascriptInterface
        public void devMode() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.xWalkWebView.setVisibility(View.INVISIBLE);
                    hideLoading();
                }
            });
        }

        @JavascriptInterface
        public float getBrightness() {
            try {
                return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @JavascriptInterface
        public void setBrightness(int brightness) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        }

        @JavascriptInterface
        public void updateApk() {
            updateApk();
        }
    }

    public void restart() {
        Log.v("RESTART", "RESTART");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });
    }

    public void shutdown() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"});
                } catch (Exception e) {
                }
            }
        };

        thread.start();
    }


    private void autoReloadInit() {
        autoReloadValue = sharedPref.getBoolean("autoReload", false);
        autoReload.setChecked(autoReloadValue);
        autoReload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPref.edit().putBoolean("autoReload", isChecked).commit();
                autoReloadValue = isChecked;
            }
        });
    }

    private void secondaryDeviceCheckboxInit() {
        secondaryDeviceActive = sharedPref.getBoolean("secondaryDeviceActive", false);
        secondaryDeviceCheckbox.setChecked(secondaryDeviceActive);
        if (secondaryDeviceActive) ipAddressForSecondaryDeviceInput.setVisibility(View.VISIBLE);
        secondaryDeviceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPref.edit().putBoolean("secondaryDeviceActive", isChecked).commit();
                secondaryDeviceActive = isChecked;
                if (secondaryDeviceActive)
                    ipAddressForSecondaryDeviceInput.setVisibility(View.VISIBLE);
            }
        });

        ipAddressForSecondaryDevice = sharedPref.getString("ipAddressForSecondaryDevice", null);
        ipAddressForSecondaryDeviceInput.setText(ipAddressForSecondaryDevice);
        ipAddressForSecondaryDeviceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                sharedPref.edit().putString("ipAddressForSecondaryDevice", ipAddressForSecondaryDeviceInput.getText().toString()).commit();
                ipAddressForSecondaryDevice = ipAddressForSecondaryDeviceInput.getText().toString();
            }
        });
    }

    public boolean ping(String url) {
        ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(url);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(500);
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    private void startTeamviewer() {
        config = new TVSessionConfiguration.Builder(
                new TVConfigurationID("p6m8ftq"))
                .setServiceCaseName("Support")
                .setServiceCaseDescription("Remote Support")
                .build();

        TVSessionFactory.createTVSession(this, "32754d6e-d1b8-1a8a-d9e3-133fd92ecd25",
                new TVSessionCreationCallback() {
                    @Override
                    public void onTVSessionCreationSuccess(TVSession session) {
                        session.start(config);
                    }

                    @Override
                    public void onTVSessionCreationFailed(TVCreationError error) {
                    }
                });
    }


    private void configServer() {
        server.get("/startWebview", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                startWebview();
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
                response.send(isOnline() ? "true" : "false");
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
                            xWalkWebView.loadUrl(address);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void startWebview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (secondaryDeviceActive) {
                    xWalkWebView.loadUrl("http://" + ipAddressForSecondaryDevice + ":8888?secondaryDevice");
                } else {
                    xWalkWebView.loadUrl("http://localhost:8888");
                }

                if (presentation != null) {
                    presentation.show();
                    presentation.startWebview();
                }
            }
        });
    }

    void startProgram() {
        showLoading();
        EnvUtils.cli(MainActivity.this, "-p linux start", "-m");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //if (hasFocus) {
        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
        //}
    }

    @Override
    protected void onXWalkReady() {
        // Set UI Client (Start stop animations)
        xWalkWebView.setUIClient(new XWalkUIClient(xWalkWebView) {
            boolean first = false;

            @Override
            public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                if (!url.isEmpty() && status == XWalkUIClient.LoadStatus.FAILED) {
                    view.setVisibility(View.GONE);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {

                                    first = true;
                                    xWalkWebView.setVisibility(View.VISIBLE);
                                    if (secondaryDeviceActive) {
                                        xWalkWebView.loadUrl("http://" + ipAddressForSecondaryDevice + ":8888?secondaryDevice");
                                    } else {
                                        xWalkWebView.loadUrl("http://localhost:8888");
                                    }

                                }
                            },
                            5000);
                } else {
                    xWalkWebView.setVisibility(View.VISIBLE);
                }
            }
        });

        final JsInterface jsInterface = new JsInterface(this);
        xWalkWebView.addJavascriptInterface(jsInterface, "Android");

        if (autoReloadValue) {
            if (!secondaryDeviceActive) startProgram();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if (xWalkWebView.getVisibility() != View.VISIBLE) startWebview();
                        }
                    },
                    10000);
        }
    }

}
