package com.example.admin.mybledemo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.mybledemo.BlueToothSocket;
import com.example.admin.mybledemo.BtDeviceAdapter;
import com.example.admin.mybledemo.ColorPickerView;
import com.example.admin.mybledemo.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.heaton.blelibrary.spp.BtDevice;
import cn.com.heaton.blelibrary.spp.BtManager;

public class ColorPickerActivity extends AppCompatActivity {

    private static final String TAG = "ColorPickerActivity";
    ImageView img_color;
    ImageView img_picker;
    ColorPickerView colorPickerView;
    //    Ble<BleDevice> mBle = Ble.getInstance();
    private BtDevice btDevice;
    private BtManager mBtManager;
    private BtDeviceAdapter mBtAdapter;
    boolean lock = false;//默认关
    private boolean isScanning = false;//是否正在扫描
    private List<BtDevice> mConnectedDevices = new ArrayList<>();//连接成功的设备

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpicker);
        initView();
        initRgbPicker();
        Intent intent = getIntent();
        btDevice = intent.getParcelableExtra("BLUE");
//        Toast.makeText(ColorPickerActivity.this,btDevice.getmState(),Toast.LENGTH_SHORT).show();
        mBtManager = new BtManager(this, mBtListener);
        BlueToothSocket myapp=((BlueToothSocket)getApplicationContext());
        btDevice.setBluetoothSocket(myapp.getSocket());
    }
    private BtManager.BtDeviceListener mBtListener = new BtManager.BtDeviceListener() {
        @Override
        public void onStateChanged(int state, BtDevice btDevice) {
            setConnectedNum();
            if (mBtAdapter != null) {
                mBtAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onDevicesChanged() {
            Log.e(TAG, "onDevicesChanged: " + "设备连接状态改变");
            if (mBtAdapter != null) {
                mBtAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onError(int errorCode, BtDevice btDevice) {

        }

        @Override
        public void onRead(byte[] buffer, BtDevice btDevice) {

        }

        @Override
        public void onWrite(byte[] buffer, BtDevice btDevice) {

        }

        @Override
        public void onFound(BtDevice btDevice) {
            synchronized (mBtManager.getLocker()) {
                mBtAdapter.addDevice(btDevice);
                mBtAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onStartScan() {
            isScanning = true;
        }

        @Override
        public void onStopScan() {
            isScanning = false;
        }
    };
    private void initView() {
        img_color = (ImageView) findViewById(R.id.img_color);
        img_picker = (ImageView) findViewById(R.id.img_picker);
    }
    private void setConnectedNum() {
        if (mBtManager != null) {
            Log.e("mConnectedNum", "已连接的数量：" + mBtManager.getConnectedDevices().size() + "");
            for (BtDevice device : mBtManager.getConnectedDevices()) {
                Log.e("device", "设备地址：" + device.getAddress());
            }
           // mConnectedNum.setText(getString(R.string.lined_num) + mBtManager.getConnectedDevices().size());
        }
    }
    private void initRgbPicker() {
        colorPickerView = (ColorPickerView) findViewById(R.id.color_picker);
        colorPickerView.setImgPicker(ColorPickerActivity.this, img_picker, 25); //最后一个参数是该颜色指示圈的大小(dp)
        colorPickerView.setColorChangedListener(new ColorPickerView.onColorChangedListener() {
            @Override
            public void colorChanged(int red, int blue, int green) {
                img_color.setColorFilter(Color.argb(255, red, green, blue));
            }

            @Override
            public void stopColorChanged(int red, int blue, int green) {
                String rgb = "04ATE" + red + blue + green + "";
                final byte[] data = rgb.getBytes();
                lock = !lock;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] oc = new byte[6];
                        oc[0] = 0;    //包的索引
                        oc[1] = 4;    //包的长度
                        oc[2] = 'A';
                        oc[3] = 'T';
                        oc[4] = 'E';
                        oc[5] = (byte) (lock ? '1' : '0');
                        boolean result = btDevice.sendOnePacket(oc, 10, true);
                        Log.e(TAG, "sendData: " + result);
                    }
                }).start();

//        colorPickerView.setImgResource(R.mipmap.colors_picture);
            }
        });
    }
}
