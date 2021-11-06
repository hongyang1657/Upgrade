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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.bean.ContentInfo;
import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.bean.TokenInfo;
import ai.fitme.ayahupgrade.presenter.EditUserPresenter;
import ai.fitme.ayahupgrade.presenter.GetUserIdByMobilePresenter;
import ai.fitme.ayahupgrade.presenter.IsOccupiedPhonePresenter;
import ai.fitme.ayahupgrade.presenter.ProfilePresenter;
import ai.fitme.ayahupgrade.presenter.TokenPresenter;
import ai.fitme.ayahupgrade.ui.activity.KukaDataCollectionActivity;
import ai.fitme.ayahupgrade.ui.activity.MainActivity;
import ai.fitme.ayahupgrade.ui.activity.impl.IloginActivityView;
import ai.fitme.ayahupgrade.ui.impl.IAboutMeActivityView;
import ai.fitme.ayahupgrade.ui.impl.IUserInfo;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.NetworkUtils;
import ai.fitme.ayahupgrade.utils.PxUtil;
import ai.fitme.ayahupgrade.utils.ToastUtil;
import ai.fitme.ayahupgrade.utils.UserUtil;

/**
 * Created by zzy on 2018/3/1.
 * IPhoneIsOccupiedView, ILoginFragmentView, IDeviceInfoUploadView
 */

public class LoginActivity extends Activity implements View.OnClickListener, IloginActivityView, IUserInfo {

