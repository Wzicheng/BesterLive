package com.neusoft.besterlive.control.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.GiftInfo;
import com.neusoft.besterlive.utils.ImgUtils;
import com.tencent.TIMUserProfile;

/**
 * Created by Wzich on 2017/11/24.
 */

public class GiftRepeatItemView extends RelativeLayout {
    private ImageView mUserAvatar;
    private TextView mUserName;
    private TextView mGiftName;
    private ImageView mGiftImg;
    private TextView mGiftNum;

    private Animation viewInAnim;
    private Animation viewOutAnim;
    private Animation imgIconInAnim;
    private Animation numScaleAnim;

    public GiftRepeatItemView(Context context) {
        super(context);
        init();
    }

    public GiftRepeatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftRepeatItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_repeat_item, this, true);
        findAllViews();
        initAnim();
    }

    private void initAnim() {
        viewInAnim = AnimationUtils.loadAnimation(getContext(),R.anim.repeat_gift_view_in);
        viewOutAnim = AnimationUtils.loadAnimation(getContext(),R.anim.repeat_gift_view_out);
        imgIconInAnim = AnimationUtils.loadAnimation(getContext(),R.anim.repeat_gift_icon_in);
        numScaleAnim = AnimationUtils.loadAnimation(getContext(), R.anim.repeat_gift_num_scale);

        viewInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
                mGiftImg.setVisibility(INVISIBLE);
                mGiftNum.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mGiftImg.startAnimation(imgIconInAnim);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imgIconInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mGiftImg.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mGiftNum.startAnimation(numScaleAnim);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        numScaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mGiftNum.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation(viewOutAnim);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void findAllViews() {
        mUserAvatar = (ImageView) findViewById(R.id.user_avatar);
        mUserName = (TextView) findViewById(R.id.user_name);
        mGiftName = (TextView) findViewById(R.id.gift_name);
        mGiftImg = (ImageView) findViewById(R.id.gift_img);
        mGiftNum = (TextView) findViewById(R.id.gift_num);
    }

    public void showGiftMsg(GiftInfo giftInfo, TIMUserProfile userProfile) {
        if (giftInfo != null && userProfile != null){
            bindData2View(giftInfo,userProfile);
        }

        startAnim();
    }

    //绑定数据
    private void bindData2View(GiftInfo giftInfo, TIMUserProfile userProfile) {
        String avatarUrl = userProfile.getFaceUrl();
        if (TextUtils.isEmpty(avatarUrl)){
            ImgUtils.loadRound(R.drawable.default_avatar,mUserAvatar);
        } else {
            ImgUtils.loadRound(avatarUrl,mUserAvatar);
        }

        String userNick = userProfile.getNickName();
        if (TextUtils.isEmpty(userNick)){
            userNick = userProfile.getIdentifier();
        }
        mUserName.setText(userNick);
        mGiftName.setText("送了一个" + giftInfo.name);

        ImgUtils.load(giftInfo.giftResId,mGiftImg);
        mGiftNum.setText("x" + 1);
    }

    private void startAnim() {
        post(new Runnable() {
            @Override
            public void run() {
                GiftRepeatItemView.this.startAnimation(viewInAnim);
            }
        });
    }



    public boolean isAvaliable() {
        return getVisibility() != VISIBLE;
    }
}
