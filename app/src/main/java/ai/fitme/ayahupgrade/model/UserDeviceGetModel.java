package ai.fitme.ayahupgrade.model;

import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.DeviceGet;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IUserDeviceGetModel;
import ai.fitme.ayahupgrade.presenter.UserDeviceGetPresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzy on 2017/12/28.
 */

public class UserDeviceGetModel implements IUserDeviceGetModel {
    //用于执行回调方法
    private UserDeviceGetPresenter mUserDeviceGetPresenter;

    public UserDeviceGetModel(UserDeviceGetPresenter mUserDeviceGetPresenter) {
        this.mUserDeviceGetPresenter = mUserDeviceGetPresenter;
    }

    @Override
    public void userDeviceGet(ProfileInfo user, String device_id) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apikey = MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        String apiSecret = MainApplication.getApp().getResources().getString(R.string.api_secret_user_center);

        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_DEVICE);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", user.getContent().getUser_id());
        map.put("token", user.getContent().getToken());
        map.put("device_id", device_id);
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, apiSecret);
        BaseHttpManager.fitmeApiService_uc.userDeviceGet(apikey, timeStamp, sign, HttpConstant.API_VERSION, HttpConstant.METHOD_UC_DEVICE, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<DeviceGet>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mUserDeviceGetPresenter.onFailure(e);
                        L.i("获取交互设备错误" + e);
                    }

                    @Override
                    public void onNext(DeviceGet deviceGet) {
                        super.onNext(deviceGet);
                        mUserDeviceGetPresenter.onSuccess(deviceGet);
                    }
                });
    }

    public interface onUserDeviceGetListener {
        void onSuccess(DeviceGet deviceGet);

        void onFailure(Throwable e);
    }
}
