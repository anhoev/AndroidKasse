package com.starkasse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WifiDirectReceiver extends BroadcastReceiver {
    static boolean firstTime = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (firstTime) {
            WifiP2pGroup p2pGroup = (WifiP2pGroup) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
            Method setPassphraseMethod = null;
            Method setNetworkNameMethod = null;

            try {
                setPassphraseMethod = p2pGroup.getClass().getMethod("setPassphrase", new Class[]{String.class});
                setNetworkNameMethod = p2pGroup.getClass().getMethod("setNetworkName", new Class[]{String.class});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            if (setNetworkNameMethod != null && setNetworkNameMethod != null) {
                try {
                    setNetworkNameMethod.invoke(p2pGroup, "TEST");
                    setPassphraseMethod.invoke(p2pGroup, "partyrock");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            firstTime = false;
        }

    }
}
