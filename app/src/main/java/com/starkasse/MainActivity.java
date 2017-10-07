package com.starkasse;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static android.content.ContentValues.TAG;
import static android.view.KeyEvent.ACTION_DOWN;


public class MainActivity extends Activity implements SensorEventListener {
    private WebView mainWebView;
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
    private boolean lock;
    private CheckBox autoReload;
    private boolean autoReloadValue;
    private int anzahl;
    private ConnectionChangeReceiver connectionChangeReceiver;
    static MainActivity self;
    private PowerManager manager;
    private boolean screenOn = true;
    private OnScreenOffReceiver onScreenOffReceiver;
    private PowerManager.WakeLock wakeLock;
    private PowerManager.WakeLock wakeLock1;

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

        if (mainWebView != null) {
            mainWebView = null;
        }
    }

    @Override
    public void onResume() {

        super.onResume();  // Always call the superclass method first

        this.registerReceiver(connectionChangeReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        preventStatusBarExpansion();

        if (!ready) return;
        try {
            mainWebView.evaluateJavascript("onResume();", null);
        } catch (Exception e) {
        }

        resetDisconnectTimer();
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            if (!ready) return;
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            if (level <= 5) {
                mainWebView.evaluateJavascript("onBatteryLow && onBatteryLow();", null);
            } else if (level > 20) {
                mainWebView.evaluateJavascript("onBatteryEnough && onBatteryEnough();", null);
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
                mainWebView.evaluateJavascript("lockTouch();", null);
            } catch (Exception e) {
            }
        }
        if ((y > -8 && y < 8) && lock) {
            lock = false;
            try {
                mainWebView.evaluateJavascript("unlockTouch();", null);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    Callable onResumeCb = new Callable() {
        @Override
        public Object call() throws Exception {
            runOnUiThread(new Runnable() {
                boolean first = false;

                @Override
                public void run() {
                    if (!first) {
                        first = true;

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        try {
                                            mainWebView.evaluateJavascript("onResume();", null);
                                            Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                        }
                                    }
                                },
                                500);

                    }
                }
            });

            return null;
        }
    };

    class JsInterface {
        Context mContext;

        public JsInterface(Context mContext) {
            this.mContext = mContext;
        }

        @JavascriptInterface
        public void restart() {
            Log.v("RESTART", "RESTART");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent mStartActivity = new Intent(mContext, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);
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
        public void refresh() {
            getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
        }

        @JavascriptInterface
        public void reconnect2() {
            connectionChangeReceiver.reconnect();
        }

        @JavascriptInterface
        public void updateApk() {
            updateApk();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(connectionChangeReceiver);
        unregisterReceiver(this.mBatInfoReceiver);

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;

        connectionChangeReceiver = new ConnectionChangeReceiver();
        ConnectionChangeReceiver.self = connectionChangeReceiver;

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_main);

        keepWiFiOn(true);

        mainWebView = (WebView) findViewById(R.id.webView);
        mainWebView.setWebContentsDebuggingEnabled(true);

        final WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        final JsInterface jsInterface = new JsInterface(this);
        mainWebView.addJavascriptInterface(jsInterface, "Android");

        final String ip = sharedPref.getString("ip", null);

        // Set UI Client (Start stop animations)
        mainWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.setVisibility(View.GONE);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (!TextUtils.isEmpty(ip)) {
                                    mainWebView.setVisibility(View.VISIBLE);
                                    mainWebView.loadUrl("http://" + ip + ":8888");
                                }
                            }
                        },
                        5000);
                // Do something
            }
        });

        autoReloadInit();

        if (!TextUtils.isEmpty(ip) && autoReloadValue) {
            mainWebView.setVisibility(View.VISIBLE);
            mainWebView.loadUrl("http://" + ip + ":8888", null);
            ready = true;
        }

        makeKioskMode();

        Button reloadButton = (Button) findViewById(R.id.reloadBtn);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ready = true;
                mainWebView.loadUrl("http://" + sharedPref.getString("ip", null) + ":8888");
                mainWebView.setVisibility(View.VISIBLE);
            }
        });


        View button = findViewById(R.id.ipSetting);
        final EditText ipInput = (EditText) findViewById(R.id.ipInput);

        anzahl = 0;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anzahl > 5) findViewById(R.id.inputGroup).setVisibility(View.VISIBLE);
                anzahl++;
            }
        });

        findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().putString("ip", ipInput.getText().toString()).commit();
            }
        });

        ipInput.setText(ip);

        // sensor

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }

        sensorView = (TextView) findViewById(R.id.sensor);
        findViewById(R.id.updateApk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apkUpdate();
            }
        });

        findViewById(R.id.root).setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (screenOn == false) {
                        screenOn = true;
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
                        findViewById(R.id.root1).setVisibility(View.VISIBLE);
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

        findViewById(R.id.openSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (wakeLock1 == null) {
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    wakeLock1 = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "screenoff");
                }

                if (wakeLock1.isHeld()) {
                    wakeLock1.release(); // release old wake lock
                }

                wakeLock1.acquire();*/


                //wakeLock1.release();

                //Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1000);
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        registerKioskModeScreenOffReceiver();

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

    public class OnScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if (screenOn == false) {
                    screenOn = true;
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
                    findViewById(R.id.root1).setVisibility(View.VISIBLE);
                }

                /*if (wakeLock == null) {
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
                }

                if (wakeLock.isHeld()) {
                    wakeLock.release(); // release old wake lock
                }

                wakeLock.acquire();
                wakeLock.release();*/
            }
        }
    }

    private void registerKioskModeScreenOffReceiver() {
        // register screen off receiver
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        registerReceiver(onScreenOffReceiver, filter);
    }

    public static final long DISCONNECT_TIMEOUT = 2 * 60 * 1000;

    private Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            if (screenOn) {
                screenOn = false;
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                findViewById(R.id.root1).setVisibility(View.INVISIBLE);
            }
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }


    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode()) && event.getAction() == ACTION_DOWN) {
            if (screenOn == false) {
                screenOn = true;
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
                findViewById(R.id.root1).setVisibility(View.VISIBLE);
                resetDisconnectTimer();
            } else {
                screenOn = false;
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                findViewById(R.id.root1).setVisibility(View.INVISIBLE);
            }

            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private void autoReloadInit() {
        autoReloadValue = sharedPref.getBoolean("autoReload", false);
        autoReload = (CheckBox) findViewById(R.id.autoReload);
        autoReload.setChecked(autoReloadValue);
        autoReload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPref.edit().putBoolean("autoReload", isChecked).commit();
                autoReloadValue = isChecked;
            }
        });
    }

    public class customViewGroup extends ViewGroup {

        public customViewGroup(Context context) {
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

    public void preventStatusBarExpansion() {

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

        localLayoutParams.height = 5;
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup view = new customViewGroup(this);
        manager.addView(view, localLayoutParams);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == -1010101) {
            if (Settings.canDrawOverlays(this)) {
                preventStatusBarExpansion();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void makeKioskMode() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, -1010101);
            }
        } else {
            preventStatusBarExpansion();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //if (hasFocus) {
        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
        //}
    }

    public void apkUpdate() {
        UpdateApp atualizaApp = new UpdateApp();
        atualizaApp.setContext(getApplicationContext());
        final String ip = sharedPref.getString("ip", null);
        atualizaApp.execute("http://" + ip + ":8888/apk/starkasse.apk");
    }

}
