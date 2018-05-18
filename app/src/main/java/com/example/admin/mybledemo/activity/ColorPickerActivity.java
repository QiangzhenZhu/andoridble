package com.example.admin.mybledemo.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.mybledemo.ColorPickerView;
import com.example.admin.mybledemo.R;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.spp.BtDevice;
import cn.com.heaton.blelibrary.spp.BtManager;

public class ColorPickerActivity extends AppCompatActivity {

    private static final String TAG = "ColorPickerActivity";
    ImageView img_color;
    ImageView img_picker;
    ColorPickerView colorPickerView;
//    Ble<BleDevice> mBle = Ble.getInstance();
    private BtDevice btDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpicker);
        initView();
        initRgbPicker();
        Intent intent = getIntent();
        btDevice = (BtDevice) intent.getSerializableExtra("BLUE");
    }

    private void initView() {
        img_color = (ImageView) findViewById(R.id.img_color);
        img_picker = (ImageView) findViewById(R.id.img_picker);
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
                String rgb = red + blue + green + "";
                byte[] data = rgb.getBytes();
                boolean bool = btDevice.writeData(data);
                if (!bool)
                {
                    Toast.makeText(ColorPickerActivity.this,"Send success!",Toast.LENGTH_SHORT).show();
                }


            }
        });
//        colorPickerView.setImgResource(R.mipmap.colors_picture);
    }

}
