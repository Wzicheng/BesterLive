package com.neusoft.besterlive.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.GiftInfo;
import com.neusoft.besterlive.utils.ImgUtils;
import com.tencent.TIMUserProfile;

/**
 * Created by Wzich on 2017/11/26.
 */

public class GiftProcheView extends LinearLayout {
    private ImageView mSenderAvatar;
    private TextView mSenderName;
    private TextView mGiftName;
    private ImageView mWheelBack;
    private ImageView mWheelFront;

    private AnimationDrawable wheelBackDrawable;
    private AnimationDrawable wheelFrontDrawable;

    private Animation inAnim;
    private Animation outAnim;

    public GiftProcheView(Context context) {
        super(context);
        init();
    }

    public GiftProcheView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftProcheView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void createAnim() {
        int left = getLeft();
        final int width = getWidth();
        inAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0 - (left + width) / width,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        inAnim.setDuration(2000);
        inAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                wheelBackDrawable.start();
                wheelFrontDrawable.start();
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                wheelBackDrawable.stop();
                wheelFrontDrawable.stop();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation(outAnim);
                    }
                },2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        outAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                (left + width) / width, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        outAnim.setDuration(2000);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                wheelBackDrawable.start();
                wheelFrontDrawable.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                wheelBackDrawable.stop();
                wheelFrontDrawable.stop();
                setVisibility(INVISIBLE);

                if (mOnAvaliableListener != null){
                    mOnAvaliableListener.onAvaliable();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void findAllViews() {
        mSenderAvatar = (ImageView) findViewById(R.id.sender_avatar);
        mSenderName = (TextView) findViewById(R.id.sender_name);
        mGiftName = (TextView) findViewById(R.id.gift_name);
        mWheelBack = (ImageView) findViewById(R.id.wheel_back);
        mWheelFront = (ImageView) findViewById(R.id.wheel_front);

        setVisibility(INVISIBLE);

        //设置车轮循环滚动
        wheelBackDrawable = (AnimationDrawable) mWheelBack.getDrawable();
        wheelFrontDrawable = (AnimationDrawable) mWheelFront.getDrawable();
        wheelBackDrawable.setOneShot(false);
        wheelFrontDrawable.setOneShot(false);
    }

    public void showGift(TIMUserProfile userProfile){
        if (userProfile != null){
            bindData2View(userProfile);
            startAnim();
        }

    }

    private void startAnim() {
        post(new Runnable() {
            @Override
            public void run() {
                createAnim();
                startAnimation(inAnim);
            }
        });
    }

    private void bindData2View(TIMUserProfile userProfile) {
        String senderAvatar = userProfile.getFaceUrl();
        if (TextUtils.isEmpty(senderAvatar)){
            ImgUtils.loadRound(R.drawable.default_avatar,mSenderAvatar);
        } else {
            ImgUtils.loadRound(senderAvatar,mSenderAvatar);
        }

        String senderName = userProfile.getNickName();
        if (TextUtils.isEmpty(senderName)){
            senderName = userProfile.getIdentifier();
        }
        mSenderName.setText(senderName);
        mGiftName.setText("送了一个" + GiftInfo.Gift_BaoShiJie.name);
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_porche, this, true);

        findAllViews();
    }

    public boolean isAvaliable() {
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
