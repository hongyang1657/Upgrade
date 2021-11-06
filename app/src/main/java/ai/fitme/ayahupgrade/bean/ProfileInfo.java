package ai.fitme.ayahupgrade.bean;

import java.io.Serializable;

public class ProfileInfo implements Serializable {
    //公共参数
    private String status;
    //设置用户是否已经登录
    private boolean is_login;
    //密码
    private String password;
    private long expires;
    //用户是否开启声音播报消息
    private boolean is_play_voice;

    //第三方登陆进来的用户信息
    private int code;
    private String message;
    private Data data;

    private ContentInfo content;

    public ContentInfo getContent() {
        if(content==null){
            content=new ContentInfo();
        }
        return content;
    }

    public void setContent(ContentInfo content) {
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable {
        private String user_id;
        private String identifier;
        private String credential;
        private String identity_type;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getCredential() {
            return credential;
        }

        public void setCredential(String credential) {
            this.credential = credential;
        }

        public String getIdentity_type() {
            return identity_type;
        }

        public void setIdentity_type(String identity_type) {
            this.identity_type = identity_type;
        }
    }

    public ProfileInfo() {
    }

    public ProfileInfo(String status, boolean is_login, String password, boolean is_play_voice) {
        this.status = status;
        this.password = password;
        //用来检验当前是否已经登录
        this.is_login = is_login;
        this.is_play_voice = is_play_voice;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public boolean is_play_voice() {
        return is_play_voice;
    }

    public void setIs_play_voice(boolean is_play_voice) {
        this.is_play_voice = is_play_voice;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean is_login() {
        return is_login;
    }

    public void setIs_login(boolean is_login) {
        this.is_login = is_login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ProfileInfo{" +
                "status='" + status + '\'' +
                ", is_login=" + is_login +
                ", password='" + password + '\'' +
                ", expires=" + expires +
                ", is_play_voice=" + is_play_voice +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", content=" + content +
                '}';
    }
}
