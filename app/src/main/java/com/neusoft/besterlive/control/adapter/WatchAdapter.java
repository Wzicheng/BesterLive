package com.neusoft.besterlive.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.neusoft.besterlive.R;
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

public class WatchAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<TIMUserProfile> watchers = new ArrayList<>();

    public WatchAdapter(Context context,List<TIMUserProfile> data) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.watchers = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WatcherHolder holder = null;
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_title_watch_item,parent,false);
        holder = new WatcherHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WatcherHolder){
            ((WatcherHolder)holder).bindData(watchers.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return watchers.size();
    }

    private class WatcherHolder extends RecyclerView.ViewHolder {
        private ImageView mWatcherAvatar;
        private FrameLayout mFlAvatar;
        public WatcherHolder(View itemView) {
            super(itemView);
            mWatcherAvatar = (ImageView) itemView.findViewById(R.id.watcher_avatar);
            mFlAvatar = (FrameLayout) itemView.findViewById(R.id.fl_avatar);
        }

        public void bindData(TIMUserProfile userProfile) {
            String avatarUrl = userProfile.getFaceUrl();
            if (TextUtils.isEmpty(avatarUrl)){
                ImgUtils.loadRound(R.drawable.default_avatar,mWatcherAvatar);
            } else {
                ImgUtils.loadRound(avatarUrl,mWatcherAvatar);
            }
            mFlAvatar.setTag(userProfile);

            mWatcherAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TIMUserProfile userProfileTag = (TIMUserProfile) mFlAvatar.getTag();
                    //显示用户详情对话框
                    showUserInfoDialog(userProfileTag.getIdentifier());
                }
            });

        }
    }

    private void showUserInfoDialog(String senderId) {
        List<String> ids = new ArrayList<>();
        ids.add(senderId);
        TIMFriendshipManager.getInstance().getUsersProfile(ids, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(mContext, "请求用户信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> userProfiles) {
                if (mContext instanceof Activity){
                    ShowUserInfoDialog dialog = new ShowUserInfoDialog((Activity) mContext,userProfiles.get(0));
                    dialog.show();
                }
            }
        });
    }
}
