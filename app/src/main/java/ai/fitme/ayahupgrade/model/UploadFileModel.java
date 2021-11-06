package ai.fitme.ayahupgrade.model;

import android.os.Environment;
import java.io.File;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.OkHttpUtil;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UploadFileModel {

    private UploadFileListener listener;

    public UploadFileModel(UploadFileListener listener) {
        this.listener = listener;
    }

    public void rxUpLoadFiles(String[] fileNameList){
        Observable.from(fileNameList)
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if (!upLoadFile(s)){
                            return false;
                        }
                        return true;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        listener.onUploadSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onUploadError();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean){
                            listener.onUploadError();
                        }
                    }
                });
    }

    /**
     *上传文件
     */
    private boolean upLoadFile(String fileName) {
        try {
            ProfileInfo info = MainApplication.getApp().getUser();
            File f = new File(Environment.getExternalStorageDirectory().toString() + Constants.KUKA_ROOT_PATH + fileName);
            //补全请求地址
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            builder.addFormDataPart("user_id", info.getContent().getUser_id());
            builder.addFormDataPart("phone", info.getContent().getMobile());
            builder.addFormDataPart("file_name", fileName);
            builder.addFormDataPart("version", "1.0");
            builder.addFormDataPart("file", f.getName(),RequestBody.create(null, f));
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder().url(Constants.upload_url).post(body).build();
            //单独设置参数 比如读取超时时间
            final Call call = OkHttpUtil.okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                String string = response.body().string();
                L.i( "response ----->" + string);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public interface UploadFileListener{
        void onUploadSuccess();
        void onUploadError();
    }
}
