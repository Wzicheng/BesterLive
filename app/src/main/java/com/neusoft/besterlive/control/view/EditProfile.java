package com.neusoft.besterlive.control.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neusoft.besterlive.R;


/**
 * Created by Wzich on 2017/10/29.
 */

public class EditProfile extends LinearLayout {

    private TextView mProfileType;
    private TextView mProfileValue;
    private ImageView mRightArrow;

    public EditProfile(Context context) {
        super(context);
        init();
    }

    public EditProfile(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditProfile(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);
        setPadding(5,5,5,5);
        setMinimumHeight(55);
        LayoutInflater.from(getContext()).inflate(R.layout.view_profile_edit, this, true);
        findAllViews();
    }

    private void findAllViews() {
        mProfileType = (TextView) findViewById(R.id.profile_type);
        mProfileValue = (TextView) findViewById(R.id.profile_value);
        mRightArrow = (ImageView) findViewById(R.id.right_arrow);
    }

    /**
     * 设置icon、type和value
     *
     * @param resId
     * @param name
     */
    public void setType(int resId, String name) {
        mProfileType.setText(name);
        mProfileType.setCompoundDrawablesWithIntrinsicBounds(resId,0,0,0);
    }

    /**
     * 更新Value的值
     *
     * @param value
     */
    public void setValue(String value) {
        mProfileValue.setText(value);
    }

    /**
     * 获取Value的值
     *
     * @return
     */
    public String getValue() {
        return mProfileValue.getText().toString();
    }

    /**
     * 设置不可编辑状态
     */
    public void disableEdit() {
        mRightArrow.setVisibility(GONE);
    }

}
