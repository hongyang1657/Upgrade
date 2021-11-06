package ai.fitme.ayahupgrade.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.bean.TokenInfo;
import ai.fitme.ayahupgrade.presenter.ChangePasswordPresenter;
import ai.fitme.ayahupgrade.presenter.ProfilePresenter;
import ai.fitme.ayahupgrade.presenter.TokenPresenter;
import ai.fitme.ayahupgrade.presenter.VerifyMobilePresenter;
import ai.fitme.ayahupgrade.ui.activity.impl.IloginActivityView;
import ai.fitme.ayahupgrade.ui.impl.ILoginFragmentView;
import ai.fitme.ayahupgrade.ui.impl.IRegister3ActivityView;
import ai.fitme.ayahupgrade.ui.impl.IRegisterFragmentView;
import ai.fitme.ayahupgrade.ui.impl.IUserInfo;
import ai.fitme.ayahupgrade.utils.L;

public class Register3Activity extends Activity implements View.OnClickListener, IRegister3ActivityView, IUserInfo, ILoginFragmentView, IRegisterFragmentView, IloginActivityView {
    //用于存储全局变量，存储当前Welcome页显示的是输入手机号页还是输入验证码页
    private MainApplication app;
    private ImageView mIvBack;//返回按钮
    private EditText mEtPasswordInput;//密码输入
    private ImageView mIvPwdShow;//密码显示与隐藏的按钮
    private Button mBtnLogin;//登录按钮

    private boolean isPwdShow;
    private String phone;//当前注册的手机号

    //修改密码的接口
    private ChangePasswordPresenter mChangePasswordPresenter;
    //获取Token
    private TokenPresenter mTokenPresenter;
    //获取用户信息 profile
    private ProfilePresenter mProfilePresenter;
    //验证用户手机
    private VerifyMobilePresenter mVerifyMobilePresenter;

