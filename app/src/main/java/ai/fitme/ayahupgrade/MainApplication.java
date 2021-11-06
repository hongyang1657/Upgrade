package ai.fitme.ayahupgrade;

import android.app.Application;
import java.util.ArrayList;
import java.util.List;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SharedPreferencesUtils;
import cn.bmob.v3.Bmob;

import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.OPENDOOR_SERIAL;


public class MainApplication extends Application {

    public static List<String> floorList;
    public static List<String> dataFloorList;

    //语音取消楼层
    public static boolean is_cancel_open = false;
    //呼救报警
    public static boolean is_alarm_open = false;
    //迎宾
    public static boolean is_welcome_open = false;
    //迎宾开门延时
    public static String GREET_OPEN_DOOR_DELAY = "2";
    //无服务提醒
    public static boolean is_nofloor_server_open = false;
    //音量 10 - 90
    public static String VOLUME_CONFIG = "90";
    //登记楼层应答
    public static String REGISTER_ANSWER = Constants.REGISTER_ANSWER_LIST[2];
    //是否答疑
    public static float ANSWER_QUESTION = Constants.OPEN_ANSWER_QUESTION;
    //自定义tts
    public static byte[] customTTs = null;
    //存储注册和登录activity里关于注册模块是否处于输入验证码页面，true表示处于输入验证码，false表示处于输入手机号
    private boolean nowIsInputIdCode;

    public static String WAKE_UP_SOUND = Constants.WAKEUP_SOUND_DEFAULT;
    public static String OPEN_DOOR_INDEX = Constants.DEFAULT_OPEN_DOOR_INDEX;
    public static String CLOSE_DOOR_INDEX = Constants.DEFAULT_CLOSE_DOOR_INDEX;
    public static String CANCEL_MODEL = Constants.CANCEL_TWICE;      //默认长按两次取消
    public static String TIME_MULTIPEL = "01";  //长按的时间倍数，默认01
    public static String TIME_INTERVAL = "600";       //时间间隔，默认600
    public static byte[] FILE_TIME = new byte[4];

    public static String TIME_INTERVAL_RELAY = "06";   //继电器协议的间隔时间，16进制，单位为0.1秒 （eg：06表示 6 * 0.1 = 600毫秒）
    public static String TIME_MULTIPEL_RELAY = "0A";   //继电器协议的时长倍数 ，16进制，单位为0.1秒


    public static int ADB_MODEL = Constants.ADB_PUSH;
    public static int PROTOCOL_VERSION = Constants.PROTOCOL_RELAY;       //1001:继电器版本  1002：光耦合版本

    public static int configPushNum = 0;

    public static int currentPage = 0;

    private static MainApplication app;

    //当前的用户信息
    private ProfileInfo user;

    //获取手机屏幕宽度
    private int width;
    //获取手机屏幕高度
    private int height;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initBmob();
        floorList = new ArrayList<>();
        dataFloorList = new ArrayList<>();
        //初始化app版本
        String channel = Constants.getChannel(this);
        L.i("当前版本："+channel);
        switch (channel){
            case "relay":
                PROTOCOL_VERSION = Constants.PROTOCOL_RELAY;
                break;
            case "optical":
                PROTOCOL_VERSION = Constants.PROTOCOL_OPTICAL;
                break;
            case "samsung":
                PROTOCOL_VERSION = Constants.PROTOCOL_SAMSUNG;
                break;
            default:
                PROTOCOL_VERSION = Constants.PROTOCOL_RELAY;
                break;
        }

        //根据缓存文件判断app供应商版本
        String serial = SharedPreferencesUtils.getInstance(getApplicationContext()).getStringValueByKey(OPENDOOR_SERIAL);
        if (null!=serial && serial.length()==8){      //光耦合
            PROTOCOL_VERSION = Constants.PROTOCOL_OPTICAL;
        }else if (null!=serial && serial.length()==10){      //继电器
            PROTOCOL_VERSION = Constants.PROTOCOL_RELAY;
        }else if (null!=serial && serial.length()==2){      //三星
            PROTOCOL_VERSION = Constants.PROTOCOL_SAMSUNG;
        }
        L.i("当前版本："+PROTOCOL_VERSION);
    }

    private void initBmob(){
        Bmob.initialize(this,"06530225ea5510f746d1e86f5c2fac55");
    }

    public static MainApplication getApp() {
        return app;
    }

    public ProfileInfo getUser() {
        return user;
    }

    public void setUser(ProfileInfo user) {
        this.user = user;
    }

    public boolean getNowIsInputIdCode() {
        return nowIsInputIdCode;
    }

    public void setNowIsInputIdCode(boolean nowIsInputIdCode) {
        this.nowIsInputIdCode = nowIsInputIdCode;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
