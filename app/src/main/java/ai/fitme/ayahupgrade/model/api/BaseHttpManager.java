package ai.fitme.ayahupgrade.model.api;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yml on 2017/10/11.
 */

public class BaseHttpManager {
    //okhttp客户端
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)//连接超时
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "application/json, charset=UTF-8")
                            .addHeader("User-Agent", "android")
                            .build();
                    return chain.proceed(request);
                }
            })
//            .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//             @Override
//             public void log(String message) {
//                 Log.e("retrofit","retrofitBack =" + message);
//             }
//            }).setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();
    //创建用于fitme的api的retrofit服务接口
    private static final Retrofit retrofit_uc = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_uc))
            .baseUrl(BaseHttpConfig.BASE_URL_UC) //自动化打包配置
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_uc = retrofit_uc.create(BaseHttpService.class);

    private static final Retrofit retrofit_at = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_at))
            .baseUrl(BaseHttpConfig.BASE_URL_AT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_at = retrofit_at.create(BaseHttpService.class);

    private static final Retrofit retrofit_nc = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_nc))
            .baseUrl(BaseHttpConfig.BASE_URL_NC)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_nc = retrofit_nc.create(BaseHttpService.class);

    private static final Retrofit retrofit_dm = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_dm))
            .baseUrl(BaseHttpConfig.BASE_URL_DM)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_dm = retrofit_dm.create(BaseHttpService.class);


    /**
     * 智能硬件2.0
     */
    private static final Retrofit retrofit_sd = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_sd))
            .baseUrl(BaseHttpConfig.BASE_URL_SD)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_sd = retrofit_sd.create(BaseHttpService.class);


    /**
     * 智能场景联动1.0
     */
    private static final Retrofit retrofit_ss = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_ss))
            .baseUrl(BaseHttpConfig.BASE_URL_SS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_ss = retrofit_ss.create(BaseHttpService.class);

    /**
     * H5路径获取
     */
    private static final Retrofit retrofit_h5 = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_h5))
            .baseUrl(BaseHttpConfig.BASE_URL_H5)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_h5 = retrofit_h5.create(BaseHttpService.class);

    /**
     * 音乐播放
     */
    private static final Retrofit retrofit_mp = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.music_player))
            .baseUrl(BaseHttpConfig.MUSIC_PLAYER)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_mp = retrofit_mp.create(BaseHttpService.class);

    /**
     * 闹钟管理
     */
    private static final Retrofit retrofit_ac = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_ac))
            .baseUrl(BaseHttpConfig.BASE_URL_AC)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共地址让model访问
    public static final BaseHttpService fitmeApiService_ac = retrofit_ac.create(BaseHttpService.class);

    /**
     * 提醒管理
     */
    private static final Retrofit retrofit_rm = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_rm))
            .baseUrl(BaseHttpConfig.BASE_URL_RM)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共地址让model访问
    public static final BaseHttpService fitmeApiService_rm = retrofit_rm.create(BaseHttpService.class);

    /**
     * 备忘录管理
     */
    private static final Retrofit retrofit_mb = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_mb))
            .baseUrl(BaseHttpConfig.BASE_URL_MB)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共地址让model访问
    public static final BaseHttpService fitmeApiService_mb = retrofit_mb.create(BaseHttpService.class);

    /**
     * 图像上传
     */
    private static final Retrofit retrofit_up = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_up))
            .baseUrl(BaseHttpConfig.BASE_URL_UP)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_up = retrofit_up.create(BaseHttpService.class);


    /**
     * App版本信息获取
     */
    private static final Retrofit retrofit_vg = new Retrofit.Builder()
//            .baseUrl(MainApplication.getApp().getResources().getString(R.string.base_url_vg))
            .baseUrl(BaseHttpConfig.BASE_URL_VG)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    //设置成公共方法让model访问
    public static final BaseHttpService fitmeApiService_vg = retrofit_vg.create(BaseHttpService.class);
}
