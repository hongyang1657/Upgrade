package ai.fitme.ayahupgrade.model;

import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IVerifyMobileModel;
import ai.fitme.ayahupgrade.presenter.VerifyMobilePresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 验证验证码的model
 * Created by blw on 2016/8/26.
 */
public class VerifyMobileModel implements IVerifyMobileModel {


    //用于执行回调方法
    private VerifyMobilePresenter mVerifyMobilePresenter;

    public VerifyMobileModel(VerifyMobilePresenter mVerifyMobilePresenter) {
        this.mVerifyMobilePresenter = mVerifyMobilePresenter;
    }

    //验证用户手机
    @Override
    public void verifyMobile(String mobile, String user_id, String verify_code) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apiKey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);

        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_VERIFY_MOBILE);
        params.put("api_key", apiKey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("user_id", user_id);
        map.put("verify_code", verify_code);
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, MainApplication.getApp().getResources().getString(R.string.api_secret_user_center));
        BaseHttpManager.fitmeApiService_uc.verifyMobile(apiKey, timeStamp, sign,HttpConstant.API_VERSION,HttpConstant.METHOD_UC_VERIFY_MOBILE, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<Status>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mVerifyMobilePresenter.onFailure(e);
                    }

                    @Override
                    public void onNext(Status jsonObject) {
                        L.logE("VerifyMobileModel:"+jsonObject);
                        mVerifyMobilePresenter.onSuccess(jsonObject);

                    }
                });

    }

    public interface OnVerifyMobileListener {
        void onSuccess(Status jsonObject);

        void onFailure(Throwable e);
    }
}
