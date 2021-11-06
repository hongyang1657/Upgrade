package ai.fitme.ayahupgrade.presenter;



import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.model.AccountCreateModel;
import ai.fitme.ayahupgrade.model.impl.IAccountCreateModel;
import ai.fitme.ayahupgrade.presenter.impl.IAccountCreatePresenter;
import ai.fitme.ayahupgrade.ui.impl.IRegisterFragmentView;

/**
 * Created by blw on 2016/8/28.
 */
public class AccountCreatePresenter implements IAccountCreatePresenter, AccountCreateModel.OnAccountCreateListener {
    //网络数据处理model
    private IAccountCreateModel mIAccountCreateModel;
    //视图fragment
    private IRegisterFragmentView mRegisterFragmentView;

    public AccountCreatePresenter(IRegisterFragmentView mRegisterFragmentView) {
        this.mIAccountCreateModel = new AccountCreateModel(this);
        this.mRegisterFragmentView = mRegisterFragmentView;
    }

    @Override
    public void onSuccess(AccountCreate jsonObject) {
        mRegisterFragmentView.showAccountCreate(jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {

    }


    //创建账号
    @Override
    public void accountCreate(String password) {
        mIAccountCreateModel.accountCreate(password);
    }
}
