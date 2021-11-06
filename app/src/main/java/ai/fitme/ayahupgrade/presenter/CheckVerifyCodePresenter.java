package ai.fitme.ayahupgrade.presenter;


import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.CheckVerifyCodeModel;
import ai.fitme.ayahupgrade.presenter.impl.ICheckVerifyCodePresenter;
import ai.fitme.ayahupgrade.ui.impl.IVerifyCodeCheckView;

/**
 * Created by zzy on 2018/3/6.
 */

public class CheckVerifyCodePresenter implements ICheckVerifyCodePresenter, CheckVerifyCodeModel.OnVerifyCodeCheckListener {
    //网络加载
    private CheckVerifyCodeModel mCheckVerifyCodeModel;
    //数据处理
    private IVerifyCodeCheckView mIVerifyCodeCheckView;

    public CheckVerifyCodePresenter(IVerifyCodeCheckView mIVerifyCodeCheckView) {
        this.mCheckVerifyCodeModel = new CheckVerifyCodeModel(this);
        this.mIVerifyCodeCheckView = mIVerifyCodeCheckView;
    }

    @Override
    public void checkVerifyCode(String user_id, String mobile, String verify_code) {
        mCheckVerifyCodeModel.checkVerifyCode(user_id,mobile,verify_code);
    }

    @Override
    public void onSuccess(Status jsonObject) {
        mIVerifyCodeCheckView.showVerifyCodeCheck(jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {

    }
}
