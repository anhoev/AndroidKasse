package com.starkasse;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;


public class MainActivity extends XWalkActivity {
    private XWalkView xWalkWebView;
    final int uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        if (sharedPref.getBoolean("NotFirstTime", true)) {
            EnvUtils.updateEnv(this);
            sharedPref.edit().putBoolean("NotFirstTime", false);
        }

        setContentView(R.layout.activity_main);

        findViewById(R.id.startLinux).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnvUtils.cli(MainActivity.this, "-p linux start","-m");
            }
        });

        findViewById(R.id.stopLinux).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnvUtils.cli(MainActivity.this, "-p linux stop","-u");
            }
        });

        xWalkWebView = (XWalkView) findViewById(R.id.xwalkWebView);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(uiVisibility);

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

        Button reloadButton = (Button) findViewById(R.id.reloadBtn);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xWalkWebView.load("http://" + sharedPref.getString("ip", null) + ":8888", null);
                xWalkWebView.setVisibility(View.VISIBLE);
            }
        });


        View button = findViewById(R.id.ipSetting);
        final EditText ipInput = (EditText) findViewById(R.id.ipInput);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.inputGroup).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().putString("ip", ipInput.getText().toString()).commit();
            }
        });

        final String ip = sharedPref.getString("ip", null);

        ipInput.setText(ip);

        registerComponentCallbacks(new MemoryHandle());
    }

    public class MemoryHandle implements ComponentCallbacks2 {

        @Override
        public void onTrimMemory(int level) {
            Log.v("MEMORY", "onTrimMemory() with level=" + level);
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {

        }

        @Override
        public void onLowMemory() {

        }
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
        final String ip = sharedPref.getString("ip", null);

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
                                    if (!TextUtils.isEmpty(ip) && !first) {
                                        first = true;
                                        xWalkWebView.setVisibility(View.VISIBLE);
                                        xWalkWebView.load("http://" + ip + ":8888", null);
                                    }
                                }
                            },
                            5000);
                }
            }
        });

        if (!TextUtils.isEmpty(ip)) {
            xWalkWebView.setVisibility(View.VISIBLE);
            xWalkWebView.load("http://" + ip + ":8888", null);
        }

    }

}
