package com.neusoft.besterlive.control.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Wzich on 2017/10/29.
 */

public class TextProfile extends EditProfile{
    public TextProfile(Context context) {
        super(context);
        disableEdit();
    }

    public TextProfile(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        disableEdit();
    }

    public TextProfile(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        disableEdit();
    }
}
