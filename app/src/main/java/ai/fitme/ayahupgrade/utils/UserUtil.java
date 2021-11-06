package ai.fitme.ayahupgrade.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.bean.ProfileInfo;

/**
 * Created by blw on 2016/8/31.
 */
public class UserUtil {
    //存储user信息到本地
    public static String USERFILE="user.info";
    public static void saveUser(ProfileInfo user){
        try {
            File file=new File(MainApplication.getApp().getFilesDir(),USERFILE);
            ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(user);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ProfileInfo getUser(){
        try {
            File file=new File(MainApplication.getApp().getFilesDir(),USERFILE);
            ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file));
            ProfileInfo user= (ProfileInfo) ois.readObject();
            if(user==null){
                return new ProfileInfo();
            }
            return user;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ProfileInfo();
    }
}
