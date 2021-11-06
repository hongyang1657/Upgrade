package ai.fitme.ayahupgrade.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.File;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.ui.activity.login.LoginActivity;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.DataCleanManager;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SharedPreferencesUtils;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;
import ai.fitme.ayahupgrade.utils.ToastUtil;
import ai.fitme.ayahupgrade.utils.UserUtil;


public class SettingActivity extends Activity {

    private MainApplication app;
    private TextView tvCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_setting);
        app = (MainApplication) getApplication();
        initView();
        initData();
    }

    private void initView() {
        tvCache = findViewById(R.id.tv_cache);
    }

    private void initData(){
        try {
            String cacheSize = DataCleanManager.getTotalCacheSize(getApplicationContext());
            L.i("cache size:"+cacheSize);
            tvCache.setText(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void click(View v){
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_clear_cache:
                //TODO 清除缓存
                SharedPreferencesUtils.getInstance(getApplicationContext()).clearSharedPreference();
                //删除文件夹
                String path = Environment.getExternalStorageDirectory().toString() + Constants.MODEL_ROOT_PATH;
                FileUtil.deleteDirWihtFile(new File(path));
                ToastUtil.showToast(getApplicationContext(),"清除缓存成功！");
                break;
            case R.id.tv_logout:
                //退出登录
                ProfileInfo user = UserUtil.getUser();
                user.setIs_login(false);
                app.setUser(user);
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }
}
