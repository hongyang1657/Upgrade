package ai.fitme.ayahupgrade.ui.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.Toast;
import java.nio.ByteBuffer;
import ai.fitme.ayahupgrade.adb.AdbDevice;
import ai.fitme.ayahupgrade.adb.AdbMessage;
import ai.fitme.ayahupgrade.adb.impl.AdbMessageListener;
import ai.fitme.ayahupgrade.bean.AdbDataPackage;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.ToastUtil;

public abstract class AdbBaseActivity extends Activity implements AdbMessageListener {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    //usb
    public UsbManager mManager;
    public UsbDevice mDevice;
    public UsbDeviceConnection mDeviceConnection;
    public UsbInterface mInterface;
    public AdbDevice mAdbDevice;
    public AdbDevice adbDevice;

    public abstract void deviceOnline(AdbDevice device);

    /**
     * adb push 按照格式设置数据
     * @return
     */
    public abstract AdbDataPackage generateData();

    /**
     * adb指令 收到A_CLSE信号
     */
    public abstract void executeCommandClose(int adbModel);

    /**
     * pull成功后 返回数据
     * @param byteBuffer
     * @param capacity
     */
    public abstract void getFileData(ByteBuffer byteBuffer,int capacity);

    /**
     * adb pull 返回需要拉取的目标文件路径
     * @return
     */
    @Override
    public String getPullFileName() {
        return null;
    }

    /**
     * 获取adb shell命令返回的message
     * @param message
     */
    @Override
    public void onMessage(AdbMessage message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbPermissionActionReceiver);
        setAdbInterface(null, null);
    }

    /**
     * 获得 usb 权限
     */
    public void openUsbDevice(){
        tryGetUsbPermission();
    }



    public void tryGetUsbPermission(){
        mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbPermissionActionReceiver, filter);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        for (final UsbDevice usbDevice : mManager.getDeviceList().values()) {
            if(mManager.hasPermission(usbDevice)){
                afterGetUsbPermission(usbDevice);
            }else{
                mManager.requestPermission(usbDevice, mPermissionIntent);
            }
        }
    }

    public void afterGetUsbPermission(UsbDevice usbDevice){
        doYourOpenUsbDevice(usbDevice);
    }

    public void doYourOpenUsbDevice(UsbDevice usbDevice){
        // check for existing devices
        for (UsbDevice device :  mManager.getDeviceList().values()) {
            UsbInterface intf = findAdbInterface(device);
            if (setAdbInterface(device, intf)) {
                break;
            }
        }
    }

    public final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(null != usbDevice){
                            afterGetUsbPermission(usbDevice);
                        }
                    }
                    else {
                        Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    // searches for an adb interface on the given USB device
    public static  UsbInterface findAdbInterface(UsbDevice device) {
        int count = device.getInterfaceCount();
        for (int i = 0; i < count; i++) {
            UsbInterface intf = device.getInterface(i);
            if (intf.getInterfaceClass() == 255 && intf.getInterfaceSubclass() == 66 &&
                    intf.getInterfaceProtocol() == 1) {
                return intf;
            }
        }
        return null;
    }

    public boolean setAdbInterface(UsbDevice device, UsbInterface intf) {
        if (mDeviceConnection != null) {
            if (mInterface != null) {
                mDeviceConnection.releaseInterface(mInterface);
                mInterface = null;
            }
            mDeviceConnection.close();
            mDevice = null;
            mDeviceConnection = null;
        }

        if (device != null && intf != null) {
            UsbDeviceConnection connection = mManager.openDevice(device);
            if (connection != null) {
                L.i("open succeeded");
                if (connection.claimInterface(intf, false)) {
                    L.i("claim interface succeeded");
                    mDevice = device;
                    mDeviceConnection = connection;
                    mInterface = intf;
                    mAdbDevice = new AdbDevice(this, mDeviceConnection, intf,this);
                    L.i("call start");
                    mAdbDevice.start();
                    return true;
                } else {
                    connection.close();
                }
            } else {
                L.i("open failed");
            }
        }

        if (mDeviceConnection == null && mAdbDevice != null) {
            mAdbDevice.stop();
            mAdbDevice = null;
        }
        return false;
    }
}
