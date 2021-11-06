package ai.fitme.ayahupgrade.presenter;


import ai.fitme.ayahupgrade.bean.CheckToken;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.model.CheckTokenModel;
import ai.fitme.ayahupgrade.presenter.impl.ICheckTokenPresenter;
import ai.fitme.ayahupgrade.ui.impl.ICheckTokenView;

/**
 * Created by zzy on 2018/1/25.
 */

public class CheckTokenPresenter implements ICheckTokenPresenter, CheckTokenModel.OnTokenCheckListener {
    //网络获取
    private CheckTokenModel checkTokenModel;
    //数据处理
    private ICheckTokenView mICheckTokenView;

    public CheckTokenPresenter(ICheckTokenView mICheckTokenView) {
        this.checkTokenModel = new CheckTokenModel(this);
        this.mICheckTokenView = mICheckTokenView;
    }

    @Override
    public void checkToken(ProfileInfo user) {
        checkTokenModel.checkToken(user);
    }

    @Override
    public void onSuccess(CheckToken checkToken) {
        mICheckTokenView.showTokenCheck(checkToken);
    }

    @Override
    public void onFailure(Throwable e) {

    }
}
