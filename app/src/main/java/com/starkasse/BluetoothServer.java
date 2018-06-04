package com.starkasse;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.starkasse.MainActivity.server;

/**
 * Created by anhtran on 01.09.17.
 */

public class BluetoothServer {
    BluetoothAdapter bluetoothAdapter;
    public Map<String, BluetoothSocket> socketMap = new HashMap<>();

    private Context context;
    @SuppressLint("HandlerLeak")
    public Handler disconnectHandler = new Handler();

    public Handler btHandler = new Handler();
    public Handler btHandler2 = new Handler();

    public Handler connectHandler = new Handler();

    public void uninit() {
    }

    ArrayList<BluetoothPrinter> deviceList_found = new ArrayList<>();

    public void init(final Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            //open bluetooth
            BluetoothAdapter.getDefaultAdapter().enable();
        }

        try {
            server.get("/", (request, response) -> response.send("Hello!!!"));

            server.get("/searchPrinters", (request, response) -> {
                disconnectCallback.run();

                if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();

                BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> {
                    boolean found = false;
                    for (BluetoothPrinter bluetoothPrinter : deviceList_found) {
                        if (bluetoothPrinter.address.equals(device.getAddress())) found = true;
                    }
                    if (!found) {
                        deviceList_found.add(new BluetoothPrinter(device.getName(), device.getAddress()));
                    }
                };

                bluetoothAdapter.startLeScan(leScanCallback);

                btHandler.postDelayed(() -> {
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    response.send(new Gson().toJson(deviceList_found));
                }, 4000);
            });

            server.post("/checkPrinterAvailable", (request, response) -> {
                JSONObject json = ((JSONObjectBody) request.getBody()).get();

                try {
                    AtomicBoolean finish = new AtomicBoolean(false);
                    String address = json.getString("address");
                    disconnectCallback.run();
                    if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();

                    BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> {
                        if (finish.get()) return;
                        if (address.equals(device.getAddress())) {
                            finish.set(true);
                            response.send(String.valueOf(rssi));
                        }
                    };
                    bluetoothAdapter.startLeScan(leScanCallback);

                    btHandler2.postDelayed(() -> {
                        bluetoothAdapter.stopLeScan(leScanCallback);
                        if (!finish.get()) response.send("0");
                    }, 4000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            server.post("/print", (request, response) -> {
                JSONObject json = ((JSONObjectBody) request.getBody()).get();
                final String address, base64Bytes;

                try {
                    address = json.getString("address");
                    base64Bytes = json.getString("data");
                    final byte[] data = Base64.decode(base64Bytes, Base64.DEFAULT);

                    new Thread(() -> sendData(address, data, response)).start();

                } catch (JSONException e) {
                }
            });

        } catch (Exception e) {
        }
    }

    private Runnable disconnectCallback = () -> {
        try {
            Log.e("bluetooth", "disconnect !!!");
            for (Map.Entry<String, BluetoothSocket> pair : socketMap.entrySet()) {
                pair.getValue().close();
                socketMap.remove(pair.getKey());
            }
        } catch (Exception e) {
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, 30 * 60 * 1000);
        Log.d("bluetooth-server", "resetDisconnectTimer");
    }

    private void sendData(String address, final byte[] buf, final AsyncHttpServerResponse res) {
        BluetoothSocket socket = null;
        if (!socketMap.containsKey(address) || !socketMap.get(address).isConnected()) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

            try {
                //socket = device.createInsecureRfcommSocketToServiceRecord(sppUuid);
                socket = (BluetoothSocket) device.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class}).invoke(device, 1);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("bluetooth", "reject");
                res.send("false");
                return;
            }

            //bluetoothAdapter.cancelDiscovery();

            BluetoothSocket finalSocket = socket;
            connectHandler.postDelayed(() -> {
                if (!finalSocket.isConnected()) {
                    try {
                        finalSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }, 15000);

            try {
                socket.connect();
            } catch (Exception e) {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                //bluetoothAdapter.disable();
                //bluetoothAdapter.enable();

                Log.d("bluetooth", "reject");
                res.send("false");
                return;
            }

            resetDisconnectTimer();

            socketMap.put(address, socket);
        } else {
            socket = socketMap.get(address);
        }

        //todo: timeout

        ConnectedThread connectedThread = new ConnectedThread(socket, res, this, address);
        connectedThread.start();
        connectedThread.write(buf);
    }


}
