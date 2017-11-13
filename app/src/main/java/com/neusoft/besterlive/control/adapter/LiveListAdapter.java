package com.neusoft.besterlive.control.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.activity.WatcherLiveActivity;
import com.neusoft.besterlive.model.bean.RoomInfo;
import com.neusoft.besterlive.utils.ImgUtils;

import java.util.List;

/**
 * Created by Wzich on 2017/11/12.
 */

public class LiveListAdapter extends BaseAdapter {
    private Context mContext;
    private List<RoomInfo> mRoomInfoList;
    private LayoutInflater mInflater;

    public LiveListAdapter(Context context, List<RoomInfo> roomInfoList) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mRoomInfoList = roomInfoList;
    }

    @Override
    public int getCount() {
        return mRoomInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRoomInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_live_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RoomInfo roomInfo = mRoomInfoList.get(position);
        viewHolder.bindData(roomInfo);
        return convertView;
    }

    private class ViewHolder {
        View mItemView;
        TextView mLiveTitle;
        ImageView mLiveCover;
        ImageView mHostAvatar;
        TextView mHostName;
        TextView mWatcherNums;

        public ViewHolder(View view) {
            mItemView = view;
            mLiveTitle = (TextView) view.findViewById(R.id.live_title);
            mLiveCover = (ImageView) view.findViewById(R.id.live_cover);
            mHostAvatar = (ImageView) view.findViewById(R.id.host_avatar);
            mHostName = (TextView) view.findViewById(R.id.host_name);
            mWatcherNums = (TextView) view.findViewById(R.id.watcher_nums);
        }

        public void bindData(final RoomInfo roomInfo){
            //根据roomInfo 绑定数据
            String userName = roomInfo.userName;
            if (TextUtils.isEmpty(userName)){
                userName = roomInfo.userId;
            }
            mHostName.setText(userName);

            String liveTitle = roomInfo.liveTitle;
            if (TextUtils.isEmpty(liveTitle)){
                mLiveTitle.setText(userName + "的直播");
            } else {
                mLiveTitle.setText(liveTitle);
            }

            String url = roomInfo.liveCover;
            if (TextUtils.isEmpty(url)){
                ImgUtils.load(R.drawable.default_avatar,mLiveCover);
            } else {
                ImgUtils.load(url,mLiveCover);
            }

            String avatar = roomInfo.userAvatar;
            if (TextUtils.isEmpty(avatar)){
                ImgUtils.loadRound(R.drawable.default_avatar,mHostAvatar);
            } else {
                ImgUtils.load(avatar,mHostAvatar);
            }

            int watcherNums = roomInfo.watcherNums;
            String watchText = watcherNums + "人\r\n正在看";
            mWatcherNums.setText(watchText);

            //点击跳转到直播界面
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,WatcherLiveActivity.class);
                    intent.putExtra("roomId",roomInfo.roomId);
                    intent.putExtra("hostId",roomInfo.userId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
