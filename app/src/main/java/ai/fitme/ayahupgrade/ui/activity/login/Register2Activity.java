package ai.fitme.ayahupgrade.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.presenter.AccountCreatePresenter;
import ai.fitme.ayahupgrade.presenter.CheckVerifyCodePresenter;
import ai.fitme.ayahupgrade.presenter.ToMobileVerifyCodePresenter;
import ai.fitme.ayahupgrade.ui.impl.IRegisterFragmentView;
import ai.fitme.ayahupgrade.ui.impl.IVerifyCodeCheckView;
import ai.fitme.ayahupgrade.utils.CountDownTimerUtil;
import ai.fitme.ayahupgrade.utils.SharedPreferencesUtils;
import ai.fitme.ayahupgrade.utils.ToastUtil;

public class Register2Activity extends Activity implements View.OnClickListener, IRegisterFragmentView, IVerifyCodeCheckView {
    //用于存储全局变量，存储当前Welcome页显示的是输入手机号页还是输入验证码页
    private MainApplication app;

    private ImageView mIvBack;//返回上一级的按钮
    private TextView mTvRemind;//提醒文本
    private TextView mTvCount;//倒计时的秒数
    private Button mBtnSetPassword;//设置密码的按钮
    private EditText mEtCheckCode;//验证码输入
    private String user_id;
    private String phone;

    //获取验证码，创建账号的presenter蹭
    private AccountCreatePresenter mAccountCreatePresenter;
    //请求向指定手机发送验证码
    private ToMobileVerifyCodePresenter mToMobileVerifyCodePresenter;
    //校验验证码
    private CheckVerifyCodePresenter mCheckVerifyCodePresenter;

    private String password;
    private String loginToken;

