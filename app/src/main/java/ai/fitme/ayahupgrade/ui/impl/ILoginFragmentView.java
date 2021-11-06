package ai.fitme.ayahupgrade.ui.impl;

import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.bean.TokenInfo;

/**
 * LoginFragment的视图接口
 * Created by blw on 2016/8/28.
 */
public interface ILoginFragmentView extends IPhoneIsOccupiedView,IUserInfo {

    void showIsOccupiedPhone(IsOccupiedMobile isOccupiedMobile);

    /**
     * 获取token处理方法
     * @param user
     */
    void showToken(String userId, TokenInfo user);

    void showToast(String erroInfo);

    /**
     * 根据mobile查询userId处理接口
     * @param user
     */
    void showGetUserId(AccountCreate user);

}
