package com.starkasse;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        mmInStream =  new DataInputStream(tmpIn);
        mmOutStream = tmpOut;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");
        byte[] buffer = new byte[10];
        // Keep listening to the InputStream while connected

        while (_continue) {
            try {
                // Read from the InputStream
                int byteNum = mmInStream.read(buffer);
                if (byteNum > 0) {
                    Log.e("starkasse.bluetooth", "print finished !!!");
                    res.send("true");
                    //cancel();
                    break;
                }

            } catch (IOException e) {
                //cancel();
                // Start the service over to restart listening mode
                //BluetoothSerialService.this.start();
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer) {
        try {
            mmOutStream.write(buffer);
            mmOutStream.flush();
        } catch (IOException e) {
            _continue = false;
            try {
                server.socketMap.get(address).close();
                server.socketMap.remove(address);
            } catch (IOException e1) {
            }
            res.send("false");

        }
    }

    public void cancel() {
        try {
            mmSocket.close();
            server.socketMap.get(address).close();
            server.socketMap.remove(address);
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}
