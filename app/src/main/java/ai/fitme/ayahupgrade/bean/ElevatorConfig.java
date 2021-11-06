package ai.fitme.ayahupgrade.bean;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 电梯配置项目汇总 上传bmob
 */
public class ElevatorConfig extends BmobObject {

    private List<String> config_DataFloor;

    private Boolean alarm_config;

    private String elevator_type;

    private List<String> config_Floor;

    private Boolean cancel_config;

    private Boolean nofloor_config;

    private String opendoor_wait;

    private String volume;

    private String wakeup_num;

    private Boolean welcome_config;

    private String opendoor_serial;

    private String closedoor_serial;

    private String cancel_floor_serial;

    private String Intval_time;

    private float ambiguous_score;

    private String register_answer;

    public ElevatorConfig(List<String> config_DataFloor, Boolean alarm_config, String elevator_type
            , List<String> config_Floor, Boolean cancel_config, Boolean nofloor_config
            , String opendoor_wait, String volume, String wakeup_num, Boolean welcome_config
            , String opendoor_serial, String closedoor_serial, String cancel_floor_serial,String Intval_time
            , float ambiguous_score,String register_answer) {
        this.config_DataFloor = config_DataFloor;
        this.alarm_config = alarm_config;
        this.elevator_type = elevator_type;
        this.config_Floor = config_Floor;
        this.cancel_config = cancel_config;
        this.nofloor_config = nofloor_config;
        this.opendoor_wait = opendoor_wait;
        this.volume = volume;
        this.wakeup_num = wakeup_num;
        this.welcome_config = welcome_config;
        this.opendoor_serial = opendoor_serial;
        this.closedoor_serial = closedoor_serial;
        this.cancel_floor_serial = cancel_floor_serial;
        this.Intval_time = Intval_time;
        this.ambiguous_score = ambiguous_score;
        this.register_answer = register_answer;
    }

    public ElevatorConfig() {
    }

    public List<String> getConfig_DataFloor() {
        return config_DataFloor;
    }

    public void setConfig_DataFloor(List<String> config_DataFloor) {
        this.config_DataFloor = config_DataFloor;
    }

    public Boolean getAlarm_config() {
        return alarm_config;
    }

    public void setAlarm_config(Boolean alarm_config) {
        this.alarm_config = alarm_config;
    }

    public String getElevator_type() {
        return elevator_type;
    }

    public void setElevator_type(String elevator_type) {
        this.elevator_type = elevator_type;
    }

    public List<String> getConfig_Floor() {
        return config_Floor;
    }

    public void setConfig_Floor(List<String> config_Floor) {
        this.config_Floor = config_Floor;
    }

    public Boolean getCancel_config() {
        return cancel_config;
    }

    public void setCancel_config(Boolean cancel_config) {
        this.cancel_config = cancel_config;
    }

    public Boolean getNofloor_config() {
        return nofloor_config;
    }

    public void setNofloor_config(Boolean nofloor_config) {
        this.nofloor_config = nofloor_config;
    }

    public String getOpendoor_wait() {
        return opendoor_wait;
    }

    public void setOpendoor_wait(String opendoor_wait) {
        this.opendoor_wait = opendoor_wait;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getWakeup_num() {
        return wakeup_num;
    }

    public void setWakeup_num(String wakeup_num) {
        this.wakeup_num = wakeup_num;
    }

    public Boolean getWelcome_config() {
        return welcome_config;
    }

    public void setWelcome_config(Boolean welcome_config) {
        this.welcome_config = welcome_config;
    }

    public String getOpendoor_serial() {
        return opendoor_serial;
    }

    public void setOpendoor_serial(String opendoor_serial) {
        this.opendoor_serial = opendoor_serial;
    }

    public String getClosedoor_serial() {
        return closedoor_serial;
    }

    public void setClosedoor_serial(String closedoor_serial) {
        this.closedoor_serial = closedoor_serial;
    }

    public String getCancel_floor_serial() {
        return cancel_floor_serial;
    }

    public void setCancel_floor_serial(String cancel_floor_serial) {
        this.cancel_floor_serial = cancel_floor_serial;
    }

    public String getIntval_time() {
        return Intval_time;
    }

    public void setIntval_time(String intval_time) {
        Intval_time = intval_time;
    }

    public float getAmbiguous_score() {
        return ambiguous_score;
    }

    public void setAmbiguous_score(float ambiguous_score) {
        this.ambiguous_score = ambiguous_score;
    }

    public String getRegister_answer() {
        return register_answer;
    }

    public void setRegister_answer(String register_answer) {
        this.register_answer = register_answer;
    }

    @Override
    public String toString() {
        return "ElevatorConfig{" +
                "config_DataFloor=" + config_DataFloor +
                ", alarm_config=" + alarm_config +
                ", elevator_type='" + elevator_type + '\'' +
                ", config_Floor=" + config_Floor +
                ", cancel_config=" + cancel_config +
                ", nofloor_config=" + nofloor_config +
                ", opendoor_wait='" + opendoor_wait + '\'' +
                ", volume='" + volume + '\'' +
                ", wakeup_num='" + wakeup_num + '\'' +
                ", welcome_config=" + welcome_config +
                ", opendoor_serial='" + opendoor_serial + '\'' +
                ", closedoor_serial='" + closedoor_serial + '\'' +
                ", cancel_floor_serial='" + cancel_floor_serial + '\'' +
                ", Intval_time='" + Intval_time + '\'' +
                ", ambiguous_score=" + ambiguous_score +
                ", register_answer='" + register_answer + '\'' +
                '}';
    }
}
