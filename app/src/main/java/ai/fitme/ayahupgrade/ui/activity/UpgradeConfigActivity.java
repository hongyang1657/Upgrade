package ai.fitme.ayahupgrade.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import ai.fitme.ayahupgrade.MainApplication;
import ai.fitme.ayahupgrade.R;
import ai.fitme.ayahupgrade.adapter.SettingFragmentAdapter;
import ai.fitme.ayahupgrade.ui.fragment.DeviceInfoFragment;
import ai.fitme.ayahupgrade.ui.fragment.FloorConfigFragment;
import ai.fitme.ayahupgrade.ui.fragment.InterfaceConfigFragment;
import ai.fitme.ayahupgrade.ui.fragment.imp.OnFragmentListener;
import ai.fitme.ayahupgrade.ui.view.SettingViewpager;
import ai.fitme.ayahupgrade.utils.Constants;
import ai.fitme.ayahupgrade.utils.L;
import ai.fitme.ayahupgrade.utils.StatusBarUtil;

public class UpgradeConfigActivity extends FragmentActivity implements OnFragmentListener {

    private TabLayout tabLayout;
    private SettingViewpager viewPager;
    private SettingFragmentAdapter settingFragmentAdapter;
    private List<Fragment> fragmentList;
    private ImageView ivBack;
    private TextView tvTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.upgrade_config_layout);
        init();
    }


    private void init(){
        tabLayout = findViewById(R.id.tl_main);
        viewPager = findViewById(R.id.vp_main);
        tvTitle = findViewById(R.id.tv_title);
        addTabToTabLayout();
        addFragment();
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tabLayout.selectTab(tabLayout.getTabAt(0));
                finish();
            }
        });

    }

    /**
     * Description：给TabLayout添加tab，Tab.setIcon()方法可以给Tab添加图片
     */
    private void addTabToTabLayout() {
        String []mTitles = {"设备信息", "楼层配置", "交互配置"};
        for (int i = 0; i < mTitles.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(mTitles[i]));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                L.i("onTabSelected:"+tab.getPosition());
                MainApplication.currentPage = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void addFragment(){
        fragmentList = new ArrayList<>();
        fragmentList.add(new DeviceInfoFragment());
        fragmentList.add(new FloorConfigFragment());
        fragmentList.add(new InterfaceConfigFragment());
        settingFragmentAdapter = new SettingFragmentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentList);
        viewPager.setAdapter(settingFragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //L.i("onPageScrolled position = "+position);
            }

            @Override
            public void onPageSelected(int position) {
                //L.i("onPageSelected position = "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //L.i("onPageScrollStateChanged = "+state);
            }
        });
        viewPager.setScrollable(false);
    }

    @Override
    public void onNext(int flag) {
        L.i("下一步:");
        if (flag==3){
            //TODO 开始配置

            Intent intent = new Intent(this, SerialTransmissionActivity.class);
            intent.putExtra(Constants.PUSH_MODE,Constants.MODE_MAIN);
            startActivity(intent);
            return;
        }
        tabLayout.selectTab(tabLayout.getTabAt(flag));
    }
}
