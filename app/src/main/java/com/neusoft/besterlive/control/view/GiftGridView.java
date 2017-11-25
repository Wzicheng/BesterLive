package com.neusoft.besterlive.control.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.GiftInfo;
import com.neusoft.besterlive.utils.ImgUtils;

import java.util.ArrayList;
import java.util.List;

import static com.neusoft.besterlive.model.bean.GiftInfo.Gift_Empty;

/**
 * Created by Wzich on 2017/11/24.
 */

public class GiftGridView extends GridView {
    private List<GiftInfo> mGiftInfos = new ArrayList<>();
    private GiftAdapter adapter;

    public GiftGridView(Context context) {
        super(context);
        init();
    }

    public GiftGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setNumColumns(4);
        adapter = new GiftAdapter();
        setAdapter(adapter);

    }

    public void addGifts(List<GiftInfo> giftInfos){
        mGiftInfos.clear();
        mGiftInfos.addAll(giftInfos);
        adapter.notifyDataSetChanged();
    }

    public int getGridViewHeight(){
        View itemView = adapter.getView(0,null,null);
        itemView.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        int height = itemView.getMeasuredHeight();
        return height * 2;
    }

    private class GiftAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mGiftInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mGiftInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_gift_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            GiftInfo giftInfo = (GiftInfo) getItem(position);
            viewHolder.bindData(giftInfo);
            return convertView;
        }

        private class ViewHolder {
            public View view;
            public ImageView mGiftImg;
            public TextView mGiftExp;
            public TextView mGiftName;
            public ImageView mGiftSelect;

            public ViewHolder(View convertView) {
                view = convertView;
                mGiftImg = (ImageView) view.findViewById(R.id.gift_img);
                mGiftExp = (TextView) view.findViewById(R.id.gift_exp);
                mGiftName = (TextView) view.findViewById(R.id.gift_name);
                mGiftSelect = (ImageView) view.findViewById(R.id.gift_select);
            }

            public void bindData(final GiftInfo giftInfo) {
                ImgUtils.load(giftInfo.giftResId,mGiftImg);
                if (giftInfo != Gift_Empty){
                    mGiftExp.setText(giftInfo.expValue + "经验值");
                    mGiftName.setText(giftInfo.name);
                    if (giftInfo != currentSelectGiftInfo){
                        if (giftInfo.type == GiftInfo.Type.ContinueGift){
                            mGiftSelect.setImageResource(R.drawable.gift_repeat);
                        } else {
                            mGiftSelect.setImageResource(R.drawable.gift_none);
                        }
                    } else {
                        mGiftSelect.setImageResource(R.drawable.gift_selected);
                    }
                } else {
                    mGiftExp.setText("");
                    mGiftName.setText("");
                    mGiftSelect.setImageResource(R.drawable.gift_none);
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (giftInfo == Gift_Empty){
                            return;
                        }
                        if (mOnGiftSelectListener != null){
                            if (currentSelectGiftInfo != giftInfo){
                                mOnGiftSelectListener.onSelected(giftInfo);
                            } else {
                                mOnGiftSelectListener.onSelected(null);
                            }
                        }
                    }
                });
            }
        }
    }
    private GiftInfo currentSelectGiftInfo = null;
    public void setSelectedGiftInfo(GiftInfo giftInfo){
        currentSelectGiftInfo = giftInfo;
        adapter.notifyDataSetChanged();
    }

    private OnGiftSelectListener mOnGiftSelectListener;
    public void setOnGiftSelectListener(OnGiftSelectListener listener){
        mOnGiftSelectListener = listener;
    }

    public interface OnGiftSelectListener {
        void onSelected(GiftInfo giftInfo);
    }
}
