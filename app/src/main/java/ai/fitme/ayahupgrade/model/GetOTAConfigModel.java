package ai.fitme.ayahupgrade.model;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ai.fitme.ayahupgrade.bean.ElevatorConfig;
import ai.fitme.ayahupgrade.utils.L;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class GetOTAConfigModel {

    public static void addConfig(String Elevator_type){
        ArrayList<String> dataFloor = new ArrayList<String>();
        dataFloor.add("1");
        dataFloor.add("2");
        dataFloor.add("3");
        dataFloor.add("4");
        ArrayList<String> floor = new ArrayList<>();
        floor.add("1");
        floor.add("2");
        floor.add("3");
        floor.add("4");
        ElevatorConfig elevatorConfig = new ElevatorConfig();
        elevatorConfig.setAlarm_config(true);
        elevatorConfig.setCancel_config(false);
        elevatorConfig.setCancel_floor_serial("CC051202");
        elevatorConfig.setClosedoor_serial("");
        elevatorConfig.setConfig_DataFloor(dataFloor);
        elevatorConfig.setConfig_Floor(floor);
        elevatorConfig.setElevator_type(Elevator_type);
        elevatorConfig.setNofloor_config(true);
        elevatorConfig.setOpendoor_serial("98");
        elevatorConfig.setOpendoor_wait("4");
        elevatorConfig.setVolume("40");
        elevatorConfig.setWakeup_num("1");
        elevatorConfig.setIntval_time("200");
        elevatorConfig.setWelcome_config(false);
        elevatorConfig.setAmbiguous_score(0f);
        elevatorConfig.setRegister_answer("<取消楼层>");
        elevatorConfig.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){

                }else {

                }
            }
        });
    }

    /**
     * 根据电梯控制板类型查询数据
     */
    public static void getConfigByElevatorType(String elevatorType,OtaDataListener listener){
        BmobQuery<ElevatorConfig> query = new BmobQuery<>();

        query.addWhereEqualTo("elevator_type",elevatorType);
        query.findObjects(new FindListener<ElevatorConfig>() {
            @Override
            public void done(List<ElevatorConfig> list, BmobException e) {
                if (e==null){
                    listener.getElevatorConfigDone(list);
                }else {
                    listener.error(e);
                }
            }
        });
    }

    /**
     *  上传文件
     */
    public static void uploadFile(Context context,String path){
        BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Toast.makeText(context,"上传文件成功:" + bmobFile.getFileUrl(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "上传文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    L.i("上传文件失败：" + e.getMessage());
                }

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }

    public interface OtaDataListener{
        void getElevatorConfigDone(List<ElevatorConfig> list);
        void error(BmobException e);
    }
}
