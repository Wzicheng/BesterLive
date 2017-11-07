package com.neusoft.besterlive.control.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.fragment.EditProfileFragment;
import com.neusoft.besterlive.control.fragment.LiveListFragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mFragmentContainer;
    private FragmentTabHost mFragmentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupTab();
    }

    private void setupTab() {
        mFragmentTab.setup(this,getSupportFragmentManager(),R.id.fragment_container);
        //直播列表
        TabHost.TabSpec liveListTabSpec = mFragmentTab.newTabSpec("liveList").setIndicator(getIndicatorView(R.drawable.tab_livelist));
        mFragmentTab.addTab(liveListTabSpec, LiveListFragment.class,null);
        //创建直播
        TabHost.TabSpec createLiveTabSpec = mFragmentTab.newTabSpec("create").setIndicator(getIndicatorView(R.drawable.tab_publish_live));
        mFragmentTab.addTab(createLiveTabSpec,null,null);

        //个人信息设置页面
        TabHost.TabSpec editProfileTabSpec = mFragmentTab.newTabSpec("editProfile").setIndicator(getIndicatorView(R.drawable.tab_profile));
        mFragmentTab.addTab(editProfileTabSpec, EditProfileFragment.class,null);

        //点击创建直播页面
        mFragmentTab.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private View getIndicatorView(int resId) {
        View view = LayoutInflater.from(this).inflate(R.layout.view_indicator,null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageResource(resId);
        return view;
    }

    private void initView() {
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mFragmentTab = (FragmentTabHost) findViewById(R.id.fragment_tab);
    }
}
