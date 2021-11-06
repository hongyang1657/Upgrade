package ai.fitme.ayahupgrade.model;

import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.CheckToken;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.ICheckTokenModel;
import ai.fitme.ayahupgrade.presenter.CheckTokenPresenter;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzy on 2018/1/25.
 */

public class CheckTokenModel implements ICheckTokenModel {
    //用于执行回调方法
    private CheckTokenPresenter mCheckTokenPresenter;

    public CheckTokenModel(CheckTokenPresenter mCheckTokenPresenter) {
        this.mCheckTokenPresenter = mCheckTokenPresenter;
    }

    @Override
    public void checkToken(ProfileInfo user) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apikey= MainApplication.getApp().getResources().getString(R.string.api_key_authorization);
        String apiSecret= MainApplication.getApp().getResources().getString(R.string.api_secret_authorization);
        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_AT_CHECK_TOKEN);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", user.getContent().getUser_id());
        map.put("token", user.getContent().getToken());
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, apiSecret);
        BaseHttpManager.fitmeApiService_at.checkToken(apikey, timeStamp, sign,HttpConstant.API_VERSION, HttpConstant.METHOD_AT_CHECK_TOKEN,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<CheckToken>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mCheckTokenPresenter.onFailure(e);
                    }

                    @Override
                    public void onNext(CheckToken checkToken) {
                        super.onNext(checkToken);
                        mCheckTokenPresenter.onSuccess(checkToken);
                    }
                });
    }

    public interface OnTokenCheckListener {
        void onSuccess(CheckToken checkToken);

        void onFailure(Throwable e);
    }
}
