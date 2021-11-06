package ai.fitme.ayahupgrade.ui.impl;

import ai.fitme.ayahupgrade.bean.Status;

/**
 * Created by zzy on 2018/3/26.
 */

public interface IDeviceInfoUploadView {
    void showDeviceInfoUpload(Status jsonObject);
    void showDeviceInfoFail(Throwable e);
}
