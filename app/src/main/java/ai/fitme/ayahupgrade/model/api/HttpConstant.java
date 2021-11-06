package ai.fitme.ayahupgrade.model.api;

/**
 * Created by yml on 2017/10/11.
 */

public class HttpConstant {

    public static final String API_VERSION = "2.0";

    public static final String COMMON_INSTANCE_NAME = "production";
    public static final String DOMAIN_NAME = "app.found-ai.cn";
    public static final String DOMAIN_PORT = "9999";
    public static final String INSTANCE_NAME = "production";

    ////////////////////////////////////////////////////////// 用户中心 /////////////////////////////////////////////////////////////////////////
    //     验证手机号是否已被占用
    public static final String METHOD_UC_IS_OCCUPIED_MOBILE = "account/is_occupied_mobile";
    //    创建用户
    public static final String METHOD_UC_CREATE = "account/create";
    //    请求向指定手机发送用于验证手机的验证码
    public static final String METHOD_UC_TO_MOBILE_VERIFY_CODE_CREATE = "account/to_mobile_verify_code/create";
    //    验证用户手机
    public static final String METHOD_UC_VERIFY_MOBILE = "account/verify_mobile";
    //校验验证码(单独校验验证码)
    public static final String METHOD_UC_VERIFY_CODE_CHECK = "verify_code/check";
    //    修改密码
    public static final String METHOD_UC_CHANGE_PASSWORD = "account/change_password";
    //    向指定手机发送用于重置密码的验证码
    public static final String METHOD_UC_RESET_PASSWORD_VERIFY_CODE_CREATE = "account/reset_password_verify_code/create";
    //    重置密码
    public static final String METHOD_UC_RESET_PASSWORD = "account/reset_password";
    //    第三方登录
    public static final String METHOD_UC_THIRD_PARTY_LOGIN = "account/third_party_login";
    //    获取用户自己的信息
    public static final String METHOD_UC_PROFILE = "profile";
    //    设置用户自己的信息
    public static final String METHOD_UC_PROFILE_PUT = "profile/put";
    //    按手机号查询user_id
    public static final String METHOD_UC_GET_USER_ID_BY_MOBILE = "get_user_id_by_mobile";
    //    交互设备设置
    public static final String METHOD_UC_DEVICE_PUT = "device/put";
    //    提交反馈
    public static final String METHOD_UC_FEEDBACK_CREATE = "feedback/create";
    //    分享
    public static final String METHOD_UC_SHARE_CREATE = "share/create";
    //    终端上传异常日志
    public static final String METHOD_UC_ERROR_LOG_CREATE = "error_log/create";
    //    查询手机登录状态
    public static final String METHOD_UC_MOBILE_LOGIN = "mobile_login";
    //    绑定其他id(向用户中心上传设备的MAC地址)
    public static final String METHOD_UC_OTHER_ID_BIND_CREATE = "other_id/bind/create";
    //    上传用户图像
    public static final String METHOD_FILE_UPLOAD = "file/upload";


    ////////////////////////////////////////////////////////// 认证服务 /////////////////////////////////////////////////////////////////////////
    //    获取访问令牌
    public static final String METHOD_AT_TOKEN = "token";
    //    鉴定访问令牌
    public static final String METHOD_AT_CHECK_TOKEN = "check_token";

    ////////////////////////////////////////////////////////// 消息推送服务 /////////////////////////////////////////////////////////////////////////
    //    推送消息给用户
    public static final String METHOD_NC_MSG_TO_USER_CREATE = "message/to_user/create";
    //    获取发给消费者的新消息
    public static final String METHOD_NC_MSG_TO_USER = "message/to_user";
    //    确认消息已收到
    public static final String METHOD_NC_MSG_ARRIVED_CREATE = "message_arrived/create";

    ////////////////////////////////////////////////////////// 对话管理 /////////////////////////////////////////////////////////////////////////
    //    新增来自用户的消息
    public static final String METHOD_DM_MSG_FROM_USER_CREATE = "message/from_user/create";


    ////////////////////////////////////////////////////////// 微服务 /////////////////////////////////////////////////////////////////////////
    //    二级打车界面的查询状态接口
    public static final String METHOD_MS_TAKE_QUERY_STATUS = "/take/query_status";
    //    确认打车
    public static final String METHOD_MS_TAKE_CONFIRM = "/take/confirm_create_request";
    //    取消打车
    public static final String METHOD_MS_CANCEL_CALL = "/take/order_cancel/dm_call";
    //    根据订单id查询订单详情
    public static final String METHOD_MS_ORDER_BY_ID = "order_by_id";
    //    支付申请
    public static final String METHOD_MS_PAY_REQUEST_CREATE = "pay_request/create";
    //    支付结果回掉给后台的接口
    public static final String METHOD_MS_PAY_CREATE = "pay/create";
    //    根据订单状态查询订单(用于app查询用户未支付或已支付的订单)
    public static final String METHOD_MS_ORDER_BY_STATUS = "order_by_status";
    //    根据用户id查询用户信用预授权状态
    public static final String METHOD_MS_FUND_AUTH_BY_USER_ID = "fund_auth_by_user_id";
    //    预授权申请
    public static final String METHOD_MS_FUND_AUTH_REQUEST_CREATE = "fund_auth_request/create";
    //    预授权结果回传给后台
    public static final String METHOD_MS_FUND_AUTH_RESULT_CREATE = "fund_auth_result/create";

