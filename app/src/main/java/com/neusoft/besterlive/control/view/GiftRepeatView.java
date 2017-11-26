package com.neusoft.besterlive.control.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.GiftInfo;
import com.tencent.TIMUserProfile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Wzich on 2017/11/24.
 */

public class GiftRepeatView extends LinearLayout {
    private GiftRepeatItemView mItem0,mItem1;

    private LinkedList<GiftCacheInfo> cacheInfoList = new LinkedList<>();

    public GiftRepeatView(Context context) {
        super(context);
        init();
    }

    public GiftRepeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftRepeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_repeat, this, true);
        findAllViews();
    }

    private void findAllViews() {
        mItem0 = (GiftRepeatItemView) findViewById(R.id.item_0);
        mItem1 = (GiftRepeatItemView) findViewById(R.id.item_1);

        mItem0.setVisibility(INVISIBLE);
        mItem1.setVisibility(INVISIBLE);

        mItem0.setOnAvaliableListener(new MyOnAvaliableListener());
        mItem1.setOnAvaliableListener(new MyOnAvaliableListener());
    }


    public void showGiftMsg(GiftInfo giftInfo, String repeatId, TIMUserProfile userProfile) {
        GiftRepeatItemView avaliableItem = getAvaliableItem(giftInfo,repeatId,userProfile);
        if (avaliableItem != null){
            avaliableItem.showGiftMsg(giftInfo,repeatId,userProfile);
        } else {
            //当前都不可用，将礼物信息缓存
            GiftCacheInfo cacheInfo = new GiftCacheInfo();
            cacheInfo.giftInfo = giftInfo;
            cacheInfo.repeatId = repeatId;
            cacheInfo.userProfile = userProfile;
            cacheInfoList.add(cacheInfo);
        }
    }

    private class GiftCacheInfo{
        public GiftInfo giftInfo;
        public String repeatId;
        public TIMUserProfile userProfile;
    }

    private GiftRepeatItemView getAvaliableItem(GiftInfo giftInfo, String repeatId, TIMUserProfile userProfile) {
        if (mItem0.isMatch(giftInfo,repeatId,userProfile)){
            return mItem0;
        }

        if (mItem1.isMatch(giftInfo,repeatId,userProfile)){
            return mItem1;
        }

        if (mItem0.isAvaliable()){
            return mItem0;
        }
        if (mItem1.isAvaliable()){
            return mItem1;
        }
        return null;
    }

    private class MyOnAvaliableListener implements GiftRepeatItemView.OnAvaliableListener {

        @Override
        public void onAvaliavle() {
            if (cacheInfoList.size() > 0){
                GiftCacheInfo firstCacheInfo = cacheInfoList.removeFirst();
                showGiftMsg(firstCacheInfo.giftInfo,firstCacheInfo.repeatId,firstCacheInfo.userProfile);
                //找出缓存中和第一个礼物相同的连发信息
                List<GiftCacheInfo> leftSameInfos = new ArrayList<>();
                for (GiftCacheInfo info : cacheInfoList) {
                    if (info.repeatId.equals(firstCacheInfo.repeatId) && info.giftInfo.giftId == firstCacheInfo.giftInfo.giftId
                            && info.userProfile.getIdentifier().equals(firstCacheInfo.userProfile.getIdentifier())
                                && info.repeatId.equals(firstCacheInfo.repeatId)){
                        //三者同时满足，则为连发礼物
                        leftSameInfos.add(info);
                    }
                }
                cacheInfoList.removeAll(leftSameInfos);
                for (GiftCacheInfo sameCacheInfo : leftSameInfos) {
                    showGiftMsg(sameCacheInfo.giftInfo,sameCacheInfo.repeatId,sameCacheInfo.userProfile);
                }
            }
        }
    }
}
