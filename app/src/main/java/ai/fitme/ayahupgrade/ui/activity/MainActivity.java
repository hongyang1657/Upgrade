package ai.fitme.ayahupgrade.ui.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.qmuiteam.qmui.layout.QMUIButton;
import java.nio.ByteBuffer;
import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.adb.AdbDevice;
import ai.fitme.ayahupgrade.adb.AdbMessage;
import ai.fitme.ayahupgrade.bean.AdbDataPackage;
import ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.OnClickUtils;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;
import ai.fitme.ayahupgrade.utils.ToastUtil;

public class MainActivity extends AdbBaseActivity{

    private QMUIButton btNext;
    private TextView tvTips;
    private TextView tvSkip;
    private TextView tvSetting;
    private static final int MESSAGE_LOG = 1;
    private static final int MESSAGE_DEVICE_ONLINE = 2;
    private static final int MESSAGE_GET_SERIAL_CONFIG = 3;
    private static final int MESSAGE_GET_MODEL_CONFIG = 4;
    private static final int MESSAGE_GET_TTS_CONFIG = 5;
    private static final int MESSAGE_GET_ADVERTISEMENT_CONFIG = 6;
    private int configType = AnalasisLocalConfigJsonTask.SERIAL_CONFIG_JSON;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    private void initView(){
        btNext = findViewById(R.id.bt_next);
        tvTips = findViewById(R.id.tv_tips);
        tvSkip = findViewById(R.id.tv_skip);
        tvSetting = findViewById(R.id.tv_setting);
        tvSkip.setOnClickListener(listener);
        btNext.setOnClickListener(listener);
        tvSetting.setOnClickListener(listener);
        btNext.setClickable(false);
        btNext.setFocusable(false);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!OnClickUtils.isFastDoubleClick(v.getId())){
                switch (v.getId()){
                    case R.id.bt_next:
                        //跳转到配置页面
                        Intent intent = new Intent(MainActivity.this,UpgradeConfigActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.tv_skip:
                        Intent intent1 = new Intent(MainActivity.this,UpgradeConfigActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.tv_setting:
                        Intent intent2 = new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private void initData(){
        // listen for new devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        //获取usb权限
        openUsbDevice();
    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
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
                    btNext.setBackground(getResources().getDrawable(R.drawable.floor_button_shape));
                    btNext.setText("设备未连接");
                    tvTips.setText("请用USB数据线连接设备与手机");
                }
            }
        }
    };

    @Override
    public void onMessage(AdbMessage message) {
        if (message.getDataString()!=null){
            log(message.getDataString().replace("[1;34m","").replace("[0m","").replace("[0;0m","").replace("[1;32m",""));
        }
    }

    @Override
    public void deviceOnline(AdbDevice device) {
        L.i("deviceOnline");
        Message m = Message.obtain(mHandler, MESSAGE_DEVICE_ONLINE);
        m.obj = device;
        mHandler.sendMessage(m);
    }

    @Override
    public AdbDataPackage generateData() {
        return null;
    }

    @Override
    public void executeCommandClose(int adbModel) {

    }

    @Override
    public void getFileData(ByteBuffer byteBuffer,int capacity) {

    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOG:
                    L.i("cat log:"+(String) msg.obj);
                    //TODO 判断版本信息，解析本地json
                    try {
                        AnalasisLocalConfigJsonTask.analasisJson(getApplicationContext(),(String) msg.obj,configType);
                    }catch (Exception e){
                        L.i("本地json文件解析失败 e:"+e.toString());
                    }
                    break;
                case MESSAGE_DEVICE_ONLINE:
                    adbDevice = (AdbDevice)msg.obj;
                    //发现设备在线，直接读取配置文件
                    getDeviceLocalConfig();
                    break;
                case MESSAGE_GET_SERIAL_CONFIG:
                    adbDevice.openSocket("shell:exec cat "+ Constants.TARGET_PATH_MODEL_CONFIG);
                    Message message = Message.obtain(mHandler,MESSAGE_GET_MODEL_CONFIG);
                    mHandler.sendMessageDelayed(message,300);
                    configType = AnalasisLocalConfigJsonTask.MODEL_CONFIG_JSON;
                    break;
                case MESSAGE_GET_MODEL_CONFIG:
                    adbDevice.openSocket("shell:exec cat "+ Constants.TARGET_PATH_TTS_CONFIG);
                    Message message1 = Message.obtain(mHandler,MESSAGE_GET_TTS_CONFIG);
                    mHandler.sendMessageDelayed(message1,300);
                    configType = AnalasisLocalConfigJsonTask.TTS_CONFIG_JSON;
                    break;
                case MESSAGE_GET_TTS_CONFIG:
                    adbDevice.openSocket("shell:exec cat "+ Constants.TARGET_PATH_ADVERTISEMENT);
                    Message message2 = Message.obtain(mHandler,MESSAGE_GET_ADVERTISEMENT_CONFIG);
                    mHandler.sendMessageDelayed(message2,300);
                    configType = AnalasisLocalConfigJsonTask.ADVERTISEMENT_CONFIG_JSON;
                    break;
                case MESSAGE_GET_ADVERTISEMENT_CONFIG:
                    //获取本地配置完毕
                    handleDeviceOnline();
                    break;
            }
        }
    };

    private void handleDeviceOnline() {
        ToastUtil.showToast(this,"设备连接成功");
        btNext.setFocusable(true);
        btNext.setClickable(true);
        btNext.setBackground(getResources().getDrawable(R.drawable.floor_button_shape_selected));
        btNext.setText("下一步");
        tvTips.setText("设备连接成功，点击下方按钮进入设备配置页面");
    }

    /**
     * 使用cat方法获取设备本地的配置文件内容
     */
    private void getDeviceLocalConfig(){
        MainApplication.ADB_MODEL = Constants.ADB_SHELL;
        adbDevice.openSocket("shell:exec cat "+ Constants.TARGET_PATH_SERIAL_CONFIG);
        Message message = Message.obtain(mHandler,MESSAGE_GET_SERIAL_CONFIG);
        mHandler.sendMessageDelayed(message,300);
        configType = AnalasisLocalConfigJsonTask.SERIAL_CONFIG_JSON;
    }

    public void log(String s) {
        Message m = Message.obtain(mHandler, MESSAGE_LOG);
        m.obj = s;
        mHandler.sendMessage(m);
    }
}
