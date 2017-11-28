package com.neusoft.besterlive.view.Dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.neusoft.besterlive.R;

/**
 * Created by Wzich on 2017/11/28.
 */

public class HostControlDialog extends TransParentNoDialog {
    private TextView mBeautyView;
    private TextView mFlashLightView;
    private TextView mVoiceView;
    private TextView mCameraView;

    private int dialogWidth;
    private int dialogHeight;

    public HostControlDialog(Activity activity) {
        super(activity);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_host_control,null,false);

        mBeautyView = (TextView) view.findViewById(R.id.beauty);
        mFlashLightView = (TextView) view.findViewById(R.id.flash_light);
        mVoiceView = (TextView) view.findViewById(R.id.voice);
        mCameraView = (TextView) view.findViewById(R.id.camera);

        mBeautyView.setOnClickListener(new MyOnClickListener());
        mFlashLightView.setOnClickListener(new MyOnClickListener());
        mVoiceView.setOnClickListener(new MyOnClickListener());
        mCameraView.setOnClickListener(new MyOnClickListener());

        setContentView(view);

        int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(width,height);
        dialogWidth = view.getMeasuredWidth();
        dialogHeight = view.getMeasuredHeight();
        setWidthAndHeight(dialogWidth,dialogHeight);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mOnHostControlClickListener != null){
                    mOnHostControlClickListener.onDialogDismiss();
                }
            }
        });
    }

    public void showDialog(View view) {
        int[] vLocation = new int[2];
        view.getLocationOnScreen(vLocation);

        int dialogX = vLocation[0] - (dialogWidth - view.getWidth())/2;
        int dialogY = vLocation[1] - dialogHeight - view.getHeight();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParam = window.getAttributes();
        layoutParam.x = dialogX;
        layoutParam.y = dialogY;
        layoutParam.alpha = 0.8f;
        window.setAttributes(layoutParam);
        window.setGravity(Gravity.LEFT|Gravity.TOP);
        show();
    }

    public void setStatusIcon(boolean beauty, boolean flashLight, boolean voice) {
        int beautyResId = beauty?R.drawable.icon_beauty_on:R.drawable.icon_beauty_off;
        mBeautyView.setCompoundDrawablesWithIntrinsicBounds(beautyResId,0,0,0);
        mBeautyView.setText(beauty?"关美颜":"开美颜");

        int flashLightResId = flashLight?R.drawable.icon_flashlight_on:R.drawable.icon_flashlight_off;
        mFlashLightView.setCompoundDrawablesWithIntrinsicBounds(flashLightResId,0,0,0);
        mFlashLightView.setText(flashLight?"关闪光":"开闪光");

        int voiceResId = voice?R.drawable.icon_voice_on:R.drawable.icon_voice_off;
        mVoiceView.setCompoundDrawablesWithIntrinsicBounds(voiceResId,0,0,0);
        mVoiceView.setText(voice?"关声音":"开声音");

    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.beauty:
                    if (mOnHostControlClickListener != null){
                        mOnHostControlClickListener.onBeautyClick();
                    }
                    break;
                case R.id.flash_light:
                    if (mOnHostControlClickListener != null){
                        mOnHostControlClickListener.onFlashLightClick();
                    }
                    break;
                case R.id.voice:
                    if (mOnHostControlClickListener != null){
                        mOnHostControlClickListener.onVoiceClick();
                    }
                    break;
                case R.id.camera:
                    if (mOnHostControlClickListener != null){
                        mOnHostControlClickListener.onCameraClick();
                    }
                    break;
            }
        }
    }

    private OnHostControlClickListener mOnHostControlClickListener;
    public void setOnHostControlClickListener(OnHostControlClickListener listener){
        mOnHostControlClickListener = listener;
    }

    public interface OnHostControlClickListener{
        void onBeautyClick();
        void onFlashLightClick();
        void onVoiceClick();
        void onCameraClick();
        void onDialogDismiss();
    }
}
