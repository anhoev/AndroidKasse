package com.starkasse;

/**
 * Created by anhtran on 01.09.17.
 */

public class BluetoothPrinter {
    public String name;
    public String address;

    public BluetoothPrinter(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
