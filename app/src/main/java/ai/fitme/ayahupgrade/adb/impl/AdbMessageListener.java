package ai.fitme.ayahupgrade.adb.impl;

import ai.fitme.ayahupgrade.adb.AdbMessage;

public interface AdbMessageListener {
    void onMessage(AdbMessage message);
    String getPullFileName();
}
