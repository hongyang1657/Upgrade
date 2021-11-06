package ai.fitme.ayahupgrade.model;

import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IAccountCreateModel;
import ai.fitme.ayahupgrade.presenter.AccountCreatePresenter;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建账号的model
 * Created by blw on 2016/8/26.
 */
public class AccountCreateModel implements IAccountCreateModel {


    //用于执行回调方法
    private AccountCreatePresenter mAccountCreatePresenter;

    public AccountCreateModel(AccountCreatePresenter mAccountCreatePresenter) {
        this.mAccountCreatePresenter = mAccountCreatePresenter;
    }

    //创建账户，得到user_id
    @Override
    public void accountCreate(String password) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apiKey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        String apiSecret= MainApplication.getApp().getResources().getString(R.string.api_secret_user_center);

        String desPassword = SignAndEncrypt.DesEncrypt(password, apiSecret);
        //签名用的参数map
        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_CREATE);
        params.put("api_key", apiKey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        //post的body内的参数map
        HashMap<String, Object> map = new HashMap<>();
        map.put("password", desPassword);
        Gson gson = new Gson();
        //转换成json格式的字符串，与服务器一致
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params,apiSecret);
        BaseHttpManager.fitmeApiService_uc.createAccount(apiKey, timeStamp, sign,HttpConstant.API_VERSION,HttpConstant.METHOD_UC_CREATE, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<AccountCreate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mAccountCreatePresenter.onFailure(e);
                    }

                    @Override
                    public void onNext(AccountCreate jsonObject) {
                        mAccountCreatePresenter.onSuccess(jsonObject);
                    }
                });

    }

    public interface OnAccountCreateListener {
        void onSuccess(AccountCreate jsonObject);
        void onFailure(Throwable e);
    }
}
