package com.neusoft.besterlive.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.activity.WatcherLiveActivity;
import com.neusoft.besterlive.control.adapter.WatchAdapter;
import com.neusoft.besterlive.utils.ImgUtils;
import com.neusoft.besterlive.view.Dialog.ShowUserInfoDialog;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wzich on 2017/11/28.
 */

public class TitleView extends LinearLayout {
    private ImageView mHostAvatar; //主播头像
    private TextView mWatchersNum; //当前观看人数
    private RecyclerView mWatchList; //观众列表

    private TIMUserProfile hostProfile;

    private WatchAdapter adapter;
    private List<TIMUserProfile> watchers = new ArrayList<>();

    public TitleView(Context context) {
        super(context);
        init();
    }

    public TitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_title, this, true);
        findAllViews();
    }

    private void findAllViews() {
        mHostAvatar = (ImageView) findViewById(R.id.host_avatar);
        mWatchersNum = (TextView) findViewById(R.id.watchers_num);
        mWatchList = (RecyclerView) findViewById(R.id.watch_list);

        mHostAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击头像，查看主播详情信息对话框
                showUserInfoDialog(hostProfile.getIdentifier());

            }
        });

        mWatchList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayout.HORIZONTAL);
        mWatchList.setLayoutManager(layoutManager);
        // 设置adapter
        adapter = new WatchAdapter(getContext(),watchers);
        mWatchList.setAdapter(adapter);
    }

    private void showUserInfoDialog(String senderId) {
        List<String> ids = new ArrayList<>();
        ids.add(senderId);
        TIMFriendshipManager.getInstance().getUsersProfile(ids, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getContext(), "请求用户信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> userProfiles) {
                Context context = TitleView.this.getContext();
                if (context instanceof Activity){
                    ShowUserInfoDialog dialog = new ShowUserInfoDialog((Activity) context,userProfiles.get(0));
                    dialog.show();
                }
            }
        });
    }

    public void setHost(TIMUserProfile userProfile){
        hostProfile = userProfile;
        String avatarUrl = userProfile.getFaceUrl();
        if (TextUtils.isEmpty(avatarUrl)){
            ImgUtils.loadRound(R.drawable.default_avatar,mHostAvatar);
        } else {
            ImgUtils.loadRound(avatarUrl,mHostAvatar);
        }
    }

    //用户加入直播间
    public void addNewWatcher(TIMUserProfile userProfile){
        if (userProfile != null){
            watchers.add(userProfile);
            mWatchersNum.setText("人数：" + watchers.size());
            adapter.notifyDataSetChanged();
        }
    }

    //用户退出直播间
    public void userQuitRoom(TIMUserProfile userProfile){
        if (userProfile != null){
            List<TIMUserProfile> removeWatcher = new ArrayList<>();
            for (TIMUserProfile watcher : watchers) {
                if (watcher.getIdentifier().equals(userProfile.getIdentifier())){
                    removeWatcher.add(watcher);
                }
            }
            watchers.removeAll(removeWatcher);
            mWatchersNum.setText("人数：" + watchers.size());
            adapter.notifyDataSetChanged();
        }
    }

    public void addWatchers(List<TIMUserProfile> userProfileList) {
        if (userProfileList == null) {
            return;
        }

        for (TIMUserProfile userProfile : userProfileList) {
            if (userProfile != null) {
                watchers.add(userProfile);
                mWatchersNum.setText(watchers.size() + " 人正在看");
            }
        }
        adapter.notifyDataSetChanged();
    }
}
