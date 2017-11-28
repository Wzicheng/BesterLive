package com.neusoft.besterlive.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.MsgInfo;

import java.util.LinkedList;

/**
 * Created by Wzich on 2017/11/23.
 */

public class DanMuView extends LinearLayout {

    private DanMuItemView item0,item1;
    private LinkedList<MsgInfo> msgInfos = new LinkedList<>();

    public DanMuView(Context context) {
        super(context);
        init();
    }

    public DanMuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanMuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_danmu, this, true);
        findAllViews();
    }

    private void findAllViews() {
        item0 = (DanMuItemView) findViewById(R.id.item_0);
        item1 = (DanMuItemView) findViewById(R.id.item_1);

        //初始化
        item0.setOnAvaliableListener(new MyOnAvaliableListener());
        item1.setOnAvaliableListener(new MyOnAvaliableListener());
    }


    public void showMsgDanMu(MsgInfo msgInfo) {
        DanMuItemView avabiableItem = getAvabiableItem();
        if (avabiableItem != null){
            avabiableItem.showMsg(msgInfo);
        } else {
            // 缓存弹幕消息，等待有可用的弹幕view后将缓存的弹幕消息装载
            synchronized (this) {
                msgInfos.add(msgInfo);
            }
        }
    }

    private DanMuItemView getAvabiableItem() {
        if (item0.isAvaliable()){
            return item0;
        }
        if (item1.isAvaliable()){
            return item1;
        }
        return null;
    }


    private class MyOnAvaliableListener implements DanMuItemView.OnAvaliableListener {
        @Override
        public void onAvaliable() {
            MsgInfo msgInfo = null;
            synchronized (this) {
                msgInfo = msgInfos.poll();
            }
            if (msgInfo != null) {
                showMsgDanMu(msgInfo);
            }
        }
    }
}
