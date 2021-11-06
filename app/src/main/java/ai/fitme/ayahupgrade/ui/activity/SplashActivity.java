package ai.fitme.ayahupgrade.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.bean.ProfileInfo;
import ai.fitme.ayahupgrade.ui.activity.login.LoginActivity;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.PermissionsUtils;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;
import ai.fitme.ayahupgrade.utils.UserUtil;

/**
 * 启动首页
 */
public class SplashActivity extends Activity {

    //获取本地用户信息，判断当前是否为登录状态
    private ProfileInfo user;
    //保存用户信息到app
    private MainApplication app;
    //权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO};

    //消息处理
    @SuppressLint("HandlerLeak")
    private android.os.Handler handler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent enterIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(enterIntent);
                    finish();
                    break;
                case 1:
                    app.setUser(user);
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    //Intent mainIntent = new Intent(SplashActivity.this, KukaDataCollectionActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_splash);
        app = (MainApplication) getApplication();
        initWindow();
        boolean isPermissionPassed = PermissionsUtils.getInstance().chekPermissions(this,permissions , new PermissionsUtils.IPermissionsResult() {
            @Override
            public void passPermissons() {
                L.i("---------------权限通过----------------");
                initUser();
            }

            @Override
            public void forbitPermissons() {
                L.i("---------------权限禁止----------------");
            }
        });
        L.i("isPermissionPassed:"+isPermissionPassed);
        if (isPermissionPassed){
            initUser();
        }
        FileUtil.makeDir(Constants.MODEL_ROOT_PATH);

    }


    //得到本地用户信息,判断该进入哪个界面
    private void initUser() {
        user = UserUtil.getUser();
        if (user.is_login()) {
            handler.sendEmptyMessageDelayed(1, 500);
            //L.i("user:"+user.toString());
        } else {
            handler.sendEmptyMessageDelayed(0, 500);
        }


    }

    //初始化屏幕宽高数据
    private void initWindow(){
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        app.setWidth(width);
        app.setHeight(height);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }
}
