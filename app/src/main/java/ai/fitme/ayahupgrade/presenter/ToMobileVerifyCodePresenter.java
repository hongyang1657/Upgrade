package ai.fitme.ayahupgrade.presenter;

import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.ToMobileVerifyCodeModel;
import ai.fitme.ayahupgrade.presenter.impl.IToMobileVerifyCodePresenter;
import ai.fitme.ayahupgrade.ui.impl.IRegisterFragmentView;

/**
 * Created by blw on 2016/8/28.
 */
public class ToMobileVerifyCodePresenter implements IToMobileVerifyCodePresenter, ToMobileVerifyCodeModel.OnToMobileVerifyCodeListener {
    //网络数据处理model
    private ToMobileVerifyCodeModel mToMobileVerifyCodeModel;
    //视图fragment
    private IRegisterFragmentView mRegisterFragmentView;

    public ToMobileVerifyCodePresenter(IRegisterFragmentView mRegisterFragmentView) {
        this.mToMobileVerifyCodeModel = new ToMobileVerifyCodeModel(this);
        this.mRegisterFragmentView = mRegisterFragmentView;
    }

    //请求向指定手机发送验证码
    @Override
    public void toMobileVerifyCode(String user_id, String mobile) {
        mToMobileVerifyCodeModel.toMobileVerifyCode(user_id, mobile);
    }

    @Override
    public void onSuccess(Status jsonObject) {
        mRegisterFragmentView.showToMobileVerifyCode(jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {

    }
}
