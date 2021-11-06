package ai.fitme.ayahupgrade.model;

import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.ICheckVerifyCodeModel;
import ai.fitme.ayahupgrade.presenter.CheckVerifyCodePresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzy on 2018/3/6.
 */

public class CheckVerifyCodeModel implements ICheckVerifyCodeModel {
    //用于执行回调方法
    private CheckVerifyCodePresenter mCheckVerifyCodePresenter;

    public CheckVerifyCodeModel(CheckVerifyCodePresenter mCheckVerifyCodePresenter) {
        this.mCheckVerifyCodePresenter = mCheckVerifyCodePresenter;
    }

    @Override
    public void checkVerifyCode(String user_id, String mobile, String verify_code) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apikey = MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        String apiSecret = MainApplication.getApp().getResources().getString(R.string.api_secret_user_center);

        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_VERIFY_CODE_CHECK);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("mobile", mobile);
        map.put("verify_code", verify_code);
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, apiSecret);
        BaseHttpManager.fitmeApiService_uc.checkVerifyCode(apikey, timeStamp, sign, HttpConstant.API_VERSION, HttpConstant.METHOD_UC_VERIFY_CODE_CHECK, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<Status>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("错误", "ok");
                        mCheckVerifyCodePresenter.onFailure(e);
                    }

                    @Override
                    public void onNext(Status jsonObject) {
                        L.i("CheckVerifyCodeModel:" + jsonObject.toString());
                        mCheckVerifyCodePresenter.onSuccess(jsonObject);
                    }
                });
    }

    public interface OnVerifyCodeCheckListener {
        void onSuccess(Status jsonObject);

        void onFailure(Throwable e);
    }
}
