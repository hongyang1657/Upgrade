package ai.fitme.ayahupgrade.ui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.qmuiteam.qmui.layout.QMUIButton;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.adb.AdbDevice;
import ai.fitme.ayahupgrade.adb.AdbMessage;
import ai.fitme.ayahupgrade.bean.AdbDataPackage;
import ai.fitme.ayahupgrade.bean.OtaConfigBean;
import ai.fitme.ayahupgrade.model.CheckNeedOTAModel;
import ai.fitme.ayahupgrade.model.OTADownloadModel;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.MD5Util;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;
import ai.fitme.ayahupgrade.utils.ToastUtil;


public class OTAActivity extends AdbBaseActivity implements OTADownloadModel.OTADownloadListener, CheckNeedOTAModel.CheckNeedOTAListener {
    private TextView tvTips;
    private QMUIButton btNext;
    private QMUILoadingView qmLoadingView;

    //传输文件
    private List<AdbDataPackage> adbDataPackageList;
    private AdbDataPackage adbDataPackage = null;
    private int currentPushIndex = 0;
    private AdbDataPackage data;
    private List<OtaConfigBean.UpgradeFile> downloadFileNameList;
    private static final int MESSAGE_DEVICE_ONLINE = 2;

    //是否可以ota升级
    private static boolean isDownloadFileFinished = false;
    private static boolean isUsbConnected = false;

    enum Status{
        INIT,
        OFFLINE,
        NO_USB,
        ALL_FINISHED
    }

