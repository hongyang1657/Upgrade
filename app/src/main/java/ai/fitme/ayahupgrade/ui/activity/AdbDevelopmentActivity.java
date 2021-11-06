/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.fitme.ayahupgrade.ui.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.util.Arrays;
import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.adb.AdbDevice;
import ai.fitme.ayahupgrade.adb.AdbMessage;
import ai.fitme.ayahupgrade.bean.AdbDataPackage;
import ai.fitme.ayahupgrade.bean.SerialConfigRoot;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.ToastUtil;

/* Main activity for the adb test program */
public class AdbDevelopmentActivity extends AdbBaseActivity{

    private static final String TAG = "AdbTestActivity";

    private TextView mLog;
    private EditText etInput;
    private Button btExec,btCat;
    private static final int MESSAGE_LOG = 1;
    private static final int MESSAGE_DEVICE_ONLINE = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.adb);
        mLog = findViewById(R.id.log);
        etInput = findViewById(R.id.et_input);
        btExec = findViewById(R.id.bt_shell);
        btCat = findViewById(R.id.bt_cat);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);


        //获取usb权限
        openUsbDevice();

        btExec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adbDevice!=null){
                    MainApplication.ADB_MODEL = Constants.ADB_SHELL;
                    adbDevice.openSocket("shell:exec "+etInput.getText().toString().trim());
                    log( etInput.getText().toString().trim());
                    etInput.setText("");
                }

            }
        });

        btCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.ADB_MODEL = Constants.ADB_SHELL;
                adbDevice.openSocket("shell:exec cat "+ Constants.TARGET_PATH_SERIAL_CONFIG);
                adbDevice.openSocket("shell:exec cat "+ Constants.TARGET_PATH_MODEL_CONFIG);
                adbDevice.openSocket("shell:exec cat "+ Constants.TARGET_PATH_TTS_CONFIG);
                adbDevice.openSocket("shell:exec cat "+ Constants.TARGET_PATH_ADVERTISEMENT);
            }
        });

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }


    public void log(String s) {
        L.i("log = "+s);
        Message m = Message.obtain(mHandler, MESSAGE_LOG);
        m.obj = s;
        mHandler.sendMessage(m);
    }

    private void appendLog(String text) {
        Rect r = new Rect();
        mLog.getDrawingRect(r);
        int maxLines = r.height() / mLog.getLineHeight() - 1;
        text = mLog.getText() + "\n\n" + text;

        // see how many lines we have
        int index = text.lastIndexOf('\n');
        int count = 0;
        while (index > 0 && count <= maxLines) {
            count++;
            index = text.lastIndexOf('\n', index - 1);
        }

        // truncate to maxLines
        if (index > 0) {
            text = text.substring(index + 1);
        }
        mLog.setText(text);
    }

    public void deviceOnline(AdbDevice device) {
        Message m = Message.obtain(mHandler, MESSAGE_DEVICE_ONLINE);
        m.obj = device;
        mHandler.sendMessage(m);
    }

    @Override
    public AdbDataPackage generateData() {
        String[] floorArr = new String[MainApplication.floorList.size()];
        String[] dataFloorArr = new String[MainApplication.dataFloorList.size()];
        Gson gson = new Gson();
        SerialConfigRoot serialConfigRoot = gson.fromJson(Constants.getFileContent(this,"serial_IO_config.json"),SerialConfigRoot.class);
        serialConfigRoot.getNumMinus2().setFloor(MainApplication.floorList.toArray(floorArr));
        serialConfigRoot.getNumMinus2().setDataFloor(MainApplication.dataFloorList.toArray(dataFloorArr));
        serialConfigRoot.getNumMinus2().setFloorMax(String.valueOf(MainApplication.dataFloorList.size()));
        serialConfigRoot.getNumMinus2().setWakeup_num(MainApplication.WAKE_UP_SOUND);
        serialConfigRoot.getNumMinus2().setCancel_config(MainApplication.is_cancel_open);
        serialConfigRoot.getNumMinus2().setAlarm_config(MainApplication.is_alarm_open);
        //设置开门关门
        String closeDoorSerial = serialConfigRoot.getNum1().getSerial();
        String openDoorSerial = serialConfigRoot.getNum3().getSerial();
        closeDoorSerial = closeDoorSerial.substring(0,2) + MainApplication.CLOSE_DOOR_INDEX + closeDoorSerial.substring(4);
        openDoorSerial = openDoorSerial.substring(0,2) + MainApplication.OPEN_DOOR_INDEX + openDoorSerial.substring(4);
        serialConfigRoot.getNum1().setSerial(closeDoorSerial);
        serialConfigRoot.getNum3().setSerial(openDoorSerial);

        L.i("root = "+new Gson().toJson(serialConfigRoot));

        AdbDataPackage data = new AdbDataPackage();
        data.setPathAndAuthority(Constants.TARGET_PATH_SERIAL_CONFIG+",0755");
        data.setData(new Gson().toJson(serialConfigRoot).getBytes());
        return data;
    }

    @Override
    public void executeCommandClose(int adbModel) {
        if (adbModel==Constants.ADB_PUSH){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(3000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainApplication.ADB_MODEL = Constants.ADB_STAT;
                                adbDevice.openSocket("sync:");
                            }
                        });
                        sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //同步sync
                                adbDevice.openSocket("shell:exec sync");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    public void getFileData(ByteBuffer byteBuffer,int capacity) {
        byte[] data = subBytes(subBytes(byteBuffer.array(),8,byteBuffer.array().length-8),0,byteBuffer.array().length-16);

        L.logE("data = "+ Arrays.toString(data));
        FileUtil.setFile(this,"hongy.pcm",data);
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    private void handleDeviceOnline(AdbDevice device) {
        L.i("device online: " + device.getSerial());
        adbDevice = device;
    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                L.i("usb设备已连接");
                openUsbDevice();

//                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                String deviceName = device.getDeviceName();
                if (mDevice != null && mDevice.equals(deviceName)) {
                    L.i("adb interface removed");
                    setAdbInterface(null, null);
                    //数据线断开
                    ToastUtil.showToast(getApplicationContext(),"数据连接线断开，请重新连接设备");
                }
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOG:
                    appendLog((String)msg.obj);
                    break;
                case MESSAGE_DEVICE_ONLINE:
                    handleDeviceOnline((AdbDevice)msg.obj);
                    break;
            }
        }
    };

    @Override
    public void onMessage(AdbMessage message) {
        L.logE("AdbTestActivity messageDataString : "+message.getDataString());
        if (message.getDataString()!=null){
            log(message.getDataString().replace("[1;34m","").replace("[0m","").replace("[0;0m","").replace("[1;32m",""));
        }
    }
}


