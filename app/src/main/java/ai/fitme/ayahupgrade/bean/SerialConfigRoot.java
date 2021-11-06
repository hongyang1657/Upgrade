package ai.fitme.ayahupgrade.bean;

import com.google.gson.annotations.SerializedName;

public class SerialConfigRoot {

    @SerializedName("配置参数")
    private SerialIOConfig numMinus2;

    @SerializedName("回应聊天")
    private Content num0;

    @SerializedName("离线电梯__关轿厢门")
    private Content num1;

    @SerializedName("离线电梯__取消轿厢楼层选择<取消楼层>")
    private Content num2;

    @SerializedName("离线电梯__开轿厢门")
    private Content num3;

    @SerializedName("离线电梯__轿厢呼救")
    private Content num4;

    @SerializedName("离线电梯__轿厢楼层选择<登记楼层>")
    private Content num5;

    @SerializedName("release_turn")
    private Content num6;

    @SerializedName("release_turn_0")
    private Content num7;

    public SerialConfigRoot() {
    }

    public SerialIOConfig getNumMinus2() {
        return numMinus2;
    }

    public void setNumMinus2(SerialIOConfig numMinus2) {
        this.numMinus2 = numMinus2;
    }

    public Content getNum0() {
        return num0;
    }

    public void setNum0(Content num0) {
        this.num0 = num0;
    }

    public Content getNum1() {
        return num1;
    }

    public void setNum1(Content num1) {
        this.num1 = num1;
    }

    public Content getNum2() {
        return num2;
    }

    public void setNum2(Content num2) {
        this.num2 = num2;
    }

    public Content getNum3() {
        return num3;
    }

    public void setNum3(Content num3) {
        this.num3 = num3;
    }

    public Content getNum4() {
        return num4;
    }

    public void setNum4(Content num4) {
        this.num4 = num4;
    }

    public Content getNum5() {
        return num5;
    }

    public void setNum5(Content num5) {
        this.num5 = num5;
    }

    public Content getNum6() {
        return num6;
    }

    public void setNum6(Content num6) {
        this.num6 = num6;
    }

    public Content getNum7() {
        return num7;
    }

    public void setNum7(Content num7) {
        this.num7 = num7;
    }

    public class Content{
        private String intent;
        private String serial;

        public Content(String intent, String serial) {
            this.intent = intent;
            this.serial = serial;
        }

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }
    }

    public class SerialIOConfig{

        private String intent;

        private String FloorMax;

        @SerializedName("注1")
        private String annotation1;

        private String[] DataFloor;

        private String[] Floor;

        private String wakeup_num;

        private boolean register_config;

        private boolean cancel_config;

        private boolean alarm_config;

        private boolean welcome_config;

        private boolean nofloor_config;

        private String opendoor_pin;

        private String opendoor_val;

        private String opendoor_wait;

        private String volume;

        @SerializedName("注2")
        private String annotation2;

        private String Repeat_time;

        private String Intval_time;

        @SerializedName("注3")
        private String annotation3;

        @SerializedName("注4")
        private String annotation4;

        public boolean isWelcome_config() {
            return welcome_config;
        }

        public void setWelcome_config(boolean welcome_config) {
            this.welcome_config = welcome_config;
        }

        public boolean isNofloor_config() {
            return nofloor_config;
        }

        public void setNofloor_config(boolean nofloor_config) {
            this.nofloor_config = nofloor_config;
        }

        public String getOpendoor_pin() {
            return opendoor_pin;
        }

        public void setOpendoor_pin(String opendoor_pin) {
            this.opendoor_pin = opendoor_pin;
        }

        public String getOpendoor_val() {
            return opendoor_val;
        }

        public void setOpendoor_val(String opendoor_val) {
            this.opendoor_val = opendoor_val;
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

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public String getFloorMax() {
            return FloorMax;
        }

        public void setFloorMax(String floorMax) {
            FloorMax = floorMax;
        }

        public String getAnnotation1() {
            return annotation1;
        }

        public void setAnnotation1(String annotation1) {
            this.annotation1 = annotation1;
        }

        public String[] getDataFloor() {
            return DataFloor;
        }

        public void setDataFloor(String[] dataFloor) {
            DataFloor = dataFloor;
        }

        public String[] getFloor() {
            return Floor;
        }

        public void setFloor(String[] floor) {
            Floor = floor;
        }

        public String getAnnotation2() {
            return annotation2;
        }

        public void setAnnotation2(String annotation2) {
            this.annotation2 = annotation2;
        }

        public String getWakeup_num() {
            return wakeup_num;
        }

        public void setWakeup_num(String wakeup_num) {
            this.wakeup_num = wakeup_num;
        }

        public boolean isRegister_config() {
            return register_config;
        }

        public void setRegister_config(boolean register_config) {
            this.register_config = register_config;
        }

        public boolean isCancel_config() {
            return cancel_config;
        }

        public void setCancel_config(boolean cancel_config) {
            this.cancel_config = cancel_config;
        }

        public boolean isAlarm_config() {
            return alarm_config;
        }

        public void setAlarm_config(boolean alarm_config) {
            this.alarm_config = alarm_config;
        }

        public String getRepeat_time() {
            return Repeat_time;
        }

        public void setRepeat_time(String repeat_time) {
            Repeat_time = repeat_time;
        }

        public String getIntval_time() {
            return Intval_time;
        }

        public void setIntval_time(String intval_time) {
            Intval_time = intval_time;
        }

        public String getAnnotation3() {
            return annotation3;
        }

        public void setAnnotation3(String annotation3) {
            this.annotation3 = annotation3;
        }

        public String getAnnotation4() {
            return annotation4;
        }

        public void setAnnotation4(String annotation4) {
            this.annotation4 = annotation4;
        }
    }
}