    private String phone_regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0678])|(18[0-9]))\\d{8}$";

    private EditText mPhoneNumber;//用户输入的手机号
    private TextView mPhoneNumberWrong;//输入的手机号非法
    private EditText mPassword;//用户输入的登录密码
    private ImageView mIvPassword_show;//是否展示密码
    private Button mBtnLogin;//登录按钮
    private TextView mTvFindPassword;//找回密码
    private TextView mTvRegister;//新用户注册
    private ImageView mIvDelete;//删除手机号
    private TextView mTvPwdWrong;//输入的密码非法

    //是否占用手机号的presenter层
    private IsOccupiedPhonePresenter mIsOccupiedPhonePresenter;
    //获取Token
    private TokenPresenter mTokenPresenter;
    //根据手机号获取user_id
    private GetUserIdByMobilePresenter mGetUserIdByMobilePresenter;
    //获取用户信息 profile
    private ProfilePresenter mProfilePresenter;



    private boolean isPwdShow;
    private String userId;
    private String loginToken;
    private long expires;

    //app保存用户信息
    private MainApplication app;
    private String phoneNumber;
    private RelativeLayout mRlInput;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        registerListeners();

    }

    protected void initView() {
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberWrong = (TextView) findViewById(R.id.phone_number_wrong);
        mPassword = (EditText) findViewById(R.id.password);
        mIvDelete = (ImageView) findViewById(R.id.phone_number_delete);
        mIvDelete.setVisibility(View.GONE);
        mIvPassword_show = (ImageView) findViewById(R.id.pwd_show);
        mIvPassword_show.setImageResource(R.mipmap.icon_eye_close);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_disabled);//初始化登录按钮为不可点击的灰色
        mTvFindPassword = (TextView) findViewById(R.id.tv_find_pwd);
        mTvRegister = (TextView) findViewById(R.id.tv_new_user_register);
        mTvPwdWrong = (TextView) findViewById(R.id.pwd_wrong);
        mRlInput = (RelativeLayout) findViewById(R.id.login_input);

        //初始化presenter
        mIsOccupiedPhonePresenter = new IsOccupiedPhonePresenter(this);
        mTokenPresenter = new TokenPresenter(this);
        mGetUserIdByMobilePresenter = new GetUserIdByMobilePresenter(this);
        mProfilePresenter = new ProfilePresenter(this);
        app = (MainApplication) getApplication();
    }

    protected void registerListeners() {
        //密码显示和隐藏的按钮点击事件
        mIvPassword_show.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);
        mTvFindPassword.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        //定义文本框的文本改变监听器
        TextWatcher phoneWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                L.i("当前输入的电话号码：" + charSequence + "字符串长度：" + charSequence.length() + "   i: " + i + "   i1: " + i1 + "   i2: " + i2);
                //判断删除图标显示或者隐藏
                if (charSequence.length() > 0) {
                    mIvDelete.setVisibility(View.VISIBLE);
                } else {
                    mIvDelete.setVisibility(View.GONE);
                }
                //判断文本提示
                if (charSequence.length() == 3) {
                    mPhoneNumber.setText(charSequence + "  ");
                    mPhoneNumber.setSelection(charSequence.length() + 2);
                } else if (charSequence.length() == 9) {
                    mPhoneNumber.setText(charSequence + "  ");
                    mPhoneNumber.setSelection(charSequence.length() + 2);
                } else if (charSequence.length() == 15) {
                    L.i("调用验证手机号的方法" + charSequence);
                    mPhoneNumberWrong.setText("");
                    String number = charSequence.toString().trim();
                    String newNumber = number.replaceAll(" ", "");
                    L.i("输入的手机号：" + newNumber);
                    Pattern pattern = Pattern.compile(phone_regex);
                    Matcher matcher = pattern.matcher(newNumber);
                    boolean isPhone = matcher.find();
                    if (isPhone) {
                        if (newNumber.length() == 11 && !"".equals(newNumber)) {
                            mIsOccupiedPhonePresenter.isOccupiedMobile(newNumber);
                        }
                    } else {
                        mPhoneNumberWrong.setText("请输入正确的手机号");
                        mBtnLogin.setEnabled(false);
                        mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_disabled);
                    }
                } else if (charSequence.length() > 15) {
                    mPhoneNumberWrong.setText("请输入正确的手机号");
                    mBtnLogin.setEnabled(false);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_disabled);
                } else if (charSequence.length() < 15) {
                    mPhoneNumberWrong.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0 || mPassword.getText().toString().length() == 0) {
                    mBtnLogin.setEnabled(false);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_disabled);
                } else {
                    mBtnLogin.setEnabled(true);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_normal);
                }
            }
        };
        mPhoneNumber.addTextChangedListener(phoneWatcher);
        mPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                L.i("是否有焦点" + b);
                if (!b) {
                    int length = mPhoneNumber.getText().toString().trim().length();
                    if (length < 15 && length != 0) {
                        mPhoneNumberWrong.setText("请输入正确的手机号");
                    } else if (length == 0) {
                        mPhoneNumberWrong.setText("");
                    }
                } else {
                    //有焦点
                    mPhoneNumberWrong.setText("");
                    //获取了焦点
                    // 动态改变控件的位置logo上移40，输入部分上移60
                    ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(mRlInput.getLayoutParams());
                    margin.setMargins(margin.leftMargin + (int) PxUtil.dpToPx(LoginActivity.this, 20), margin.topMargin + +(int) PxUtil.dpToPx(LoginActivity.this, 103), margin.rightMargin, margin.bottomMargin);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(margin);
                    mRlInput.setLayoutParams(params);
                }
            }
        });


        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0 || mPhoneNumber.getText().toString().length() == 0) {
                    mBtnLogin.setEnabled(false);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_disabled);
                } else {
                    mBtnLogin.setEnabled(true);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_bg_normal);
                }
            }
        };
        mPassword.addTextChangedListener(passwordWatcher);
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                L.i("登录页面密码输入是否有焦点" + b);
                if (b) {
                    mTvPwdWrong.setText("");
                    //获取了焦点
                    // 动态改变控件的位置logo上移40，输入部分上移60
                    ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(mRlInput.getLayoutParams());
                    margin.setMargins(margin.leftMargin + (int) PxUtil.dpToPx(LoginActivity.this, 20), margin.topMargin + +(int) PxUtil.dpToPx(LoginActivity.this, 103), margin.rightMargin, margin.bottomMargin);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(margin);
                    mRlInput.setLayoutParams(params);
                } else {
                    //失去焦点
                    String password = mPassword.getText().toString().trim();
                    if (password.length() == 0) {
                        mTvPwdWrong.setText("");
                    } else if (password.length() < 6 || password.length() > 20) {
                        mTvPwdWrong.setText("密码格式不正确");
                    }
                }
            }
        });

        //监听手机号输入框的软键盘删除键监听，重写删除按键
        mPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if (keycode == keyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    L.i("点击了软键盘的删除按键");
                    phoneNumber = mPhoneNumber.getText().toString().trim();
                    if (phoneNumber.length() != 0) {
                        phoneNumber = phoneNumber.substring(0, phoneNumber.length());
                        mPhoneNumber.setText(phoneNumber);
                        mPhoneNumber.setSelection(phoneNumber.length());
                    }
                }
                return false;
            }
        });
    }

    protected void initData() {
//        String phone = getIntent().getStringExtra("phone");
//        if (phone != null && !"".equals(phone)) {
//            mPhoneNumber.setText(phone);
//        }
        //记住密码
//        profileInfo = app.getUser();
//        if (profileInfo != null) {
//            String mobile = profileInfo.getContent().getMobile();
//            String password = profileInfo.getPassword();
//            if (mobile != null && !"".equals(mobile) && password != null && !"".equals(password)) {
//                char[] chars = mobile.toCharArray();
////                L.logE("本地存储的手机号字符数组-----------------------------------" + Arrays.toString(chars));
//                if (chars.length == 11) {
//                    mobile = "" + chars[0] + chars[1] + chars[2] + "  " + chars[3] + chars[4] + chars[5] + chars[6] + "  " + chars[7] + chars[8] + chars[9] + chars[10];
//                }
//                mPhoneNumber.setText(mobile);
//                mPassword.setText(password);
//            }
//        }
    }

    //点击空白处赢藏软键盘
