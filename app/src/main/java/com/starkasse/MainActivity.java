package com.starkasse;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaRouter;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.posin.device.CashDrawer;
import com.posin.device.CustomerDisplay;
import com.teamviewer.sdk.screensharing.api.TVConfigurationID;
import com.teamviewer.sdk.screensharing.api.TVCreationError;
import com.teamviewer.sdk.screensharing.api.TVSession;
import com.teamviewer.sdk.screensharing.api.TVSessionConfiguration;
import com.teamviewer.sdk.screensharing.api.TVSessionCreationCallback;
import com.teamviewer.sdk.screensharing.api.TVSessionFactory;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends XWalkActivity {

    private static final String TAG = "starkasse";
    XWalkView xWalkWebView;

    final int uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    SharedPreferences sharedPref;

    static AsyncHttpServer server = new AsyncHttpServer();

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

    @InjectView(R.id.startMongo)
    Button startMongoBtn;

    @InjectView(R.id.startNode)
    Button startNodeBtn;

    @InjectView(R.id.stopMongo)
    Button stopMongoBtn;

    @InjectView(R.id.stopNode)
    Button stopNodeBtn;

    @InjectView(R.id.continue2)
    Button continueBtn;

    @InjectView(R.id.chmod)
    Button chmodBtn;

    @InjectView(R.id.brightnessDown)
    Button brightnessDownBtn;

    @InjectView(R.id.brightnessUp)
    Button brightnessUpBtn;

    @InjectView(R.id.wifiDebug)
    Button wifiDebugBtn;

    @InjectView(R.id.downloadFromSmb)
    Button downloadFromSmbBtn;

    @InjectView(R.id.downloadIndexFromSmb)
    Button downloadIndexFromSmbBtn;

    @InjectView(R.id.downloadDataFromSmb)
    Button downloadDataFromSmbBtn;

    @InjectView(R.id.wifiDebugDeactive)
    Button wifiDebugDeactiveBtn;

    @InjectView(R.id.repairMongo)
    Button repairMongoBtn;

    @InjectView(R.id.reinstallApk)
    Button reinstallApkBtn;

    @InjectView(R.id.loading)
    View loadingView;

    @InjectView(R.id.usbPath)
    EditText usbPathInput;

    @InjectView(R.id.copyFromUsb)
    Button copyFromUsbBtn;

    @InjectView(R.id.deployScript)
    Button deployScriptBtn;

    @InjectView(R.id.deployScript2)
    Button deployScript2Btn;

    static CustomerPresentation presentation;
    private BluetoothServer bluetoothServer;
    static WifiManager wifiManager;
    private TVSessionConfiguration config;
    private boolean autoReloadValue;

    @InjectView(R.id.autoReload)
    CheckBox autoReload;

    private int loadingAnzahl;
    boolean secondaryDeviceActive;
    String ipAddressForSecondaryDevice;
    private PowerManager.WakeLock wakeLock;
    public Process processMongo;
    public Process processNode;
    public Utils utils = new Utils(this);
    private WifiDirectReceiver wifiDirectReceiver;
    private IntentFilter wifiDirectReceiverIntent;
    private Thread logThread;
    private boolean APrunning;
    private Process logcat;
    private int disconnectTimes;
    private CustomerDisplay dsp;
    private int clickNumber;
    private CashDrawer cashDrawer;

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
        stop();
        restart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigServer.configServer(server, this);
        server.listen(5000);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        if (sharedPref.getBoolean("NotFirstTime", true)) {
            //EnvUtils.updateEnv(this);

            sharedPref.edit().putBoolean("NotFirstTime", false).commit();
        }

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        autoReloadInit();

        findViewById(R.id.startLinux).setOnClickListener(v -> {
            //EnvUtils.cli(MainActivity.this, "-p linux start", "-m");
            stop();
            utils.startProgram();
        });

        findViewById(R.id.stopLinux).setOnClickListener(v -> stop());

        xWalkWebView = (XWalkView) findViewById(R.id.xwalkWebView);

        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
                    }
                });

        // prevent lockscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        reloadButton.setOnClickListener(v -> startWebview());

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
        loadingView.setOnClickListener(v -> {
            if (loadingAnzahl == 3) {
                loadingAnzahl = 0;
                hideLoading();
            } else {
                loadingAnzahl++;
            }
        });

        downloadFromSmbBtn.setOnClickListener(v -> new Thread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                utils.downloadFromSmb();
            }
        }).start());

        settingBtn.setOnClickListener(v -> startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0));

        brightnessDownBtn.setOnClickListener(v -> {
            createAP();
        });

        brightnessUpBtn.setOnClickListener(v -> {
            try {
                int current = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                if (current < 250)
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, current + 5);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        });

        wifiDebugBtn.setOnClickListener(v -> {
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "setprop persist.adb.tcp.port 5555"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        wifiDebugDeactiveBtn.setOnClickListener(v -> {
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "setprop persist.adb.tcp.port \"\""});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        disableSystemUiButton.setOnClickListener(v -> setSystemUIEnabled(false));

        enableSystemUiButton.setOnClickListener(v -> setSystemUIEnabled(true));

        secondaryDeviceCheckboxInit();
        //reconnect();

        if (autoReloadValue) loadingView.setVisibility(View.VISIBLE);

        /*try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "service call SurfaceFlinger 1008 i32 1"});
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        setWakelock();

        startMongoBtn.setOnClickListener(v -> utils.startMongo());

        startNodeBtn.setOnClickListener(v -> utils.startNodejs());

        stopMongoBtn.setOnClickListener(v -> utils.stopMongo());

        stopNodeBtn.setOnClickListener(v -> utils.stopNode());

        chmodBtn.setOnClickListener(v -> {
            setPreferredHomeActivity(this);
        });

        downloadIndexFromSmbBtn.setOnClickListener(v -> new Thread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                utils.downloadIndexFromSmb();
            }

        }).start());

        downloadDataFromSmbBtn.setOnClickListener(v -> new Thread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                utils.downloadDataFromSmb();
            }
        }).start());

        repairMongoBtn.setOnClickListener(v -> {
            try {
                utils.repairMongo(utils.getEnv());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        reinstallApkBtn.setOnClickListener(v -> installApk());
        usbPathInput.setText(utils.getUsbPath());
        copyFromUsbBtn.setOnClickListener(v -> utils.copyFromUsb(usbPathInput.getText().toString()));

        deployScriptBtn.setOnClickListener(v -> utils.runDeployScript(usbPathInput.getText().toString()));
        deployScript2Btn.setOnClickListener(v -> utils.runDeployScript2(usbPathInput.getText().toString()));

        findViewById(R.id.hideBtns).setVisibility(View.GONE);
        continueBtn.setOnClickListener(v -> {
            if (clickNumber > 5) {
                findViewById(R.id.hideBtns).setVisibility(View.VISIBLE);
            } else {
                clickNumber = clickNumber + 1;
            }

        });

        holdWifiLock();
        //createReadLogThread();
        //createAP();

        new Handler().postDelayed(() -> {
            try {
                dsp = CustomerDisplay.newInstance();
                cashDrawer = CashDrawer.newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (dsp != null) dsp.close();
            }
        }, 5000);
        //Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 48 * 60 * 60 * 1000);

        if (sharedPref.getBoolean("autoStartWifiDirect", true)) {
            createAP();
        }
    }

    private void disableIpv6() {
        if (EnvUtils.isRooted()) {
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "echo 0 > /proc/sys/net/ipv6/conf/wlan0/accept_ra"});
                Runtime.getRuntime().exec(new String[]{"su", "-c", "echo 1 > /proc/sys/net/ipv6/conf/all/disable_ipv6"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    WifiManager.WifiLock mWifiLock = null;


    /***
     * Calling this method will aquire the lock on wifi. This is avoid wifi
     * from going to sleep as long as <code>releaseWifiLock</code> method is called.
     **/
    private void holdWifiLock() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (mWifiLock == null)
            mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);

        mWifiLock.setReferenceCounted(false);

        if (!mWifiLock.isHeld())
            mWifiLock.acquire();
    }

    boolean lockRestartWifi;

    public void restartWifi() {
        lockRestartWifi = true;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> lockRestartWifi = false, 20000);
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        Handler handler2 = new Handler(Looper.getMainLooper());
        handler2.postDelayed(() -> {
            wifiManager.setWifiEnabled(true);
            Handler handler3 = new Handler(Looper.getMainLooper());
            handler3.postDelayed(() -> createAP(), 4000);
        }, 2000);
    }

    public void createReadLogThread() {
        try {

            Runtime.getRuntime().exec("logcat -c");

        } catch (Exception e) {
        }
        try {
            logcat = Runtime.getRuntime().exec(new String[]{"logcat"});
        } catch (IOException e) {
            e.printStackTrace();
        }
        logThread = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 1024);
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        //AP-STA-DISCONNECTED
                        if (line.contains("wpa_sm_step()")) {
                            if (APrunning && !lockRestartWifi) {
                                disconnectTimes++;
                                if (disconnectTimes >= 0) {
                                    disconnectTimes = 0;
                                    restartWifi();
                                    Log.d("AP-p2p", "p2p ap restart !!!");
                                } else {
                                    createAP();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        logThread.start();
    }

    private void createAP() {
        lockRestartWifi = true;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> lockRestartWifi = false, 5000);

        WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = manager.initialize(this, getMainLooper(), null);
        final WifiP2pManager.ActionListener listener = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                APrunning = true;
            }

            @Override
            public void onFailure(int reason) {
            }
        };

        manager.requestGroupInfo(channel, group -> {
            if (group != null) {
                APrunning = false;
                manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> manager.createGroup(channel, listener), 1000);
                        APrunning = true;
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d("AP-p2p", "reason" + reason);
                    }
                });
            } else {
                manager.createGroup(channel, listener);
            }
        });

        // AP-STA-DISCONNECTED
    }


    public static boolean setPreferredHomeActivity(Context context) {
        ComponentName oldPreferredActivity = getPreferredHomeActivity(context);
        if (oldPreferredActivity != null && context.getPackageName().equals(oldPreferredActivity.getPackageName())) {
            return false;
        }
        PackageManager p = context.getPackageManager();
        ComponentName cN = new ComponentName(context, Main2Activity.class);
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(selector);

        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        return true;
    }

    private static ComponentName getPreferredHomeActivity(Context context) {
        ArrayList<IntentFilter> filters = new ArrayList<>();
        List<ComponentName> componentNames = new ArrayList<>();
        context.getPackageManager().getPreferredActivities(filters, componentNames, null);
        for (int i = 0; i < filters.size(); i++) {
            IntentFilter filter = filters.get(i);
            if (filter.hasAction(Intent.ACTION_MAIN) && filter.hasCategory(Intent.CATEGORY_HOME)) {
                return componentNames.get(i);
            }
        }
        return null;
    }

    private static ComponentName[] getActivitiesListByActionAndCategory(Context context, String action, String category) {
        Intent queryIntent = new Intent(action);
        queryIntent.addCategory(category);
        List<ResolveInfo> resInfos = context.getPackageManager().queryIntentActivities(queryIntent, PackageManager.MATCH_DEFAULT_ONLY);
        ComponentName[] componentNames = new ComponentName[resInfos.size()];
        for (int i = 0; i < resInfos.size(); i++) {
            ActivityInfo activityInfo = resInfos.get(i).activityInfo;
            componentNames[i] = new ComponentName(activityInfo.packageName, activityInfo.name);
        }
        return componentNames;
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
                () -> {
                    wifiManager.setWifiEnabled(true);
                    new android.os.Handler().postDelayed(
                            () -> {
                                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                                for (WifiConfiguration i : list) {
                                    wifiManager.disconnect();
                                    wifiManager.enableNetwork(i.networkId, true);
                                    wifiManager.reconnect();
                                    break;
                                }
                            },
                            5000);
                },
                3000);
    }

    private void stop() {
        utils.stopNode();
        utils.stopMongo();
        //EnvUtils.cli(MainActivity.this, "-p linux stop", "-u");
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
            runOnUiThread(() -> {
                MainActivity.this.xWalkWebView.setVisibility(View.INVISIBLE);
                hideLoading();
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
            installApk();
        }

        @JavascriptInterface
        public void reboot() {
            MainActivity.this.reboot();
        }

        @JavascriptInterface
        public void writeToDsp(String text) {
            if (dsp != null) dsp.write(text);
        }

        @JavascriptInterface
        public void clearDsp() {
            if (dsp != null) dsp.clear();
        }

        @JavascriptInterface
        public void resetDsp(String text) {
            if (dsp != null) dsp.reset();
        }

        @JavascriptInterface
        public void setCursorDsp(int x, int y) {
            if (dsp != null) dsp.setCursorPos(x, y);
        }

        @JavascriptInterface
        public void setBacklightDsp(boolean on) {
            if (dsp != null) dsp.setBacklight(on);
        }

        @JavascriptInterface
        public void setBacklightTimeout(int timeout) {
            if (dsp != null) dsp.setBacklightTimeout(timeout);
        }

        @JavascriptInterface
        public void openCashdrawer() {
            if (cashDrawer != null) {
                cashDrawer.kickOutPin2(100);
            }
        }

        @JavascriptInterface
        public String getDeviceName() {
            return android.os.Build.MODEL;
        }

        @JavascriptInterface
        public void openCashdrawer5() {
            if (cashDrawer != null) {
                cashDrawer.kickOutPin5(100);
            }
        }
    }

    public void restart() {
        Log.v("RESTART", "RESTART");
        runOnUiThread(() -> {
            Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
        });
    }

    public void shutdown() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                utils.stopNode();
                utils.stopMongo();
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"});
                } catch (Exception e) {
                }
            }
        };

        thread.start();
    }

    public void reboot() {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot now"});
        } catch (IOException e) {
        }
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

    public void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File("/sdcard/starkasse-main.apk")), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startWebview() {
        runOnUiThread(() -> {
            if (secondaryDeviceActive) {
                xWalkWebView.loadUrl("http://" + ipAddressForSecondaryDevice + ":8888?secondaryDevice");
            } else {
                xWalkWebView.loadUrl("http://localhost:8888");
            }

            try {
                if (presentation != null) {
                    presentation.show();
                    presentation.startWebview();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //if (hasFocus) {
        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
        //}
    }

    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected void onXWalkReady() {
        // Set UI Client (Start stop animations)
        xWalkWebView.setUIClient(new XWalkUIClient(xWalkWebView) {
            boolean first = false;

            @Override
            public void onPageLoadStopped(final XWalkView view, final String url, final LoadStatus status) {
                runOnUiThread(() -> {
                    if (!url.isEmpty() && status == LoadStatus.FAILED) {
                        view.setVisibility(View.GONE);
                        new android.os.Handler().postDelayed(
                                () -> {

                                    first = true;
                                    xWalkWebView.setVisibility(View.VISIBLE);
                                    if (secondaryDeviceActive) {
                                        xWalkWebView.loadUrl("http://" + ipAddressForSecondaryDevice + ":8888?secondaryDevice");
                                    } else {
                                        xWalkWebView.loadUrl("http://localhost:8888");
                                    }

                                },
                                5000);
                    } else {
                        xWalkWebView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                mUploadMessage = uploadFile;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });

        final JsInterface jsInterface = new JsInterface(this);
        xWalkWebView.addJavascriptInterface(jsInterface, "Android");

        if (autoReloadValue) {
            if (!secondaryDeviceActive) utils.startProgram();
            new android.os.Handler().postDelayed(
                    () -> {
                        if (xWalkWebView.getVisibility() != View.VISIBLE) startWebview();
                    },
                    10000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILECHOOSER_RESULTCODE) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (mUploadMessage != null) {
                    String dataString = intent.getDataString();
                    if (dataString != null) {
                        mUploadMessage.onReceiveValue(Uri.parse(dataString));
                        mUploadMessage = null;
                    }
                }
            }

        }
    }

}
