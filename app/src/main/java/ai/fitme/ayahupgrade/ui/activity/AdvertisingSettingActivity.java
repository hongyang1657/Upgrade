package ai.fitme.ayahupgrade.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.adapter.AdvertisingSettingAdapter;
import ai.fitme.ayahupgrade.bean.AdvertisingBean;
import ai.fitme.ayahupgrade.model.GenerateJsonData;
import ai.fitme.ayahupgrade.ui.view.CustomAdSettingDialog;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.FileUtil;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SharedPreferencesUtils;
import ai.fitme.ayahupgrade.utils.SoundPlayUtils;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;

import static ai.fitme.ayahupgrade.model.AnalasisLocalConfigJsonTask.ADVERTISEMENT_JSON;

public class AdvertisingSettingActivity extends Activity {

    private List<AdvertisingBean> advertisingBeans;
    private RecyclerView rvAdvertising;
    private AdvertisingSettingAdapter advertisingSettingAdapter;
    private CustomAdSettingDialog customAdSettingDialog;
    private Map<String,List<String>> advertisingMap;
    private List<String> ttsFileNameList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_ad_setting);
        initView();
    }

    private void initView() {
        initOriginalData();
        rvAdvertising = findViewById(R.id.rv_ad);
        advertisingSettingAdapter = new AdvertisingSettingAdapter(advertisingBeans,adSettingListener);
        rvAdvertising.setLayoutManager(new LinearLayoutManager(this));
        rvAdvertising.setAdapter(advertisingSettingAdapter);
        advertisingSettingAdapter.notifyData(advertisingBeans);
    }

    private void initOriginalData(){
        //初始化原始数据
        advertisingBeans = new ArrayList<>();
        String strJson = SharedPreferencesUtils.getInstance(getApplicationContext()).getStringValueByKey(ADVERTISEMENT_JSON);
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            Iterator<String> objs = jsonObject.keys();
            String key;
            while (objs.hasNext()){
                key = objs.next();
                JSONObject object = jsonObject.getJSONObject(key);
                JSONArray jsonArray = object.getJSONArray("names");
                for (int i=0;i<jsonArray.length();i++){
                    String ttsName = jsonArray.getString(i);
                    String tts = ttsName.substring(ttsName.indexOf("_")+1);
                    String adContent = tts.substring(0,tts.indexOf("_"));
                    AdvertisingBean advertisingBean = new AdvertisingBean(key+"楼",adContent,false,ttsName);
                    advertisingBeans.add(advertisingBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void click(View v){
        switch (v.getId()){
            case R.id.iv_add:
                addCard();
                break;
            case R.id.bt_start_config:
                //跳转配置页面
                ttsFileNameList = new ArrayList<>();
                advertisingMap = new HashMap<>();
                for (int i=0;i<advertisingBeans.size();i++){
                    if (null==advertisingMap.get(advertisingBeans.get(i).getFloor())){
                        if (null!=advertisingBeans.get(i).getTtsFilePath() && !"".equals(advertisingBeans.get(i).getTtsFilePath())){
                            List<String> list = new ArrayList<>();
                            list.add(advertisingBeans.get(i).getTtsFilePath());
                            advertisingMap.put(advertisingBeans.get(i).getFloor(),list);
                        }
                    }else {
                        List<String> list = advertisingMap.get(advertisingBeans.get(i).getFloor());
                        list.add(advertisingBeans.get(i).getTtsFilePath());
                        advertisingMap.put(advertisingBeans.get(i).getFloor(),list);
                    }
                    if (null!=advertisingBeans.get(i).getTtsFilePath()){
                        ttsFileNameList.add(advertisingBeans.get(i).getTtsFilePath());
                    }
                }

                //生成json格式字符串用于传输文件
                Intent intent = new Intent(this, SerialTransmissionActivity.class);
                intent.putExtra(Constants.PUSH_MODE,Constants.MODE_ADVERTISING);
                intent.putExtra(Constants.TARGET_PATH_ADVERTISEMENT,GenerateJsonData.getAdvertisementData(advertisingMap));   //广告词
                String[] audioNames = new String[ttsFileNameList.size()];
                intent.putExtra("audio_names",ttsFileNameList.toArray(audioNames));
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * item点击回调
     */
    private AdvertisingSettingAdapter.AdSettingListener adSettingListener = new AdvertisingSettingAdapter.AdSettingListener() {
        @Override
        public void onItemClickListener(int i) {
            showAdSettingDialog(i);
        }

        @Override
        public void deleteItem(int position) {
            advertisingSettingAdapter.removeItem(position);
        }

        @Override
        public void audition(int position) {
            //试听本地文件
            SoundPlayUtils.getInstance(getApplicationContext()).playSound(getFilesDir().getPath()+"/"+advertisingBeans.get(position).getTtsFilePath());
        }
    };

    /**
     * 添加楼层广告卡片
     */
    private void addCard(){
        showMenuDialog();
    }

    /**
     * 选择楼层dialog
     */
    private void showMenuDialog() {
        QMUIDialog qmuiDialog = new QMUIDialog.MenuDialogBuilder(this)
                .addItems(Constants.floorItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //添加楼层广告tts
                        advertisingBeans.add(new AdvertisingBean(Constants.floorItems[which],"",false,null));
                        advertisingSettingAdapter.addItem(advertisingBeans);
                    }
                }).create(com.qmuiteam.qmui.R.style.QMUI_Dialog);
        qmuiDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                L.i("取消");

            }
        });
        qmuiDialog.show();
    }


    /**
     * 设置广告语dialog
     */
    private void showAdSettingDialog(int position){
        customAdSettingDialog = new CustomAdSettingDialog(this, new CustomAdSettingDialog.CustomAdDialogListener() {
            @Override
            public void onCancel() {
                customAdSettingDialog.destory();
            }

            @Override
            public void onTTSGetSuccess(byte[] audioData, String content) {
                //音频文件存在本地
                String ttsFileName = null;
                if (!"".equals(content) && content.length()>2){
                    ttsFileName = advertisingBeans.get(position).getFloor()+"_"+content+"_"+System.currentTimeMillis()+".wav";
                }else if (!"".equals(content) && content.length()<=2){
                    ttsFileName = advertisingBeans.get(position).getFloor()+"_"+content+"_"+System.currentTimeMillis()+".wav";
                }

                boolean isSuccess = FileUtil.setFileAtRoot(ttsFileName,audioData);
                L.i("生成音频文件名："+ttsFileName+" 是否成功："+isSuccess);

                customAdSettingDialog.destory();
                customAdSettingDialog.cancel();
                //获取自定义广告词后更新list
                advertisingBeans.set(position,new AdvertisingBean(advertisingBeans.get(position).getFloor(),content,true,ttsFileName));
                advertisingSettingAdapter.notifyData(advertisingBeans);
            }
        });
        customAdSettingDialog.show();
        //延迟弹出键盘
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customAdSettingDialog.getEtFocus();
            }
        },300);
    }
}
