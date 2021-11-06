package ai.fitme.ayahupgrade.bean;

import java.util.Arrays;

/**
 * 获取用户设备信息
 * Created by zzy on 2017/12/27.
 */

public class DeviceGet {
    private String state;
    private Devices[] devices;

    public DeviceGet() {

    }

    public DeviceGet(String state, Devices[] devices) {
        this.state = state;
        this.devices = devices;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Devices[] getDevices() {
        return devices;
    }

    public void setDevices(Devices[] devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "DeviceGet{" +
                "state='" + state + '\'' +
                ", devices=" + Arrays.toString(devices) +
                '}';
    }

    public class Devices {
        private String device_id;//设备id
        private String last_put_time;//Unix time ，精确到毫秒
        private String device_type;//设备类型 ， mobile,smart_speaker

        public Devices() {

        }

        public Devices(String device_id, String last_put_time, String device_type) {
            this.device_id = device_id;
            this.last_put_time = last_put_time;
            this.device_type = device_type;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getLast_put_time() {
            return last_put_time;
        }

        public void setLast_put_time(String last_put_time) {
            this.last_put_time = last_put_time;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        @Override
        public String toString() {
            return "Devices{" +
                    "device_id='" + device_id + '\'' +
                    ", last_put_time=" + last_put_time +
                    ", device_type='" + device_type + '\'' +
                    '}';
        }
    }
}
