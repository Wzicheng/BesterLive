package com.neusoft.besterlive.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.neusoft.besterlive.view.weight.EditProfile;

/**
 * Created by Wzich on 2017/10/29.
 */

public class TextProfile extends EditProfile {
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
