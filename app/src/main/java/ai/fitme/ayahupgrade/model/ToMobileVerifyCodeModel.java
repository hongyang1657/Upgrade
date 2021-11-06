package ai.fitme.ayahupgrade.model;

import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IToMobileVerifyCodeModel;
import ai.fitme.ayahupgrade.presenter.ToMobileVerifyCodePresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 发送验证码的model
 * Created by blw on 2016/8/26.
 */
public class ToMobileVerifyCodeModel implements IToMobileVerifyCodeModel {


    //用于执行回调方法
    private ToMobileVerifyCodePresenter mToMobileVerifyCodePresenter;

    public ToMobileVerifyCodeModel(ToMobileVerifyCodePresenter mToMobileVerifyCodePresenter) {
        this.mToMobileVerifyCodePresenter = mToMobileVerifyCodePresenter;
    }


    //给指定手机号发送验证码
    @Override
    public void toMobileVerifyCode(String user_id, String mobile) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apiKey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);

        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_TO_MOBILE_VERIFY_CODE_CREATE);
        params.put("api_key", apiKey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("mobile", mobile);
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, MainApplication.getApp().getResources().getString(R.string.api_secret_user_center));
        BaseHttpManager.fitmeApiService_uc.toMobileVerifyCode(apiKey, timeStamp, sign,HttpConstant.API_VERSION,HttpConstant.METHOD_UC_TO_MOBILE_VERIFY_CODE_CREATE, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<Status>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mToMobileVerifyCodePresenter.onFailure(e);
                    }

                    @Override
                    public void onNext(Status jsonObject) {
                        L.logE("ToMobileVerifyCodeModel:"+jsonObject);
                        mToMobileVerifyCodePresenter.onSuccess(jsonObject);
                    }
                });
    }

    public interface OnToMobileVerifyCodeListener {
        void onSuccess(Status jsonObject);

        void onFailure(Throwable e);
    }
}
