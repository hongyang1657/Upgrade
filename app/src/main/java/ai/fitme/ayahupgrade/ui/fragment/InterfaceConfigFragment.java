package ai.fitme.ayahupgrade.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.ui.activity.AdbDevelopmentActivity;
import ai.fitme.ayahupgrade.ui.activity.AdvertisingSettingActivity;
import ai.fitme.ayahupgrade.ui.activity.OTAActivity;
import ai.fitme.ayahupgrade.ui.activity.OTAActivity_old;
import ai.fitme.ayahupgrade.ui.fragment.imp.OnFragmentListener;
import ai.fitme.ayahupgrade.ui.view.WelcomeSettingDialog;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.DigitalComputationUtil;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.OnClickUtils;
import ai.fitme.ayahupgrade.utils.SharedPreferencesUtils;
import ai.fitme.ayahupgrade.utils.SoundPlayUtils;
import ai.fitme.ayahupgrade.utils.ToastUtil;

import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.ALARM_CONFIG;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.AMBIGOUS_SCORE;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.CANCEL_CONFIG;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.CANCEL_FLOOR_SELECTED;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.INTVAL_TIME;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.NOFLOOR_CONFIG;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.REGISTER_ANSWER_URL;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.VOLUME;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.WAKEUP_NUM;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.WELCOME_CONFIG;

