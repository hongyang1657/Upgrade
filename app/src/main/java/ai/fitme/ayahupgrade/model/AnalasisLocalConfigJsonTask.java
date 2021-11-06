package ai.fitme.ayahupgrade.model;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import ai.fitme.ayahupgrade.bean.ModelConfig;
import ai.fitme.ayahupgrade.bean.SerialConfigRoot;
import ai.fitme.ayahupgrade.bean.TTSConfig;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.SharedPreferencesUtils;

public class AnalasisLocalConfigJsonTask{

    public static final int SERIAL_CONFIG_JSON = 1;
    public static final int MODEL_CONFIG_JSON = 2;
    public static final int TTS_CONFIG_JSON = 3;
    public static final int ADVERTISEMENT_CONFIG_JSON = 4;

    public static final String DATA_FLOOR = "DataFloor";
    public static final String FLOOR = "Floor";
    public static final String ALARM_CONFIG = "alarm_config";
    public static final String CANCEL_CONFIG = "cancel_config";
    public static final String NOFLOOR_CONFIG = "nofloor_config";
    public static final String REGISTER_CONFIG = "register_config";
    public static final String WELCOME_CONFIG = "welcome_config";
    public static final String OPENDOOR_WAIT = "opendoor_wait";
    public static final String VOLUME = "volume";
    public static final String WAKEUP_NUM = "wakeup_num";
    public static final String OPENDOOR_PIN = "opendoor_pin";
    public static final String OPENDOOR_VAL = "opendoor_val";
    public static final String REPEAT_TIME = "Repeat_time";
    public static final String INTVAL_TIME = "Intval_time";
    public static final String AMBIGOUS_SCORE = "ambiguous_score";
    public static final String REGISTER_ANSWER_URL = "registerAnswerUrl";
    public static final String OPENDOOR_SERIAL = "opendoor_serial";
    public static final String CLOSEDOOR_SERIAL = "closedoor_serial";
    public static final String CANCEL_FLOOR_SELECTED = "cancel_floor_selected";
    public static final String ADVERTISEMENT_JSON = "advertisement_json";

    public static void analasisJson(Context context,String strJson, int configType){
        switch (configType){
            case SERIAL_CONFIG_JSON:
                L.i("hongy json:"+strJson);
                SerialConfigRoot serialConfigRoot = new Gson().fromJson(strJson, SerialConfigRoot.class);

                SharedPreferencesUtils.getInstance(context).setStringKeyValue(DATA_FLOOR,Arrays.toString(serialConfigRoot.getNumMinus2().getDataFloor()));
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(FLOOR,Arrays.toString(serialConfigRoot.getNumMinus2().getFloor()));
                SharedPreferencesUtils.getInstance(context).setBooleanKeyValue(ALARM_CONFIG,serialConfigRoot.getNumMinus2().isAlarm_config());
                SharedPreferencesUtils.getInstance(context).setBooleanKeyValue(CANCEL_CONFIG,serialConfigRoot.getNumMinus2().isCancel_config());
                SharedPreferencesUtils.getInstance(context).setBooleanKeyValue(NOFLOOR_CONFIG,serialConfigRoot.getNumMinus2().isNofloor_config());
                SharedPreferencesUtils.getInstance(context).setBooleanKeyValue(REGISTER_CONFIG,serialConfigRoot.getNumMinus2().isRegister_config());
                SharedPreferencesUtils.getInstance(context).setBooleanKeyValue(WELCOME_CONFIG,serialConfigRoot.getNumMinus2().isWelcome_config());
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(OPENDOOR_WAIT,serialConfigRoot.getNumMinus2().getOpendoor_wait());
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(VOLUME,serialConfigRoot.getNumMinus2().getVolume());
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(WAKEUP_NUM,serialConfigRoot.getNumMinus2().getWakeup_num());
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(OPENDOOR_PIN,serialConfigRoot.getNumMinus2().getOpendoor_pin());
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(OPENDOOR_VAL,serialConfigRoot.getNumMinus2().getOpendoor_val());
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(REPEAT_TIME,serialConfigRoot.getNumMinus2().getRepeat_time());
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(INTVAL_TIME,serialConfigRoot.getNumMinus2().getIntval_time());
                //开关门serial
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(OPENDOOR_SERIAL,serialConfigRoot.getNum3().getSerial());
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(CLOSEDOOR_SERIAL,serialConfigRoot.getNum1().getSerial());
                //取消楼层选择
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(CANCEL_FLOOR_SELECTED,serialConfigRoot.getNum2().getSerial());
                break;
            case MODEL_CONFIG_JSON:
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonElements = jsonParser.parse(strJson).getAsJsonArray();
                ArrayList<ModelConfig> modelConfigs = new ArrayList<>();
                for (JsonElement jsonElement : jsonElements){
                    ModelConfig modelConfig = gson.fromJson(jsonElement,ModelConfig.class);
                    modelConfigs.add(modelConfig);
                }
                float ambiguous_score = modelConfigs.get(0).getAmbiguous_score();
                SharedPreferencesUtils.getInstance(context).setFloatKeyValue(AMBIGOUS_SCORE, ambiguous_score);
                L.i("hongy ambiguous_score:"+ambiguous_score);
                break;
            case TTS_CONFIG_JSON:
                TTSConfig ttsConfig = new Gson().fromJson(strJson,TTSConfig.class);
                String url = ttsConfig.getContent6().getUrls().get(0)[0];
                SharedPreferencesUtils.getInstance(context).setStringKeyValue(REGISTER_ANSWER_URL,url);     //登记楼层应答
                L.i("hongy url:"+url);
                break;
            case ADVERTISEMENT_CONFIG_JSON:

                SharedPreferencesUtils.getInstance(context).setStringKeyValue(ADVERTISEMENT_JSON,strJson);

                break;
            default:
                break;
        }
    }
}
