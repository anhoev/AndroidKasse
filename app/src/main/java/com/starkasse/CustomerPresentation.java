package com.starkasse;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anhtran on 07.09.17.
 */

public class CustomerPresentation extends Presentation {
    @InjectView(R.id.customerdisplay)
    public WebView webview;

    @InjectView(R.id.loading2)
    public View loading2;

    public MainActivity mainActivity;

    public CustomerPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        mainActivity = (MainActivity) outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the custom layout
        setContentView(R.layout.customer_display);
        ButterKnife.inject(this);
    }

    void startWebview() {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (mainActivity.secondaryDeviceActive) {
                                    webview.loadUrl("http://" + mainActivity.ipAddressForSecondaryDevice + ":8888/customerdisplay?secondaryDevice");
                                } else {
                                    webview.loadUrl("http://localhost:8888/customerdisplay");
                                }
                            }
                        },
                        5000);
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.setVisibility(View.VISIBLE);
                loading2.setVisibility(View.INVISIBLE);
            }
        });

        if (mainActivity.secondaryDeviceActive) {
            webview.loadUrl("http://" + mainActivity.ipAddressForSecondaryDevice + ":8888/customerdisplay?secondaryDevice");
        } else {
            webview.loadUrl("http://localhost:8888/customerdisplay");
        }
    }


}
