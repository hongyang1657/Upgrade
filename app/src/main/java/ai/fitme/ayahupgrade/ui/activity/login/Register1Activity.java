package ai.fitme.ayahupgrade.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.AccountCreate;
import ai.fitme.ayahupgrade.bean.IsOccupiedMobile;
import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.bean.TokenInfo;
import ai.fitme.ayahupgrade.presenter.AccountCreatePresenter;
import ai.fitme.ayahupgrade.presenter.IsOccupiedPhonePresenter;
import ai.fitme.ayahupgrade.presenter.ToMobileVerifyCodePresenter;
import ai.fitme.ayahupgrade.ui.activity.impl.IloginActivityView;
import ai.fitme.ayahupgrade.ui.impl.IPhoneIsOccupiedView;
import ai.fitme.ayahupgrade.ui.impl.IRegisterFragmentView;
import ai.fitme.ayahupgrade.utils.L;

public class Register1Activity extends Activity implements View.OnClickListener, IPhoneIsOccupiedView, IRegisterFragmentView, IloginActivityView {
    private String phone_regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0678])|(18[0-9]))\\d{8}$";
    private static String pwd[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
            , "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private ImageView mIvBack;//返回按钮
    private EditText mEtPhoneNumber;//用户输入的电话号码
    private Button mBtnGetCheckCode;//获取验证码的按钮
    private TextView mTvTreaty1;//用户服务协议
    private TextView mTvTreaty2;//用户隐私条款
//    private TextView mTvPhoneNumberWrong;//手机号码输入错误的提示文本

    //是否占用手机号的presenter层
    private IsOccupiedPhonePresenter mIsOccupiedPhonePresenter;
    //获取验证码，创建账号的presenter蹭
    private AccountCreatePresenter mAccountCreatePresenter;
    //请求向指定手机发送验证码
    private ToMobileVerifyCodePresenter mToMobileVerifyCodePresenter;


    //手机号是否被注册的标记
    private boolean isRegister;
    //随机生成的伪随机密码
    private String passWord = "";
    //获取的user_id
    private String user_id;
    private String phoneNumber;
    private TextView mTvStep1;
    private ImageView mIvDelete;//删除手机号的按钮
    private String newNumber;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        initView();
        registerListeners();
    }

    protected void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mEtPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        mBtnGetCheckCode = (Button) findViewById(R.id.btn_get_check_code);
        mBtnGetCheckCode.setBackgroundResource(R.mipmap.btn_bg_disabled);
        mIvDelete = (ImageView) findViewById(R.id.phone_number_delete);
        mIvDelete.setVisibility(View.GONE);
        mTvStep1 = (TextView) findViewById(R.id.tv_register_step1);
        mTvStep1.getPaint().setFakeBoldText(true);

        mTvTreaty1 = (TextView) findViewById(R.id.tv_treaty1);
        mTvTreaty2 = (TextView) findViewById(R.id.tv_treaty2);

        mIsOccupiedPhonePresenter = new IsOccupiedPhonePresenter(this);
        mAccountCreatePresenter = new AccountCreatePresenter(this);
        mToMobileVerifyCodePresenter = new ToMobileVerifyCodePresenter(this);
    }

    protected void registerListeners() {
        mIvBack.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);
        mBtnGetCheckCode.setOnClickListener(this);
        mTvTreaty1.setOnClickListener(this);
        mTvTreaty2.setOnClickListener(this);
        //注册手机号码输入的监听
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //判断删除图标显示或者隐藏
                if (charSequence.length() > 0) {
                    mIvDelete.setVisibility(View.VISIBLE);
                    mBtnGetCheckCode.setEnabled(true);
                    mBtnGetCheckCode.setBackgroundResource(R.mipmap.btn_bg_normal);
                } else {
                    mIvDelete.setVisibility(View.GONE);
                    mBtnGetCheckCode.setEnabled(false);
                    mBtnGetCheckCode.setBackgroundResource(R.mipmap.btn_bg_disabled);
                }

                //判断文本提示
                if (charSequence.length() == 3) {
                    mEtPhoneNumber.setText(charSequence + "  ");
                    mEtPhoneNumber.setSelection(charSequence.length() + 2);
                } else if (charSequence.length() == 9) {
                    mEtPhoneNumber.setText(charSequence + "  ");
                    mEtPhoneNumber.setSelection(charSequence.length() + 2);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    mBtnGetCheckCode.setEnabled(false);
                    mBtnGetCheckCode.setBackgroundResource(R.mipmap.btn_bg_disabled);
                } else {
                    mBtnGetCheckCode.setEnabled(true);
                    mBtnGetCheckCode.setBackgroundResource(R.mipmap.btn_bg_normal);
                }
            }
        };
        mEtPhoneNumber.addTextChangedListener(passwordWatcher);

        //监听手机号输入框的软键盘删除键监听，重写删除按键
        mEtPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if (keycode == keyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    phoneNumber = mEtPhoneNumber.getText().toString().trim();
                    if (phoneNumber.length() != 0) {
                        phoneNumber = phoneNumber.substring(0, phoneNumber.length());
                        mEtPhoneNumber.setText(phoneNumber);
                        mEtPhoneNumber.setSelection(phoneNumber.length());
                    }
                }
                return false;
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //返回上一级的按钮
                finish();
                break;
            case R.id.btn_get_check_code:
                //校验手机号格式是否正确
                String number = mEtPhoneNumber.getText().toString().trim();
                //去除输入手机号中的空格
                newNumber = number.replaceAll("  ", "");
                Pattern pattern = Pattern.compile(phone_regex);
                Matcher matcher = pattern.matcher(newNumber);
                boolean isPhone = matcher.find();
                if (isPhone) {
                    if (newNumber.length() == 11 && !"".equals(newNumber)) {
                        mIsOccupiedPhonePresenter.isOccupiedMobile(newNumber);
                    }
                } else {
                    isRegister = false;
                    Toast toast = Toast.makeText(Register1Activity.this, "请输入正确的手机号", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
            case R.id.tv_treaty1:
                  //进入用户服务协议的入口
//                if (serviceUrl != null && !"".equals(serviceUrl)){
//                    Intent intent = new Intent(this, ChatWebViewActivity.class);
//                    intent.putExtra("url", serviceUrl);
//                    intent.putExtra("skill_name", "服务协议");
//                    startActivity(intent);
//                }
                break;
            case R.id.tv_treaty2:
                //进入用户隐私条款的入口
//                if (secretUrl != null && !"".equals(secretUrl)){
//                    Intent intent = new Intent(this, ChatWebViewActivity.class);
//                    intent.putExtra("url", secretUrl);
//                    intent.putExtra("skill_name", "隐私条款");
//                    startActivity(intent);
//                }
                break;
            case R.id.phone_number_delete:
                //删除输入的手机号
                mEtPhoneNumber.setText("");
                break;
        }
    }


    //手机号未被注册
    private void doNotRegister() {
        passWord = "";
        //1.生成随机伪随机密码并保存
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            //根据数组长度生成随机角标索引
            int nextInt = random.nextInt(pwd.length);
            passWord = passWord + pwd[nextInt];
        }
        L.i("生成的随机6位密码为:" + passWord);
        //2.通过密码获取user_id
        //创建账号
        if (passWord != null) {
            mAccountCreatePresenter.accountCreate(passWord);
        }
    }

    //手机号已经被注册
    private void doIsRegister() {
        Toast toast = Toast.makeText(this, "手机号已注册，请直接登录", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
    public void showGetUserId(AccountCreate accountCreate) {

    }

    /**
     * 后台返回的手机号是否被注册的结果
     *
     * @param isOccupiedMobile
     */
    @Override
    public void showIsOccupiedPhone(IsOccupiedMobile isOccupiedMobile) {
//        L.i("注册页面拿到的手机号是否被占用的结果" + new Gson().toJson(isOccupiedMobile));
        String status = isOccupiedMobile.getStatus();
        if ("success".equals(status)) {
            if (isOccupiedMobile.is_occupied()) {
                //手机号已经被注册
                doIsRegister();
            } else {
                //手机未被注册
                doNotRegister();
            }
        }
    }

    @Override
    public void showToken(String userId, TokenInfo jsonObject) {

    }

    @Override
    public void showAccountCreate(AccountCreate accountCreate) {
        if ("success".equals(accountCreate.getStatus())) {
            //保存用户id
            user_id = accountCreate.getUser_id();
            String number = mEtPhoneNumber.getText().toString().trim();
            String newNumber = number.replaceAll("  ", "");//去除输入手机号中的空格
            if (user_id != null && newNumber != null) {
                mToMobileVerifyCodePresenter.toMobileVerifyCode(user_id, newNumber);
            }
        }
    }

    @Override
    public void showToMobileVerifyCode(Status status) {

        // 跳转到下一个页面设置密码，传入手机号和随机密码
        if ("success".equals(status.getStatus())) {
            String phone = mEtPhoneNumber.getText().toString().trim();
            String newPhone = phone.replaceAll("  ", "");
            if (user_id != null && newPhone != null && passWord != null) {
                Intent it = new Intent(this, Register2Activity.class);
                it.putExtra("user_id", user_id);
                it.putExtra("phone", newPhone);
                it.putExtra("pwd", passWord);
                startActivity(it);
            }
        }
    }

    @Override
    public void showVerifyMobile(Status status) {

    }

    @Override
    public void showToast(String erroInfo) {

    }

}
