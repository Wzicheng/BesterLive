package com.neusoft.besterlive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.neusoft.besterlive.model.bean.GiftInfo;
import com.tencent.TIMUserProfile;

import java.util.LinkedList;

/**
 * Created by Wzich on 2017/11/26.
 */

public class GiftFullView extends RelativeLayout {
    private GiftProcheView porcheView;

    private LinkedList<GiftCacheInfo> cacheListInfo = new LinkedList<>();
    public GiftFullView(Context context) {
        super(context);
        init();
    }

    public GiftFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftFullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    private boolean inShow = false;

    public void showGift(GiftInfo giftInfo, TIMUserProfile userProfile){
        if(inShow){
            // 缓存礼物消息
            GiftCacheInfo cacheInfo = new GiftCacheInfo();
            cacheInfo.giftInfo = giftInfo;
            cacheInfo.userProfile = userProfile;
            cacheListInfo.add(cacheInfo);
        } else {
            if (giftInfo.giftId == GiftInfo.Gift_BaoShiJie.giftId){
                //说明发送的是保时捷
                showPorcheGift(userProfile);
                inShow = true;
            }
        }
    }

    private void showPorcheGift(TIMUserProfile userProfile) {
        if (porcheView == null){
            porcheView = new GiftProcheView(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(CENTER_IN_PARENT);

            porcheView.setOnAvaliableListener(new GiftProcheView.OnAvaliableListener() {
                @Override
                public void onAvaliable() {
                    inShow = false;
                    if (cacheListInfo.size() > 0) {
                        GiftCacheInfo cacheInfo = cacheListInfo.removeFirst();
                        showGift(cacheInfo.giftInfo,cacheInfo.userProfile );
                    }
                }
            });
            addView(porcheView,params);
        }
        if (porcheView.isAvaliable()){
            porcheView.showGift(userProfile);
        }
    }


    private class GiftCacheInfo{
        public GiftInfo giftInfo;
        public TIMUserProfile userProfile;
    }
}
