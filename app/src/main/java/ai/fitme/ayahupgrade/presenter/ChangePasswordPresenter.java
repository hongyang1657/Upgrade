package ai.fitme.ayahupgrade.presenter;

import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.ChangePasswordModel;
import ai.fitme.ayahupgrade.presenter.impl.IChangePasswordPresenter;
import ai.fitme.ayahupgrade.ui.impl.IRegister3ActivityView;

/**
 * Created by zzy on 2018/3/6.
 */

public class ChangePasswordPresenter implements IChangePasswordPresenter, ChangePasswordModel.OnChangePasswordListener {
    //网络加载
    private ChangePasswordModel mChangePasswordModel;
    //数据处理
    private IRegister3ActivityView mIRegister3ActivityView;

    public ChangePasswordPresenter(IRegister3ActivityView mIRegister3ActivityView) {
        this.mChangePasswordModel = new ChangePasswordModel(this);
        this.mIRegister3ActivityView = mIRegister3ActivityView;
    }

    @Override
    public void changePassword(String user_id, String password, String new_password) {
        mChangePasswordModel.changePassword(user_id,password,new_password);
    }

    @Override
    public void onSuccess(Status jsonObject) {
        mIRegister3ActivityView.showPassWordChanged(jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {

    }
}