    private Status status = Status.INIT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_ota);
        initView();
        initData();
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
                //开始升级
                switch (status){
                    case ALL_FINISHED:
                        MainApplication.ADB_MODEL = Constants.ADB_PUSH;
                        adbDevice.openSocket("sync:");
                        setUIStatus("正在升级，请保持设备连接...","正在升级...",false,true);
                        break;
                    case OFFLINE:
                        new CheckNeedOTAModel(this).checkNeedOTA();
                        break;
                }
                break;
        }
    }

    private void initView() {
        tvTips = findViewById(R.id.tv_tips);
        btNext = findViewById(R.id.bt_next);
        qmLoadingView = findViewById(R.id.qm_loading_view);
        qmLoadingView.setColor(getResources().getColor(R.color.alipayBlue));
    }

    private void initData(){
        // listen for new devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        //获取usb权限
        openUsbDevice();
        //检查是否有固件可以更新
        new CheckNeedOTAModel(this).checkNeedOTA();
    }


    /**
     * 设置需要push的文件数据
     */
    private void setPushConfig(List<OtaConfigBean.UpgradeFile> downloadFileNameList){
        this.downloadFileNameList = downloadFileNameList;
        adbDataPackageList = new ArrayList<>();
        for (int i=0;i<downloadFileNameList.size();i++){
            data = new AdbDataPackage();
            data.setPathAndAuthority(downloadFileNameList.get(i).getTarget_path() + downloadFileNameList.get(i).getName()+Constants.ADB_PROTOCOL_AUTHORITY);
            data.setData(FileUtil.getFileStreamFromSDcard(Constants.MODEL_ROOT_PATH + downloadFileNameList.get(i).getName()));
            data.setProgress(100*(i/downloadFileNameList.size()));
            adbDataPackageList.add(data);
            L.i("setPushConfig name:"+downloadFileNameList.get(i).getTarget_path()+downloadFileNameList.get(i).getName());
        }
        L.i("升级文件个数："+adbDataPackageList.size());
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
                    setUIStatus("请用USB数据线连接设备与手机","设备未连接",false,false);
                    isUsbConnected = false;
                }
            }
        }
    };



    @Override
    public void onMessage(AdbMessage message) {
        if (MainApplication.ADB_MODEL==Constants.ADB_SHELL){
            if (null!=message.getDataString() && message.getDataString().contains(getLocalFileMD5())){
                L.i("message md5比对相等:"+message.getDataString());
                currentPushIndex = 0;
                //MD5值相等，文件升级成功
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getApplicationContext(),"升级成功！");
                        setUIStatus("升级成功，请拔掉数据线，并重启设备","升级成功",true,false);
                    }
                });

            }
        }
    }

    @Override
    public void deviceOnline(AdbDevice device) {
        Message m = Message.obtain(mHandler, MESSAGE_DEVICE_ONLINE);
        m.obj = device;
        mHandler.sendMessage(m);
    }

    @Override
    public AdbDataPackage generateData() {
        L.i("生成数据 开始传输："+currentPushIndex);
        adbDataPackage = adbDataPackageList.get(currentPushIndex);
        return adbDataPackage;
    }


    @Override
    public void executeCommandClose(int adbModel) {
        switch (adbModel){
            case Constants.ADB_PUSH:
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        L.i("测试 收到传输成功："+currentPushIndex);
                        try {
                            Thread.sleep(1000);
                            if(currentPushIndex < adbDataPackageList.size()-1){
                                currentPushIndex++;
                                MainApplication.ADB_MODEL = Constants.ADB_PUSH;
                                adbDevice.openSocket("sync:");
                            }else if (currentPushIndex==adbDataPackageList.size()-1){
                                //同步sync
                                MainApplication.ADB_MODEL = Constants.ADB_SHELL;
                                adbDevice.openSocket("shell:exec sync");
                                sleep(1000);
                                //比对文件的MD5
                                compareFileMD5();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case Constants.ADB_SHELL:

                break;
            default:
                break;
        }
    }

    private void compareFileMD5(){
        OtaConfigBean.UpgradeFile upgradeFile = downloadFileNameList.get(downloadFileNameList.size()-1);
        MainApplication.ADB_MODEL = Constants.ADB_SHELL;
        adbDevice.openSocket("shell:exec md5sum "+ upgradeFile.getTarget_path()+upgradeFile.getName());
    }

    private String getLocalFileMD5(){
        File sdCardFile = Environment.getExternalStorageDirectory();
        File file = new File(sdCardFile, Constants.MODEL_ROOT_PATH + downloadFileNameList.get(downloadFileNameList.size()-1).getName());
        try {
            return MD5Util.getFileMD5String(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
        adbDevice = device;
        ToastUtil.showToast(getApplicationContext(),"设备连接成功！");
        isUsbConnected = true;
        if (isDownloadFileFinished){
            setUIStatus("设备连接成功，点击下方按钮开始升级，过程中请勿拔掉数据线","开始升级",true,false);
            status = Status.ALL_FINISHED;
        }else {
            //setUIStatus("设备连接成功，点击下方按钮开始升级，过程中请勿拔掉数据线","开始升级",true,false);
            status = Status.OFFLINE;
        }
    }

    private void setUIStatus(String tips,String btText,boolean isButtonClickable,boolean isLoadingStart){
        btNext.setClickable(isButtonClickable);
        btNext.setFocusable(isButtonClickable);
        btNext.setText(btText);
        tvTips.setText(tips);
        if (isButtonClickable){
            btNext.setBackground(getResources().getDrawable(R.drawable.floor_button_shape_selected));
        }else {
            btNext.setBackground(getResources().getDrawable(R.drawable.floor_button_shape));
        }
        if (isLoadingStart){
            qmLoadingView.setVisibility(View.VISIBLE);
            qmLoadingView.start();
        }else {
            qmLoadingView.stop();
            qmLoadingView.setVisibility(View.GONE);
        }

    }

    //OTA升级文件下载完成
    @Override
    public void onCompleted(List<OtaConfigBean.UpgradeFile> downloadFileNameList) {
        setPushConfig(downloadFileNameList);
        isDownloadFileFinished = true;
        if (isUsbConnected){
            setUIStatus("设备连接成功，点击下方按钮开始升级，过程中请勿拔掉数据线","开始升级",true,false);
            status = Status.ALL_FINISHED;
        }else {
            setUIStatus("有固件可以更新，请连接设备开始在线升级","请连接设备",false,false);
            status = Status.NO_USB;
        }
    }

    @Override
    public void onDownloadError(Throwable e) {
        //TODO 下载文件失败，重新下载
        setUIStatus("下载失败，请检查网络后重试","检查更新",true,false);
        status = Status.OFFLINE;
    }


    //获取ota升级配置文件
    @Override
    public void onGetOTAConfig(OtaConfigBean otaConfigBean) {
        L.i("ota升级配置文件："+otaConfigBean.toString());
        if (otaConfigBean.isNeedToUpgrade()){
            //下载升级文件
            List<OtaConfigBean.UpgradeFile> upgradeFileList =  otaConfigBean.getUpgradeFileList();
            new OTADownloadModel(this).downloadAllFile(upgradeFileList.toArray(new OtaConfigBean.UpgradeFile[upgradeFileList.size()]));
        }else {
            //不需要OTA升级
            setUIStatus("当前没有可升级的固件","检查更新",true,false);
            status = Status.OFFLINE;
        }
    }

    @Override
    public void onCheckOTAConfigError() {
        setUIStatus("下载失败，请检查网络后重试","检查更新",true,false);
        status = Status.OFFLINE;
    }
}