//    public boolean onTouchEvent(MotionEvent event) {
//        if (null != this.getCurrentFocus()) {
//            // 将控件调整回原来的位置
//            ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(mRlInput.getLayoutParams());
//            margin.setMargins(margin.leftMargin + (int) PxUtil.dpToPx(LoginActivity.this, 20), margin.topMargin + +(int) PxUtil.dpToPx(LoginActivity.this, 163), margin.rightMargin, margin.bottomMargin);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(margin);
//            mRlInput.setLayoutParams(params);
//            //点击空白处的时候清除输入框内的焦点
//            mPhoneNumber.clearFocus();
//            mPassword.clearFocus();
//            /**
//             * 点击空白位置 隐藏软键盘
//             */
//            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    public void onClick(View view) {
        Intent it = null;
        switch (view.getId()) {
            case R.id.pwd_show:
                //控制密码的显示隐藏
                if (isPwdShow) {
                    mIvPassword_show.setImageResource(R.mipmap.icon_eye_close);
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mIvPassword_show.setImageResource(R.mipmap.icon_eye_open);
                }
                isPwdShow = !isPwdShow;
                break;
            case R.id.btn_login:
                //登录按钮
                boolean available = NetworkUtils.checkNetworkAvailable();
                if (available){
                    String trim = mPhoneNumber.getText().toString().trim();
                    String phone = trim.replaceAll("  ", "");
                    mGetUserIdByMobilePresenter.getUserIdByMobile(phone);
                    setWidgetParams();//重新调整控件位置
                } else {
                    Toast toast = Toast.makeText(this, "网络异常，请检查网络", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    setWidgetParams();//重新调整控件位置
                }
                break;
            case R.id.tv_find_pwd:
                //找回密码
                //it = new Intent(this, FindPassWord1Activity.class);
                //startActivity(it);
                break;
            case R.id.tv_new_user_register:
                //新用户注册
                it = new Intent(this, Register1Activity.class);
                startActivity(it);
                break;
            case R.id.phone_number_delete:
                //删除输入的手机号
                mPhoneNumber.setText("");
                break;
        }
    }


    //重新设置控件位置
    private void setWidgetParams() {
        // 将控件调整回原来的位置
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(mRlInput.getLayoutParams());
        margin.setMargins(margin.leftMargin + (int) PxUtil.dpToPx(LoginActivity.this, 20), margin.topMargin + +(int) PxUtil.dpToPx(LoginActivity.this, 163), margin.rightMargin, margin.bottomMargin);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(margin);
        mRlInput.setLayoutParams(params);
        //隐藏软键盘
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * 验证手机号是否被占用
     *
     * @param isOccupiedMobile
     */
    @Override
    public void showIsOccupiedPhone(IsOccupiedMobile isOccupiedMobile) {
        String status = isOccupiedMobile.getStatus();
        if ("success".equals(status)) {
            if (isOccupiedMobile.is_occupied()) {
//                mPhoneNumberWrong.setText("手机号被占用");
            } else {
                mPhoneNumberWrong.setText("账号未注册");
            }
        }
    }

    @Override
    public void showToken(String userId, TokenInfo user) {
        if ("success".equals(user.getStatus())) {
            //获取到token后处理，并请求用户信息
            this.userId = userId;
            this.loginToken = user.getToken();
            this.expires = user.getExpires();
            ProfileInfo profileInfo = new ProfileInfo();
            profileInfo.setExpires(expires);
            mProfilePresenter.profile(userId, user.getToken(), "user_info/app");
        } else {
            showToast("手机号或密码错误");
        }
    }


    /**
     * 显示错误信息
     *
     * @param erroInfo
     */
    public void showToast(String erroInfo) {
        ToastUtil.showToast(getApplicationContext(), erroInfo);
    }

    /**
     * 回去用户id
     *
     * @param user
     */
    public void showGetUserId(AccountCreate user) {
        if ("success".equals(user.getStatus())) {
            //获取到userId后请求token(login)
            if (mPassword.getText().toString().trim() != null && !"".equals(mPassword.getText().toString().trim())) {
                mTokenPresenter.token(user.getUser_id(), mPassword.getText().toString().trim());
            } else {
                showToast("请输入密码");
            }
        } else {
            showToast("手机号或密码错误");
        }
    }

    public void showProfile(ProfileInfo profileInfo) {
        L.i("---------------------------------" + new Gson().toJson(profileInfo));
        //登录最后步骤，获取用户信息
        if ("success".equals(profileInfo.getStatus())) {
            //存储用户信息到本地
            profileInfo.setPassword(mPassword.getText().toString());
            profileInfo.setIs_login(true);
            profileInfo.setIs_play_voice(true);
            profileInfo.getContent().setToken(loginToken);
            UserUtil.saveUser(profileInfo);
            app.setUser(profileInfo);
            //将手机号保存到本地，用作登陆时的提示信息
            ContentInfo contentInfo = profileInfo.getContent();
            saveHistory("history", contentInfo.getMobile());

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //Intent intent = new Intent(LoginActivity.this, KukaDataCollectionActivity.class);
            startActivity(intent);
            finish();
        } else {
            showToast("手机号或密码错误");
        }
    }

    /**
     * 将登录账号保存到本地中
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 上传交互设备的回调
     *
     * @param jsonObject
     */
    public void showDeviceInfoUpload(Status jsonObject) {
        L.logE("上传交互设备信息" + new Gson().toJson(jsonObject));
        if ("success".equals(jsonObject.getStatus())) {
            //上传交互设备成功，跳转到登录主界面
//            Intent intent = new Intent();
//            intent.setClass(this, ScrollMainActivity.class);
//            startActivity(intent);
//            showToast("登录成功");
//            finish();
        }
    }

}
