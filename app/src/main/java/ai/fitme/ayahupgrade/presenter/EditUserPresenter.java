package ai.fitme.ayahupgrade.presenter;

import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.EditUserModel;
import ai.fitme.ayahupgrade.model.ToMobileVerifyCodeModel;
import ai.fitme.ayahupgrade.presenter.impl.IEditUserPresenter;
import ai.fitme.ayahupgrade.ui.impl.IAboutMeActivityView;

/**
 * Created by blw on 2016/8/28.
 */
public class EditUserPresenter implements IEditUserPresenter, ToMobileVerifyCodeModel.OnToMobileVerifyCodeListener {
    //网络数据处理model
    private EditUserModel mEditUserModel;
    //视图fragment
    private IAboutMeActivityView mAboutMeActivityView;

    public EditUserPresenter(IAboutMeActivityView mAboutMeActivityView) {
        this.mEditUserModel = new EditUserModel(this);
        this.mAboutMeActivityView = mAboutMeActivityView;
    }



    @Override
    public void onSuccess(Status jsonObject) {
        mAboutMeActivityView.showEditUser(jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {

    }

    //请求更改用户信息
    @Override
    public void editUser(ProfileInfo user) {
        mEditUserModel.editUser(user);
    }
}
