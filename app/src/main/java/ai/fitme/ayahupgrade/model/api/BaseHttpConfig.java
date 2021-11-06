package ai.fitme.ayahupgrade.model.api;



/**
 * Created by zzy on 2018/6/29.
 */

public class BaseHttpConfig {
    //对话管理器
    public static final String BASE_URL_DM = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/dialogmanage/";
    //消息推送服务
    public static final String BASE_URL_NC = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.COMMON_INSTANCE_NAME + "/notification/";
    //对话策略服务
    public static final String BASE_URL_DP = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/dialogpolicy/";
    //订单与支付
    public static final String BASE_URL_PAY = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.COMMON_INSTANCE_NAME + "/fitmepay/";
    //认证服务
    public static final String BASE_URL_AT = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.COMMON_INSTANCE_NAME + "/authentication/";
    //用户中心
    public static final String BASE_URL_UC = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.COMMON_INSTANCE_NAME + "/usercenter/";
    //打车微服务
    public static final String BASE_URL_AS = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/car/";
    //智能硬件
    public static final String BASE_URL_SD = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/smarthardwarecontrol/";
    //H5url获取
    public static final String BASE_URL_H5 = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/apppresentation/";
    //音乐播放
    public static final String MUSIC_PLAYER = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/contentservice/";
    //智能场景联动
    public static final String BASE_URL_SS = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/smartscen/";
    //闹钟管理
    public static final String BASE_URL_AC = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/alarmclock/";
    //提醒管理
    public static final String BASE_URL_RM = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/remind/";
    //备忘录
    public static final String BASE_URL_MB = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/memo/";
    //图像上传
    public static final String BASE_URL_UP = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.COMMON_INSTANCE_NAME + "/resources/";
    //app版本信息
    public static final String BASE_URL_VG = "http://" + HttpConstant.DOMAIN_NAME + ":" + HttpConstant.DOMAIN_PORT + "/" + HttpConstant.INSTANCE_NAME + "/apprepository/";

}
