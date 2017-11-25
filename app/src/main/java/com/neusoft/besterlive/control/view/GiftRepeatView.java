package com.neusoft.besterlive.control.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.GiftInfo;
import com.tencent.TIMUserProfile;

/**
 * Created by Wzich on 2017/11/24.
 */

public class GiftRepeatView extends LinearLayout {
    private GiftRepeatItemView mItem0,mItem1;

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
    }


    public void showGiftMsg(GiftInfo giftInfo, TIMUserProfile userProfile) {
        GiftRepeatItemView avaliableItem = getAvaliableItem();
        if (avaliableItem == null){
            //TODO 当前都不可用，将礼物信息缓存
        } else {
            avaliableItem.showGiftMsg(giftInfo,userProfile);
        }
    }

    private GiftRepeatItemView getAvaliableItem() {
        if (mItem0.isAvaliable()){
            return mItem0;
        }
        if (mItem1.isAvaliable()){
            return mItem1;
        }
        return null;
    }
}
