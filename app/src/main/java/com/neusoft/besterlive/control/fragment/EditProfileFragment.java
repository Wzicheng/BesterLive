package com.neusoft.besterlive.control.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.neusoft.besterlive.BesterApplication;
import com.neusoft.besterlive.R;
import com.neusoft.besterlive.view.Dialog.EditGenderDialog;
import com.neusoft.besterlive.view.weight.EditProfile;
import com.neusoft.besterlive.view.Dialog.EditProfileDialog;
import com.neusoft.besterlive.view.TextProfile;
import com.neusoft.besterlive.model.bean.CustomProfile;
import com.neusoft.besterlive.utils.ImgUtils;
import com.neusoft.besterlive.utils.PicChooserHelper;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Wzich on 2017/11/1.
 */

public class EditProfileFragment extends Fragment{
    private Toolbar mTitleBarEditProfile;
    private LinearLayout mAvatarView;
    private ImageView mAvatarImg;
    private EditProfile mNickName;
    private EditProfile mGender;
    private EditProfile mSign;
    private EditProfile mRenzhen;
    private EditProfile mLocation;
    private TextProfile mId;
    private TextProfile mLevel;
    private TextProfile mGet;
    private TextProfile mSend;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile,container,false);
        findAllViews(view);
        setTitleBar();
        setIconKey();//设置字段
        updateView();

        //第一次登陆进入个人信息编辑页面后修改sharedPreferences中的第一次登陆标记
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstLogin",MODE_PRIVATE);
        SharedPreferences.Editor spe = sharedPreferences.edit();
        spe.putBoolean("firstLogin",false);
        spe.commit();

        return view;
    }
    private void updateView() {
        TIMUserProfile myProfile = BesterApplication.getApp().getSelfProfile();
        if (myProfile != null){
            String faceUrl = myProfile.getFaceUrl();
            if (TextUtils.isEmpty(faceUrl)){
                ImgUtils.loadRound(R.drawable.default_avatar,mAvatarImg);
            } else {
                ImgUtils.loadRound(faceUrl,mAvatarImg);
            }
            mNickName.setValue(myProfile.getNickName());
            mGender.setValue(myProfile.getGender().getValue() == 1 ? "男" : "女");
            mSign.setValue(myProfile.getSelfSignature());
            mLocation.setValue(myProfile.getLocation());
            mId.setValue(myProfile.getIdentifier());

            Map<String,byte[]> customInfo = myProfile.getCustomInfo();
            mRenzhen.setValue(getValue(customInfo, CustomProfile.CUSTOM_RENZHENG,"未知"));
            mLevel.setValue(getValue(customInfo,CustomProfile.CUSTOM_LEVEL,"1"));
            mGet.setValue(getValue(customInfo,CustomProfile.CUSTOM_GET,"0"));
            mSend.setValue(getValue(customInfo,CustomProfile.CUSTOM_SEND,"0"));
        } else{
            getSelfInfo();
        }

    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getContext(), "获取用户字段信息失败！错误码：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取用户字段信息成功，将其保存
                BesterApplication.getApp().saveSelfProfile(timUserProfile);
                updateView();
            }
        });
    }

    public static String getValue(Map<String, byte[]> customInfo, String key, String defaultValue) {
        if (customInfo != null){
            byte[] valueBytes = customInfo.get(key);
            if (valueBytes != null){
                return new String(valueBytes);
            }
        }
        return defaultValue;
    }

    private void setIconKey() {
        mNickName.setType(R.drawable.ic_info_nickname,"昵称");
        mGender.setType(R.drawable.ic_info_gender,"性别");
        mSign.setType(R.drawable.ic_info_sign,"签名");
        mRenzhen.setType(R.drawable.ic_info_renzhen,"认证");
        mLocation.setType(R.drawable.ic_info_location,"地区");
        mId.setType(R.drawable.ic_info_id,"ID");
        mLevel.setType(R.drawable.ic_info_level,"等级");
        mGet.setType(R.drawable.ic_info_get,"获得票数");
        mSend.setType(R.drawable.ic_info_send,"送出票数");
    }

    private void setTitleBar() {
        mTitleBarEditProfile.setTitle("编辑个人信息");
        mTitleBarEditProfile.setTitleTextColor(Color.WHITE);
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity){
            ((AppCompatActivity)activity).setSupportActionBar(mTitleBarEditProfile);
        }
    }

    private void findAllViews(View view) {
        mTitleBarEditProfile = (Toolbar) view.findViewById(R.id.title_bar_edit_profile);
        mAvatarView = (LinearLayout) view.findViewById(R.id.avatar_view);
        mAvatarImg = (ImageView) view.findViewById(R.id.avatar_img);
        mNickName = (EditProfile) view.findViewById(R.id.nick_name);
        mGender = (EditProfile) view.findViewById(R.id.gender);
        mSign = (EditProfile) view.findViewById(R.id.sign);
        mRenzhen = (EditProfile) view.findViewById(R.id.renzhen);
        mLocation = (EditProfile) view.findViewById(R.id.location);
        mId = (TextProfile) view.findViewById(R.id.id);
        mLevel = (TextProfile) view.findViewById(R.id.level);
        mGet = (TextProfile) view.findViewById(R.id.get);
        mSend = (TextProfile) view.findViewById(R.id.send);


        //设置监听
        mAvatarView.setOnClickListener(new MyOnClickListener());
        mNickName.setOnClickListener(new MyOnClickListener());
        mGender.setOnClickListener(new MyOnClickListener());
        mSign.setOnClickListener(new MyOnClickListener());
        mRenzhen.setOnClickListener(new MyOnClickListener());
        mLocation.setOnClickListener(new MyOnClickListener());

    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.avatar_view:
                    showEditAvatar();
                    break;
                case R.id.nick_name:
                    showEditNickNameDialog();
                    break;
                case R.id.gender:
                    showEditGenderDialog();
                    break;
                case R.id.sign:
                    showEditSignDialog();
                    break;
                case R.id.renzhen:
                    showEditRenZhenDialog();
                    break;
                case R.id.location:
                    showEditLocationDialog();
                    break;
            }
        }
    }

    private PicChooserHelper picChooseHelper;
    private void showEditAvatar() {
        picChooseHelper = new PicChooserHelper(this, PicChooserHelper.PicType.Avatar);
        picChooseHelper.setOnUpdateListener(new PicChooserHelper.OnUpdateListener() {
            @Override
            public void onSuccess(String url) {
                //更新头像信息
                TIMFriendshipManager.getInstance().setFaceUrl(url, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getContext(), "更新头像失败" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "更新头像成功" , Toast.LENGTH_SHORT).show();
                        getSelfInfo();
                    }
                });
            }

            @Override
            public void onFail() {
                Toast.makeText(getContext(), "更新头像失败" , Toast.LENGTH_SHORT).show();
            }
        });
        picChooseHelper.showPicChooserDialog();
    }

    private void showEditLocationDialog() {
        EditProfileDialog dialog = new EditProfileDialog(getActivity());
        dialog.setOnOKListener(new EditProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, String content) {
                TIMFriendshipManager.getInstance().setLocation(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getContext(), "更新地区失败" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() { //更新信息成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("地区", R.drawable.ic_info_location, mLocation.getValue());
    }

    private void showEditRenZhenDialog() {
        EditProfileDialog dialog = new EditProfileDialog(getActivity());
        dialog.setOnOKListener(new EditProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, String content) {
                TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_RENZHENG, content.getBytes(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getContext(), "更新认证失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() { //更新信息成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("认证", R.drawable.ic_info_renzhen, mRenzhen.getValue());
    }

    private void showEditSignDialog() {
        EditProfileDialog dialog = new EditProfileDialog(getActivity());
        dialog.setOnOKListener(new EditProfileDialog.OnOKListener(){
            @Override
            public void onOk(String title, String content) {
                TIMFriendshipManager.getInstance().setSelfSignature(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getContext(), "更新签名失败！错误码：" + i, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() { //更新信息成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("签名",R.drawable.ic_info_sign,mSign.getValue());
    }

    private void showEditGenderDialog() {
        EditGenderDialog dialog = new EditGenderDialog(getActivity());
        dialog.setOnChangeGenderListener(new EditGenderDialog.OnChangeGenderListener() {
            @Override
            public void onChangeGender(boolean isMale) {
                TIMFriendGenderType gender = isMale ? TIMFriendGenderType.Male : TIMFriendGenderType.Female;
                TIMFriendshipManager.getInstance().setGender(gender, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getContext(), "更新性别失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() { //更新信息成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show(mGender.getValue().equals("男"));
    }

    private void showEditNickNameDialog() {
        EditProfileDialog dialog = new EditProfileDialog(getActivity());
        dialog.setOnOKListener(new EditProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setNickName(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getContext(), "更新昵称失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() { //更新信息成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("昵称", R.drawable.ic_info_nickname, mNickName.getValue());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (picChooseHelper != null){
            picChooseHelper.onActivityResult(requestCode,resultCode,data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
