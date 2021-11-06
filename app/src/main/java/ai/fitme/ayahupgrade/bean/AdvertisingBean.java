package ai.fitme.ayahupgrade.bean;

public class AdvertisingBean {

    private String floor;
    private String adContent;
    private boolean isCanListen;
    private String ttsFilePath;

    public AdvertisingBean(String floor, String adContent, boolean isCanListen) {
        this.floor = floor;
        this.adContent = adContent;
        this.isCanListen = isCanListen;
    }

    public AdvertisingBean(String floor, String adContent, boolean isCanListen, String ttsFilePath) {
        this.floor = floor;
        this.adContent = adContent;
        this.isCanListen = isCanListen;
        this.ttsFilePath = ttsFilePath;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getAdContent() {
        return adContent;
    }

    public void setAdContent(String adContent) {
        this.adContent = adContent;
    }

    public boolean isCanListen() {
        return isCanListen;
    }

    public void setCanListen(boolean canListen) {
        isCanListen = canListen;
    }

    public String getTtsFilePath() {
        return ttsFilePath;
    }

    public void setTtsFilePath(String ttsFilePath) {
        this.ttsFilePath = ttsFilePath;
    }

    @Override
    public String toString() {
        return "AdvertisingBean{" +
                "floor='" + floor + '\'' +
                ", adContent='" + adContent + '\'' +
                ", isCanListen=" + isCanListen +
                ", ttsFilePath='" + ttsFilePath + '\'' +
                '}';
    }
}
