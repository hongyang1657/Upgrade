package ai.fitme.ayahupgrade.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.bean.ElevatorConfig;
import ai.fitme.ayahupgrade.bean.ModelConfig;
import ai.fitme.ayahupgrade.bean.SerialConfigRoot;
import ai.fitme.ayahupgrade.bean.TTSConfig;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.L;

public class GenerateJsonData {

    //生成serial_IO_config.json
    public static String getSerialIOConfigData(Context context){
        String[] floorArr = new String[MainApplication.floorList.size()];
        String[] dataFloorArr = new String[MainApplication.dataFloorList.size()];
        Gson gson = new Gson();
        SerialConfigRoot serialConfigRoot = null;
        //选择配置的版本
        if (MainApplication.PROTOCOL_VERSION== Constants.PROTOCOL_RELAY){
            serialConfigRoot = gson.fromJson(Constants.getFileContent(context,"serial_IO_config_relay.json"),SerialConfigRoot.class);
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL){       //光耦合版本
            serialConfigRoot = gson.fromJson(Constants.getFileContent(context,"serial_IO_config.json"),SerialConfigRoot.class);
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_SAMSUNG){       //三星版本
            serialConfigRoot = gson.fromJson(Constants.getFileContent(context,"SX_serial_IO_config.json"),SerialConfigRoot.class);
        }

        serialConfigRoot.getNumMinus2().setFloor(MainApplication.floorList.toArray(floorArr));
        serialConfigRoot.getNumMinus2().setDataFloor(MainApplication.dataFloorList.toArray(dataFloorArr));
        serialConfigRoot.getNumMinus2().setFloorMax(String.valueOf(MainApplication.dataFloorList.size()));
        serialConfigRoot.getNumMinus2().setWakeup_num(MainApplication.WAKE_UP_SOUND);
        serialConfigRoot.getNumMinus2().setCancel_config(MainApplication.is_cancel_open);
        serialConfigRoot.getNumMinus2().setAlarm_config(MainApplication.is_alarm_open);
        serialConfigRoot.getNumMinus2().setNofloor_config(MainApplication.is_nofloor_server_open);
        serialConfigRoot.getNumMinus2().setWelcome_config(MainApplication.is_welcome_open);
        serialConfigRoot.getNumMinus2().setVolume(MainApplication.VOLUME_CONFIG);
        serialConfigRoot.getNumMinus2().setOpendoor_wait(MainApplication.GREET_OPEN_DOOR_DELAY);

        if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL){
            /**
             * ------------ 光耦合版本-------------
             */
            //设置开门关门
            String closeDoorSerial = serialConfigRoot.getNum1().getSerial();
            String openDoorSerial = serialConfigRoot.getNum3().getSerial();
            closeDoorSerial = closeDoorSerial.substring(0,2) + MainApplication.CLOSE_DOOR_INDEX + closeDoorSerial.substring(4);
            openDoorSerial = openDoorSerial.substring(0,2) + MainApplication.OPEN_DOOR_INDEX + openDoorSerial.substring(4);
            serialConfigRoot.getNum1().setSerial(closeDoorSerial);
            serialConfigRoot.getNum3().setSerial(openDoorSerial);
            //设置取消的方式（长按，双击，三击）
            String cancelSerial = serialConfigRoot.getNum2().getSerial();
            cancelSerial = cancelSerial.substring(0,4) + MainApplication.TIME_MULTIPEL + MainApplication.CANCEL_MODEL + MainApplication.TIME_INTERVAL;
            serialConfigRoot.getNum2().setSerial(cancelSerial);
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_RELAY){
            /**
             * -------------继电器版本-------------
             */
            String closeDoorSerial = serialConfigRoot.getNum1().getSerial();
            String openDoorSerial = serialConfigRoot.getNum3().getSerial();
            String closeDoorSerialHead = closeDoorSerial.substring(0,2);
            String openDoorSerialHead = openDoorSerial.substring(0,2);
            //数值超过32时，serial头两位设为B1
            if (Integer.parseInt(MainApplication.CLOSE_DOOR_INDEX)<33){
                closeDoorSerialHead = "B0";
            }else {
                closeDoorSerialHead = "B1";
            }
            if (Integer.parseInt(MainApplication.OPEN_DOOR_INDEX)<33){
                openDoorSerialHead = "B0";
            }else {
                openDoorSerialHead = "B1";
            }
            closeDoorSerial = closeDoorSerialHead + MainApplication.CLOSE_DOOR_INDEX + closeDoorSerial.substring(4);
            openDoorSerial = openDoorSerialHead + MainApplication.OPEN_DOOR_INDEX + openDoorSerial.substring(4);
            serialConfigRoot.getNum1().setSerial(closeDoorSerial);
            serialConfigRoot.getNum3().setSerial(openDoorSerial);
            //设置取消的方式（长按，双击，三击）
            String cancelSerial = serialConfigRoot.getNum2().getSerial();
            cancelSerial = cancelSerial.substring(0,2) + MainApplication.TIME_MULTIPEL_RELAY + MainApplication.TIME_INTERVAL_RELAY + MainApplication.CANCEL_MODEL;
            serialConfigRoot.getNum2().setSerial(cancelSerial);
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_SAMSUNG){
            /**
             * ---------------三星版本--------------
             */
            serialConfigRoot.getNumMinus2().setIntval_time(MainApplication.TIME_INTERVAL);
            String cancelSerial = serialConfigRoot.getNum2().getSerial();
            cancelSerial = cancelSerial.substring(0,2) + MainApplication.CANCEL_MODEL + cancelSerial.substring(4);
            serialConfigRoot.getNum2().setSerial(cancelSerial);
        }

