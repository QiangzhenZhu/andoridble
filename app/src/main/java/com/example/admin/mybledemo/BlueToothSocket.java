package com.example.admin.mybledemo;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

public class BlueToothSocket extends Application {
    private BluetoothSocket _socket;

    public BluetoothSocket getSocket() {
        return _socket;
    }

    public void setSocket(BluetoothSocket a) {
        _socket = a;
    }

}
