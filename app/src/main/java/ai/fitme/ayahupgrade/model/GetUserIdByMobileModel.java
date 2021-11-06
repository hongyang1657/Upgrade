package ai.fitme.ayahupgrade.model;

import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IGetUserIdByMobileModel;
import ai.fitme.ayahupgrade.presenter.GetUserIdByMobilePresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 实现请求根据手机号查询 user_id
 * Created by yml on 2017/10/12.
 */

public class GetUserIdByMobileModel implements IGetUserIdByMobileModel {

    private GetUserIdByMobilePresenter mGetUserIdByMobilePresenter;

    public GetUserIdByMobileModel(GetUserIdByMobilePresenter mGetUserIdByMobilePresenter) {
        this.mGetUserIdByMobilePresenter = mGetUserIdByMobilePresenter;
    }

    @Override
    public void getUserIdByMobile(String phoneNumber) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        //MD5非对称加密
        String apikey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_GET_USER_ID_BY_MOBILE);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        //post的body内的参数map
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile", phoneNumber);
        Gson gson = new Gson();
        //转换成json格式的字符串，与服务器一致
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, MainApplication.getApp().getResources().getString(R.string.api_secret_user_center));
        BaseHttpManager.fitmeApiService_uc.getUserIdByMobile(apikey, timeStamp,sign,HttpConstant.API_VERSION,HttpConstant.METHOD_UC_GET_USER_ID_BY_MOBILE,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<AccountCreate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mGetUserIdByMobilePresenter.onFailure(e);
                    }
                    @Override
                    public void onNext(AccountCreate jsonObject) {
                        L.logE("GetUserIdByMobileModel:"+jsonObject.toString());
                        mGetUserIdByMobilePresenter.onSuccess(jsonObject);
                    }
                });
    }

    public interface GetUserIdByMobileListener {
        void onSuccess(AccountCreate jsonObject);

        void onFailure(Throwable e);
    }

}
