package ai.fitme.ayahupgrade.model.impl;

import org.json.JSONObject;

/**
 * Created by blw on 2016/8/26.
 */
public interface INewLoginModel {
    void login(String login_type, String identifier, String credential, JSONObject login_user_info);
}
