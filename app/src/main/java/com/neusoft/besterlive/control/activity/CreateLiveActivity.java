package com.neusoft.besterlive.control.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.neusoft.besterlive.BesterApplication;
import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.http.request.CreateLiveRoomRequest;
import com.neusoft.besterlive.model.bean.RoomInfo;
import com.neusoft.besterlive.utils.BaseRequest;
import com.neusoft.besterlive.utils.ImgUtils;
import com.neusoft.besterlive.utils.PicChooserHelper;
import com.tencent.TIMUserProfile;

/**
 * Created by Wzich on 2017/11/10.
 */

public class CreateLiveActivity extends AppCompatActivity {

    private Toolbar mTitlebar;
    private FrameLayout mSetCover;
    private EditText mTitle;
    private TextView mCreate;
    private ImageView mCover;
    private TextView mTvPicTip;
    private String coverUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlive);
        initView();
        setTitleBar();
    }

    private void setTitleBar() {
        mTitlebar.setTitle("创建直播");
        mTitlebar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mTitlebar);
    }

    private void initView() {
        mTitlebar = (Toolbar) findViewById(R.id.titlebar);
        mSetCover = (FrameLayout) findViewById(R.id.set_cover);
        mTitle = (EditText) findViewById(R.id.title);
        mCreate = (TextView) findViewById(R.id.create);
        mCover = (ImageView) findViewById(R.id.cover);
        mTvPicTip = (TextView) findViewById(R.id.tv_pic_tip);

        mSetCover.setOnClickListener(new MyOnClickListener());
        mCreate.setOnClickListener(new MyOnClickListener());

    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.set_cover://选择视频封面
                    showPicChooseDialog();
                    break;
                case R.id.create://创建直播房间
                    createLiveRoom();
                    break;

            }
        }
    }

    private void createLiveRoom() {
        //去服务器请求，获取roomID
        CreateLiveRoomRequest request = new CreateLiveRoomRequest();
        CreateLiveRoomRequest.CreateLiveRoomParam param = new CreateLiveRoomRequest.CreateLiveRoomParam();
        TIMUserProfile selfProfile = BesterApplication.getApp().getSelfProfile();
        if (selfProfile != null){
            param.userId = selfProfile.getIdentifier();
            param.userName = selfProfile.getNickName();
            param.userAvatar = selfProfile.getFaceUrl();
            param.liveTitle = mTitle.getText().toString();
            param.liveCover = coverUrl;
            request.setOnResultListener(new BaseRequest.OnResultListener<RoomInfo>() {
                @Override
                public void onFail(int code, String msg) {
                    Toast.makeText(CreateLiveActivity.this, "创建失败：" + msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(RoomInfo roomInfo) {
                    Intent intent = new Intent(CreateLiveActivity.this,HostLiveActivity.class);
                    intent.putExtra("roomId",roomInfo.roomId);
                    startActivity(intent);
                    finish();
                }
            });

            request.request(param);
        } else {
            Log.e("TAG","null");
        }

    }

    private PicChooserHelper picChooserHelper;

    private void showPicChooseDialog() {
        picChooserHelper = new PicChooserHelper(this, PicChooserHelper.PicType.Cover);
        picChooserHelper.setOnUpdateListener(new PicChooserHelper.OnUpdateListener() {
            @Override
            public void onSuccess(String url) { //图片选择成功
                coverUrl = url;
                ImgUtils.load(url,mCover);
                mTvPicTip.setVisibility(View.GONE);

            }

            @Override
            public void onFail() { //图片选择失败
                Toast.makeText(CreateLiveActivity.this, "封面获取失败", Toast.LENGTH_SHORT).show();

            }
        });
        picChooserHelper.showPicChooserDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (picChooserHelper != null) {
            picChooserHelper.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
