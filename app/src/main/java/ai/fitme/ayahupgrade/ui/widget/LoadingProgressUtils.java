package ai.fitme.ayahupgrade.ui.widget;

import android.content.Context;
import android.content.DialogInterface;


import ai.fitme.ayahupgrade.utils.StringUtils;


/**
 * Created by yml on 17/11/29.
 */
public class LoadingProgressUtils {
    private static LoadingDialog dialog = null;
    /**
     * 显示进度对话框
     */
    public static void showProgressDialog(Context activity){
        showProgressDialog (activity,false, null);
    }

    /**
     * 显示进度对话框
     * @param tipInfo
     */
    public static void showProgressDialog(Context activity, String tipInfo){
        showProgressDialog (activity,false, StringUtils.trimNull (tipInfo, "正在加载，请稍后..."));
    }

    /**
     * 显示进度对话框--重载
     * @param cancelable
     * @param text
     */
    public static void showProgressDialog(Context activity, boolean cancelable, String text){
        closeDialog ();
        if(StringUtils.isEmpty(text)){
            text="正在加载，请稍后...";
        }
        dialog = new LoadingDialog(activity,text);
        dialog.setCancelable (cancelable);
        dialog.setOnCancelListener (new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog){}
        });
        try {
            dialog.show ();
        } catch (Exception e) {}
    }

    /**
     * 关闭对话框
     */
    public static void closeDialog(){
        if (dialog != null && dialog.isShowing ()) {
            try {
                dialog.dismiss ();
            } catch (Exception e) {}
            dialog = null;
        }
    }
}
