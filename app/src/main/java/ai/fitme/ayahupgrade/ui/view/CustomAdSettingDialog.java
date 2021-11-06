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
import android.widget.Toast;

import androidx.annotation.NonNull;

import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.model.GenerateTTsOnlineModel;
import ai.fitme.ayahupgrade.utils.L;

public class CustomAdSettingDialog extends Dialog {

    private Context mContext;
    private EditText etGreet;
    private TextView tvEnter;
    private TextView tvCancel;
    private ImageView ivLoading;
    private CustomAdDialogListener listener;
    private GenerateTTsOnlineModel generateTTsOnlineModel = null;
    private ObjectAnimator rota;

    public CustomAdSettingDialog(@NonNull Context context, CustomAdDialogListener listener) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_setting_ad_layout,null);
        setContentView(view);
        etGreet = view.findViewById(R.id.et_greet);
        tvEnter = view.findViewById(R.id.tv_enter);
        tvCancel = view.findViewById(R.id.tv_cancel);
        ivLoading = findViewById(R.id.iv_loading);

        etGreet.setFocusable(true);
        etGreet.setFocusableInTouchMode(true);
        etGreet.requestFocus();

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

        tvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收起键盘
                //hideSoftInput();

                if ("".equals(etGreet.getText().toString().trim())){
                    Toast.makeText(mContext, "广告词不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
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
        String content = etGreet.getText().toString().trim();
        if (generateTTsOnlineModel==null){
            generateTTsOnlineModel = new GenerateTTsOnlineModel(mContext);
        }
        generateTTsOnlineModel.generateTTs(content, new GenerateTTsOnlineModel.OnTTsGenerateListener() {
            @Override
            public void onError() {
                L.i("生成失败");
                listener.onCancel();
                cancel();
                stopLoading();
                Toast.makeText(mContext, "生成迎宾词失败，请检查网络后重试", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess(byte[] audioData) {
                //L.i("获取音频成功:"+ Arrays.toString(audioData));
                listener.onTTSGetSuccess(audioData,content);
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

    public void hideSoftInput(){
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(etGreet.getWindowToken(),0);
    }

    public interface CustomAdDialogListener{
        void onCancel();
        void onTTSGetSuccess(byte[] audioData, String adContent);
    }
}
