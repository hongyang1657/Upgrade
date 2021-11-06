package ai.fitme.ayahupgrade.presenter;


import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.VerifyMobileModel;
import ai.fitme.ayahupgrade.presenter.impl.IVerifyMobilePresenter;
import ai.fitme.ayahupgrade.ui.impl.IRegisterFragmentView;

/**
 * Created by blw on 2016/8/28.
 */
public class VerifyMobilePresenter implements IVerifyMobilePresenter, VerifyMobileModel.OnVerifyMobileListener {
    //网络数据处理model
    private VerifyMobileModel mVerifyMobileModel;
    //视图fragment
    private IRegisterFragmentView mRegisterFragmentView;

    public VerifyMobilePresenter(IRegisterFragmentView mRegisterFragmentView) {
        this.mVerifyMobileModel = new VerifyMobileModel(this);
        this.mRegisterFragmentView = mRegisterFragmentView;
    }

    @Override
    public void onSuccess(Status jsonObject) {
        mRegisterFragmentView.showVerifyMobile(jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {

    }

    //验证用户手机
    @Override
    public void verifyMobile(String mobile, String user_id, String verify_code) {
        mVerifyMobileModel.verifyMobile(mobile, user_id, verify_code);
    }
}
