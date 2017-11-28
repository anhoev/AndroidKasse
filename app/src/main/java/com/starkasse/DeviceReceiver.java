package com.starkasse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.util.ArrayList;

/**
 * Created by charlie on 2017/4/2.
 * Bluetooth search state braodcastrecever
 */

public class DeviceReceiver extends BroadcastReceiver {


    ArrayList<BluetoothPrinter> deviceList_found;
    private AsyncHttpServerResponse res;
    Gson gson = new Gson();

    public DeviceReceiver() {
        this.deviceList_found = new ArrayList<BluetoothPrinter>();
    }

    public void setResponse(AsyncHttpServerResponse res) {
        this.res = res;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice btd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (btd.getBondState() != BluetoothDevice.BOND_BONDED) {
                if (!deviceList_found.contains(btd.getName() + '\n' + btd.getAddress())) {
                    deviceList_found.add(new BluetoothPrinter(btd.getName(), btd.getAddress()));
                }
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            if (res != null) {
                //convert to json
                String json = gson.toJson(deviceList_found);
                res.send(json.toString());
            }
            this.deviceList_found.clear();
        }

    }
}
