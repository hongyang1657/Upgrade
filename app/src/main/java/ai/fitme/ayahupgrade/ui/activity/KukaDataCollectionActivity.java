package ai.fitme.ayahupgrade.ui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.qmuiteam.qmui.layout.QMUIButton;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.adb.AdbDevice;
import ai.fitme.ayahupgrade.adb.AdbMessage;
import ai.fitme.ayahupgrade.adb.impl.AdbMessageListener;
import ai.fitme.ayahupgrade.bean.AdbDataPackage;
import ai.fitme.ayahupgrade.model.UploadFileModel;
import ai.fitme.ayahupgrade.utils.BytesUtil;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;
import ai.fitme.ayahupgrade.utils.ToastUtil;


public class KukaDataCollectionActivity extends AdbBaseActivity implements UploadFileModel.UploadFileListener, AdbMessageListener {

    private QMUIButton bt_next;
    private TextView tv_tips;
    private QMUILoadingView qm_loading_view;
    private static final int MESSAGE_DEVICE_ONLINE = 1;
    private static final int PULL_PROCESS = 2;
    private static int PULL_PROCESS_TIMER = 0;
    private boolean isExecute = false;
    private UploadFileModel uploadFileModel;
    //pull多文件
    public List<String> fileList;
    //拉取文件的个数
    public static int pullFileIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_kuka_collection);
        init();
        initData();
        //获取usb权限
        openUsbDevice();
    }

    private void init(){
        bt_next = findViewById(R.id.bt_next);
        tv_tips = findViewById(R.id.tv_tips);
        qm_loading_view = findViewById(R.id.qm_loading_view);
        qm_loading_view.setColor(getResources().getColor(R.color.alipayBlue));
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取目录下的文件
                MainApplication.ADB_MODEL = Constants.ADB_SHELL;
                adbDevice.openSocket("shell:exec ls "+Constants.TARGET_RECORD_PATH);
                setUIStatusAtMain("正在配置...","正在配置...",false,true);
            }
        });
        setUIStatusAtMain("请用USB数据线连接设备与手机","设备未连接",false,true);

    }

    private void initData(){
        // listen for new devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        //获取usb权限
        openUsbDevice();
        FileUtil.makeDir(Constants.KUKA_ROOT_PATH);
        uploadFileModel = new UploadFileModel(this);
        fileList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }

    @Override
    public void onMessage(final AdbMessage message) {
        //获取/data/found/record路径下文件列表
        if (MainApplication.ADB_MODEL==Constants.ADB_SHELL){
            if (message.getDataString()!=null){
                String[] files = message.getDataString().replace("\u001B[1;32m"," ")
                        .replace("\u001B[0m"," ")
                        .replace("\u001B[0;0m"," ").split("\\ ");
                for (int i=0;i<files.length;i++){
                    if (!"".equals(files[i].trim())){
                        fileList.add(files[i].trim());
                    }
                }
            }
        }
    }

    @Override
    public void deviceOnline(AdbDevice device) {
        Message m = Message.obtain(mHandler, MESSAGE_DEVICE_ONLINE);
        m.obj = device;
        mHandler.sendMessage(m);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DEVICE_ONLINE:
                    handleDeviceOnline((AdbDevice)msg.obj);
                    break;
                case PULL_PROCESS:
                    if (MainApplication.ADB_MODEL==Constants.ADB_PULL){
                        if (PULL_PROCESS_TIMER>0){
                            //1秒未收到pull A_CLSE 信号 重新拉取该文件
                            L.i("1秒未收到pull A_CLSE 信号 重新拉取该文件");
                            MainApplication.ADB_MODEL = Constants.ADB_PULL;
                            adbDevice.openSocket("sync:");
                            PULL_PROCESS_TIMER = 0;
                        }else{
                            PULL_PROCESS_TIMER++;
                            L.i("倒计时："+PULL_PROCESS_TIMER);
                        }
                        mHandler.sendEmptyMessageDelayed(PULL_PROCESS,1000);
                    }
                    break;
            }
        }
    };

    private void handleDeviceOnline(AdbDevice device) {
        adbDevice = device;
        ToastUtil.showToast(getApplicationContext(),"设备连接成功");
        setUIStatusAtMain("设备连接成功，点击下方按钮开始设备配置，过程中请勿拔掉数据线","开始配置",true,false);
    }


    @Override
    public void executeCommandClose(int adbModel) {
        //一个shell命令执行完毕
        //已获取指定目录下所有文件，开始pull
        if (adbModel==Constants.ADB_SHELL){
            if (!isExecute){         //确保只执行一次
                isExecute = true;
                L.i(Arrays.toString(fileList.toArray()));
                L.i("文件个数："+fileList.size());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainApplication.ADB_MODEL = Constants.ADB_PULL;
                        pullFileIndex = 0;
                        adbDevice.openSocket("sync:");
                        mHandler.sendEmptyMessage(PULL_PROCESS);
                    }
                },1000);
            }
        }
    }

    @Override
    public String getPullFileName() {
        //返回目标文件路径
        return Constants.TARGET_RECORD_PATH + fileList.get(pullFileIndex);
    }

    @Override
    public void getFileData(final ByteBuffer byteBuffer, final int capacity) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                byte[] datas = BytesUtil.subByte(BytesUtil.subByte(byteBuffer.array(),8,capacity-8),0,capacity-16);
                boolean isSucess = FileUtil.setFileAtRoot(Constants.KUKA_ROOT_PATH + fileList.get(pullFileIndex),datas);
                L.i("getFileData pullFileIndex:"+pullFileIndex);
                if (isSucess){
                    if (pullFileIndex==fileList.size()-1){
                        //结束pull
                        L.i("pull 结束");
                        //上传/fitme/kuka/文件夹下的文件
                        uploadFileModel.rxUpLoadFiles(fileList.toArray(new String[fileList.size()]));
                        MainApplication.ADB_MODEL = Constants.ADB_AWAIT;
                        pullFileIndex = 0;
                        isExecute = false;
                    }else {
                        PULL_PROCESS_TIMER = 0;
                        pullFileIndex++;
                        MainApplication.ADB_MODEL = Constants.ADB_PULL;
                        adbDevice.openSocket("sync:");
                    }
                }
            }
        }.start();
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
                    setUIStatusAtMain("请用USB数据线连接设备与手机","设备未连接",false,false);
                }
            }
        }
    };

    @Override
    public AdbDataPackage generateData() {
        return null;
    }

    @Override
    public void onUploadSuccess() {
        L.i("文件上传全部成功");
        setUIStatusAtMain("数据上传成功！","上传成功",false,false);
    }

    @Override
    public void onUploadError() {
        L.i("有文件上传失败");
        //TODO 停止RxJava进程，重新上传

    }

    private void setUIStatusAtMain(String tips,String btText,boolean isButtonClickable,boolean isLoadingStart){
        if (Looper.getMainLooper()==Looper.myLooper()){   //在主线程
            setUIStatus(tips,btText,isButtonClickable,isLoadingStart);
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUIStatus(tips,btText,isButtonClickable,isLoadingStart);
                }
            });
        }
    }

    private void setUIStatus(String tips,String btText,boolean isButtonClickable,boolean isLoadingStart){
        bt_next.setClickable(isButtonClickable);
        bt_next.setFocusable(isButtonClickable);
        bt_next.setText(btText);
        tv_tips.setText(tips);
        if (isButtonClickable){
            bt_next.setBackground(getResources().getDrawable(R.drawable.floor_button_shape_selected));
        }else {
            bt_next.setBackground(getResources().getDrawable(R.drawable.floor_button_shape));
        }
        if (isLoadingStart){
            qm_loading_view.setVisibility(View.VISIBLE);
            qm_loading_view.start();
        }else {
            qm_loading_view.stop();
            qm_loading_view.setVisibility(View.GONE);
        }
    }
}
