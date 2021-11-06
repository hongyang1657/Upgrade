package ai.fitme.ayahupgrade.model;

import android.os.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import ai.fitme.ayahupgrade.bean.OtaConfigBean;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.OkHttpUtil;
import ai.fitme.ayahupgrade.utils.ZipUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OTADownloadModel {

    //下载的文件列表
    private List<OtaConfigBean.UpgradeFile> downloadFileNameList;
    private OTADownloadListener listener;

    public OTADownloadModel(OTADownloadListener listener) {
        this.listener = listener;
    }

    public void downloadAllFile(OtaConfigBean.UpgradeFile[] upgradeFiles){
        FileUtil.makeDir(Constants.MODEL_ROOT_PATH);
        downloadFileNameList = new ArrayList<>();
        Observable.create(new Observable.OnSubscribe<OtaConfigBean.UpgradeFile>() {
            @Override
            public void call(Subscriber<? super OtaConfigBean.UpgradeFile> subscriber) {
                //下载并解压
                if (downloadFile(Constants.modelZip)){
                    String path = Environment.getExternalStorageDirectory().toString() + Constants.MODEL_ROOT_PATH;
                    try {
                        Set<String> filenames = ZipUtils.UnZipFolder(path+Constants.modelZip,path);
                        //根据本地下载的文件，查询得到对应的upgradeFiles
                        for (String file : filenames){

                            for (int i=0;i<upgradeFiles.length;i++){
                                if (file.equals(upgradeFiles[i].getName())){
                                    subscriber.onNext(upgradeFiles[i]);
                                    Thread.sleep(100);
                                    //L.i("onNext:"+upgradeFiles[i].toString());
                                }
                            }
                        }
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }

                }

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OtaConfigBean.UpgradeFile>() {
                    @Override
                    public void onCompleted() {
                        //配置所有文件数据，准备push
                        listener.onCompleted(downloadFileNameList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.i("OTADownloadModel onError:"+e.toString());
                        //有文件下载失败
                        listener.onDownloadError(e);
                    }

                    @Override
                    public void onNext(OtaConfigBean.UpgradeFile upgradeFile) {
                        if (null!=upgradeFile){
                            L.i("已下载 filename:"+upgradeFile.getName());
                            downloadFileNameList.add(upgradeFile);
                        }
                    }
                });
    }


    private boolean downloadFile(String filename){
        String Url = Constants.upgrade_url + filename;
        Request request = new Request.Builder()
                .get()
                .url(Url)
                .build();
        Call call = OkHttpUtil.okHttpClient.newCall(request);
        //采用同步方式下载文件
        try {
            Response response = call.execute();
            ResponseBody body = response.body();
            byte[] res = body.bytes();
            if (new String(res).contains("\"status\":\"Fail\"")){
                L.i(new String(res));
                return false;
            }
            return FileUtil.setFileAtRoot(Constants.MODEL_ROOT_PATH + filename,res);
        } catch (IOException e) {
            e.printStackTrace();
            L.i("downloadFile error:"+e.toString());
            return false;
        }
    }

    public interface OTADownloadListener{
        void onCompleted(List<OtaConfigBean.UpgradeFile> downloadFileNameList);
        void onDownloadError(Throwable e);
    }
}
