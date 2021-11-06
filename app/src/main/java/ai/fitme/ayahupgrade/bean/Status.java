package ai.fitme.ayahupgrade.bean;

/**
 * 通用的应答参数只有一个status的实体
 * Created by blw on 2016/8/29.
 */
public class Status {
    //公共参数
    private String status;
    private String bind_status;//音箱绑定结果状态
    private String verify_code_status;

    public Status() {

    }

    public Status(String status, String bind_status, String verify_code_status) {
        this.status = status;
        this.bind_status = bind_status;
        this.verify_code_status = verify_code_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBind_status() {
        return bind_status;
    }

    public void setBind_status(String bind_status) {
        this.bind_status = bind_status;
    }

    public String getVerify_code_status() {
        return verify_code_status;
    }

    public void setVerify_code_status(String verify_code_status) {
        this.verify_code_status = verify_code_status;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status='" + status + '\'' +
                ", bind_status='" + bind_status + '\'' +
                ", verify_code_status='" + verify_code_status + '\'' +
                '}';
    }
}
