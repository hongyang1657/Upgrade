package ai.fitme.ayahupgrade.bean;

import java.io.Serializable;

/**
 * 2代接口，用户信息
 * Created by yml on 2017/10/12.
 */

public class ContentInfo implements Serializable {
    //    用户id
    private String user_id;
    //    性别
    private String gender;
    //    出生年份
    private String born_year;
    //    出生月份
    private String born_month;
    //    出生日期
    private String born_day;
    //    家庭地址
    private String home_address;
    //    公司地址
    private String company_address;
    //    手机号码
    private String mobile;
    //    个性标签
    private String preferences;
    //be_called
    private String be_called;
    private String token;
    //   用户头像
    private String headimgurl;

    public ContentInfo() {
    }

    public ContentInfo(String user_id, String gender, String born_year, String born_month, String born_day, String home_address, String company_address, String mobile, String preferences, String be_called, String token, String headimgurl) {
        this.user_id = user_id;
        this.gender = gender;
        this.born_year = born_year;
        this.born_month = born_month;
        this.born_day = born_day;
        this.home_address = home_address;
        this.company_address = company_address;
        this.mobile = mobile;
        this.preferences = preferences;
        this.be_called = be_called;
        this.token = token;
        this.headimgurl = headimgurl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBorn_year() {
        return born_year;
    }

    public void setBorn_year(String born_year) {
        this.born_year = born_year;
    }

    public String getBorn_month() {
        return born_month;
    }

    public void setBorn_month(String born_month) {
        this.born_month = born_month;
    }

    public String getBorn_day() {
        return born_day;
    }

    public void setBorn_day(String born_day) {
        this.born_day = born_day;
    }

    public String getHome_address() {
        return home_address;
    }

    public void setHome_address(String home_address) {
        this.home_address = home_address;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getBe_called() {
        return be_called;
    }

    public void setBe_called(String be_called) {
        this.be_called = be_called;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Override
    public String toString() {
        return "ContentInfo{" +
                "user_id='" + user_id + '\'' +
                ", gender='" + gender + '\'' +
                ", born_year='" + born_year + '\'' +
                ", born_month='" + born_month + '\'' +
                ", born_day='" + born_day + '\'' +
                ", home_address='" + home_address + '\'' +
                ", company_address='" + company_address + '\'' +
                ", mobile='" + mobile + '\'' +
                ", preferences='" + preferences + '\'' +
                ", be_called='" + be_called + '\'' +
                ", token='" + token + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                '}';
    }
}
