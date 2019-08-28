package com.gigasource;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Instrumentation;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gigasource.webview3.content_shell_apk.ContentShellWebView;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.content.ContentValues.TAG;
import static android.view.KeyEvent.ACTION_DOWN;


public class MainActivity extends Activity implements SensorEventListener {
    final int uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    SharedPreferences sharedPref;
    private static WifiManager.WifiLock wifiLock;
    private boolean ready = false;
    private SensorManager sm;
    private TextView sensorView;
    public boolean lock = false;
    private CheckBox autoReload;
    private boolean autoReloadValue;
    private int anzahl;
    private ConnectionChangeReceiver connectionChangeReceiver;
    static MainActivity self;
    private PowerManager manager;
    public boolean screenOn = true;
    private OnScreenOffReceiver onScreenOffReceiver;
    public PowerManager.WakeLock wakeLock;
    public boolean lockState = true;

    @InjectView(R.id.disableAdvertising)
    View disableAdvertisingBtn;

    @InjectView(R.id.devView)
    View devView;

    ContentShellWebView mainWebView;

    @InjectView(R.id.loading)
    View loadingView;

    @InjectView(R.id.serverNotOnline)
    View serverNotOnlineView;

    @InjectView(R.id.routerNotWorking)
    View routerNotWorkingView;

    @InjectView(R.id.downloadApk)
    View downloadApkView;

    @InjectView(R.id.downloadProgressBar)
    ProgressBar downloadProgressBar;

    @InjectView(R.id.wifiName)
    EditText wifiNameEdittext;

    @InjectView(R.id.wifiPassword)
    EditText wifiPasswordEdittext;

    @InjectView(R.id.staticIp)
    EditText staticIpEdittext;

    @InjectView(R.id.defaultGateway)
    EditText defaultGatewayEdittext;

    @InjectView(R.id.unlock)
    Button unlockBtn;

    @InjectView(R.id.setWifi)
    Button setWifiBtn;


    @InjectView(R.id.root1)
    View overlay;

    private WifiManager wifiManager;
    String ip;
    private int devAnzahl;
    private NotificationManager mNotificationManager;
    private int anzahlWifiClick;
    boolean updateTime = false;
    private OnScreenOnReceiver onScreenOnReceiver;
    private ActivityManager activityManager;
    private boolean turnOffScreenForce = false;
    private OnUserPresentReceiver onUserPresentReceiver;

    static boolean kindlefire8inch;
    private Intent mServiceIntent;
    private Instrumentation m_Instrumentation;
    private AudioManager mAudioManager;
    private EditText ipInput;

    public void keepWiFiOn(boolean on) {
        if (wifiLock == null) {
            WifiManager wm = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
                wifiLock.setReferenceCounted(true);
            }
        }
        if (wifiLock != null) { // May be null if wm is null
            if (on) {
                wifiLock.acquire();
            } else if (wifiLock.isHeld()) {
                wifiLock.release();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mServiceIntent);
        changeBrightness("high");

        if (mainWebView != null) {
            mainWebView = null;
        }
    }

