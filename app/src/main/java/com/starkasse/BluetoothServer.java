package com.starkasse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import net.posprinter.posprinterface.IMyBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.starkasse.MainActivity.server;

/**
 * Created by anhtran on 01.09.17.
 */

public class BluetoothServer {
    BluetoothAdapter bluetoothAdapter;
    private DeviceReceiver myDevice;
    public static IMyBinder binder;
    static boolean firstTime = true;
    public Map<String, BluetoothSocket> socketMap = new HashMap<>();

    final UUID sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Bind successfully
            binder = (IMyBinder) iBinder;
            Log.e("binder", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("disbinder", "disconnected");
        }
    };
    private Context context;
    public Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    public void uninit() {
        context.unregisterReceiver(myDevice);
    }

    public void init(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            //open bluetooth
            BluetoothAdapter.getDefaultAdapter().enable();
        }

        myDevice = new DeviceReceiver();

        //register the receiver
        IntentFilter filterStart = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterEnd = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        try {
            context.registerReceiver(myDevice, filterStart);
            context.registerReceiver(myDevice, filterEnd);

            server.get("/", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    response.send("Hello!!!");
                }
            });

            server.get("/searchPrinters", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    if (!bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.startDiscovery();
                    }

                    myDevice.setResponse(response);
                }
            });

            server.post("/print", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                    JSONObject json = ((JSONObjectBody) request.getBody()).get();
                    final String address, base64Bytes;

                    try {
                        address = json.getString("address");
                        base64Bytes = json.getString("data");
                        final byte[] data = Base64.decode(base64Bytes, Base64.DEFAULT);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendData(address, data, response);
                            }
                        }).start();

                    } catch (JSONException e) {
                    }
                }
            });

        } catch (Exception e) {
        }
    }

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("bluetooth", "disconnect !!!");
                for (Map.Entry<String, BluetoothSocket> pair : socketMap.entrySet()) {
                    pair.getValue().close();
                    socketMap.remove(pair.getKey());
                }
            } catch (Exception e) {
            }
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

            bluetoothAdapter.cancelDiscovery();

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
