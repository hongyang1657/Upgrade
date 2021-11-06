package ai.fitme.ayahupgrade.model;


import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IIsOccupiedMobileModel;
import ai.fitme.ayahupgrade.presenter.IsOccupiedPhonePresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 验证手机号是否存在的model
 * Created by blw on 2016/8/26.
 */
public class IsOccupiedMobileModel implements IIsOccupiedMobileModel {


    //用于执行回调方法
    private IsOccupiedPhonePresenter mIsOccupiedPhonePresenter;

    public IsOccupiedMobileModel(IsOccupiedPhonePresenter mIsOccupiedPhonePresenter) {
        this.mIsOccupiedPhonePresenter = mIsOccupiedPhonePresenter;
    }

    //验证手机号是否存在
    @Override
    public void isOccupiedMobile(String mobile) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        HashMap<String, Object> params = new HashMap<>();
        String apikey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        params.put("method", HttpConstant.METHOD_UC_IS_OCCUPIED_MOBILE);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        //post的body内的参数map
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        Gson gson = new Gson();
        //转换成json格式的字符串，与服务器一致
        params.put("http_body", gson.toJson(map));
        L.i("验证手机号是否被占用的请求体" + gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, MainApplication.getApp().getResources().getString(R.string.api_secret_user_center));
        BaseHttpManager.fitmeApiService_uc.isOccupiedMobile(apikey, timeStamp,sign,HttpConstant.API_VERSION,HttpConstant.METHOD_UC_IS_OCCUPIED_MOBILE,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<IsOccupiedMobile>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        L.i("验证手机号是否被占用的错误信息返回" + e);
                        mIsOccupiedPhonePresenter.onFailure(e);
                    }

                    @Override
                    public void onNext(IsOccupiedMobile jsonObject) {
                        L.logE("IsOccupiedMobileModel:"+jsonObject.toString());
                        mIsOccupiedPhonePresenter.onSuccess(jsonObject);
                    }
                });

    }

    public interface OnPhoneEditListener {
        void onSuccess(IsOccupiedMobile jsonObject);
        void onFailure(Throwable e);
    }
}
