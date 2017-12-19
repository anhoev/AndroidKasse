package com.starkasse;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by anhtran on 01.09.17.
 */

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private AsyncHttpServerResponse res;
    private BluetoothServer server;
    private String address;
    private final DataInputStream mmInStream;
    private final OutputStream mmOutStream;
    private boolean _continue = true;

    public ConnectedThread(BluetoothSocket socket, AsyncHttpServerResponse res, BluetoothServer server, String address) {
        mmSocket = socket;
        this.res = res;
        this.server = server;
        this.address = address;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (Exception e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        mmInStream = new DataInputStream(tmpIn);
        mmOutStream = tmpOut;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");
        byte[] buffer = new byte[2];
        // Keep listening to the InputStream while connected

        while (_continue) {
            try {
                // Read from the InputStream
                int byteNum = mmInStream.read(buffer);
                if (byteNum > 0) {
                    Log.e("starkasse.bluetooth", "print finished !!!");
                    Log.d("bluetooth", "resolve");
                    res.send("true");
                    _continue = false;
                    break;
                }

            } catch (Exception e) {
                // Start the service over to restart listening mode
                //BluetoothSerialService.this.start();
                break;
            }
        }

        server.resetDisconnectTimer();
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer) {
        Log.i(TAG, "begin to write");
        try {
            mmOutStream.write(buffer);
            mmOutStream.flush();
            Log.i(TAG, "write successful !!!");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (_continue) {
                        Log.e("bluetooth", "TIMEOUT");
                        _continue = false;
                        res.send("true");
                    }
                    this.cancel();
                }
            }, 6000, 6000);
        } catch (Exception e) {
            _continue = false;
            try {
                if (server.socketMap.get(address) != null) server.socketMap.get(address).close();
                server.socketMap.remove(address);
            } catch (IOException e1) {
            }
            Log.d("bluetooth", "reject");
            res.send("false");
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
            server.socketMap.get(address).close();
            server.socketMap.remove(address);
        } catch (Exception e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}
