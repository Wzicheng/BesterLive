package com.neusoft.besterlive.view.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Wzich on 2017/11/22.
 */

public class SizeChangeRelativeLayout extends RelativeLayout {

    private OnKeyBoardStatusListener mOnKeyBoardStatusListener;

    public SizeChangeRelativeLayout(Context context) {
        super(context);
    }

    public SizeChangeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeChangeRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h > oldh){
            //键盘隐藏
            if (mOnKeyBoardStatusListener != null){
                mOnKeyBoardStatusListener.onHide();
            }
        } else {
            //键盘显示
            if (mOnKeyBoardStatusListener != null){
                mOnKeyBoardStatusListener.onShow();
            }
        }
    }

    public void setOnKeyBoardStatusListener(OnKeyBoardStatusListener listener){
        mOnKeyBoardStatusListener = listener;
    }



    public interface OnKeyBoardStatusListener{
        void onShow();
        void onHide();
    }
}
