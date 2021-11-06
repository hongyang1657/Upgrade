package ai.fitme.ayahupgrade.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TTSConfig {

    @SerializedName("0")
    private Content Content1;

    @SerializedName("1")
    private Content Content2;

    @SerializedName("2")
    private Content Content3;

    @SerializedName("3")
    private Content Content4;

    @SerializedName("4")
    private Content Content5;

    @SerializedName("5")
    private Content Content6;

    @SerializedName("6")
    private Content Content7;

    @SerializedName("7")
    private Content Content8;

    public Content getContent1() {
        return Content1;
    }

    public void setContent1(Content content1) {
        Content1 = content1;
    }

    public Content getContent2() {
        return Content2;
    }

    public void setContent2(Content content2) {
        Content2 = content2;
    }

    public Content getContent3() {
        return Content3;
    }

    public void setContent3(Content content3) {
        Content3 = content3;
    }

    public Content getContent4() {
        return Content4;
    }

    public void setContent4(Content content4) {
        Content4 = content4;
    }

    public Content getContent5() {
        return Content5;
    }

    public void setContent5(Content content5) {
        Content5 = content5;
    }

    public Content getContent6() {
        return Content6;
    }

    public void setContent6(Content content6) {
        Content6 = content6;
    }

    public Content getContent7() {
        return Content7;
    }

    public void setContent7(Content content7) {
        Content7 = content7;
    }

    public Content getContent8() {
        return Content8;
    }

    public void setContent8(Content content8) {
        Content8 = content8;
    }

    public static class Content{
        private String mode;
        private List<String[]> urls;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public List<String[]> getUrls() {
            return urls;
        }

        public void setUrls(List<String[]> urls) {
            this.urls = urls;
        }
    }
}
