package ai.fitme.ayahupgrade.presenter.impl;

/**
 * Created by zzy on 2018/3/6.
 */

public interface ICheckVerifyCodePresenter {
    void checkVerifyCode(String user_id, String mobile, String verify_code);//user_id为null的时候表示找回密码，不为null的时候表示用户注册
}
