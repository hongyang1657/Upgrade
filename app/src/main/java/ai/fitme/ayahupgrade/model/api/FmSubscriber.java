package ai.fitme.ayahupgrade.model.api;


import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.ui.widget.LoadingProgressUtils;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.ToastUtil;
import rx.Subscriber;

/**
 * Created by yml on 2017/11/17.
 */

public abstract class FmSubscriber<T> extends Subscriber<T> {

    public void onCompleted() {
        LoadingProgressUtils.closeDialog();
    }

    @Override
    public void onError(Throwable e) {
        LoadingProgressUtils.closeDialog();
        L.i("----onError--"+e.getMessage());
        if("connect timed out".equals(e.getMessage())){
            ToastUtil.showToast(MainApplication.getApp(),MainApplication.getApp().getResources().getString(R.string.request_error));
        }
    }

    @Override
    public void onNext(T t) {
        LoadingProgressUtils.closeDialog();
    }

}
