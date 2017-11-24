package com.neusoft.besterlive.control.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.MsgInfo;

import java.util.List;

import static com.neusoft.besterlive.R.id.get;


/**
 * Created by Wzich on 2017/11/22.
 */

public class MsgListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MsgInfo> mMsgInfoList;

    public MsgListAdapter(Context context, List<MsgInfo> msgInfoList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMsgInfoList = msgInfoList;
    }

    @Override
    public int getCount() {
        return mMsgInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMsgInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_msg_list,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MsgInfo msgInfo = mMsgInfoList.get(position);
        viewHolder.bindMsgInfo(msgInfo);
        return convertView;
    }



    private class ViewHolder{
        View mItemView;
        private ImageView mIvLevelIcon;
        private TextView mTvMsg;

        public ViewHolder(View view) {
            mItemView = view;
            mIvLevelIcon = (ImageView) view.findViewById(R.id.iv_level_icon);
            mTvMsg = (TextView) view.findViewById(R.id.tv_msg);

        }

        private void bindMsgInfo(MsgInfo msgInfo) {
            int level = msgInfo.userLevel;
            //TODO 不同的level显示不同的levelIcon
            mIvLevelIcon.setImageResource(R.drawable.level_1);
            SpannableStringBuilder ssb = new SpannableStringBuilder("");
            {
                String nickStr = msgInfo.userNick + ":";
                int startIndex = 0;
                int endIndex = nickStr.length();
                SpannableStringBuilder nickSpanStr = new SpannableStringBuilder(nickStr);
                nickSpanStr.setSpan(new ForegroundColorSpan(Color.parseColor("#23BE9F")),
                        startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ssb.append(nickSpanStr);
            }
            {
                String content = msgInfo.msgContent;
                int startIndex = 0;
                int endIndex = content.length();
                SpannableStringBuilder contentSpanStr = new SpannableStringBuilder(content);
                contentSpanStr.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")),
                    startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ssb.append(contentSpanStr);
            }
            mTvMsg.setText(ssb);
        }
    }
}
