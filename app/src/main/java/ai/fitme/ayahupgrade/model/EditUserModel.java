package ai.fitme.ayahupgrade.model;
import com.google.gson.Gson;

import java.util.HashMap;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.ContentInfo;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IEditUserModel;
import ai.fitme.ayahupgrade.presenter.EditUserPresenter;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 编辑用户信息的model
 * Created by blw on 2016/8/26.
 */
public class EditUserModel implements IEditUserModel {


    //用于执行回调方法
    private EditUserPresenter mEditUserPresenter;

    public EditUserModel(EditUserPresenter mEditUserPresenter) {
        this.mEditUserPresenter = mEditUserPresenter;
    }


    //修改个人信息
    @Override
    public void editUser(ProfileInfo user) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apikey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        String apiSecret= MainApplication.getApp().getResources().getString(R.string.api_secret_user_center);

        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_PROFILE_PUT);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        ContentInfo contentInfo=user.getContent();
        map.put("user_id", contentInfo.getUser_id());
        map.put("token", contentInfo.getToken());
        map.put("path", "user_info/app");

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("be_called", contentInfo.getBe_called());
        map1.put("born_day", contentInfo.getBorn_day());
        map1.put("born_month", contentInfo.getBorn_month());
        map1.put("born_year", contentInfo.getBorn_year());
        map1.put("company_address", contentInfo.getCompany_address());
        map1.put("gender", contentInfo.getGender());
        map1.put("home_address", contentInfo.getHome_address());
        map1.put("mobile",contentInfo.getMobile());
        map1.put("preferences", contentInfo.getPreferences());
        map1.put("user_id", contentInfo.getUser_id());

        map.put("content",map1);
        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params,apiSecret);
        BaseHttpManager.fitmeApiService_uc.editUser(apikey, timeStamp, sign,HttpConstant.API_VERSION, HttpConstant.METHOD_UC_PROFILE_PUT, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<Status>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mEditUserPresenter.onFailure(e);
                    }

                    @Override
                    public void onNext(Status jsonObject) {
                        mEditUserPresenter.onSuccess(jsonObject);
                    }
                });

    }

    public interface OnEditUserListener {
        void onSuccess(Status jsonObject);

        void onFailure(Throwable e);
    }
}
