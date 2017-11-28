package com.neusoft.besterlive.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.IMConstants;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVText;

/**
 * Created by Wzich on 2017/11/14.
 */

public class ChatView extends LinearLayout {
    private CheckBox mChatMode;
    private EditText mChatContent;
    private TextView mChatSend;

    public ChatView(Context context) {
        super(context);
        init();
    }

    public ChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat, this, true);
        findAllView();
    }

    private void findAllView() {
        mChatMode = (CheckBox) findViewById(R.id.chat_mode);
        mChatContent = (EditText) findViewById(R.id.chat_content);
        mChatSend = (TextView) findViewById(R.id.chat_send);

        mChatMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mChatContent.setHint("发送弹幕信息...");
                } else {
                    mChatContent.setHint("和大家说点什么吧...");
                }
            }
        });

        mChatSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击发送聊天信息
                if (mOnSendClickListener != null){
                    String content = mChatContent.getText().toString();
                    ILVCustomCmd customCmd = new ILVCustomCmd();
                    customCmd.setType(ILVText.ILVTextType.eGroupMsg);
                    if(mChatMode.isChecked()){
                        customCmd.setCmd(IMConstants.CMD_MSG_DANMU);
                    }else {
                        customCmd.setCmd(IMConstants.CMD_MSG_LIST);
                    }
                    customCmd.setParam(content);
                    customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
                    mOnSendClickListener.onSendClick(customCmd);
                }
            }
        });
        if (mChatMode.isChecked()){
            mChatContent.setHint("发送弹幕信息...");
        } else {
            mChatContent.setHint("和大家说点什么吧...");
        }
    }

    private OnSendClickListener mOnSendClickListener;
    public void setOnSendClickListener(OnSendClickListener listener){
        mOnSendClickListener = listener;
    }

    public interface OnSendClickListener{
        void onSendClick(ILVCustomCmd customCmd);
    }
}