    ////////////////////////////////////////////////////智能硬件改版相关接口/////////////////////////////////////////////////////////////////////
    //     获取所有房间
    public static final String METHOD_SD_ROOM_ALL = "room/all";
    //     获取可筛选的房间
    public static final String METHOD_SD_ROOM_GET_FILTER = "room/get_filter";
    //     获取品牌列表
    public static final String METHOD_SD_BRAND_LIST = "brand/list";
    //     获取品牌介绍和支持设备
    public static final String METHOD_SD_BRAND_INFO_AND_SUPPORT_DEVICE = "brand/introduction_devices";
    //     上传家庭组
    public static final String METHOD_SD_FAMILY_UPLOAD = "family/upload";
    //     切换家庭
    public static final String METHOD_SD_FAMILY_SELECT = "family/select";
    //     获取当前家庭组
    public static final String METHOD_SD_FAMILY_CURRENT = "family/current";
    //     同步设备
    public static final String METHOD_SD_DEVICE_SYNCHRONOUS = "device/synchronous";
    //     获取属性设置中的所有设备名选择列表
    public static final String METHOD_SD_DEVICE_NAME_LIST = "device/device_name_list";
    //     设备编辑后上传服务器的接口
    public static final String METHOD_SD_DEVICE_EDIT = "device/edit";
    //     查询设备配置的接口
    public static final String METHOD_SD_DEVICE_GET = "device/get";
    //     删除设备接口
    public static final String METHOD_SD_DEVICE_DELETE = "device/delete";
    //     上传绑定品牌的账号
    public static final String METHOD_SD_ACCOUNT_UPLOAD = "account/upload";
    //     解除账号绑定
    public static final String METHOD_SD_ACCOUNT_REMOVE = "account/delete";
    //     用户设备列表
    public static final String METHOD_SD_DEVICE_USER_DEVICES = "device/user_devices";
    //     获取广告列表
    public static final String METHOD_SD_ADVERT_LIST = "advert/list";
    //     获取账号
    public static final String METHOD_SD_ACCOUNT_GET = "account/get";
    //     设备控制后的状态上传
    public static final String METHOD_SD_CONTROL_STATE_UPLOAD = "control/state/upload";
    //     清除上下文的测试接口
    public static final String METHOD_DM_CLOSE_DIALOG = "close_dialog";
    //     环境信息上传
    public static final String METHOD_SD_ENVIRONMENT_STATE_UPLOAD = "environment/state/upload";
    //     解除账号绑定
    public static final String METHOD_SD_ACCOUNT_RELIEVE = "account/relieve";
    //     获取登入H5的链接
    public static final String METHOD_SD_ACCOUNT_ENTER_URL = "account/enter_url";

    ////////////////////////////////////////////////////历史消息回显//////////////////////////////////////////////////////////

    //     历史消息获取
    public static final String METHOD_NC_MESSAGE = "message";

    ////////////////////////////////////////////////////智能音箱//////////////////////////////////////////////////////////

    //     获取指定交互设备的配置信息
    public static final String METHOD_UC_DEVICE_CONFIG_GET = "device_config/get";
    //     设置指定交互设备的配置节点信息
    public static final String METHOD_UC_DEVICE_CONFIG_PUT = "device_config/put";
    //     检查当前设备的配置信息是否有更新
    public static final String METHOD_UC_DEVICE_CONFIG_CHEAK = "device_config/cheak";
    //     检查音箱设备信息是否被修改
    public static final String METHOS_UC_DEVICE = "device";
    //     解绑音箱
    public static final String METHOD_UC_DEVICE_DELETE = "device/delete";
    //     重新配网
    public static final String METHOD_UC_DEVICE_REBIND = "device/rebind";
    //     查询绑定状态结果
    public static final String METHOD_UC_DEVICE_BIND_STATUS = "device/bind/status";
    //     确定交互设备已经绑定成功
    public static final String METHOD_UC_DEVICE_BIND_SUCCESS = "device/bind/success";

    ///////////////////////////////////////////////////H5Url获取//////////////////////////////////////////////////////////

    //     获取侧栏的h5url
    public static final String METHOD_H5_SIDE = "side";
    //     用户交互设备查询
    public static final String METHOD_UC_DEVICE = "device";
    //     获取服务协议隐私条款
    public static final String METHOD_APP_PRESENTATION = "agreement";

    /////////////////////////////////////////////////// 音乐播放 //////////////////////////////////////////////////////

    //    播放器获取下一个节目
    public static final String METHOD_UC_PLAYER_NEXT = "player/next";
    //    播放器获取上一个节目
    public static final String METHOD_UC_PLAYER_PREVIOUS = "player/previous";
    //    暂停播放
    public static final String METHOD_UC_PLAYER_PAUSE = "player/pause";
    //    继续播放
    public static final String METHOD_UC_PLAYER_RESUME = "player/resume";
    //    获取当前播放内容
    public static final String METHOD_UC_PLAYER_CONTENT = "player/content";
    //    上传播放器信息状态
    public static final String METHOD_UC_PLAYER_REPORT_STATUS = "player/report/status";

