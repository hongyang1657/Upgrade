package ai.fitme.ayahupgrade.utils;

public interface UdpReceiveListener {
    void onReceiver(byte[] bytes);
    void onReceiver(String msg);
}
