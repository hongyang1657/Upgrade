package ai.fitme.ayahupgrade.model;
import com.google.gson.Gson;
import java.util.HashMap;
import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.api.BaseHttpManager;
import ai.fitme.ayahupgrade.model.api.FmSubscriber;
import ai.fitme.ayahupgrade.model.api.HttpConstant;
import ai.fitme.ayahupgrade.model.impl.IDeviceInfoUploadModel;
import ai.fitme.ayahupgrade.presenter.DeviceInfoUploadPresenter;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SignAndEncrypt;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 上传手机设备信息
 * Created by yml on 2017/10/18.
 */

public class DeviceInfoUploadModel implements IDeviceInfoUploadModel {
    private DeviceInfoUploadPresenter mDeviceInfoUploadPresenter;

    public DeviceInfoUploadModel(DeviceInfoUploadPresenter mDeviceInfoUploadPresenter) {
        this.mDeviceInfoUploadPresenter = mDeviceInfoUploadPresenter;
    }

    @Override
    public void deviceInfoUpload(String user_id,String token,String device_id) {
        String timeStamp = SignAndEncrypt.getTimeStamp();
        String apikey= MainApplication.getApp().getResources().getString(R.string.api_key_user_center);
        String apiSecret= MainApplication.getApp().getResources().getString(R.string.api_secret_user_center);
        HashMap<String, Object> params = new HashMap<>();
        params.put("method", HttpConstant.METHOD_UC_DEVICE_PUT);
        params.put("api_key", apikey);
        params.put("timestamp", timeStamp);
        params.put("version", HttpConstant.API_VERSION);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("token", token);
        map.put("device_id", device_id);
        map.put("device_type","mobile");

        Gson gson = new Gson();
        params.put("http_body", gson.toJson(map));
        L.logE("______deviceInfoUpload:"+ gson.toJson(map));
        String sign = SignAndEncrypt.signRequest(params, apiSecret);
        BaseHttpManager.fitmeApiService_uc.deviceInfoUpload(apikey, timeStamp, sign,HttpConstant.API_VERSION, HttpConstant.METHOD_UC_DEVICE_PUT,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FmSubscriber<Status>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mDeviceInfoUploadPresenter.onFailure(e);
                    }
                    @Override
                    public void onNext(Status jsonObject) {
                        mDeviceInfoUploadPresenter.onSuccess(jsonObject);
                    }
                });
    }

    public interface OnDeviceInfoUploadListener {
        void onSuccess(Status jsonObject);

        void onFailure(Throwable e);
    }
}
