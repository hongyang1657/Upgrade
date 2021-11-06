package ai.fitme.ayahupgrade.model;

import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IProfileModel;
import ai.fitme.ayahupgrade.presenter.ProfilePresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yml on 2017/10/12.
 */

public class ProfileModel implements IProfileModel {
    private ProfilePresenter mProfilePresenter;

    public ProfileModel(ProfilePresenter mProfilePresenter) {
        this.mProfilePresenter = mProfilePresenter;
    }

    @Override
    public void profile(String user_id, String token, String path) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apikey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        String apiSecret= MainApplication.getApp().getResources().getString(R.string.api_secret_user_center);
        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_PROFILE);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("token", token);
        map.put("path", path);
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, apiSecret);
        L.logE("method:"+HttpConstant.METHOD_UC_PROFILE+"__api_key:"+apikey+"__timestamp:"+
                timeStamp+"___version:"+HttpConstant.API_VERSION +
                "__sign:"+sign+"___http_body:"+gson.toJson(map)
        );
        BaseHttpManager.fitmeApiService_uc.profile(apikey, timeStamp, sign,HttpConstant.API_VERSION, HttpConstant.METHOD_UC_PROFILE,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<ProfileInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mProfilePresenter.onFailure(e);
                    }
                    @Override
                    public void onNext(ProfileInfo jsonObject) {
                        L.logE("ProfileModel:"+jsonObject.toString());

                        mProfilePresenter.onSuccess(jsonObject);
                    }
                });
    }

    public interface ProfileListener{
        void onSuccess(ProfileInfo jsonObject);

        void onFailure(Throwable e);
    }
}