    private String user_id;
    private String password;
    private String loginToken;
    private String new_password;
    private String check_code;
    private TextView mTvStep1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        initView();
        initData();
        registerListeners();
    }

    protected void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mEtPasswordInput = (EditText) findViewById(R.id.password_input);
        mIvPwdShow = (ImageView) findViewById(R.id.pwd_show);
        mIvPwdShow.setImageResource(R.mipmap.icon_eye_close);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_disabled);
        mTvStep1 = (TextView) findViewById(R.id.tv_register_step1);
        mTvStep1.getPaint().setFakeBoldText(true);

        mChangePasswordPresenter = new ChangePasswordPresenter(this);
        mVerifyMobilePresenter = new VerifyMobilePresenter(this);
        mTokenPresenter = new TokenPresenter(this);
        mProfilePresenter = new ProfilePresenter(this);

        app = (MainApplication) getApplication();
    }

    protected void registerListeners() {
        mIvBack.setOnClickListener(this);
        mIvPwdShow.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mBtnLogin.setEnabled(true);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_normal);
                } else {
                    mBtnLogin.setEnabled(false);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_disabled);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        mEtPasswordInput.addTextChangedListener(passwordWatcher);
        mEtPasswordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });
    }

    protected void initData() {
        phone = getIntent().getStringExtra("phone");//用户注册的手机号
        user_id = getIntent().getStringExtra("user_id");//初始的user_id
        password = getIntent().getStringExtra("pwd");//初始的随机密码
        check_code = getIntent().getStringExtra("check_code");//验证码
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //返回按钮
                finish();
                break;
            case R.id.pwd_show:
                //控制密码的显示隐藏
                if (isPwdShow) {
                    mIvPwdShow.setImageResource(R.mipmap.icon_eye_close);
                    mEtPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    mEtPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mIvPwdShow.setImageResource(R.mipmap.icon_eye_open);
                }
                isPwdShow = !isPwdShow;
                break;
            case R.id.btn_login:
                // 完成并登录
                setPwdAndLogin();
                break;
        }
    }

    //设置密码并完成登录
    private void setPwdAndLogin() {
        L.i("点击登录按钮");
        int PwdLength = mEtPasswordInput.getText().toString().trim().length();
        if (PwdLength < 6) {
            Toast toast = Toast.makeText(Register3Activity.this, "请输入6-20位密码", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        } else {
            new_password = mEtPasswordInput.getText().toString().trim();
            L.i("user_id::" + user_id + "--密码：" + password + "::新密码：" + new_password);
            if (user_id != null && password != null && new_password != null) {
                L.i("是否执行修改密码的方法");
                mChangePasswordPresenter.changePassword(user_id, password, new_password);
            }
        }
    }


    //点击空白处赢藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void showIsOccupiedPhone(IsOccupiedMobile isOccupiedMobile) {

    }

    @Override
    public void showAccountCreate(AccountCreate accountCreate) {

    }

    @Override
    public void showToMobileVerifyCode(Status status) {

    }

    @Override
    public void showVerifyMobile(Status status) {
        if ("success".equals(status.getStatus())) {
            //转登录逻辑
            if (user_id != null && new_password != null) {
                mTokenPresenter.token(user_id, new_password);
            }
        } else {
            Toast toast = Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    @Override
    public void showToken(String userId, TokenInfo tokenInfo) {
        if ("success".equals(tokenInfo.getStatus())) {
            //获取到token后处理，并请求用户信息
            loginToken = tokenInfo.getToken();
            mProfilePresenter.profile(userId, tokenInfo.getToken(), "user_info/app");
        } else {
//            showToast("手机号或密码错误");
        }
    }


    @Override
    public void showToast(String erroInfo) {

    }

    @Override
    public void showGetUserId(AccountCreate user) {

    }

    //密码是否修改成功的回调
    @Override
    public void showPassWordChanged(Status jsonObject) {
        L.i("修改密码的回调方法" + jsonObject.getStatus());
        if ("success".equals(jsonObject.getStatus())) {
            if (phone != null && user_id != null && check_code != null) {
                mVerifyMobilePresenter.verifyMobile(phone, user_id, check_code);
            }
        } else {
            Toast toast = Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    @Override
    public void showProfile(ProfileInfo profileInfo) {
        if ("success".equals(profileInfo.getStatus())) {
            //存储注册的用户信息,默认注册成功即完成登录
//            ProfileInfo user = new ProfileInfo();
//            user.getContent().setUser_id(user_id);
//            user.getContent().setMobile(phone);
//            user.getContent().setToken(loginToken);
//            user.setPassword(new_password);
//            user.setIs_login(true);
//            user.setIs_play_voice(true);
//            UserUtil.saveUser(user);
//
//            //把用户信息插入到数据库中
//            DBManager dbManager = DBManager.getInstance(getApplicationContext());
//            QueryBuilder<UserInfo> qb = dbManager.getUserInfoDao().queryBuilder();
//            //未存在增加，已存在更新，聊天记录用到
//            List<UserInfo> list = qb.where(UserInfoDao.Properties.UserId.eq(user.getContent().getUser_id())).list();
//            if (list.size() == 0) {
//                UserInfo userInfo = new UserInfo();
//                userInfo.setPhone(user.getContent().getMobile());
//                userInfo.setIsLogin(1);
//                userInfo.setIsPlay(1);
//                userInfo.setUserId(user.getContent().getUser_id());
//                dbManager.getUserInfoDao().insert(userInfo);
//            } else {
//                qb.where(UserInfoDao.Properties.UserId.eq(user.getContent().getUser_id())).unique().setIsLogin(1);
//                dbManager.getUserInfoDao().update(qb.where(UserInfoDao.Properties.UserId.eq(user.getContent().getUser_id())).unique());
//            }
//
//
//            app.setUser(user);
//            //友盟账号统计
//            MobclickAgent.onProfileSignIn(user.getContent().getUser_id());
//            //将手机号保存到本地，用作登陆时的提示信息
//            saveHistory("history", phone);
//            showToast("注册成功");
//            Intent intent = new Intent();
//            intent.setClass(this, ScrollMainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
        } else {
            showToast("注册失败");
        }
    }


    /**
     * 将注册账号保存到本地中
     */
    private void saveHistory(String field, String text) {
        SharedPreferences sp = getSharedPreferences("login_history", 0);
        String longhistory = sp.getString(field, "nothing");
        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            sp.edit().putString("history", sb.toString()).commit();
        }
    }
}
