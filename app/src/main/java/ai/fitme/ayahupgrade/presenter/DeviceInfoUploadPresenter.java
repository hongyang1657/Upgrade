package ai.fitme.ayahupgrade.presenter;


import ai.fitme.ayahupgrade.bean.Status;
import ai.fitme.ayahupgrade.model.DeviceInfoUploadModel;
import ai.fitme.ayahupgrade.presenter.impl.IDeviceInfoUploadPresenter;
import ai.fitme.ayahupgrade.ui.impl.IDeviceInfoUploadView;

/**
 * Created by zzy on 2018/3/26.
 */

public class DeviceInfoUploadPresenter implements IDeviceInfoUploadPresenter,DeviceInfoUploadModel.OnDeviceInfoUploadListener {

    private DeviceInfoUploadModel deviceInfoUploadModel;
    private IDeviceInfoUploadView deviceInfoUploadView;

    public DeviceInfoUploadPresenter(IDeviceInfoUploadView deviceInfoUploadView) {
        this.deviceInfoUploadModel = new DeviceInfoUploadModel(this);
        this.deviceInfoUploadView = deviceInfoUploadView;
    }

    @Override
    public void onSuccess(Status jsonObject) {
        deviceInfoUploadView.showDeviceInfoUpload(jsonObject);
    }

    @Override
    public void onFailure(Throwable e) {
        deviceInfoUploadView.showDeviceInfoFail(e);
    }

    @Override
    public void deviceInfoUpload(String user_id,String token,String device_id) {
        deviceInfoUploadModel.deviceInfoUpload(user_id,token,device_id);
    }
}
