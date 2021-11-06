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

package ai.fitme.ayahupgrade.adb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.adb.impl.AdbMessageListener;
import ai.fitme.ayahupgrade.bean.AdbDataPackage;
import ai.fitme.ayahupgrade.ui.activity.AdbBaseActivity;
import ai.fitme.ayahupgrade.utils.BytesUtil;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.L;

import static ai.fitme.ayahupgrade.adb.AdbMessage.MAX_PAYLOAD;


/* This class represents an adb socket.  adb supports multiple independent
 * socket connections to a single device.  Typically a socket is created
 * for each adb command that is executed.
 */
public class AdbSocket {

    private final AdbDevice mDevice;
    private final int mId;
    private int mPeerId;

    private int preId = 0;
    private AdbBaseActivity mActivity;
    private AdbMessageListener listener;
    private int capacity = 0;
    private ByteBuffer mDataBuffer = null;

    public AdbSocket(AdbDevice device, int id, AdbBaseActivity mActivity) {
        mDevice = device;
        mId = id;
        this.mActivity = mActivity;
    }

    public AdbSocket(AdbDevice device, int id, AdbBaseActivity mActivity, AdbMessageListener listener) {
        mDevice = device;
        mId = id;
        this.mActivity = mActivity;
        this.listener = listener;
    }

    public int getId() {
        return mId;
    }

    public boolean open(String destination) {
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_OPEN, mId, 0, destination);
        if (! message.write(mDevice)) {
            return false;
        }

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    private int bigFileOff = 0;