        //L.i("root = "+new Gson().toJson(serialConfigRoot));
        return new Gson().toJson(serialConfigRoot);
    }

    //生成model_config.json
    public static String getModelConfigData(Context context){
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(Constants.getFileContent(context,"model_config.json")).getAsJsonArray();//获取JsonArray对象
        ArrayList<ModelConfig> beans = new ArrayList<>();
        for (JsonElement bean : jsonElements) {
            ModelConfig modelConfig= gson.fromJson(bean, ModelConfig.class);//解析
            beans.add(modelConfig);
        }
        beans.get(0).setAmbiguous_score(MainApplication.ANSWER_QUESTION);
        return new Gson().toJson(beans);
    }

    //生成ttsConfig.json
    public static String getTTsConfigData(Context context){
        Gson gson = new Gson();
        TTSConfig ttsConfig = gson.fromJson(Constants.getFileContent(context,"ttsConfig.json"), TTSConfig.class);
        TTSConfig.Content content6 = new TTSConfig.Content();
        List<String[]> urls = new ArrayList<>();
        urls.add(new String[]{MainApplication.REGISTER_ANSWER});
        content6.setUrls(urls);
        content6.setMode("random");
        ttsConfig.setContent6(content6);
        return new Gson().toJson(ttsConfig).replace("\\u003c","<").replace("\\u003e",">");   //尖括号转码问题
    }

    //生成advertisement.json
    public static String getAdvertisementData(Map<String,List<String>> stringListMap){
        JsonObject jsonObject = new JsonObject();

        for (Iterator i = stringListMap.keySet().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof String) {
                String key = (String) obj;
                L.i("Key = " + key + "  ----  " + "Value = " + stringListMap.get(key));
                JsonObject names = new JsonObject();
                JsonArray jsonArray = new JsonArray();
                for (int j=0;j<stringListMap.get(key).size();j++){
                    jsonArray.add(stringListMap.get(key).get(j));
                    names.add("names",jsonArray);
                }
                jsonObject.add(key.substring(0,key.length()-1),names);
            }
        }
        return jsonObject.toString();
    }

    /**
     * ------------------------------------根据云数据库生成本地配置文件---------------------------------------
     */
    public static String getSerialIOConfigData(Context context,ElevatorConfig elevatorConfig){
        String[] floorArr = new String[elevatorConfig.getConfig_Floor().size()];
        String[] dataFloorArr = new String[elevatorConfig.getConfig_DataFloor().size()];
        Gson gson = new Gson();
        SerialConfigRoot serialConfigRoot = null;
        //选择配置的版本
        if (MainApplication.PROTOCOL_VERSION== Constants.PROTOCOL_RELAY){
            serialConfigRoot = gson.fromJson(Constants.getFileContent(context,"serial_IO_config_relay.json"),SerialConfigRoot.class);
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL){       //光耦合版本
            serialConfigRoot = gson.fromJson(Constants.getFileContent(context,"serial_IO_config.json"),SerialConfigRoot.class);
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_SAMSUNG){       //三星版本
            serialConfigRoot = gson.fromJson(Constants.getFileContent(context,"SX_serial_IO_config.json"),SerialConfigRoot.class);
        }

        serialConfigRoot.getNumMinus2().setFloor(elevatorConfig.getConfig_Floor().toArray(floorArr));
        serialConfigRoot.getNumMinus2().setDataFloor(elevatorConfig.getConfig_DataFloor().toArray(dataFloorArr));
        serialConfigRoot.getNumMinus2().setFloorMax(String.valueOf(elevatorConfig.getConfig_DataFloor().size()));
        serialConfigRoot.getNumMinus2().setWakeup_num(elevatorConfig.getWakeup_num());
        serialConfigRoot.getNumMinus2().setCancel_config(elevatorConfig.getCancel_config());
        serialConfigRoot.getNumMinus2().setAlarm_config(elevatorConfig.getAlarm_config());
        serialConfigRoot.getNumMinus2().setNofloor_config(elevatorConfig.getNofloor_config());
        serialConfigRoot.getNumMinus2().setWelcome_config(elevatorConfig.getWelcome_config());
        serialConfigRoot.getNumMinus2().setVolume(elevatorConfig.getVolume());
        serialConfigRoot.getNumMinus2().setOpendoor_wait(elevatorConfig.getOpendoor_wait());

        if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_OPTICAL){
            /**
             * ------------ 光耦合版本-------------
             */
            //设置开门关门
            serialConfigRoot.getNum1().setSerial(elevatorConfig.getClosedoor_serial());
            serialConfigRoot.getNum3().setSerial(elevatorConfig.getOpendoor_serial());
            //设置取消的方式（长按，双击，三击）
            serialConfigRoot.getNum2().setSerial(elevatorConfig.getCancel_floor_serial());
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_RELAY){
            /**
             * -------------继电器版本-------------
             */
            serialConfigRoot.getNum1().setSerial(elevatorConfig.getClosedoor_serial());
            serialConfigRoot.getNum3().setSerial(elevatorConfig.getOpendoor_serial());
            //设置取消的方式（长按，双击，三击）
            serialConfigRoot.getNum2().setSerial(elevatorConfig.getCancel_floor_serial());
        }else if (MainApplication.PROTOCOL_VERSION==Constants.PROTOCOL_SAMSUNG){
            /**
             * ---------------三星版本--------------
             */
            serialConfigRoot.getNumMinus2().setIntval_time(elevatorConfig.getIntval_time());
            serialConfigRoot.getNum2().setSerial(elevatorConfig.getCancel_floor_serial());
        }

        //L.i("root = "+new Gson().toJson(serialConfigRoot));
        return new Gson().toJson(serialConfigRoot);
    }


    //生成model_config.json
    public static String getModelConfigData(Context context,ElevatorConfig elevatorConfig){
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(Constants.getFileContent(context,"model_config.json")).getAsJsonArray();//获取JsonArray对象
        ArrayList<ModelConfig> beans = new ArrayList<>();
        for (JsonElement bean : jsonElements) {
            ModelConfig modelConfig= gson.fromJson(bean, ModelConfig.class);//解析
            beans.add(modelConfig);
        }
        beans.get(0).setAmbiguous_score(elevatorConfig.getAmbiguous_score());
        return new Gson().toJson(beans);
    }

    public static String getTTsConfigData(Context context,ElevatorConfig elevatorConfig){
        Gson gson = new Gson();
        TTSConfig ttsConfig = gson.fromJson(Constants.getFileContent(context,"ttsConfig.json"), TTSConfig.class);
        TTSConfig.Content content6 = new TTSConfig.Content();
        List<String[]> urls = new ArrayList<>();
        urls.add(new String[]{elevatorConfig.getRegister_answer()});
        content6.setUrls(urls);
        content6.setMode("random");
        ttsConfig.setContent6(content6);
        return new Gson().toJson(ttsConfig).replace("\\u003c","<").replace("\\u003e",">");   //尖括号转码问题
    }
}
