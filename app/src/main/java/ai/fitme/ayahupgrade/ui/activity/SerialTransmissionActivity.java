package ai.fitme.ayahupgrade.ui.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.qmuiteam.qmui.widget.QMUIProgressBar;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.adb.AdbDevice;
import ai.fitme.ayahupgrade.adb.AdbMessage;
import ai.fitme.ayahupgrade.bean.AdbDataPackage;
import ai.fitme.ayahupgrade.model.GenerateJsonData;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;
import ai.fitme.ayahupgrade.utils.ToastUtil;

public class SerialTransmissionActivity extends AdbBaseActivity {

    private QMUIProgressBar qmuiProgressBar;
    private Button btStart;
    private ProgressHandler myHandler = new ProgressHandler();
    //usb
    private TextView tvTips;

    private static final int MESSAGE_DEVICE_ONLINE = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_transmission);
        init();
        initData();
        initPushData();
        //TEST
        L.i(GenerateJsonData.getSerialIOConfigData(getApplicationContext()));
        L.i(GenerateJsonData.getModelConfigData(getApplicationContext()));
        L.i(GenerateJsonData.getTTsConfigData(getApplicationContext()));
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

    private void init(){
        MainApplication.ADB_MODEL = Constants.ADB_PUSH;
        MainApplication.configPushNum = 0;
        btStart = findViewById(R.id.bt_next);
        tvTips = findViewById(R.id.tv_tips);
        qmuiProgressBar = findViewById(R.id.rectProgressBar);
        qmuiProgressBar.setQMUIProgressBarTextGenerator(new QMUIProgressBar.QMUIProgressBarTextGenerator() {
            @Override
            public String generateText(QMUIProgressBar progressBar, int value, int maxValue) {
                return value + "/" + maxValue;
            }
        });
        myHandler.setProgressBar(qmuiProgressBar);

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.ADB_MODEL = Constants.ADB_PUSH;
                adbDevice.openSocket("sync:");
                btStart.setClickable(false);
                btStart.setFocusable(false);
                btStart.setText("正在配置...");
                tvTips.setText("正在配置...");
                btStart.setBackground(getResources().getDrawable(R.drawable.floor_button_shape));
            }
        });
        btStart.setClickable(false);
        btStart.setFocusable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
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
                    btStart.setClickable(false);
                    btStart.setFocusable(false);
                    btStart.setBackground(getResources().getDrawable(R.drawable.floor_button_shape));
                    btStart.setText("设备未连接");
                    tvTips.setText("请用USB数据线连接设备与手机");
                    Message message3 = new Message();
                    message3.what = NEXT;
                    message3.arg1 = 0;
                }
            }
        }
    };


    private void handleDeviceOnline(AdbDevice device) {
        L.i("device online: " + device.getSerial());
        //device.openSocket("shell:exec cat a.txt");
        adbDevice = device;
        ToastUtil.showToast(getApplicationContext(),"设备连接成功");
        btStart.setFocusable(true);
        btStart.setClickable(true);
        btStart.setBackground(getResources().getDrawable(R.drawable.floor_button_shape_selected));
        btStart.setText("开始配置");
        tvTips.setText("设备连接成功，点击下方按钮开始设备配置，过程中请勿拔掉数据线");
    }

    @Override
    public void deviceOnline(AdbDevice device) {
        Message m = Message.obtain(mHandler, MESSAGE_DEVICE_ONLINE);
        m.obj = device;
        mHandler.sendMessage(m);
    }



    //TEST 传输文件
    private List<AdbDataPackage> adbDataPackageList;
    private AdbDataPackage data;
    private void initPushData(){
        //判断需要push的内容
        int pushMode = getIntent().getIntExtra(Constants.PUSH_MODE,Constants.MODE_MAIN);
        if (pushMode==Constants.MODE_MAIN){
            pushMainConfig();
        }else if (pushMode==Constants.MODE_ADVERTISING){
            //TODO
            pushAdversitingConfig();
        }
    }

    /**
     * adb 推送主要的配置项
     */
    private void pushMainConfig(){
        adbDataPackageList = new ArrayList<>();
        //serial_IO_config
        data = new AdbDataPackage();
        data.setPathAndAuthority(Constants.TARGET_PATH_SERIAL_CONFIG + ",0755");
        data.setData(GenerateJsonData.getSerialIOConfigData(getApplicationContext()).getBytes());
        data.setProgress(30);
        adbDataPackageList.add(data);

        //model_config
        data = new AdbDataPackage();
        data.setPathAndAuthority(Constants.TARGET_PATH_MODEL_CONFIG + ",0755");
        data.setData(GenerateJsonData.getModelConfigData(getApplicationContext()).getBytes());
        data.setProgress(50);
        adbDataPackageList.add(data);

        //ttsConfig
        data = new AdbDataPackage();
        data.setPathAndAuthority(Constants.TARGET_PATH_TTS_CONFIG + ",0755");
        data.setData(GenerateJsonData.getTTsConfigData(getApplicationContext()).getBytes());
        data.setProgress(70);
        adbDataPackageList.add(data);

        //welcome.wav    当迎宾开关打开且迎宾音频数据不为空时push
        if (MainApplication.is_welcome_open && MainApplication.customTTs!=null){
            data = new AdbDataPackage();
            data.setPathAndAuthority(Constants.TARGET_PATH_GREET_TTS + ",0755");
            data.setData(MainApplication.customTTs);
            data.setProgress(90);
            adbDataPackageList.add(data);
        }
    }

    /**
     * adb 推送广告配置
     */
    private void pushAdversitingConfig(){
        String strAdJson = getIntent().getStringExtra(Constants.TARGET_PATH_ADVERTISEMENT);
        String[] audioNames = getIntent().getStringArrayExtra("audio_names");
        L.i("audioNames:"+ Arrays.toString(audioNames));

        adbDataPackageList = new ArrayList<>();

        //advertisement.json
        data = new AdbDataPackage();
        data.setPathAndAuthority(Constants.TARGET_PATH_ADVERTISEMENT + ",0755");
        data.setData(strAdJson.getBytes());
        data.setProgress(5);
        adbDataPackageList.add(data);

        //push 广告语音频文件
        for (int i=0;i<audioNames.length;i++){
            data = new AdbDataPackage();
            data.setPathAndAuthority(Constants.TARGET_PATH_ADVER+ audioNames[i] + ",0755");
            //L.i("data:"+ Arrays.toString(FileUtil.getFileStream(getApplicationContext(), audioNames[i])));
            data.setData(FileUtil.getFileStreamFromData(getApplicationContext(), audioNames[i]));
            data.setProgress(((i+1)*100/audioNames.length));
            adbDataPackageList.add(data);
        }
    }

    private AdbDataPackage adbDataPackage = null;
    private int currentPushIndex = 0;

    @Override
    public AdbDataPackage generateData(){
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
                        setProgressbar(adbDataPackage.getProgress());
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
                                btStart.setText("配置成功");
                                tvTips.setText("配置成功，请拔掉数据线，并重启设备");
                                btStart.setFocusable(false);
                                btStart.setClickable(false);
                                setProgressbar(100);
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

    //进度条
    protected static final int STOP = 0x10000;
    protected static final int NEXT = 0x10001;

    @Override
    public void onMessage(AdbMessage message) {

    }

    private static class ProgressHandler extends Handler {
        private WeakReference<QMUIProgressBar> weakRectProgressBar;

        void setProgressBar(QMUIProgressBar rectProgressBar) {
            weakRectProgressBar = new WeakReference<>(rectProgressBar);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP:
                    break;
                case NEXT:
                    if (!Thread.currentThread().isInterrupted()) {
                        if (weakRectProgressBar.get() != null ) {
                            weakRectProgressBar.get().setProgress(msg.arg1);
                        }
                    }
            }
        }
    }

    private void setProgressbar(int progress){
        Message message = new Message();
        message.what = NEXT;
        message.arg1 = progress;
        myHandler.handleMessage(message);
    }
}