    private int flag = 0;
    public void handleMessage(AdbMessage message) {
        listener.onMessage(message);

        switch (message.getCommand()) {
            case AdbMessage.A_OKAY:
                mPeerId = message.getArg0();
                //L.i("handleMessage A_OKAY peeId:"+mPeerId);
                synchronized (this) {
                    notify();
                }
                /**
                 * -----------------------------pull--------------------------------
                 */
                if (MainApplication.ADB_MODEL== Constants.ADB_PULL){
                    String filePath = listener.getPullFileName();
                    L.i("filePath:"+filePath);
                    if (flag==0){
                        byte[] recv = new byte[]{'R','E','C','V',0,0,0,0};
                        recv[4] = (byte) filePath.getBytes().length;   //低位存放路径长度
                        sendAWrite(recv);
                        flag = 1;
                    }else if (flag==1){
                        sendAWrite(filePath);
                        flag = 2;
                    }
                }
                /**
                 * ----------------------------push--------------------------------
                 */
                else if (MainApplication.ADB_MODEL==Constants.ADB_PUSH){
                    if (flag==0){
                        byte[] send = new byte[]{'S','E','N','D',0,0,0,0};
                        byte[] data = new byte[]{'D','A','T','A', 0,0,0,0};
                        AdbDataPackage adbDataPackage = mActivity.generateData();
                        //设置数据长度
                        int dataLength = adbDataPackage.getData().length;
                        //L.i("数据长度:"+dataLength);
                        data[4] = (byte) (dataLength%256);
                        data[5] = (byte) ((dataLength/256)%256);
                        data[6] = (byte) ((dataLength/256/256)%256);
                        data[7] = (byte) (dataLength/(256*256*256));
                        //L.i("数据长度data = "+ Arrays.toString(data));
                        //设置文件名长度
                        send[4] = (byte) adbDataPackage.getPathAndAuthority().getBytes().length;
                        L.i("send = "+ Arrays.toString(send));

                        // data长度超过MAX_PAYLOAD时需要分段发送
                        if (dataLength < MAX_PAYLOAD){
                            L.i("0发送的数据 :"+new String(byteMerger(byteMerger(byteMerger(send,adbDataPackage.getPathAndAuthority().getBytes()),data),adbDataPackage.getData())));
                            sendAWrite(byteMerger(byteMerger(byteMerger(send,adbDataPackage.getPathAndAuthority().getBytes()),data),adbDataPackage.getData()));
                            flag = 2;
                        }else {      //大文件push
                            if (bigFileOff + MAX_PAYLOAD/2 < dataLength){    //前面n-1帧
                                byte[] bytes = BytesUtil.subByte(adbDataPackage.getData(),bigFileOff,MAX_PAYLOAD/2);

                                //转成byte[]
                                data[4] = (byte) (MAX_PAYLOAD/2%256);
                                data[5] = (byte) ((MAX_PAYLOAD/2/256)%256);
                                data[6] = (byte) ((MAX_PAYLOAD/2/256/256)%256);
                                data[7] = (byte) (MAX_PAYLOAD/2/(256*256*256));
                                if (bigFileOff==0){       //第一帧，前面要带上sendnnnn
                                    sendAWrite(byteMerger(byteMerger(byteMerger(send,adbDataPackage.getPathAndAuthority().getBytes()),data),bytes));
                                    //L.i("1111bigFileOff:"+bigFileOff);
                                }else {
                                    sendAWrite(byteMerger(data,bytes));
                                    //L.i("2222bigFileOff:"+bigFileOff);
                                }
                                bigFileOff = bigFileOff + MAX_PAYLOAD/2;
                            }else {
                                //最后一帧
                                byte[] bytes = BytesUtil.subByte(adbDataPackage.getData(),bigFileOff,dataLength%(MAX_PAYLOAD/2));
                                //转成byte[]
                                data[4] = (byte) (dataLength%(MAX_PAYLOAD/2)%256);
                                data[5] = (byte) ((dataLength%(MAX_PAYLOAD/2)/256)%256);
                                data[6] = (byte) ((dataLength%(MAX_PAYLOAD/2)/256/256)%256);
                                data[7] = (byte) (dataLength%(MAX_PAYLOAD/2)/(256*256*256));
                                sendAWrite(byteMerger(data,bytes));
                                flag = 2;
                                L.i("3333bigFileOff:"+bigFileOff);

                            }

                        }

                    } else if (flag==2){
                        byte[] data = new byte[]{'D','O','N','E',0,0,0,0};
                        //获取时间
                        long currentTime = System.currentTimeMillis()/1000;
                        L.i("currentime "+currentTime);
                        data[4] = (byte) (currentTime%256);
                        data[5] = (byte) ((currentTime/256)%256);
                        data[6] = (byte) ((currentTime/256/256)%256);
                        data[7] = (byte) (currentTime/(256*256*256));
                        L.i("time:"+ Arrays.toString(data));
                        sendAWrite(data);
                        L.i("5发送的数据： DONE：");
                        flag = 3;
                        bigFileOff = 0;  //重置大文件计数
                    }else if (flag==3){
                        sendReady();
                        L.i("6发送的数据： OKAY：");
                        byte[] data = new byte[]{'Q','U','I','T',0,0,0,0};
                        sendAWrite(data);
                        L.i("7发送的数据： QUIT：");
                        flag = 4;
                    }else if (flag==4){
//                        sendCommand(AdbMessage.A_CLSE);
//                        L.i("发送的数据： A_CLSE：");
                    }

                }

                /**
                 * --------------------------stat-----------------------------
                 */
                else if (MainApplication.ADB_MODEL==Constants.ADB_STAT){
                    if (flag==0){
                        byte[] send = new byte[]{'S','T','A','T',0,0,0,0};
                        send[4] = (byte) Constants.TARGET_PATH_SERIAL_CONFIG.getBytes().length;
                        sendAWrite(send);
                        L.i("发送的数据：STAT");
                        flag = 1;
                    }else if (flag==1){
                        sendAWrite(Constants.TARGET_PATH_SERIAL_CONFIG.getBytes());
                        L.i("发送的数据：路径");
                        flag = 2;
                    }else if (flag==2){
                        sendReady();
                        L.i("发送的数据：OKAY");
                        flag = 3;
                    }
                }
                break;
            case AdbMessage.A_WRTE:
                sendReady();
                //判断收到stat数据
                if (MainApplication.ADB_MODEL==Constants.ADB_STAT){
                    if (message.getDataLength()==16&&message.getDataString().substring(0,4).equals("STAT")){
                        MainApplication.FILE_TIME[0] = message.getData().array()[12];
                        MainApplication.FILE_TIME[1] = message.getData().array()[13];
                        MainApplication.FILE_TIME[2] = message.getData().array()[14];
                        MainApplication.FILE_TIME[3] = message.getData().array()[15];
                        L.i("file_time = "+ Arrays.toString(MainApplication.FILE_TIME));
                    }
                }else if (MainApplication.ADB_MODEL==Constants.ADB_PULL){
                    //获取拉取的文件数据
                    //L.logE("data Length:"+message.getDataLength()+" 文件data："+ Arrays.toString(message.getData().array()));
                    if (message.getDataString().substring(0,4).equals("DATA")){
                        //初始化buffer长度,length为4个16进制byte
                        byte length_1 = message.getData().array()[4];
                        byte length_2 = message.getData().array()[5];
                        byte length_3 = message.getData().array()[6];
                        byte length_4 = message.getData().array()[7];
                        int length1 = length_1 & 0xff;
                        int length2 = length_2 & 0xff;
                        int length3 = length_3 & 0xff;
                        int length4 = length_4 & 0xff;
                        capacity = length4*256*256*256+length3*256*256+length2*256+length1+16;
                        L.i("pull 文件大小："+capacity);
                        mDataBuffer = ByteBuffer.allocate(capacity);
                        mDataBuffer.order(ByteOrder.LITTLE_ENDIAN);

                    }
                    //获取数据长度
                    L.i("getDataLength:"+message.getDataLength());
                    for (int i=0;i<message.getDataLength();i++){
                        mDataBuffer.put(message.getData().get(i));
                    }
                    //数据传输完成
                    if (message.getDataString().contains("DONE")){
                        L.i("ADB流程 收到 DONE");
                        byte[] quit = new byte[]{'Q','U','I','T',0,0,0,0};
                        sendAWrite(quit);
                    }
                }
                break;
            case AdbMessage.A_CLSE:
                sendCommand(AdbMessage.A_CLSE);
                L.i("收到A_CLSE");
                if (MainApplication.ADB_MODEL==Constants.ADB_PUSH){
                    //push成功后sync一下
                    mActivity.executeCommandClose(MainApplication.ADB_MODEL);
                }else if (MainApplication.ADB_MODEL==Constants.ADB_PULL){
                    //pull成功后 返回数据
                    mActivity.getFileData(mDataBuffer,capacity);
                    flag = 0;
                }else if (MainApplication.ADB_MODEL==Constants.ADB_SHELL){
                    //结束了一个shell命令,可能会调用多次
                    mActivity.executeCommandClose(MainApplication.ADB_MODEL);
                }
                break;
        }
    }

    private void sendReady() {
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_OKAY, mId, mPeerId++);
        message.write(mDevice);
    }

    public void sendAWrite(byte[] data){
        preId = mPeerId;
        //L.i("handleMessage preId:"+preId);
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_WRTE, mId, mPeerId++,data);
        message.write(mDevice);
    }

    public void sendAWrite(String data){
        preId = mPeerId;
        //L.i("handleMessage preId:"+preId);
        AdbMessage message = new AdbMessage();
        message.set(AdbMessage.A_WRTE, mId, mPeerId++,data);
        message.write(mDevice);
    }

    private void sendCommand(int command) {
        AdbMessage message = new AdbMessage();
        message.set(command, mId, mPeerId++);
        message.write(mDevice);
    }

    public int getUnsignedByte (byte data){      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data&0x0FF ;
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

}
