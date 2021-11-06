package ai.fitme.ayahupgrade.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.qmuiteam.qmui.layout.QMUIButton;
import com.qmuiteam.qmui.widget.QMUILoadingView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.adb.AdbDevice;
import ai.fitme.ayahupgrade.adb.AdbMessage;
import ai.fitme.ayahupgrade.bean.AdbDataPackage;
import ai.fitme.ayahupgrade.bean.ElevatorConfig;
import ai.fitme.ayahupgrade.model.GenerateJsonData;
import ai.fitme.ayahupgrade.model.GetOTAConfigModel;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;
import ai.fitme.ayahupgrade.utils.ToastUtil;
import cn.bmob.v3.exception.BmobException;


public class OTAActivity_old extends AdbBaseActivity {
    private ImageView ivBack;
    private TextView tvTips;
    private QMUIButton btNext;
    private QMUILoadingView qmLoadingView;

    //TEST 传输文件
    private List<AdbDataPackage> adbDataPackageList;
    private AdbDataPackage adbDataPackage = null;
    private int currentPushIndex = 0;
    private AdbDataPackage data;

    private static final int MESSAGE_DEVICE_ONLINE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_ota);
        initView();
        initData();
        //GetOTAConfigModel.uploadFile(getApplicationContext(),Environment.getExternalStorageDirectory().getPath()+"/senseflow");
        //GetOTAConfigModel.addConfig("samsung");
        //获取app对应版本的配置
        GetOTAConfigModel.getConfigByElevatorType(Constants.getChannel(this), new GetOTAConfigModel.OtaDataListener() {
            @Override
            public void getElevatorConfigDone(List<ElevatorConfig> list) {
                L.i("list:"+list.size());
                L.i("elevator:"+list.get(0).toString());
                //TODO 根据云端配置生成本地配置文件
                pushMainConfig(list.get(0));
                tvTips.setText("请用USB数据线连接设备与手机");
            }

            @Override
            public void error(BmobException e) {
                L.i("e:"+e.toString());
            }

        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    public void click(View v){
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_next:
                MainApplication.ADB_MODEL = Constants.ADB_PUSH;
                adbDevice.openSocket("sync:");
                btNext.setClickable(false);
                btNext.setFocusable(false);
                btNext.setText("正在配置...");
                tvTips.setText("正在配置...");
                btNext.setBackgroundColor(getResources().getColor(R.color.colorRbNotSelected));
                qmLoadingView.setVisibility(View.VISIBLE);
                qmLoadingView.start();
                break;
        }
    }

    private void initView() {
        tvTips = findViewById(R.id.tv_tips);
        ivBack = findViewById(R.id.iv_back);
        btNext = findViewById(R.id.bt_next);
        qmLoadingView = findViewById(R.id.qm_loading_view);
        qmLoadingView.setColor(getResources().getColor(R.color.alipayBlue));
        qmLoadingView.setVisibility(View.VISIBLE);
        qmLoadingView.start();
    }

    private void initData(){
        // listen for new devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        //获取usb权限
        openUsbDevice();
    }

    /**
     * 推送
     */
    private void pushMainConfig(ElevatorConfig elevatorConfig){
        adbDataPackageList = new ArrayList<>();
        //serial_IO_config
        data = new AdbDataPackage();
        data.setPathAndAuthority(Constants.TARGET_PATH_SERIAL_CONFIG + ",0755");
        data.setData(GenerateJsonData.getSerialIOConfigData(getApplicationContext(),elevatorConfig).getBytes());
        data.setProgress(30);
        adbDataPackageList.add(data);

        //model_config
        data = new AdbDataPackage();
        data.setPathAndAuthority(Constants.TARGET_PATH_MODEL_CONFIG + ",0755");
        data.setData(GenerateJsonData.getModelConfigData(getApplicationContext(),elevatorConfig).getBytes());
        data.setProgress(50);
        adbDataPackageList.add(data);

        //ttsConfig
        data = new AdbDataPackage();
        data.setPathAndAuthority(Constants.TARGET_PATH_TTS_CONFIG + ",0755");
        data.setData(GenerateJsonData.getTTsConfigData(getApplicationContext(),elevatorConfig).getBytes());
        data.setProgress(70);
        adbDataPackageList.add(data);

        //senseflow
        data = new AdbDataPackage();
        data.setPathAndAuthority("/usr/bin/senseflow"+",0755");
        data.setData(FileUtil.getAssetsFileStream(getApplicationContext(),"senseflow"));
        data.setProgress(90);
        adbDataPackageList.add(data);
    }

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                L.i("usb设备已连接");
                openUsbDevice();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                String deviceName = device.getDeviceName();
                if (mDevice != null && mDevice.equals(deviceName)) {
                    L.i("adb interface removed");
                    setAdbInterface(null, null);
                    //数据线断开
                    ToastUtil.showToast(getApplicationContext(),"数据连接线断开，请重新连接设备");
                    btNext.setClickable(false);
                    btNext.setFocusable(false);
                    btNext.setBackgroundColor(getResources().getColor(R.color.colorRbNotSelected));
                    btNext.setText("设备未连接");
                    tvTips.setText("请用USB数据线连接设备与手机");
                }
            }
        }
    };


    @Override
    public void onMessage(AdbMessage message) {

    }

    @Override
    public void deviceOnline(AdbDevice device) {
        Message m = Message.obtain(mHandler, MESSAGE_DEVICE_ONLINE);
        m.obj = device;
        mHandler.sendMessage(m);
    }

    @Override
    public AdbDataPackage generateData() {
        L.i("测试 生成数据 开始传输："+currentPushIndex);
        adbDataPackage = adbDataPackageList.get(currentPushIndex);
        return adbDataPackage;
    }

    @Override
    public void executeCommandClose(int adbModel) {
        if (adbModel==Constants.ADB_PUSH){
            L.i("测试 收到传输成功："+currentPushIndex);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(currentPushIndex < adbDataPackageList.size()-1){
                        currentPushIndex++;
                        MainApplication.ADB_MODEL = Constants.ADB_PUSH;
                        adbDevice.openSocket("sync:");
                        L.i("测试 currentPushIndex："+currentPushIndex);
                    }else if (currentPushIndex==adbDataPackageList.size()-1){
                        //最后一次push
                        //同步sync
                        currentPushIndex++;
                        MainApplication.ADB_MODEL = Constants.ADB_SHELL;
                        adbDevice.openSocket("shell:exec sync");
                        L.i("测试 同步sync currentPushIndex："+currentPushIndex);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO 发送sync后 暂时认为配置成功
                                ToastUtil.showToast(getApplicationContext(),"配置成功！");
                                btNext.setText("配置成功");
                                tvTips.setText("配置成功，请拔掉数据线，并重启设备");
                                btNext.setFocusable(false);
                                btNext.setClickable(false);
                                qmLoadingView.stop();
                                qmLoadingView.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }.start();
        }

    }

    @Override
    public void getFileData(ByteBuffer byteBuffer,int capacity) {

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DEVICE_ONLINE:
                    handleDeviceOnline((AdbDevice)msg.obj);
                    break;
            }
        }
    };

    private void handleDeviceOnline(AdbDevice device) {
        L.i("device online: " + device.getSerial());
        //device.openSocket("shell:exec cat a.txt");
        adbDevice = device;
        ToastUtil.showToast(getApplicationContext(),"设备连接成功");
        btNext.setFocusable(true);
        btNext.setClickable(true);
        btNext.setBackgroundColor(getResources().getColor(R.color.alipayBlue));
        btNext.setText("开始配置");
        tvTips.setText("设备连接成功，点击下方按钮开始设备配置，过程中请勿拔掉数据线");
        qmLoadingView.stop();
        qmLoadingView.setVisibility(View.GONE);
    }
}
