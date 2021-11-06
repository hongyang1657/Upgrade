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
import ai.fitme.ayahupgrade.model.impl.IChangePasswordModel;
import ai.fitme.ayahupgrade.presenter.ChangePasswordPresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzy on 2018/3/6.
 */

public class ChangePasswordModel implements IChangePasswordModel {

    //用于执行回调方法
    private ChangePasswordPresenter mChangePasswordPresenter;

    public ChangePasswordModel(ChangePasswordPresenter mChangePasswordPresenter) {
        this.mChangePasswordPresenter = mChangePasswordPresenter;
    }

    @Override
    public void changePassword(String user_id, String password, String new_password) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apikey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        String apiSecret= MainApplication.getApp().getResources().getString(R.string.api_secret_user_center);

        //MD5非对称加密
        final String asymmetricPassword = SignAndEncrypt.asymmetricEncryptMd5(password,apiSecret, timeStamp);
        //对称加密
        String desPassword = SignAndEncrypt.DesEncrypt(new_password,apiSecret);
        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_CHANGE_PASSWORD);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("password", asymmetricPassword);
        map.put("new_password", desPassword);
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, apiSecret);
        BaseHttpManager.fitmeApiService_uc.changePassword(apikey, timeStamp, sign,HttpConstant.API_VERSION, HttpConstant.METHOD_UC_CHANGE_PASSWORD, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<Status>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("错误","ok");
                        mChangePasswordPresenter.onFailure(e);
                    }

                    @Override
                    public void onNext(Status jsonObject) {
                        L.i("ChangePasswordModel:"+jsonObject.toString());
                        mChangePasswordPresenter.onSuccess(jsonObject);
                    }
                });
    }

    public interface OnChangePasswordListener {
        void onSuccess(Status jsonObject);

        void onFailure(Throwable e);
    }
}
