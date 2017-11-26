package com.neusoft.besterlive.control.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.widget.TransParentNoDialog;
import com.neusoft.besterlive.model.bean.GiftInfo;
import com.neusoft.besterlive.model.bean.IMConstants;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVText;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Wzich on 2017/11/24.
 */

public class GiftSelectDialog extends TransParentNoDialog {

    private static final int WHAT_REPEATGIFT_FINISH = 0;
    private ViewPager mGiftPager;
    private ImageView mIndicatorOne;
    private ImageView mIndicatorTwo;
    private Button mSend;
    private GiftAdapter adapter;


    private List<GiftInfo> giftInfoList = new ArrayList<>();
    private List<GiftGridView> gridViewPages = new ArrayList<>();
    private GiftInfo selectedGiftInfo;
    private String repeatId = "";

    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_REPEATGIFT_FINISH:
                    //2s内没有点击发送相同的礼物，视为非连续发送
                    repeatId = "";
                    break;
            }
        }
    };

    public GiftSelectDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_gift_select, null, false);
        setContentView(view);

        setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        bindAllData(giftInfoList);
        findAllViews(view);
    }

    private void bindAllData(List<GiftInfo> giftInfoList) {
        giftInfoList.add(GiftInfo.getGiftById(1));
        giftInfoList.add(GiftInfo.getGiftById(2));
        giftInfoList.add(GiftInfo.getGiftById(3));
        giftInfoList.add(GiftInfo.getGiftById(4));
        giftInfoList.add(GiftInfo.getGiftById(5));
        giftInfoList.add(GiftInfo.getGiftById(6));
        giftInfoList.add(GiftInfo.getGiftById(7));
        giftInfoList.add(GiftInfo.getGiftById(8));
        giftInfoList.add(GiftInfo.getGiftById(9));
    }

    private void findAllViews(View view) {
        mGiftPager = (ViewPager) view.findViewById(R.id.gift_pager);
        mIndicatorOne = (ImageView) view.findViewById(R.id.indicator_one);
        mIndicatorTwo = (ImageView) view.findViewById(R.id.indicator_two);


        adapter = new GiftAdapter();
        mGiftPager.setAdapter(adapter);
        mGiftPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    mIndicatorOne.setImageResource(R.drawable.ind_s);
                    mIndicatorTwo.setImageResource(R.drawable.ind_uns);
                } else if (position == 1){
                    mIndicatorOne.setImageResource(R.drawable.ind_uns);
                    mIndicatorTwo.setImageResource(R.drawable.ind_s);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSend = (Button) view.findViewById(R.id.send);
        mSend.setVisibility(View.INVISIBLE);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(repeatId)){
                    repeatId = System.currentTimeMillis() + "";
                }

                //发送礼物消息
                if (onGiftSendListener != null) {
                    ILVCustomCmd customCmd = new ILVCustomCmd();
                    customCmd.setCmd(IMConstants.CMD_MGS_GIFT);
                    customCmd.setType(ILVText.ILVTextType.eGroupMsg);
                    customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
                    GiftCmdInfo giftCmdInfo = new GiftCmdInfo();
                    giftCmdInfo.giftId = selectedGiftInfo.giftId;
                    giftCmdInfo.repeatId = repeatId;
                    customCmd.setParam(new Gson().toJson(giftCmdInfo));

                    onGiftSendListener.onGiftSend(customCmd);

                    if(selectedGiftInfo.type == GiftInfo.Type.ContinueGift){
                        startRepeatTimer();
                    }
                }
            }
        });
    }

    private void startRepeatTimer() {
        handle.removeMessages(WHAT_REPEATGIFT_FINISH);
        handle.sendEmptyMessageDelayed(WHAT_REPEATGIFT_FINISH,2000);
    }


    public static class GiftCmdInfo {
        public int giftId;
        public String repeatId;
    }


    private class GiftAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //构建View
            final GiftGridView giftGridView = new GiftGridView(activity);
            List<GiftInfo> giftInfos = new ArrayList<>();
            int startIndex = position * 8;
            int endIndex = (position + 1) * 8;
            int empytNum = 0;
            if (endIndex > giftInfoList.size()) {
                //空白的区域，需要用Gift_Empty填充的个数
                empytNum = endIndex - giftInfoList.size();
                endIndex = giftInfoList.size();
            }

            giftInfos.addAll(giftInfoList.subList(startIndex, endIndex));
            for (int i = 0; i < empytNum; i++) {
                giftInfos.add(GiftInfo.Gift_Empty);
            }

            giftGridView.addGifts(giftInfos);
            giftGridView.setOnGiftSelectListener(new GiftGridView.OnGiftSelectListener() {
                @Override
                public void onSelected(GiftInfo giftInfo) {
                    //选择礼物
                    selectedGiftInfo = giftInfo;
                    repeatId = "";
                    for (GiftGridView item : gridViewPages){
                        item.setSelectedGiftInfo(selectedGiftInfo);
                    }
                    if (giftGridView != null){
                        mSend.setVisibility(View.VISIBLE);
                    } else {
                        mSend.setVisibility(View.INVISIBLE);
                    }
                }
            });
            container.addView(giftGridView);
            gridViewPages.add(giftGridView);
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            layoutParams.height = giftGridView.getGridViewHeight();
            container.setLayoutParams(layoutParams);
            return giftGridView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //销毁View
            container.removeView(gridViewPages.remove(position));
        }
    }
    @Override
    public void show() {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        super.show();
    }



    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        dialog.setOnDismissListener(listener);
    }

    private OnGiftSendListener onGiftSendListener;
    public void setOnGiftSendListener(OnGiftSendListener listener){
        onGiftSendListener = listener;
    }

    public interface OnGiftSendListener{
        void onGiftSend(ILVCustomCmd customCmd);
    }
}
