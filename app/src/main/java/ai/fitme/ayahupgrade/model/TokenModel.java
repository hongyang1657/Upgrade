package ai.fitme.ayahupgrade.model;



import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.TokenInfo;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.ITokenModel;
import ai.fitme.ayahupgrade.presenter.TokenPresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 登录获取用户信息的model
 * Created by blw on 2016/8/26.
 */
public class TokenModel implements ITokenModel {
    private static final String TAG = TokenModel.class.getSimpleName();
    //用于执行回调方法
    private TokenPresenter mTokenPresenter;

    public TokenModel(TokenPresenter mTokenPresenter) {
        this.mTokenPresenter = mTokenPresenter;
    }

    //登录请求
    @Override
    public void token(final String userId, String password) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apikey= MainApplication.getApp().getResources().getString(R.string.api_key_authorization);
        String apiSecret= MainApplication.getApp().getResources().getString(R.string.api_secret_authorization);
        //MD5非对称加密
        final String asymmetric = SignAndEncrypt.asymmetricEncryptMd5(password,apiSecret, timeStamp);
        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_AT_TOKEN);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("password", asymmetric);
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, apiSecret);
        BaseHttpManager.fitmeApiService_at.token(apikey, timeStamp, sign,HttpConstant.API_VERSION, HttpConstant.METHOD_AT_TOKEN,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<TokenInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mTokenPresenter.onFailure(e);
                    }
                    @Override
                    public void onNext(TokenInfo jsonObject) {
                        L.logE("TokenModel:"+jsonObject.toString());

                        mTokenPresenter.onSuccess(userId,jsonObject);
                    }
                });
    }

    public interface OnTokenListener {
        void onSuccess(String userId, TokenInfo jsonObject);

        void onFailure(Throwable e);
    }
}
