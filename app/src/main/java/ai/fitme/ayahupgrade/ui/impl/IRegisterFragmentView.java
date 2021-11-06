package ai.fitme.ayahupgrade.ui.impl;


import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.bean.Status;

/**
 * RegisterFragment的视图接口
 * Created by blw on 2016/8/28.
 */
public interface IRegisterFragmentView extends IPhoneIsOccupiedView {

    void showIsOccupiedPhone(IsOccupiedMobile isOccupiedMobile);
    void showAccountCreate(AccountCreate accountCreate);
    void showToMobileVerifyCode(Status status);
    void showVerifyMobile(Status status);
    void showToast(String erroInfo);
}
