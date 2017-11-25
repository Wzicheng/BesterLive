package com.neusoft.besterlive.control.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.neusoft.besterlive.R;

/**
 * Created by Wzich on 2017/11/14.
 */

public class BottomControlView extends RelativeLayout {
    private ImageView mClose;
    private ImageView mChat;
    private ImageView mGift;
    private ImageView mOperate;

    public BottomControlView(Context context) {
        super(context);
        init();
    }

    public BottomControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_bottom_control, this, true);
        findAllView();
    }

    private void findAllView() {
        mClose = (ImageView) findViewById(R.id.close);
        mChat = (ImageView) findViewById(R.id.chat);
        mGift = (ImageView) findViewById(R.id.gift);
//        mOperate = (ImageView) findViewById(R.id.operate);

        mClose.setOnClickListener(new MyOnClickListener());
        mChat.setOnClickListener(new MyOnClickListener());
        mGift.setOnClickListener(new MyOnClickListener());
//        mOperate.setOnClickListener(new MyOnClickListener());
    }

    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close:
                    if (mOnControlClickListener != null) {
                        mOnControlClickListener.onCloseClick();
                    }
                    break;
                case R.id.chat:
                    if (mOnControlClickListener != null) {
                        mOnControlClickListener.onChatClick();
                    }
                    break;
                case R.id.gift:
                    if (mOnControlClickListener != null) {
                        mOnControlClickListener.onGiftClick();
                    }
                    break;
//                case R.id.operate:
//                    if (mOnControlClickListener != null) {
//                        mOnControlClickListener.onOperateClick();
//                    }
//                    break;
            }

        }
    }

    private OnControlClickListener mOnControlClickListener;

    public void setOnControlClickListener(OnControlClickListener listener) {
        mOnControlClickListener = listener;
    }

    public interface OnControlClickListener {
        void onCloseClick();

        void onChatClick();

        void onGiftClick();

//        void onOperateClick();
    }
}
