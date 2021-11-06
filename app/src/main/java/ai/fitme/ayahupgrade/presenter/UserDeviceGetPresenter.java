package ai.fitme.ayahupgrade.presenter;

import ai.fitme.ayahupgrade.bean.DeviceGet;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.model.UserDeviceGetModel;
import ai.fitme.ayahupgrade.presenter.impl.IUserDeviceGetPresenter;
import ai.fitme.ayahupgrade.ui.activity.impl.IUserDeviceGetView;

/**
 * Created by zzy on 2017/12/28.
 */

public class UserDeviceGetPresenter implements IUserDeviceGetPresenter,UserDeviceGetModel.onUserDeviceGetListener {
    //网络加载
    private UserDeviceGetModel mUserDeviceGetModel;
    //数据处理
    private IUserDeviceGetView mIUserDeviceGetView;

    public UserDeviceGetPresenter(IUserDeviceGetView mIUserDeviceGetView) {
        this.mUserDeviceGetModel = new UserDeviceGetModel(this);
        this.mIUserDeviceGetView = mIUserDeviceGetView;
    }

    @Override
    public void userDeviceGet(ProfileInfo user, String device_id) {
        mUserDeviceGetModel.userDeviceGet(user,device_id);
    }

    @Override
    public void onSuccess(DeviceGet deviceGet) {
        mIUserDeviceGetView.showUserDeviceGet(deviceGet);
    }

    @Override
    public void onFailure(Throwable e) {

    }
}
