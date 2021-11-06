package ai.fitme.ayahupgrade.ui.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import ai.fitme.ayahupgrade.R;


/**
 * Created by yml on 17/11/29.
 */
public class LoadingDialog extends ProgressDialog {

    private Context mContext;
    private ImageView mImageView;
    private String mLoadingTip;
    private TextView mLoadingTv;

    public LoadingDialog(Context context, String content) {
        super(context, R.style.MyDialogStyle);
        this.mContext = context;
        this.mLoadingTip = content;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        // 加载动画
        Animation anim= AnimationUtils.loadAnimation(mContext, R.anim.take_taxi_progressbar);
        anim.setInterpolator(new LinearInterpolator());
        mImageView.startAnimation(anim);
        mLoadingTv.setText(mLoadingTip);

    }

    public void setContent(String str) {
        mLoadingTv.setText(str);
    }

    private void initView() {
        setContentView(R.layout.loading_dialog);
        mImageView=(ImageView) findViewById(R.id.loading_progress);
        mLoadingTv = (TextView) findViewById(R.id.loading_tip_tv);

    }

}
