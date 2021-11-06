package ai.fitme.ayahupgrade.presenter;


import ai.fitme.ayahupgrade.bean.TokenInfo;
import ai.fitme.ayahupgrade.model.TokenModel;
import ai.fitme.ayahupgrade.presenter.impl.ITokenPresenter;
import ai.fitme.ayahupgrade.ui.activity.impl.IloginActivityView;

/**
 * Created by blw on 2016/8/28.
 */
public class TokenPresenter implements ITokenPresenter, TokenModel.OnTokenListener {
    //网络数据处理model
    private TokenModel mTokenModel;
    //视图fragment
    private IloginActivityView iloginActivityView;

    public TokenPresenter(IloginActivityView iloginActivityView) {
        this.mTokenModel = new TokenModel(this);
        this.iloginActivityView = iloginActivityView;
    }

    @Override
    public void onSuccess(String userId, TokenInfo jsonObject) {
        iloginActivityView.showToken(userId,jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {

    }

    //登录网络请求
    @Override
    public void token(String userId, String password) {
        mTokenModel.token(userId, password);
    }
}
