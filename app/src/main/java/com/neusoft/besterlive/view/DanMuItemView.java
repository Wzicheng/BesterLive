package com.neusoft.besterlive.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.MsgInfo;
import com.neusoft.besterlive.utils.ImgUtils;

/**
 * Created by Wzich on 2017/11/23.
 */

public class DanMuItemView extends RelativeLayout {
    private ImageView mUserAvatar;
    private TextView mUserName;
    private TextView mChatContent;

    private Animation mAnimation;

    public DanMuItemView(Context context) {
        super(context);
        init();
    }

    public DanMuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanMuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_danmu_item,this,true);
        findAllViews();
        mAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.danmu_item_anim);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(INVISIBLE);
                if (mOnAvaliableListener != null){
                    mOnAvaliableListener.onAvaliable();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        setVisibility(INVISIBLE);
    }

    private void findAllViews() {
        mUserAvatar = (ImageView) findViewById(R.id.user_avatar);
        mUserName = (TextView) findViewById(R.id.user_name);
        mChatContent = (TextView) findViewById(R.id.chat_content);
    }

    public void showMsg(MsgInfo danmuMsg){
        if (danmuMsg != null){
            bindMsg2View(danmuMsg);
        }
        //启动动画
        this.post(new Runnable() {
            @Override
            public void run() {
                DanMuItemView.this.startAnimation(mAnimation);
            }
        });
    }

    private void bindMsg2View(MsgInfo danmuMsg) {
        if (TextUtils.isEmpty(danmuMsg.userAvatar)){
            ImgUtils.loadRound(R.drawable.default_avatar,mUserAvatar);
        } else {
            ImgUtils.loadRound(danmuMsg.userAvatar,mUserAvatar);
        }
        String userNick = danmuMsg.userNick;
        if (TextUtils.isEmpty(userNick)){
            userNick = danmuMsg.userId;
        }
        mUserName.setText(userNick);
        mChatContent.setText(danmuMsg.msgContent);
    }

    public boolean isAvaliable(){
        return getVisibility() != VISIBLE;
    }

    private OnAvaliableListener mOnAvaliableListener;
    public void setOnAvaliableListener(OnAvaliableListener listener){
        mOnAvaliableListener = listener;
    }

    public interface OnAvaliableListener{
        void onAvaliable();
    }
}
