package ai.fitme.ayahupgrade.model.impl;

/**
 * 上传手机设备信息
 * Created by yml on 2017/10/18.
 */

public interface IDeviceInfoUploadModel {
    void deviceInfoUpload(String user_id, String token, String device_id);
}
