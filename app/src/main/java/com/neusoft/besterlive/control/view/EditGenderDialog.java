package com.neusoft.besterlive.control.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.widget.TransParentDialog;

/**
 * Created by Wzich on 2017/10/30.
 */

public class EditGenderDialog extends TransParentDialog {
    private RadioButton maleView;
    private RadioButton femaleView;

    public EditGenderDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_gender, null, false);

        maleView = (RadioButton) view.findViewById(R.id.male);
        femaleView = (RadioButton) view.findViewById(R.id.female);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMaleChecked = maleView.isChecked();
                if (onChangeGenderListener != null) {
                    onChangeGenderListener.onChangeGender(isMaleChecked);
                }
                hide();
            }
        });
        setContentView(view);

        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void show(boolean isMale) {
        maleView.setChecked(isMale);
        femaleView.setChecked(!isMale);
        show();
    }

    private OnChangeGenderListener onChangeGenderListener;

    public void setOnChangeGenderListener(OnChangeGenderListener l) {
        onChangeGenderListener = l;
    }

    public interface OnChangeGenderListener {
        void onChangeGender(boolean isMale);
    }
}
