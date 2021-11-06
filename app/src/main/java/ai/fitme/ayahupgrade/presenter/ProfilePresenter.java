package ai.fitme.ayahupgrade.presenter;


import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.model.ProfileModel;
import ai.fitme.ayahupgrade.presenter.impl.IProfilePresenter;
import ai.fitme.ayahupgrade.ui.impl.IUserInfo;

/**
 * Created by yml on 2017/10/12.
 */

public class ProfilePresenter implements IProfilePresenter, ProfileModel.ProfileListener {

    private IUserInfo mIUserInfo;
    private ProfileModel mProfileModel;

    public ProfilePresenter(IUserInfo mIUserInfo) {
        this.mIUserInfo = mIUserInfo;
        mProfileModel = new ProfileModel(this);
    }

    /**
     * path user_info/app
     *
     * @param user_id
     * @param token
     * @param path
     */
    @Override
    public void profile(String user_id, String token, String path) {
        mProfileModel.profile(user_id, token, path);
    }

    @Override
    public void onSuccess(ProfileInfo jsonObject) {
        mIUserInfo.showProfile(jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {

    }
}
