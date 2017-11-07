package com.neusoft.besterlive.control.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.widget.TransParentDialog;

/**
 * Created by Wzich on 2017/10/29.
 */

public class EditProfileDialog extends TransParentDialog {
    private TextView titleView;
    private EditText contentView;

    public EditProfileDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_str_profile, null, false);
        titleView = (TextView) view.findViewById(R.id.title);
        contentView = (EditText) view.findViewById(R.id.content);
        setContentView(view);

        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100, WindowManager.LayoutParams.WRAP_CONTENT);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = contentView.getText().toString();
                if (onOKListener != null) {
                    onOKListener.onOk(mTitle, content);
                }

                hide();
            }
        });
    }
    private String mTitle;

    public void show(String title, int resId, String defaultContent) {
        mTitle = title;
        titleView.setText("请输入" + title);

        contentView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        contentView.setText(defaultContent);
        show();
    }

    private OnOKListener onOKListener;

    public void setOnOKListener(OnOKListener l) {
        onOKListener = l;
    }

    public interface OnOKListener {
        void onOk(String title, String content);
    }
}
