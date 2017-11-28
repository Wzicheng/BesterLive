package com.neusoft.besterlive.view.Dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.neusoft.besterlive.R;
import com.neusoft.besterlive.model.bean.CustomProfile;
import com.neusoft.besterlive.utils.ImgUtils;
import com.tencent.TIMUserProfile;

import java.util.Map;

import static com.neusoft.besterlive.R.id.user_avatar;
import static com.neusoft.besterlive.R.id.user_name;

/**
 * Created by Wzich on 2017/11/28.
 */

public class ShowUserInfoDialog extends TransParentDialog {
    private TIMUserProfile timUserProfile;

    private ImageView mUserClose;
    private ImageView mUserAvatar;
    private TextView mUserName;
    private ImageView mUserGender;
    private TextView mUserId;
    private TextView mUserLevel;
    private TextView mUserRenzhen;
    private TextView mUserSign;
    private TextView mUserSongchu;
    private TextView mUserDePiao;

    public ShowUserInfoDialog(Activity activity, TIMUserProfile userProfile) {
        super(activity);
        this.timUserProfile = userProfile;
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_userinfo, null);
        setContentView(view);

        findAllViews(view);
        bindData();
    }

    private void bindData() {
        String avatarUrl = timUserProfile.getFaceUrl();
        if (TextUtils.isEmpty(avatarUrl)) {
            ImgUtils.loadRound(R.drawable.default_avatar, mUserAvatar);
        } else {
            ImgUtils.loadRound(avatarUrl, mUserAvatar);
        }

        String nickName = timUserProfile.getNickName();
        if(TextUtils.isEmpty(nickName)){
            nickName = "用户";
        }
        mUserName.setText(nickName);

        long genderValue = timUserProfile.getGender().getValue();
        mUserGender.setImageResource(genderValue == 1 ? R.drawable.ic_male : R.drawable.ic_female);

        mUserId.setText("ID：" + timUserProfile.getIdentifier());

        String sign = timUserProfile.getSelfSignature();
        mUserSign.setText(TextUtils.isEmpty(sign) ? "Ta好像忘记写签名了..." : sign);

        Map<String, byte[]> customInfo = timUserProfile.getCustomInfo();

        String rezhen = getValue(customInfo, CustomProfile.CUSTOM_RENZHENG, "未知");
        mUserRenzhen.setText(rezhen);

        int sendNum = Integer.valueOf(getValue(customInfo, CustomProfile.CUSTOM_SEND, "0"));
        mUserSongchu.setText("送出：" + sendNum);

        int getNum = Integer.valueOf(getValue(customInfo,CustomProfile.CUSTOM_GET,"0"));
        mUserDePiao.setText("播票：" + getNum);

        String level = getValue(customInfo, CustomProfile.CUSTOM_LEVEL, "0");
        mUserLevel.setText(level);
    }

    private void findAllViews(View view) {
        mUserClose = (ImageView) view.findViewById(R.id.user_close);
        mUserAvatar = (ImageView) view.findViewById(user_avatar);
        mUserName = (TextView) view.findViewById(user_name);
        mUserGender = (ImageView) view.findViewById(R.id.user_gender);
        mUserId = (TextView) view.findViewById(R.id.user_id);
        mUserLevel = (TextView) view.findViewById(R.id.user_level);
        mUserRenzhen = (TextView) view.findViewById(R.id.user_renzhen);
        mUserSign = (TextView) view.findViewById(R.id.user_sign);
        mUserSongchu = (TextView) view.findViewById(R.id.user_songchu);
        mUserDePiao = (TextView) view.findViewById(R.id.user_depiao);

        mUserClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    private String getValue(Map<String, byte[]> customInfo, String key, String defaultValue) {
        if (customInfo != null) {
            byte[] valueBytes = customInfo.get(key);
            if (valueBytes != null) {
                return new String(valueBytes);
            }
        }
        return defaultValue;
    }

}
