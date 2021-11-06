package ai.fitme.ayahupgrade.presenter;


import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.model.GetUserIdByMobileModel;
import ai.fitme.ayahupgrade.presenter.impl.IGetUserIdByMobilePresenter;
import ai.fitme.ayahupgrade.ui.activity.impl.IloginActivityView;

/**
 * Created by yml on 2017/10/12.
 */

public class GetUserIdByMobilePresenter implements IGetUserIdByMobilePresenter, GetUserIdByMobileModel.GetUserIdByMobileListener {

    private IloginActivityView mILoginFragmentView;
    private GetUserIdByMobileModel mGetUserIdByMobileModel;

    public GetUserIdByMobilePresenter(IloginActivityView mILoginFragmentView) {
        this.mILoginFragmentView = mILoginFragmentView;
        this.mGetUserIdByMobileModel = new GetUserIdByMobileModel(this);
    }

    @Override
    public void getUserIdByMobile(String phoneNumber) {
        mGetUserIdByMobileModel.getUserIdByMobile(phoneNumber);
    }

    @Override
    public void onSuccess(AccountCreate user) {
        mILoginFragmentView.showGetUserId(user);
    }

    @Override
    public void onFailure(Throwable e) {

    }
}