    /////////////////////////////////////////////////// 娱乐收藏 //////////////////////////////////////////////////////

    //    获取收藏列表
    public static final String METHOD_UC_AMUSEMENT_COLLECTION_LIST = "/amusement/collection/list";
    //    取消收藏
    public static final String METHOD_UC_AMUSEMENT_COLLECTION_DEL = "/amusement/collection/del";

    /////////////////////////////////////////////////// 智能场景联动 //////////////////////////////////////////////////////

    //   查询个人场景
    public static final String METHOD_SS_SCENE_QUERY = "scene/query";
    //   查询场景详情
    public static final String METHOD_SS_SCENE_DETAIL = "scene/detail";
    //   编辑场景
    public static final String METHOD_SS_SCENE_EDIT = "scene/edit";
    //   添加场景
    public static final String METHOD_SS_SCENE_ADD = "scene/add";
    //   删除场景
    public static final String METHOD_SS_SCENE_DELETE = "scene/delete";
    //   获取动作类型
    public static final String METHOD_SS_ACTION_ALL = "action/all";
    //   获取动作模板
    public static final String METHOD_SS_ACTION_TEMPLATE = "action/template";
    //   执行场景
    public static final String METHOD_SS_SCENE_ACTIVATE = "scene/activate";
    //   获取用户自定义配置
    public static final String METHOD_UC_DEVICE_CONFIG_USER_CONFIG_GET = "device_config/user_config/get";
    //   校验speech
    public static final String METHOD_SS_SCENE_VERIFY = "scene/verify";

    //////////////////////////////////////////////////// 自定义问答 //////////////////////////////////////////////////////

    //   获取精选问答
    public static final String METHOD_SS_DIALOGUE_PICK_LIST = "dialogue/pick/list";
    //   获取精选问答详情
    public static final String METHOD_SS_DIALOGUE_PICK_DETAIL = "dialogue/pick/detail";
    //   添加精选问答到用户
    public static final String METHOD_SS_DIALOGUE_CUSTOM_ADD_PICK = "dialogue/custom/add/pick";
    //   获取自定义问答列表
    public static final String METHOD_SS_DIALOGUE_CUSTOM_LIST = "dialogue/custom/list";
    //   获取自定义问答详情
    public static final String METHOD_SS_DIALOGUE_CUSTOM_DETAIL = "dialogue/custom/detail";
    //   删除自定义问答
    public static final String METHOD_SS_DIALOGUE_CUSTOM_DELETE = "dialogue/custom/delete";
    //   添加自定义问答
    public static final String METHOD_SS_DIALOGUE_CUSTOM_ADD = "dialogue/custom/add";
    //   编辑自定义问答
    public static final String METHOD_SS_DIALOGUE_CUSTOM_EDIT = "dialogue/custom/edit";
    //   校验question合法性
    public static final String METHOD_SS_DIALOGUE_CUSTOM_VERIFY = "dialogue/custom/verify";

    /////////////////////////////////////////////////// 反馈评价 //////////////////////////////////////////////////////

    //反馈评价
    public static final String METHOD_DM_FEEDBACK_EVALUATE_PUT = "feedback/evaluate/put";
    //纠错
    public static final String METHOD_DM_FEEDBACK_CORRECT_PUT = "feedback/correct/put";

    /////////////////////////////////////////////////// 闹钟管理 //////////////////////////////////////////////////////

    //  查看闹钟
    public static final String METHOD_AC_ALARM_CLOCK_SEARCH = "alarm_clock/search";
    //  删除闹钟
    public static final String METHOD_AC_ALARM_CLOCK_DELETE = "alarm_clock/delete";
    //  关闭闹钟
    public static final String METHOD_AC_ALARM_CLOCK_UPDATE = "alarm_clock/update";
    //  编辑闹钟
    public static final String METHOD_AC_ALARM_CLOCK_EDIT = "alarm_clock/edit";

    /////////////////////////////////////////////////// 提醒管理 //////////////////////////////////////////////////////

    //  查看提醒
    public static final String METHOD_RM_REMIND_SEARCH = "remind/search";
    //  删除提醒
    public static final String METHOD_RM_REMIND_DELETE = "remind/delete";
    //  修改提醒
    public static final String METHOD_RM_REMIND_UPDATE = "remind/update";

    /////////////////////////////////////////////////// 备忘录管理 ////////////////////////////////////////////////////

    // 查看备忘录
    public static final String METHOD_MB_MEMO_SEARCH = "memo/search";
    // 编辑备忘录
    public static final String METHOD_MB_MEMO_UPDATE = "memo/update";
    // 删除备忘录
    public static final String METHOD_MB_MEMO_DELETE = "memo/delete";

    /////////////////////////////////////////////////// pp版本升级管理 ////////////////////////////////////////////////

    //查询App版本信息
    public static final String METHOD_APP_VERSION_GET = "app/version_info/get";
}