public class InterfaceConfigFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Spinner spWakeUp;
    private ArrayAdapter<String> sp_adapter;
    private Switch swCancel,swAlarm,swGreet,swOutServerAlert,swAnswerQuestion;
    private Button btStart;
    private RadioButton rbTwice,rbThrice,rbLongpress;
    private EditText etTwice,etThrice,etLongpress;
    private LinearLayout llTwice,llThrice,llLongpress,llVolume,llRegisterAnswer,ll_developer,llAD,llOTA;
    private ImageButton bt_testplay_sound;
    private ImageButton bt_setting;
    private ImageView ivPlaySound;
    private TextView tvGreetDelay,tvVolume,tvRegister,tvVersion;
    private OnFragmentListener listener;
    private String[] sound = {Constants.WAKEUP_SOUND_DEFAULT,Constants.WAKEUP_SOUND_1,Constants.WAKEUP_SOUND_2,Constants.WAKEUP_SOUND_3,Constants.WAKEUP_SOUND_4};
    private static int soundPosition = 0;

    //音量
    private final String[] volumes = new String[]{"10","20","30","40","50","60","70","80","90"};
    //登记楼层应答语
    private final String[] registerAnswers = new String[]{"无应答语","已登记","XX楼已登记"};
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private WelcomeSettingDialog welcomeSettingDialog = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_interface_config,container,false);
        initView(v);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentListener) context;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    private void initView(View view){
        rbLongpress = view.findViewById(R.id.rb_longpress);
        rbThrice = view.findViewById(R.id.rb_thrice);
        rbTwice = view.findViewById(R.id.rb_twice);
        etLongpress = view.findViewById(R.id.et_time_interval_3);
        etThrice = view.findViewById(R.id.et_time_interval_2);
        etTwice = view.findViewById(R.id.et_time_interval_1);
        llLongpress = view.findViewById(R.id.ll_longpress);
        llThrice = view.findViewById(R.id.ll_thrice);
        llTwice = view.findViewById(R.id.ll_twice);
        llVolume = view.findViewById(R.id.ll_volume);
        llRegisterAnswer = view.findViewById(R.id.ll_register_answer);
        ll_developer = view.findViewById(R.id.ll_developer);
        llAD = view.findViewById(R.id.ll_ad);
        llOTA = view.findViewById(R.id.ll_ota);
        bt_testplay_sound = view.findViewById(R.id.bt_testplay_sound);
        bt_setting = view.findViewById(R.id.bt_setting);
        ivPlaySound = view.findViewById(R.id.bt_play_sound);

        btStart = view.findViewById(R.id.bt_start);
        swCancel = view.findViewById(R.id.sw_cancel_floor);
        swAlarm = view.findViewById(R.id.sw_alarm);
        swGreet = view.findViewById(R.id.sw_greet);
        swOutServerAlert = view.findViewById(R.id.sw_out_server_alert);
        swAnswerQuestion = view.findViewById(R.id.sw_answer_question);
        tvGreetDelay = view.findViewById(R.id.tv_greet_delay);
        tvVolume = view.findViewById(R.id.tv_volume);
        tvRegister = view.findViewById(R.id.tv_register);
        //spinner
        spWakeUp = view.findViewById(R.id.sp_wakeup);
        tvVersion = view.findViewById(R.id.tv_version);
        //设置版本信息
        if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_RELAY){
            tvVersion.setText("version "+ Constants.getVersionName(getActivity().getApplicationContext())+"-R");
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL){
            tvVersion.setText("version "+ Constants.getVersionName(getActivity().getApplicationContext())+"-O");
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_SAMSUNG){
            tvVersion.setText("version "+ Constants.getVersionName(getActivity().getApplicationContext())+"-S");
        }

        ivPlaySound.setOnClickListener(this);
        btStart.setOnClickListener(this);
        rbTwice.setOnCheckedChangeListener(this);
        rbThrice.setOnCheckedChangeListener(this);
        rbLongpress.setOnCheckedChangeListener(this);
        llVolume.setOnClickListener(this);
        llRegisterAnswer.setOnClickListener(this);
        ll_developer.setOnClickListener(this);
        llAD.setOnClickListener(this);
        llOTA.setOnClickListener(this);
        bt_testplay_sound.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
        //为下拉列表定义一个适配器
        List<String> list = new ArrayList<>();
        list.add("咚咚声");
        list.add("唤醒音1");
        list.add("唤醒音2");
        list.add("唤醒音3");
        list.add("唤醒音4");

        sp_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
        //设置下拉列表下拉时的菜单样式
        sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器添加到下拉列表上
        spWakeUp.setAdapter(sp_adapter);
        //添加监听器，为下拉列表设置事件的响应
        spWakeUp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //L.i("选择的唤醒声为:" + sp_adapter.getItem(position));
                /* 将 spinnertext 显示^*/
                parent.setVisibility(View.VISIBLE);
                MainApplication.WAKE_UP_SOUND = sound[position];
                soundPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spWakeUp.setOnTouchListener(new Spinner.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // 将mySpinner隐藏
                //v.setVisibility(View.INVISIBLE);
                return false;
            }
        });
        //焦点改变事件处理
        spWakeUp.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                v.setVisibility(View.VISIBLE);
                L.i( "Spinner FocusChange事件被触发！");
            }
        });

        swCancel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (MainApplication.PROTOCOL_VERSION!=Constants.PROTOCOL_SAMSUNG){
                        llLongpress.setVisibility(View.VISIBLE);
                    }else {
                        llLongpress.setVisibility(View.GONE);
                    }
                    llThrice.setVisibility(View.VISIBLE);
                    llTwice.setVisibility(View.VISIBLE);
                    rbTwice.setChecked(true);
                    rbThrice.setChecked(false);
                    rbLongpress.setChecked(false);
                    etTwice.setText("600");
                }else {
                    llLongpress.setVisibility(View.GONE);
                    llThrice.setVisibility(View.GONE);
                    llTwice.setVisibility(View.GONE);
                }
            }
        });

        swGreet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                L.i("MainApplication.currentPage:"+MainApplication.currentPage);
                if (isChecked && MainApplication.currentPage==2){
                    //showWelcomeSettingDialog();
                    bt_testplay_sound.setImageResource(R.mipmap.play_checked);
                    bt_testplay_sound.setClickable(true);
                    bt_testplay_sound.setFocusable(true);
                }else {
                    bt_testplay_sound.setImageResource(R.mipmap.play_onchecked);
                    bt_testplay_sound.setClickable(false);
                    bt_testplay_sound.setFocusable(false);
                }
            }
        });
        //初始化板子上的数据
        try {
            initOriginalData();
        }catch (Exception e){
            L.i("版本不对应 e:"+e.toString());
        }
    }

    private void initOriginalData(){
        //唤醒提示音
        String wakeupNum = SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getStringValueByKey(WAKEUP_NUM);
        spWakeUp.setSelection(0);
        for (int i=0;i<sound.length;i++){
            if (wakeupNum.equals(sound[i])){
                spWakeUp.setSelection(i);
                break;
            }
        }
        //语音取消楼层
        swCancel.setChecked(SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getBooleanValueByKey(CANCEL_CONFIG,false));
        //取消楼层的方式
        String cancel_floor_serial = SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getStringValueByKey(CANCEL_FLOOR_SELECTED);
        String millisecondCancel = MainApplication.TIME_INTERVAL;
        if (null==cancel_floor_serial || "".equals(cancel_floor_serial)){
            L.i("无缓存数据 cancel_floor_serial");
            return;
        }
        String cancelMode = Constants.CANCEL_TWICE;   //默认值
        //光耦合版本
        if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL){
            cancelMode = cancel_floor_serial.substring(6,8);
            if (cancelMode.equals("01")){
                //长按
                millisecondCancel = String.valueOf(Integer.parseInt(cancel_floor_serial.substring(4,6)) * 1000);
            }else {
                millisecondCancel = cancel_floor_serial.substring(8,11);
            }

        }
        //继电器版本
        else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_RELAY){
            cancelMode = cancel_floor_serial.substring(6,8);
            MainApplication.TIME_MULTIPEL_RELAY = cancel_floor_serial.substring(2,4);
            if (cancelMode.equals(Constants.CANCEL_LONGPRESS)){
                millisecondCancel = String.valueOf(DigitalComputationUtil.covert(cancel_floor_serial.substring(2,4))*100);
            }else {
                millisecondCancel = String.valueOf(DigitalComputationUtil.covert(cancel_floor_serial.substring(4,6))*100);
            }


        }
        //三星版本
        else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_SAMSUNG){
            millisecondCancel = SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getStringValueByKey(INTVAL_TIME);
            cancelMode = cancel_floor_serial.substring(2,4);
        }
        MainApplication.CANCEL_MODEL = cancelMode;
        switch (cancelMode){
            case Constants.CANCEL_TWICE:
                rbTwice.setChecked(true);
                etTwice.setText(millisecondCancel);
                break;
            case Constants.CANCEL_THRICE:
                rbThrice.setChecked(true);
                etThrice.setText(millisecondCancel);
                break;
            case Constants.CANCEL_LONGPRESS:
                rbLongpress.setChecked(true);
                etLongpress.setText(millisecondCancel);
                break;
        }

        //语音呼救报警
        swAlarm.setChecked(SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getBooleanValueByKey(ALARM_CONFIG,false));
        //迎宾
        swGreet.setChecked(SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getBooleanValueByKey(WELCOME_CONFIG,false));
        //无服务提醒
        swOutServerAlert.setChecked(SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getBooleanValueByKey(NOFLOOR_CONFIG,false));
        //音量
        MainApplication.VOLUME_CONFIG = SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getStringValueByKey(VOLUME);
        tvVolume.setText(MainApplication.VOLUME_CONFIG);
        //答疑
        float ambigous_score = SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getFloatValueByKey(AMBIGOUS_SCORE,Constants.CLOSE_ANSWER_QUESTION);
        if (ambigous_score==Constants.OPEN_ANSWER_QUESTION){
            swAnswerQuestion.setChecked(true);
        }else if (ambigous_score==Constants.CLOSE_ANSWER_QUESTION){
            swAnswerQuestion.setChecked(false);
        }
        //登记楼层应答
        String url = SharedPreferencesUtils.getInstance(getActivity().getApplicationContext()).getStringValueByKey(REGISTER_ANSWER_URL);
        for (int i=0;i<Constants.REGISTER_ANSWER_LIST.length;i++){
            if (url.equals(Constants.REGISTER_ANSWER_LIST[i])){
                tvRegister.setText(registerAnswers[i]);
                MainApplication.REGISTER_ANSWER = Constants.REGISTER_ANSWER_LIST[i];    //设置登记应答语
            }
        }
    }

    //语音取消的三种操作
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.rb_twice:
                if (isChecked){
                    rbThrice.setChecked(false);
                    rbLongpress.setChecked(false);
                    etTwice.setText("600");
                    etThrice.setText("");
                    etLongpress.setText("");
                    etThrice.setFocusable(false);
                    etThrice.setFocusableInTouchMode(false);
                    etLongpress.setFocusable(false);
                    etLongpress.setFocusableInTouchMode(false);
                    etTwice.setFocusableInTouchMode(true);
                    etTwice.setFocusable(true);
                    etTwice.requestFocus();
                    MainApplication.CANCEL_MODEL = Constants.CANCEL_TWICE;
                }

                break;
            case R.id.rb_thrice:
                if (isChecked){
                    rbTwice.setChecked(false);
                    rbLongpress.setChecked(false);
                    etTwice.setText("");
                    etThrice.setText("600");
                    etLongpress.setText("");
                    etThrice.setFocusable(true);
                    etThrice.setFocusableInTouchMode(true);
                    etLongpress.setFocusable(false);
                    etLongpress.setFocusableInTouchMode(false);
                    etTwice.setFocusable(false);
                    etTwice.setFocusableInTouchMode(false);
                    etThrice.requestFocus();
                    MainApplication.CANCEL_MODEL = Constants.CANCEL_THRICE;
                }
                break;
            case R.id.rb_longpress:
                if (isChecked){
                    rbThrice.setChecked(false);
                    rbTwice.setChecked(false);
                    etTwice.setText("");
                    etThrice.setText("");
                    etLongpress.setText("3000");
                    etThrice.setFocusable(false);
                    etThrice.setFocusableInTouchMode(false);
                    etLongpress.setFocusable(true);
                    etLongpress.setFocusableInTouchMode(true);
                    etTwice.setFocusable(false);
                    etTwice.setFocusableInTouchMode(false);
                    etLongpress.requestFocus();
                    MainApplication.CANCEL_MODEL = Constants.CANCEL_LONGPRESS;
                }
                break;
            default:
                break;
        }
    }

    private static int developFlag = 0;

   @Override
    public void onClick(View v) {
       if (!OnClickUtils.isFastDoubleClick(v.getId())){
           switch (v.getId()){
               case R.id.ll_volume:
                   showMenuDialog(volumes,tvVolume);
                   break;
               case R.id.ll_register_answer:
                   showMenuDialog(registerAnswers,tvRegister);
                   break;
               case R.id.ll_developer:
                   //进入adbTestActivity
                   developFlag++;
                   if (developFlag>3){
                       developFlag = 0;
                       startActivity(new Intent(getActivity(), AdbDevelopmentActivity.class));
                   }
                   break;
               case R.id.bt_testplay_sound:
                   //试听tts
                   L.i("试听tts");
                   SoundPlayUtils.getInstance(getActivity()).playSound(getContext().getFilesDir().getPath()+"/tts.wav");
                   break;
               case R.id.bt_setting:
                   //弹出迎宾词设置窗口
                   showWelcomeSettingDialog();
                   break;
               case R.id.ll_ad:
                   //设置广告语页面
                   startActivity(new Intent(getActivity(), AdvertisingSettingActivity.class));
                   break;
               case R.id.bt_play_sound:
                   SoundPlayUtils.getInstance(getContext().getApplicationContext()).playSound(SoundPlayUtils.soundList[soundPosition]);
                   break;
               case R.id.ll_ota:
                   startActivity(new Intent(getActivity(), OTAActivity.class));
                   break;
               case R.id.bt_start:
                   MainApplication.is_cancel_open = swCancel.isChecked();
                   MainApplication.is_alarm_open = swAlarm.isChecked();
                   MainApplication.is_welcome_open = swGreet.isChecked();
                   MainApplication.is_nofloor_server_open = swOutServerAlert.isChecked();
                   if (swAnswerQuestion.isChecked()){
                       MainApplication.ANSWER_QUESTION = Constants.OPEN_ANSWER_QUESTION;
                   }else {
                       MainApplication.ANSWER_QUESTION = Constants.CLOSE_ANSWER_QUESTION;
                   }

                   String timeInterVal;
                   if (swCancel.isChecked()){
                       switch (MainApplication.CANCEL_MODEL){
                           case Constants.CANCEL_TWICE:
                               timeInterVal = etTwice.getText().toString();
                               if (!timeInterVal.equals("")){
                                   if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL||MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_SAMSUNG){
                                       //光耦合或三星版本
                                       if (timeInterVal.length()==1){
                                           timeInterVal = "00"+timeInterVal;
                                       }else if (timeInterVal.length()==2){
                                           timeInterVal = "0"+timeInterVal;
                                       }
                                       MainApplication.TIME_INTERVAL = timeInterVal;
                                   }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_RELAY){
                                       //继电器版本
                                       if (timeInterVal.length()!=3){
                                           ToastUtil.showToast(getContext().getApplicationContext(),"请设置3位数字");
                                           return;
                                       }else {
                                           int timeInterval = Integer.parseInt(timeInterVal)/100;
                                           timeInterVal = DigitalComputationUtil.decimalToHex(timeInterval);
                                           if (timeInterVal.length()==1){
                                               timeInterVal = "0"+timeInterVal;
                                           }
                                           MainApplication.TIME_INTERVAL_RELAY = timeInterVal;
                                       }
                                   }

                               }else {
                                   ToastUtil.showToast(getContext().getApplicationContext(),"请勿填写空数据");
                                   return;
                               }

                               break;
                           case Constants.CANCEL_THRICE:
                               timeInterVal = etThrice.getText().toString();
                               if (!timeInterVal.equals("")){
                                   if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL||MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_SAMSUNG){
                                       //光耦合或三星版本
                                       if (timeInterVal.length()==1){
                                           timeInterVal = "00"+timeInterVal;
                                       }else if (timeInterVal.length()==2){
                                           timeInterVal = "0"+timeInterVal;
                                       }
                                       MainApplication.TIME_INTERVAL = timeInterVal;
                                   }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_RELAY){
                                       //继电器版本
                                       if (timeInterVal.length()!=3){
                                           ToastUtil.showToast(getContext().getApplicationContext(),"请设置3位数字");
                                           return;
                                       }else {
                                           int timeInterval = Integer.parseInt(timeInterVal)/100;
                                           timeInterVal = DigitalComputationUtil.decimalToHex(timeInterval);
                                           if (timeInterVal.length()==1){
                                               timeInterVal = "0"+timeInterVal;
                                           }
                                           MainApplication.TIME_INTERVAL_RELAY = timeInterVal;
                                       }
                                   }
                               }else {
                                   ToastUtil.showToast(getContext().getApplicationContext(),"请勿填写空数据");
                                   return;
                               }
                               break;
                           case Constants.CANCEL_LONGPRESS:
                               timeInterVal = etLongpress.getText().toString();
                               if (!timeInterVal.equals("")){
                                   if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL){
                                       //光耦合版本
                                       if (timeInterVal.length()!=4){
                                           ToastUtil.showToast(getContext().getApplicationContext(),"请填写4位数字");
                                           return;
                                       }else {
                                           int multipel = Integer.parseInt(timeInterVal)/1000;
                                           MainApplication.TIME_MULTIPEL = "0"+multipel;
                                       }
                                   }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_RELAY){
                                       //继电器版本
                                       if (timeInterVal.length()!=4){
                                           ToastUtil.showToast(getContext().getApplicationContext(),"请设置4位数字");
                                           return;
                                       }else {
                                           int timeInterval = Integer.parseInt(timeInterVal)/100;
                                           timeInterVal = DigitalComputationUtil.decimalToHex(timeInterval);
                                           if (timeInterVal.length()==1){
                                               timeInterVal = "0"+timeInterVal;
                                           }
                                           L.i("16jinzhi:"+timeInterVal);
                                           MainApplication.TIME_MULTIPEL_RELAY = timeInterVal;
                                       }
                                   }

                               }else {
                                   ToastUtil.showToast(getContext().getApplicationContext(),"请勿填写空数据");
                                   return;
                               }
                               break;
                       }
                   }

                   listener.onNext(3);
                   break;
               default:
                   break;
           }
       }
    }

    private void showMenuDialog(String[] items, TextView textView) {
        QMUIDialog qmuiDialog = new QMUIDialog.MenuDialogBuilder(getActivity())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (textView.getId()==R.id.tv_volume){
                            textView.setText(items[which]+"%");
                            MainApplication.VOLUME_CONFIG = items[which];   //设置音量
                        } else if (textView.getId()==R.id.tv_register){
                            textView.setText(items[which]);
                            MainApplication.REGISTER_ANSWER = Constants.REGISTER_ANSWER_LIST[which];    //设置登记应答语
                        }
                    }
                }).create(mCurrentDialogStyle);
        qmuiDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                L.i("取消");
            }
        });
        qmuiDialog.show();
    }

    //自定义迎宾语
    private void showWelcomeSettingDialog(){
        welcomeSettingDialog = new WelcomeSettingDialog(getActivity(), new WelcomeSettingDialog.WelcomeDialogListener() {
            @Override
            public void onCancel() {
                L.i("取消生成自定义迎宾词");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //swGreet.setChecked(false);
                        welcomeSettingDialog.destory();
                    }
                });

            }

            @Override
            public void onTTSGetSuccess(byte[] audioData,int second) {
                L.i("获取自定义音频成功 延时："+second);
                MainApplication.GREET_OPEN_DOOR_DELAY = String.valueOf(second);
                MainApplication.customTTs = audioData;
                FileUtil.setFile(getActivity().getApplicationContext(),"tts.wav",audioData);
                welcomeSettingDialog.destory();
                welcomeSettingDialog.cancel();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        L.i("Thread:"+Thread.currentThread());
                        ToastUtil.showToast(getContext().getApplicationContext(),"迎宾词生成成功！");
                        swGreet.setChecked(true);
                    }
                });
            }
        });
        welcomeSettingDialog.show();
        //延迟弹出键盘
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeSettingDialog.getEtFocus();
            }
        },300);

    }

}
