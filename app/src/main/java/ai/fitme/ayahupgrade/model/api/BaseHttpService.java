package ai.fitme.ayahupgrade.model.api;

import java.util.Map;

import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.bean.CheckToken;
import ai.fitme.ayahupgrade.bean.DeviceGet;
import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.bean.TokenInfo;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by yml on 2017/10/11.
 */

public interface BaseHttpService {

    //验证手机号是否被占用
    @POST(HttpConstant.METHOD_UC_IS_OCCUPIED_MOBILE)
    Observable<IsOccupiedMobile> isOccupiedMobile(@Query("api_key") String apiKey,
                                                  @Query("timestamp") String timestamp,
                                                  @Query("sign") String sign,
                                                  @Query("version") String version,
                                                  @Query("method") String method,
                                                  @Body Map<String, Object> http_body);

    //注册新账号
    @POST(HttpConstant.METHOD_UC_CREATE)
    Observable<AccountCreate> createAccount(@Query("api_key") String apiKey,
                                            @Query("timestamp") String timestamp,
                                            @Query("sign") String sign,
                                            @Query("version") String version,
                                            @Query("method") String method,
                                            @Body Map<String, Object> http_body);

    //给指定手机号发送验证码
    @POST(HttpConstant.METHOD_UC_TO_MOBILE_VERIFY_CODE_CREATE)
    Observable<Status> toMobileVerifyCode(@Query("api_key") String apiKey,
                                          @Query("timestamp") String timestamp,
                                          @Query("sign") String sign,
                                          @Query("version") String version,
                                          @Query("method") String method,
                                          @Body Map<String, Object> http_body);

    //验证用户手机
    @POST(HttpConstant.METHOD_UC_VERIFY_MOBILE)
    Observable<Status> verifyMobile(@Query("api_key") String apiKey,
                                    @Query("timestamp") String timestamp,
                                    @Query("sign") String sign,
                                    @Query("version") String version,
                                    @Query("method") String method,
                                    @Body Map<String, Object> http_body);

    //根据手机号获取user_id
    @POST(HttpConstant.METHOD_UC_GET_USER_ID_BY_MOBILE)
    Observable<AccountCreate> getUserIdByMobile(@Query("api_key") String apiKey,
                                                @Query("timestamp") String timestamp,
                                                @Query("sign") String sign,
                                                @Query("version") String version,
                                                @Query("method") String method,
                                                @Body Map<String, Object> http_body);

    //登录功能，即获取用户信息功能
    @POST(HttpConstant.METHOD_AT_TOKEN)
    Observable<TokenInfo> token(@Query("api_key") String apiKey,
                                @Query("timestamp") String timestamp,
                                @Query("sign") String sign,
                                @Query("version") String version,
                                @Query("method") String method,
                                @Body Map<String, Object> http_body);

    //鉴定访问令牌是否失效
    @POST(HttpConstant.METHOD_AT_CHECK_TOKEN)
    Observable<CheckToken> checkToken(@Query("api_key") String apiKey,
                                      @Query("timestamp") String timestamp,
                                      @Query("sign") String sign,
                                      @Query("version") String version,
                                      @Query("method") String method,
                                      @Body Map<String, Object> http_body);

    //登录功能，即获取用户信息功能
    @POST(HttpConstant.METHOD_UC_PROFILE)
    Observable<ProfileInfo> profile(@Query("api_key") String apiKey,
                                    @Query("timestamp") String timestamp,
                                    @Query("sign") String sign,
                                    @Query("version") String version,
                                    @Query("method") String method,
                                    @Body Map<String, Object> http_body);


    //单独校验验证码
    @POST(HttpConstant.METHOD_UC_VERIFY_CODE_CHECK)
    Observable<Status> checkVerifyCode(@Query("api_key") String apiKey,
                                       @Query("timestamp") String timestamp,
                                       @Query("sign") String sign,
                                       @Query("version") String version,
                                       @Query("method") String method,
                                       @Body Map<String, Object> http_body);

    //修改密码
    @POST(HttpConstant.METHOD_UC_CHANGE_PASSWORD)
    Observable<Status> changePassword(@Query("api_key") String apiKey,
                                      @Query("timestamp") String timestamp,
                                      @Query("sign") String sign,
                                      @Query("version") String version,
                                      @Query("method") String method,
                                      @Body Map<String, Object> http_body);

    // 上传交互设备
    @POST(HttpConstant.METHOD_UC_DEVICE_PUT)
    Observable<Status> deviceInfoUpload(@Query("api_key") String apiKey,
                                        @Query("timestamp") String timestamp,
                                        @Query("sign") String sign,
                                        @Query("version") String version,
                                        @Query("method") String method,
                                        @Body Map<String, Object> http_body);


    //用户交互设备查询
    @POST(HttpConstant.METHOD_UC_DEVICE)
    Observable<DeviceGet> userDeviceGet(@Query("api_key") String apiKey,
                                        @Query("timestamp") String timestamp,
                                        @Query("sign") String sign,
                                        @Query("version") String version,
                                        @Query("method") String method,
                                        @Body Map<String, Object> http_body);

    //修改用户信息
    @POST(HttpConstant.METHOD_UC_PROFILE_PUT)
    Observable<Status> editUser(@Query("api_key") String apiKey,
                                @Query("timestamp") String timestamp,
                                @Query("sign") String sign,
                                @Query("version") String version,
                                @Query("method") String method,
                                @Body Map<String, Object> http_body);

}
