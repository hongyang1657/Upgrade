package ai.fitme.ayahupgrade.ui.view;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.model.GenerateTTsOnlineModel;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SharedPreferencesUtils;
import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.OPENDOOR_WAIT;
import static ai.fitme.ayahupgrade.utils.Constants.CUSTOM_GREET;

public class WelcomeSettingDialog extends Dialog {

    private Context mContext;
    private EditText etGreet;
    private ImageButton ibReduce;
    private ImageButton ibPlus;
    private TextView tvSecond;
    private TextView tvEnter;
    private TextView tvCancel;
    private ImageView ivLoading;
    private WelcomeDialogListener listener;
    private GenerateTTsOnlineModel generateTTsOnlineModel = null;
    private ObjectAnimator rota;
    private int second = 2;

    public WelcomeSettingDialog( @NonNull Context context, WelcomeDialogListener listener) {
        super(context);
        mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void destory(){
        if (generateTTsOnlineModel!=null){
            generateTTsOnlineModel.destory();
        }
    }

    private void initView(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_welcome_layout,null);
        setContentView(view);
        //初始化开门迎宾延时
        second = Integer.parseInt(SharedPreferencesUtils.getInstance(mContext.getApplicationContext()).getStringValueByKey(OPENDOOR_WAIT,"2"));

        etGreet = view.findViewById(R.id.et_greet);
        ibReduce = view.findViewById(R.id.ib_reduce);
        ibPlus = view.findViewById(R.id.ib_plus);
        tvSecond = view.findViewById(R.id.tv_greet_delay);
        tvEnter = view.findViewById(R.id.tv_enter);
        tvCancel = view.findViewById(R.id.tv_cancel);
        ivLoading = findViewById(R.id.iv_loading);

        etGreet.setFocusable(true);
        etGreet.setFocusableInTouchMode(true);
        etGreet.requestFocus();
        etGreet.setText(SharedPreferencesUtils.getInstance(mContext.getApplicationContext()).getStringValueByKey(CUSTOM_GREET,""));

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(false);   //点击外部，不关闭dialog

        rota = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 360f);
        rota.setDuration(1000);
        rota.setInterpolator(new LinearInterpolator());
        rota.setRepeatCount(-1);

//        setOnCancelListener(new OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                listener.onCancel();
//            }
//        });

        ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second = Integer.parseInt(tvSecond.getText().toString().trim().substring(0,1));
                if (second<9){
                    second++;
                    tvSecond.setText(second+"秒");
                }
            }
        });
        ibReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second = Integer.parseInt(tvSecond.getText().toString().trim().substring(0,1));
                if (second>1){
                    second--;
                    tvSecond.setText(second+"秒");
                }
            }
        });
        tvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoading();
                getTTsData();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                listener.onCancel();
            }
        });
    }

    private void getTTsData(){
        //在线生成自定义的tts
        String content = etGreet.getText().toString().trim() + mContext.getResources().getString(R.string.greet);
        if (generateTTsOnlineModel==null){
            generateTTsOnlineModel = new GenerateTTsOnlineModel(mContext);
        }
        generateTTsOnlineModel.generateTTs(content, new GenerateTTsOnlineModel.OnTTsGenerateListener() {
            @Override
            public void onError() {
                L.i("生成失败");
                //stopLoading();
                cancel();
                listener.onCancel();
                //Toast.makeText(mContext, "生成迎宾词失败，请检查网络后重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(byte[] audioData) {
                //L.i("获取音频成功:"+ Arrays.toString(audioData));
                SharedPreferencesUtils.getInstance(mContext.getApplicationContext()).setStringKeyValue(CUSTOM_GREET,etGreet.getText().toString().trim());
                listener.onTTSGetSuccess(audioData,second);
                stopLoading();
            }
        });
    }

    //开始loading
    private void startLoading(){
        if (ivLoading.getVisibility() == View.GONE){
            ivLoading.setVisibility(View.VISIBLE);
            tvEnter.setVisibility(View.GONE);
            rota.start();
        }
    }

    private void stopLoading(){
        if (ivLoading.getVisibility() == View.VISIBLE){
            ivLoading.setVisibility(View.GONE);
            tvEnter.setVisibility(View.VISIBLE);
            rota.cancel();
        }
    }
    public void getEtFocus(){
        etGreet.setFocusable(true);
        etGreet.setFocusableInTouchMode(true);
        etGreet.requestFocus();
        //调用系统输入法
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etGreet, 0);
    }

    public interface WelcomeDialogListener{
        void onCancel();
        void onTTSGetSuccess(byte[] audioData,int second);
    }
}
