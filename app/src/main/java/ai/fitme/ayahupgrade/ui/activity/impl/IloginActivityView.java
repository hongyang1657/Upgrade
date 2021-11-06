package ai.fitme.ayahupgrade.ui.activity.impl;

import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.bean.TokenInfo;

public interface IloginActivityView {
    void showGetUserId(AccountCreate accountCreate);
    void showIsOccupiedPhone(IsOccupiedMobile jsonObject);
    void showToken(String userId, TokenInfo jsonObject);
}