    @Override
    public void onResume() {

        super.onResume();  // Always call the superclass method first

        new android.os.Handler().postDelayed(() -> {
            paused = false;
        }, 2 * 60 * 1000);

        this.registerReceiver(connectionChangeReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        preventStatusBarExpansion(10);

        if (!ready) return;
        try {
            mainWebView.loadUrl("javascript:onResume();");
        } catch (Exception e) {
        }

        /*if (!screenOn) {
            overlay.setVisibility(View.INVISIBLE);
            screenOn = true;
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        }*/
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            if (!ready) return;
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            if (level <= 1) {
                mainWebView.loadUrl("javascript:onBatteryLow && onBatteryLow();");
            } else if (level > 5) {
                mainWebView.loadUrl("javascript:onBatteryEnough && onBatteryEnough();");
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!ready) return;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if ((y < -8 || y > 8) && !lock) {
            lock = true;
            try {
                mainWebView.loadUrl("javascript:lockTouch();");
            } catch (Exception e) {
            }
        }
        if ((y > -8 && y < 8) && lock) {
            lock = false;
            try {
                mainWebView.loadUrl("javascript:unlockTouch();");
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void restart() {
        Log.v("RESTART", "RESTART");
        runOnUiThread(() -> {
            changeBrightness("high");
            Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
        });
    }

    public void devMode() {
        runOnUiThread(() -> {
            mainWebView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.INVISIBLE);
            serverNotOnlineView.setVisibility(View.INVISIBLE);
            routerNotWorkingView.setVisibility(View.INVISIBLE);
            devView.setVisibility(View.VISIBLE);
        });
    }

    public void logout() {
        try {
            mainWebView.loadUrl("javascript:logout();");
        } catch (Exception e) {
        }
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
        public float getBrightness() {
            if (sharedPref.getInt("currentBrightness", -1) != -1)
                return sharedPref.getInt("currentBrightness", -1);
            try {
                return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @JavascriptInterface
        public void pageLoaded() {
            MainActivity.self.pageLoaded = true;
        }

        @JavascriptInterface
        public void showServerNotOnlineView() {
            if (!MainActivity.this.paused) {
                MainActivity.self.showServerNotOnlineView();
            }
        }

        @JavascriptInterface
        public void setBrightness(int brightness) {
            sharedPref.edit().putInt("currentBrightness", brightness).apply();
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        }

        @JavascriptInterface
        public void refresh() {
            getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
        }

        @JavascriptInterface
        public void reconnect2() {
            connectionChangeReceiver.reconnect();
        }

        @JavascriptInterface
        public void updateApk() {
            MainActivity.this.apkUpdate2();
        }

        @JavascriptInterface
        public void devMode() {
            MainActivity.this.devMode();
        }
    }

    public boolean firstFocus = false;

    public boolean paused = false;

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;

        unregisterReceiver(connectionChangeReceiver);
        unregisterReceiver(this.mBatInfoReceiver);

        activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        if (updateTime && !firstFocus) {
            firstFocus = true;
            new android.os.Handler().postDelayed(
                    () -> {
                        activityManager.moveTaskToFront(getTaskId(), 0);
                        installApk();
                    },
                    500);
        } else if (updateTime && firstFocus) {
            firstFocus = false;
            // do no thing
        } else if (!updateTime) {
            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }

    public void showServerNotOnlineView() {
        runOnUiThread(() -> {
            serverNotOnlineView.setVisibility(View.VISIBLE);
            mainWebView.setVisibility(View.INVISIBLE);
            mainWebView.loadUrl("about:blank");
            checkNetworkAndLoad();
        });
    }

    boolean pageLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            if (pm.isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        self = this;

        connectionChangeReceiver = new ConnectionChangeReceiver();
        ConnectionChangeReceiver.self = connectionChangeReceiver;

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        keepWiFiOn(true);

        mainWebView = findViewById(R.id.webView);
//        AmazonWebSettings webSettings = mainWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        mainWebView.clearCache(true);

        final JsInterface jsInterface = new JsInterface(this);
        mainWebView.addJavascriptInterface(jsInterface, "Android");

        ip = sharedPref.getString("ip", null);

        // Set UI Client (Start stop animations)
        mainWebView.setWebViewClient(new WebViewClient() {
            public boolean hasErr = false;

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
                hasErr = true;
                mainWebView.setVisibility(View.INVISIBLE);
                serverNotOnlineView.setVisibility(View.VISIBLE);
                if (!autoReloadValue) return;
                new Handler().postDelayed(() -> {
                            if (!TextUtils.isEmpty(ip)) {
                                hasErr = false;
                                mainWebView.loadUrl("http://" + ip + ":8888");
                            }
                        },
                        5000);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!hasErr) {
                    serverNotOnlineView.setVisibility(View.INVISIBLE);
                    mainWebView.setVisibility(View.VISIBLE);
                    ready = true;
                }
                mainWebView.loadUrl("javascript:console.log('abc')");
                mainWebView.loadUrl("javascript:Android.pageLoaded()");
                new Handler().postDelayed(() -> {
                    if (!pageLoaded) this.onReceivedError(view, 0, null, null);
                }, 500);
            }
        });

        makeKioskMode();

        Button reloadButton = (Button) findViewById(R.id.reloadBtn);
        reloadButton.setOnClickListener(v -> {
            ready = true;
            mainWebView.loadUrl("http://" + sharedPref.getString("ip", null) + ":8888");
            mainWebView.setVisibility(View.VISIBLE);
        });

        ipInput = (EditText) findViewById(R.id.ipInput);

        findViewById(R.id.okBtn).setOnClickListener(v -> sharedPref.edit().putString("ip", ipInput.getText().toString()).apply());
        ipInput.setText(ip);

        // sensor

        sm = (SensorManager)

                getSystemService(SENSOR_SERVICE);
        if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).

                size() != 0) {
            Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }

        sensorView = (TextView)

                findViewById(R.id.sensor);

        findViewById(R.id.updateApk).

                setOnClickListener(v -> apkUpdate2());

        overlay.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (screenOn == false) {
                        screenOn = true;
                        changeBrightness("high");
                        overlay.setVisibility(View.INVISIBLE);
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 48 * 60 * 60 * 1000);
                    }

                    return super.onDoubleTap(e);
                }

            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        if (checkSystemWritePermission()) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 48 * 60 * 60 * 1000);
        }

        findViewById(R.id.openSetting).setOnClickListener(v -> startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0));

        registerKioskModeScreenOffReceiver();

        disableAdvertisingBtn.setOnClickListener(v -> {
        });

        autoReloadInit();

        if (autoReloadValue && !TextUtils.isEmpty(ip)) {
            devView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.VISIBLE);

            // check wifi on
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
                wifiManager.setWifiEnabled(true);
                new Handler().postDelayed(
                        () -> checkNetworkAndLoad(),
                        7000);
            } else {
                checkNetworkAndLoad();
            }


        }

        devAnzahl = 0;
        View.OnClickListener devListener = v -> {
            if (devAnzahl == 3) {
                devAnzahl = 0;
                devMode();
            } else {
                devAnzahl++;
            }
        };
        serverNotOnlineView.setOnClickListener(devListener);
        loadingView.setOnClickListener(devListener);
        routerNotWorkingView.setOnClickListener(devListener);

        initWifiSetup();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        if (screenWidth == 1280) kindlefire8inch = true;

        //mServiceIntent = new Intent(this, WakeupService.class);
        //if (!ServiceMan.isMyServiceRunning(WakeupService.class, this)) startService(mServiceIntent);
        changeLockstate();
        unlockBtn.setOnClickListener(v -> showPasswordDialog());
    }

    public void changeLockstate() {
        ipInput.setEnabled(!lockState);
        wifiNameEdittext.setEnabled(!lockState);
        wifiPasswordEdittext.setEnabled(!lockState);
        staticIpEdittext.setEnabled(!lockState);
        defaultGatewayEdittext.setEnabled(!lockState);
    }

    public void showPasswordDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.password, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextPassword);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    if (userInput.getText().toString().equals("99")) {
                        lockState = false;
                        changeLockstate();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void initWifiSetup() {
        wifiNameEdittext.setText(sharedPref.getString("wifiName", ""));
        wifiPasswordEdittext.setText(sharedPref.getString("wifiPassword", ""));
        staticIpEdittext.setText(sharedPref.getString("staticIp", ""));
        defaultGatewayEdittext.setText(sharedPref.getString("defaultGateway", ""));

        setWifiBtn.setOnClickListener(v -> {
            sharedPref.edit().putString("wifiName", wifiNameEdittext.getText().toString()).apply();
            sharedPref.edit().putString("wifiPassword", wifiPasswordEdittext.getText().toString()).apply();
            sharedPref.edit().putString("staticIp", staticIpEdittext.getText().toString()).apply();
            sharedPref.edit().putString("defaultGateway", defaultGatewayEdittext.getText().toString()).apply();
        });
    }

    public void forceWifi() {
        if (TextUtils.isEmpty(sharedPref.getString("wifiName", ""))) return;
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            wifiManager.removeNetwork(i.networkId);
            wifiManager.saveConfiguration();
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + sharedPref.getString("wifiName", "") + "\"";
        conf.preSharedKey = "\"" + sharedPref.getString("wifiPassword", "") + "\"";

        try {
            WifiUtils.changeWifiConfiguration(conf, false, sharedPref.getString("staticIp", ""), 24, "8.8.8.8", sharedPref.getString("defaultGateway", ""));
            wifiManager.addNetwork(conf); //apply the setting
            wifiManager.saveConfiguration(); //Save it
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            wifiManager.disconnect();
            wifiManager.enableNetwork(wifiManager.getConfiguredNetworks().get(0).networkId, true);
            wifiManager.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkNetworkAndLoad() {
        if (!autoReloadValue) return;
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {
            routerNotWorkingView.setVisibility(View.VISIBLE);
            forceWifi();
            new android.os.Handler().postDelayed(
                    () -> checkNetworkAndLoad(),
                    8000);
        } else {
            if (!("\"" + sharedPref.getString("wifiName", "") + "\"").equals(wifiManager.getConnectionInfo().getSSID())
                    || !(sharedPref.getString("staticIp", "")).equals(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()))) {
                forceWifi();
                routerNotWorkingView.setVisibility(View.VISIBLE);
                new android.os.Handler().postDelayed(
                        () -> checkNetworkAndLoad(),
                        8000);
            } else {
                mainWebView.loadUrl("http://" + ip + ":8888");
            }
        }
    }


    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
            if (!retVal) {
                openAndroidPermissionsMenu();
            }
        }
        return retVal;
    }

    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        startActivity(intent);
    }

    public boolean isPlugged(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public class OnScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) return;
            try {
                int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                if (sharedPref.getInt("currentBrightness", -1) != brightness && brightness > 100) {
                    sharedPref.edit().putInt("currentBrightness", brightness).apply();
                }
            } catch (Exception e) {
            }

            resetDisconnectTimer();
            if (!isPlugged(getApplicationContext()) && !turnOffScreenForce) {
                playMusic();
            } else if (isPlugged(getApplicationContext())) {
                playMusic();
            } else if (turnOffScreenForce) {
                turnOffScreenForce = false;
            }
            screenOn = false;
        }
    }

    public class OnScreenOnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopDisconnectTimer();
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                runOnUiThread(() -> {
                    if (screenOn == false) {
                        screenOn = true;
                        changeBrightness("high");
                        overlay.setVisibility(View.INVISIBLE);
                    }
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 48 * 60 * 60 * 1000);
                    sleepHandler();
                });
                activityManager.moveTaskToFront(getTaskId(), 0);
            }
        }
    }

    public class OnUserPresentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            resetDisconnectTimer();
        }
    }

    private void registerKioskModeScreenOffReceiver() {
        // register screen off receiver
        onScreenOffReceiver = new OnScreenOffReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(onScreenOffReceiver, filter);

        onScreenOnReceiver = new OnScreenOnReceiver();
        IntentFilter filter1 = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(onScreenOnReceiver, filter1);

        onUserPresentReceiver = new OnUserPresentReceiver();
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(onUserPresentReceiver, filter2);
    }

    public Handler disconnectHandler = new Handler();
    public Handler disconnectHandler2 = new Handler();

    public void turnOffScreen() {
        playMusic();

        turnOffScreenForce = true;
        changeBrightness("low");
    }

    public void playMusic() {
        /*runOnUiThread(() -> {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(MainActivity.self, R.raw.sound);
                mediaPlayer.setVolume(0, 0);
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });*/
    }

    private Runnable disconnectCallback2 = new Runnable() {
        @Override
        public void run() {
            if (screenOn) {
                screenOn = false;
                changeBrightness("low");
                overlay.setVisibility(View.VISIBLE);
            }

            turnOffScreen();
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            if (screenOn) {
                screenOn = false;
                changeBrightness("low");
                overlay.setVisibility(View.VISIBLE);
            }

            if (isPlugged(getApplicationContext())) {
                turnOffScreen();
                return;
            }
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, 4 * 60 * 1000);
        sleepHandler();
    }

    public void sleepHandler() {
        disconnectHandler2.removeCallbacks(disconnectCallback2);
        disconnectHandler2.postDelayed(disconnectCallback2, 30 * 60 * 1000);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler2.removeCallbacks(disconnectCallback2);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }


    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode()) && event.getAction() == ACTION_DOWN) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private void autoReloadInit() {
        autoReloadValue = sharedPref.getBoolean("autoReload", false);
        autoReload = (CheckBox) findViewById(R.id.autoReload);
        autoReload.setChecked(autoReloadValue);
        autoReload.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.edit().putBoolean("autoReload", isChecked).apply();
            autoReloadValue = isChecked;
        });
    }

    public class CustomViewGroup extends ViewGroup {

        public CustomViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }

    }

    boolean firstTimepreventStatusBarExpansion = false;

    public void preventStatusBarExpansion(int height) {
        /*if (firstTimepreventStatusBarExpansion) return;
        firstTimepreventStatusBarExpansion = true;
        WindowManager manager = ((WindowManager) this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        int resId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = this.getResources().getDimensionPixelSize(resId);
        } else {
            // Use Fallback size:
            result = 5; // 60px Fallback
        }

        localLayoutParams.height = 0;
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(this);
        manager.addView(view, localLayoutParams);*/
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == -1010101) {
            if (Settings.canDrawOverlays(this)) {
                preventStatusBarExpansion(10);
            }
        }
    }

    int backAnzahl = 0;

    @Override
    public void onBackPressed() {
        if (backAnzahl == 3) {
            backAnzahl = 0;
            restart();
        } else {
            backAnzahl++;
        }
    }

    private void makeKioskMode() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, -1010101);
            }
        } else {
            preventStatusBarExpansion(10);
        }

        int orientation;
        int rotation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case Surface.ROTATION_270:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
        }

        setRequestedOrientation(orientation);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
                    }
                });


        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);

        if (!hasFocus) {
            // Method that handles loss of window focus
            preventStatusBarExpansion(10);
        } else {
            preventStatusBarExpansion(10);
        }


    }

    public void apkUpdate2() {
        runOnUiThread(new Runnable() {
            public int lastStatus;

            @Override
            public void run() {
                lastStatus = mainWebView.getVisibility();
                mainWebView.setVisibility(View.INVISIBLE);
                downloadApkView.setVisibility(View.VISIBLE);
                final String ip = sharedPref.getString("ip", null);
                Ion.with(getApplicationContext())
                        .load("http://" + ip + ":8888/apk/starkasse.apk")
                        .progressBar(downloadProgressBar)
                        .progress((downloaded, total) -> System.out.println("" + downloaded + " / " + total))
                        .write(new File("/mnt/sdcard/Download/starkasse.apk"))
                        .setCallback((e, file) -> {
                            downloadApkView.setVisibility(View.INVISIBLE);
                            runOnUiThread(() -> {
                                updateTime = true;
                                mainWebView.setVisibility(lastStatus);
                                installApk();
                            });
                        });
            }
        });
    }

    public void installApk() {
        File toInstall = new File("/mnt/sdcard/Download/starkasse.apk");
        Intent intent;
        Uri apkUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", toInstall);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(toInstall);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivity(intent);
    }

    public void changeBrightness(String direction) {
        //if (kindlefire8inch) return;
        if (direction.equals("high")) {
            //mainWebView.loadUrl("javascript:screenOn();", null);
            overlay.setVisibility(View.INVISIBLE);
            Settings.System.putInt(MainActivity.self.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, sharedPref.getInt("currentBrightness", 255));
        } else {
            try {
                int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                if (sharedPref.getInt("currentBrightness", -1) != brightness && brightness > 100) {
                    sharedPref.edit().putInt("currentBrightness", brightness).apply();
                }
            } catch (Exception e) {
            }
            //mainWebView.loadUrl("javascript:screenOff();", null);
            overlay.setVisibility(View.VISIBLE);
            Settings.System.putInt(MainActivity.self.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        }
    }
}