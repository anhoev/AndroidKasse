package com.starkasse;

import android.content.Context;
import android.net.LinkAddress;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anhtran on 14.11.17.
 */

public class WifiUtils {
    static void changeWifiConfiguration(WifiConfiguration wifiConf, boolean dhcp, String ip, int prefix, String dns1, String gateway) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                Class<?> ipAssignment = wifiConf.getClass().getMethod("getIpAssignment").invoke(wifiConf).getClass();
                Object staticConf = wifiConf.getClass().getMethod("getStaticIpConfiguration").invoke(wifiConf);
                if (dhcp) {
                    wifiConf.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConf, Enum.valueOf((Class<Enum>) ipAssignment, "DHCP"));
                    if (staticConf != null) {
                        staticConf.getClass().getMethod("clear").invoke(staticConf);
                    }
                } else {
                    wifiConf.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConf, Enum.valueOf((Class<Enum>) ipAssignment, "STATIC"));
                    if (staticConf == null) {
                        Class<?> staticConfigClass = Class.forName("android.net.StaticIpConfiguration");
                        staticConf = staticConfigClass.newInstance();
                    }
                    // STATIC IP AND MASK PREFIX
                    Constructor<?> laConstructor = null;
                    laConstructor = LinkAddress.class.getConstructor(InetAddress.class, int.class);
                    LinkAddress linkAddress = (LinkAddress) laConstructor.newInstance(InetAddress.getByName(ip), 24);
                    staticConf.getClass().getField("ipAddress").set(staticConf, linkAddress);
                    // GATEWAY
                    staticConf.getClass().getField("gateway").set(staticConf, InetAddress.getByName(gateway));
                    // DNS
                    List<InetAddress> dnsServers = (List<InetAddress>) staticConf.getClass().getField("dnsServers").get(staticConf);
                    dnsServers.clear();
                    dnsServers.add(InetAddress.getByName(dns1));
                    dnsServers.add(InetAddress.getByName("8.8.8.8")); // Google DNS as DNS2 for safety
                    // apply the new static configuration
                    wifiConf.getClass().getMethod("setStaticIpConfiguration", staticConf.getClass()).invoke(wifiConf, staticConf);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
