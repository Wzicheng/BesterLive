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
import com.tencent.TIMUserProfile;

import java.util.LinkedList;
/**
 * Created by Wzich on 2017/11/29.
 */

public class WatcherEnterView extends RelativeLayout {
    private TextView mUserName;
    private ImageView mSplash;
    private Animation viewInAnim;
    private Animation splahInAnim;

    private LinkedList<TIMUserProfile> enterWathers = new LinkedList<>();
    private boolean inShow = false;

    public WatcherEnterView(Context context) {
        super(context);
        init();
    }

    public WatcherEnterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WatcherEnterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.watch_enter_bkg);
        LayoutInflater.from(getContext()).inflate(R.layout.view_watcher_enter, this, true);
        findAllViews();
        initAnim();
    }

    private void initAnim() {
        viewInAnim = AnimationUtils.loadAnimation(getContext(),R.anim.watch_enter_view_in);
        splahInAnim = AnimationUtils.loadAnimation(getContext(),R.anim.watch_enter_splash_in);
        viewInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mSplash.startAnimation(splahInAnim);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splahInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSplash.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSplash.setVisibility(INVISIBLE);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        inShow = false;
                        setVisibility(INVISIBLE);
                        if (enterWathers.size() > 0){
                            TIMUserProfile userProfile = enterWathers.removeFirst();
                            showEnterWatcher(userProfile);
                        }
                    }
                },500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void findAllViews() {
        mUserName = (TextView) findViewById(R.id.user_name);
        mSplash = (ImageView) findViewById(R.id.splash);

        setVisibility(INVISIBLE);
        mSplash.setVisibility(INVISIBLE);
    }

    public void showEnterWatcher(TIMUserProfile userProfile){
        if(inShow){
            // 缓存新加入用户信息
            enterWathers.add(userProfile);
        } else {
            if (userProfile != null){
                BindData2View(userProfile);
            }
            startAnim();
            inShow = true;
        }
    }

    private void startAnim() {
        startAnimation(viewInAnim);
    }

    private void BindData2View(TIMUserProfile userProfile) {
        String userName = userProfile.getNickName();
        if (TextUtils.isEmpty(userName)){
            userName = userProfile.getIdentifier();
        }
        mUserName.setText("欢迎"+userName+"加入房间");
    }

}