    //60秒完整倒计时工具类
    private CountDownTimerUtil countDownTimerUtil;
    //当点击返回键时，记录当前计时是否已经完成,设置初始值默认为true已完成
    private boolean isCountDownFinish = true;
    //用于保存计时未完成时的剩余秒数
    private long remainTime = 0;
    private String checkIdCode;
    private TextView mTvStep1;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        initView();
        initData();
        registerListeners();
    }

    protected void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvRemind = (TextView) findViewById(R.id.tv_remind);
        mTvCount = (TextView) findViewById(R.id.tv_count);
        mBtnSetPassword = (Button) findViewById(R.id.btn_set_password);
        mBtnSetPassword.setBackgroundResource(R.mipmap.btn_bg_disabled);
        mEtCheckCode = (EditText) findViewById(R.id.check_code_input);
        mTvStep1 = (TextView) findViewById(R.id.tv_register_step1);
        mTvStep1.getPaint().setFakeBoldText(true);

        mAccountCreatePresenter = new AccountCreatePresenter(this);
        mToMobileVerifyCodePresenter = new ToMobileVerifyCodePresenter(this);
        mCheckVerifyCodePresenter = new CheckVerifyCodePresenter(this);

        app = (MainApplication) getApplication();
    }

    protected void registerListeners() {
        mIvBack.setOnClickListener(this);
        mBtnSetPassword.setOnClickListener(this);
        mTvCount.setOnClickListener(this);

        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    mBtnSetPassword.setEnabled(false);
                    mBtnSetPassword.setBackgroundResource(R.mipmap.btn_bg_disabled);
                } else {
                    mBtnSetPassword.setEnabled(true);
                    mBtnSetPassword.setBackgroundResource(R.mipmap.btn_bg_normal);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    mBtnSetPassword.setEnabled(false);
                    mBtnSetPassword.setBackgroundResource(R.mipmap.btn_bg_disabled);
                } else {
                    mBtnSetPassword.setEnabled(true);
                    mBtnSetPassword.setBackgroundResource(R.mipmap.btn_bg_normal);
                }
            }
        };
        mEtCheckCode.addTextChangedListener(passwordWatcher);
    }

    protected void initData() {
        user_id = getIntent().getStringExtra("user_id");
        phone = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("pwd");
        remainTime = SharedPreferencesUtils.getInstance(getApplicationContext()).getLongCountMin("min", 0);
        if (phone != null) {
            mTvRemind.setText("已发送验证码到" + phone + "的手机");
            if (this.remainTime == 0) {
                countDownTimerUtil = new CountDownTimerUtil(0, mTvCount, 60000, 1000);
            } else {
                countDownTimerUtil = new CountDownTimerUtil(0, mTvCount, this.remainTime * 1000, 1000);
            }
            countDownTimerUtil.start();
        } else {
            mTvRemind.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //返回按钮,全局记录当前倒计时的秒数，下次进入按记录的秒数倒计时
                String msg = mTvCount.getText().toString();
                if (!"重新发送".equals(msg)) {
                    remainTime = Long.parseLong(msg.substring(0, (msg.length() - 1)));
                    SharedPreferencesUtils.getInstance(getApplicationContext()).setLongCountMin("min", remainTime);
                }
                finish();
                break;
            case R.id.btn_set_password:
                //设置密码
                checkIdCode();
                break;
            case R.id.tv_count:
                //倒计时文本
                if ("重新发送".equals(mTvCount.getText().toString().trim())) {
                    if (password != null) {
                        mAccountCreatePresenter.accountCreate(password);
                    }
                }
                break;
        }
    }

    //校验用户输入的验证码是否错误
    private void checkIdCode() {
        //获取验证码
        checkIdCode = mEtCheckCode.getText().toString().trim();
        if (!"".equals(checkIdCode)){
            //验证验证码
            if (phone != null && user_id != null && checkIdCode != null && !"".equals(checkIdCode)) {
                mCheckVerifyCodePresenter.checkVerifyCode(user_id, phone, checkIdCode);
            }
        } else {
            Toast toast = Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
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
        if ("success".equals(accountCreate.getStatus())) {
            //保存用户id
            user_id = accountCreate.getUser_id();
            if (user_id != null && phone != null) {
                mToMobileVerifyCodePresenter.toMobileVerifyCode(user_id, phone);
            }
        }
    }

    @Override
    public void showToMobileVerifyCode(Status status) {
        if ("success".equals(status.getStatus())) {
            //重新开启倒计时
            countDownTimerUtil = new CountDownTimerUtil(0, mTvCount, 60000, 1000);
            countDownTimerUtil.start();
        }
    }

    @Override
    public void showVerifyMobile(Status status) {

    }

    //再请求失败后返回的错误信息
    @Override
    public void showToast(String erroInfo) {
        ToastUtil.showToast(getApplicationContext(), erroInfo);
    }


    @Override
    public void onBackPressed() {
        if (app.getNowIsInputIdCode()) {
            backToRegister();
            return;
        }
        Intent in = new Intent(Intent.ACTION_MAIN);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.addCategory(Intent.CATEGORY_HOME);
        startActivity(in);
    }

    //点击返回键执行的方法
    public void backToRegister() {
//        goRegister();
        app.setNowIsInputIdCode(false);
        String msg = mTvCount.getText().toString();
        //当点击返回键时，判断当前倒计时是否已经完成，计时已完成
        if (countDownTimerUtil != null) {
            countDownTimerUtil.cancel();
            countDownTimerUtil = null;
        }
        if ("重新发送".equals(msg)) {
            isCountDownFinish = true;
        } else {
            isCountDownFinish = false;
            remainTime = Long.parseLong(msg.substring(0, (msg.length() - 1)));
        }
    }

    //校验验证码是否正确
    @Override
    public void showVerifyCodeCheck(Status status) {
        if ("success".equals(status.getStatus()) && "success".equals(status.getVerify_code_status())) {
            //将之前存储的倒计时置空
            SharedPreferencesUtils.getInstance(getApplicationContext()).setLongCountMin("min", 0);
            //跳转到设置密码页面
            Intent it = new Intent(this, Register3Activity.class);
            it.putExtra("user_id", user_id);
            it.putExtra("phone", phone);
            it.putExtra("pwd", password);
            it.putExtra("check_code", checkIdCode);
            startActivity(it);
        } else {
            Toast toast = Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}
