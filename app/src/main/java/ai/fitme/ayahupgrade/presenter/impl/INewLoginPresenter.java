package ai.fitme.ayahupgrade.presenter.impl;

import org.json.JSONObject;

/**
 * Created by blw on 2016/8/28.
 */
public interface INewLoginPresenter {
    void login(String login_type, String identifier, String credential, JSONObject login_user_info);
}
