package com.neusoft.besterlive.control.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.neusoft.besterlive.BesterApplication;
import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.fragment.EditProfileFragment;
import com.neusoft.besterlive.control.view.BottomControlView;
import com.neusoft.besterlive.control.view.ChatView;
import com.neusoft.besterlive.control.view.DanMuView;
import com.neusoft.besterlive.control.view.MsgListView;
import com.neusoft.besterlive.control.view.SizeChangeRelativeLayout;
import com.neusoft.besterlive.model.bean.CustomProfile;
import com.neusoft.besterlive.model.bean.IMConstants;
import com.neusoft.besterlive.model.bean.MsgInfo;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

/**
 * Created by Wzich on 2017/11/13.
 */

public class HostLiveActivity extends AppCompatActivity {
    private SizeChangeRelativeLayout mActivityHostLive;
    private AVRootView mLiveView;
    private BottomControlView mBottomControlView;
    private MsgListView mMsgListView;
    private DanMuView mDanMuView;

    private ChatView mChatView;
    private int roomId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_live);
        initView();
        createRoom();
    }

    private void createRoom() {
        roomId = getIntent().getIntExtra("roomId",-1);
        if (roomId < 0){
            Toast.makeText(this.getApplicationContext(), "房间ID异常", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //创建房间配置项
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(ILiveLoginManager.getInstance().getMyUserId())
                .controlRole("LiveMaster")//角色设置
                .autoFocus(true)
                .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                .cameraId(ILiveConstants.FRONT_CAMERA)//设置前后置摄像头
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接受

        //创建直播房间
        ILVLiveManager.getInstance().createRoom(roomId, hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void initView() {
        //对整个RelativeLayout进行监听
        mActivityHostLive = (SizeChangeRelativeLayout) findViewById(R.id.activity_host_live);
        mActivityHostLive.setOnKeyBoardStatusListener(new SizeChangeRelativeLayout.OnKeyBoardStatusListener() {
            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {
                mBottomControlView.setVisibility(View.VISIBLE);
                mChatView.setVisibility(View.INVISIBLE);
            }
        });

        //视频直播窗口
        mLiveView = (AVRootView) findViewById(R.id.live_view);

        //初始化LiveConfig
        ILVLiveManager.getInstance().setAvVideoView(mLiveView);
        BesterApplication.getApp().getLiveConfig().setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
            @Override
            public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {

            }

            @Override
            public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {
                MsgInfo msgInfo = getMsgInfo(cmd, userProfile);
                switch (cmd.getCmd()){
                   case IMConstants.CMD_MSG_LIST: //来自信息
                       mMsgListView.addMsg(msgInfo);
                       break;

                   case IMConstants.CMD_MSG_DANMU: //来自弹幕
                       mMsgListView.addMsg(msgInfo);
                       mDanMuView.showMsgDanMu(msgInfo);
                       break;

                   case IMConstants.CMD_MGS_GIFT: //来自礼物
                       break;
                }
            }

            @Override
            public void onNewOtherMsg(TIMMessage message) {

            }
        });

        //弹幕显示部分
        mDanMuView = (DanMuView) findViewById(R.id.danmu_view);

        //消息显示窗口
        mMsgListView = (MsgListView) findViewById(R.id.msg_list_view);

        //底部控制窗口，设置监听
        mBottomControlView = (BottomControlView) findViewById(R.id.bottom_control_view);
        mBottomControlView.setOnControlClickListener(new BottomControlView.OnControlClickListener() {
            @Override
            public void onCloseClick() {
                //点击关闭直播
                finish();
            }

            @Override
            public void onChatClick() {
                //显示聊天操作栏
                mBottomControlView.setVisibility(View.INVISIBLE);
                mChatView.setVisibility(View.VISIBLE);
            }
        });

        //聊天信息输入窗口
        mChatView = (ChatView) findViewById(R.id.bottom_chat_view);
        mChatView.setOnSendClickListener(new ChatView.OnSendClickListener() {
            @Override
            public void onSendClick(ILVCustomCmd customCmd) {
                sendChatMsg(customCmd);
            }
        });

        //设置初始化的显示状态
        mBottomControlView.setVisibility(View.VISIBLE);
        mChatView.setVisibility(View.INVISIBLE);
    }

    @NonNull
    private MsgInfo getMsgInfo(ILVCustomCmd cmd, TIMUserProfile userProfile) {
        MsgInfo msgInfo = new MsgInfo();
        msgInfo.msgContent = cmd.getParam();
        msgInfo.userId = userProfile.getIdentifier();
        msgInfo.userLevel = Integer.valueOf(EditProfileFragment.getValue(
                userProfile.getCustomInfo(), CustomProfile.CUSTOM_LEVEL,"1"
        ));
        String userNick = userProfile.getNickName();
        if (TextUtils.isEmpty(userNick)){
            userNick = userProfile.getIdentifier();
        }
        msgInfo.userNick = userNick;
        msgInfo.userAvatar = userProfile.getFaceUrl();
        return msgInfo;
    }

    private void sendChatMsg(final ILVCustomCmd customCmd) {
        //发送消息
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
            @Override
            public void onSuccess(TIMMessage data) {
                MsgInfo msgInfo = new MsgInfo();
                msgInfo.msgContent = customCmd.getParam();
                TIMUserProfile timSelfprofile = BesterApplication.getApp().getSelfProfile();
                msgInfo.userId = timSelfprofile.getIdentifier();
                msgInfo.userLevel = Integer.valueOf(EditProfileFragment.getValue(
                        timSelfprofile.getCustomInfo(),CustomProfile.CUSTOM_LEVEL,"1"
                ));
                String userNick = timSelfprofile.getNickName();
                if (TextUtils.isEmpty(userNick)){
                    userNick = timSelfprofile.getIdentifier();
                }
                msgInfo.userNick = userNick;
                msgInfo.userAvatar = timSelfprofile.getFaceUrl();
                mMsgListView.addMsg(msgInfo);

                if (customCmd.getCmd() == IMConstants.CMD_MSG_DANMU) {
                    //同时添加到弹幕显示里面
                    mDanMuView.showMsgDanMu(msgInfo);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ILVLiveManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ILVLiveManager.getInstance().onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quitRoom();
    }

    private void quitRoom() {
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Toast.makeText(HostLiveActivity.this, "退出房间成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(HostLiveActivity.this, "退出房间失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
