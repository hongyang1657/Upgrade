package ai.fitme.ayahupgrade.model;

import com.google.gson.Gson;
import java.io.IOException;
import ai.fitme.ayahupgrade.bean.OtaConfigBean;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.OkHttpUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CheckNeedOTAModel {

    private CheckNeedOTAListener listener;

    public CheckNeedOTAModel(CheckNeedOTAListener listener) {
        this.listener = listener;
    }

    public void checkNeedOTA(){
        Observable.create(new Observable.OnSubscribe<OtaConfigBean>() {

            @Override
            public void call(Subscriber<? super OtaConfigBean> subscriber) {
                //下载文件，并解析json
                String otaJson = downloadOTAConfig();
                if ("".equals(otaJson)){
                    subscriber.onError(new Throwable());
                    return;
                }
                OtaConfigBean otaConfigBean = new Gson().fromJson(otaJson,OtaConfigBean.class);
                //L.i("ota:"+otaConfigBean.toString());
                subscriber.onNext(otaConfigBean);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<OtaConfigBean>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                  L.i("onError:"+e.toString());
                  listener.onCheckOTAConfigError();
              }

              @Override
              public void onNext(OtaConfigBean otaConfigBean) {
                  listener.onGetOTAConfig(otaConfigBean);
              }
          });
    }

    private String downloadOTAConfig(){
        String Url = Constants.upgrade_url + Constants.ota_config;
        Request request = new Request.Builder()
                .get()
                .url(Url)
                .build();
        Call call = OkHttpUtil.okHttpClient.newCall(request);
        //采用同步方式下载文件
        try {
            Response response = call.execute();
            ResponseBody body = response.body();
            String res = body.string();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            L.i("downloadFile error:"+e.toString());
            listener.onCheckOTAConfigError();
            return "";
        }
    }

    public interface CheckNeedOTAListener{
        void onGetOTAConfig(OtaConfigBean otaConfigBean);
        void onCheckOTAConfigError();
    }
}
