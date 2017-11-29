package com.neusoft.besterlive.control.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.neusoft.besterlive.BesterApplication;
import com.neusoft.besterlive.R;
import com.neusoft.besterlive.control.HostOperateStatus;
import com.neusoft.besterlive.control.fragment.EditProfileFragment;
import com.neusoft.besterlive.control.http.request.GetGiftRequest;
import com.neusoft.besterlive.view.BottomControlView;
import com.neusoft.besterlive.view.ChatView;
import com.neusoft.besterlive.view.DanMuView;
import com.neusoft.besterlive.view.Dialog.HostControlDialog;
import com.neusoft.besterlive.view.GiftFullView;
import com.neusoft.besterlive.view.GiftRepeatView;
import com.neusoft.besterlive.view.Dialog.GiftSelectDialog;
import com.neusoft.besterlive.view.MsgListView;
import com.neusoft.besterlive.view.TitleView;
import com.neusoft.besterlive.view.weight.SizeChangeRelativeLayout;
import com.neusoft.besterlive.model.bean.CustomProfile;
import com.neusoft.besterlive.model.bean.GiftInfo;
import com.neusoft.besterlive.model.bean.IMConstants;
import com.neusoft.besterlive.model.bean.MsgInfo;
import com.neusoft.besterlive.model.bean.UserInfo;
import com.neusoft.besterlive.utils.BaseRequest;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tyrantgit.widget.HeartLayout;

import static com.tencent.qalsdk.base.a.ca;

/**
 * Created by Wzich on 2017/11/13.
 */

public class HostLiveActivity extends AppCompatActivity {
    private SizeChangeRelativeLayout mActivityHostLive;
    private AVRootView mLiveView;
    private TitleView mTitleView;
    private BottomControlView mBottomControlView;
    private MsgListView mMsgListView;
    private HeartLayout mHeartLayout;
    private DanMuView mDanMuView;
    private GiftRepeatView mGiftRepeateView;
    private GiftFullView mGiftFullView;
    private ChatView mChatView;
    private int roomId;

    private Timer heartTimer = new Timer();
    private Random colorRandom = new Random();

    private HostOperateStatus mHostOperateStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_live);
        initView();
        createRoom();
    }

    private void createRoom() {
        roomId = getIntent().getIntExtra("roomId", -1);
        if (roomId < 0) {
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
                //创建房间成功后将主播信息传递到TitleView
                mTitleView.setHost(BesterApplication.getApp().getSelfProfile());

                //创建直播房间成功，显示心形欢迎动画
                heartTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        showHeartAnim();
                    }
                }, 0, 1000);

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(HostLiveActivity.this, "创建房间失败,原因:" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //获取心形动画随机颜色
    private int getRandomColor() {
        int randomColor = Color.rgb(colorRandom.nextInt(255), colorRandom.nextInt(255)
                , colorRandom.nextInt(255));
        return randomColor;
    }

    //初始化各控件
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

        //顶部信息显示窗口
        mTitleView = (TitleView) findViewById(R.id.title_view);


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
                switch (cmd.getCmd()) {
                    case IMConstants.CMD_MSG_LIST: //来自信息
                        MsgInfo msgInfo = getMsgInfo(cmd, userProfile);
                        mMsgListView.addMsg(msgInfo);
                        break;
                    case IMConstants.CMD_MSG_DANMU: //来自弹幕
                        MsgInfo msgDanmuInfo = getMsgInfo(cmd, userProfile);
                        mMsgListView.addMsg(msgDanmuInfo);
                        mDanMuView.showMsgDanMu(msgDanmuInfo);
                        break;
                    case IMConstants.CMD_MGS_GIFT: //来自礼物
                        //显示动画
                        GiftSelectDialog.GiftCmdInfo cmdInfo = new Gson().fromJson(cmd.getParam()
                                , GiftSelectDialog.GiftCmdInfo.class);
                        if (cmdInfo == null) {
                            return;
                        }
                        GiftInfo giftInfo = GiftInfo.getGiftById(cmdInfo.giftId);
                        if (giftInfo.giftId == GiftInfo.Gift_Heart.giftId) {
                            mHeartLayout.addHeart(getRandomColor());
                        } else if (giftInfo.type == GiftInfo.Type.ContinueGift) {
                            getGift(giftInfo);
                            mGiftRepeateView.showGiftMsg(giftInfo, cmdInfo.repeatId, BesterApplication.getApp().getSelfProfile());
                        } else if (giftInfo.type == GiftInfo.Type.FullScreenGift) {
                            getGift(giftInfo);
                            mGiftFullView.showGift(giftInfo, BesterApplication.getApp().getSelfProfile());
                        }
                        break;
                    case ILVLiveConstants.ILVLIVE_CMD_ENTER:
                        //用户加入直播间
                        mTitleView.addNewWatcher(userProfile);
                        break;
                    case ILVLiveConstants.ILVLIVE_CMD_LEAVE:
                        //用户离开房间
                        mTitleView.userQuitRoom(userProfile);
                        break;
                }
            }

            @Override
            public void onNewOtherMsg(TIMMessage message) {

            }
        });

        //心形点赞窗口
        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);


        //礼物显示窗口
        mGiftRepeateView = (GiftRepeatView) findViewById(R.id.gift_repeate_view);

        //弹幕显示窗口
        mDanMuView = (DanMuView) findViewById(R.id.danmu_view);

        //消息显示窗口
        mMsgListView = (MsgListView) findViewById(R.id.msg_list_view);

        //底部控制窗口，设置监听
        mBottomControlView = (BottomControlView) findViewById(R.id.bottom_control_view);
        mBottomControlView.isHost(true);
        mHostOperateStatus = new HostOperateStatus();
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

            @Override
            public void onGiftClick() {

            }

            @Override
            public void onOperateClick(View view) {
                //主播操作栏
                showHostControlDialog(view);
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

        //全屏礼物部分
        mGiftFullView = (GiftFullView) findViewById(R.id.gift_full_view);

        //设置初始化的显示状态
        mBottomControlView.setVisibility(View.VISIBLE);
        mChatView.setVisibility(View.INVISIBLE);
    }

    private void showHostControlDialog(View view) {
        final HostControlDialog hostControlDialog = new HostControlDialog(this);
        hostControlDialog.setOnHostControlClickListener(new HostControlDialog.OnHostControlClickListener() {
            @Override
            public void onBeautyClick() {
                //点击美颜
                mHostOperateStatus.switchBeauty();
                refreshIcon(hostControlDialog);
            }

            @Override
            public void onFlashLightClick() {
                //点击闪关灯
                mHostOperateStatus.switchFlashLight();
                refreshIcon(hostControlDialog);
            }

            @Override
            public void onVoiceClick() {
                //点击静音
                mHostOperateStatus.switchVoice();
                refreshIcon(hostControlDialog);
            }

            @Override
            public void onCameraClick() {
                //点击切换摄像头
                mHostOperateStatus.switchCamera();
                refreshIcon(hostControlDialog);
            }

            @Override
            public void onDialogDismiss() {
                mBottomControlView.setOperateIcon(false);

            }
        });
        refreshIcon(hostControlDialog);
        hostControlDialog.showDialog(view);
    }

    private void refreshIcon(HostControlDialog hostControlDialog) {
        hostControlDialog.setStatusIcon(mHostOperateStatus.isBeauty(),
                mHostOperateStatus.isFlashLight(), mHostOperateStatus.isVoice());
    }

    private void getGift(GiftInfo giftInfo) {
        GetGiftRequest request = new GetGiftRequest();
        request.setOnResultListener(new BaseRequest.OnResultListener<UserInfo>() {
            @Override
            public void onFail(int code, String msg) {

            }

            @Override
            public void onSuccess(UserInfo object) {
                //更新IM的信息
                //更新等级信息
                TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_LEVEL, (object.level + "").getBytes(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess() {
                        saveSelfInfo();
                    }
                });
                TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_GET, (object.getNums + "").getBytes(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess() {
                        saveSelfInfo();
                    }
                });
            }
        });
        GetGiftRequest.GetGiftParam param = new GetGiftRequest.GetGiftParam();
        param.exp = giftInfo.expValue;
        param.userId = BesterApplication.getApp().getSelfProfile().getIdentifier();
        request.request(param);
    }


    //获得Msg信息
    @NonNull
    private MsgInfo getMsgInfo(ILVCustomCmd cmd, TIMUserProfile userProfile) {
        MsgInfo msgInfo = new MsgInfo();
        msgInfo.msgContent = cmd.getParam();
        msgInfo.userId = userProfile.getIdentifier();
        msgInfo.userLevel = Integer.valueOf(EditProfileFragment.getValue(
                userProfile.getCustomInfo(), CustomProfile.CUSTOM_LEVEL, "1"
        ));
        String userNick = userProfile.getNickName();
        if (TextUtils.isEmpty(userNick)) {
            userNick = userProfile.getIdentifier();
        }
        msgInfo.userNick = userNick;
        msgInfo.userAvatar = userProfile.getFaceUrl();
        return msgInfo;
    }

    //发送聊天消息
    private void sendChatMsg(final ILVCustomCmd customCmd) {
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
            @Override
            public void onSuccess(TIMMessage data) {
                MsgInfo msgInfo = new MsgInfo();
                msgInfo.msgContent = customCmd.getParam();
                TIMUserProfile timSelfprofile = BesterApplication.getApp().getSelfProfile();
                msgInfo.userId = timSelfprofile.getIdentifier();
                msgInfo.userLevel = Integer.valueOf(EditProfileFragment.getValue(
                        timSelfprofile.getCustomInfo(), CustomProfile.CUSTOM_LEVEL, "1"
                ));
                String userNick = timSelfprofile.getNickName();
                if (TextUtils.isEmpty(userNick)) {
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
        //避免内存泄漏
        heartTimer.cancel();
        quitRoom();
    }

    private void quitRoom() {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        customCmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_LEAVE);
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
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

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });

    }

    // 保存更新信息
    private void saveSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取用户字段信息成功，将其保存
                BesterApplication.getApp().saveSelfProfile(timUserProfile);
            }
        });
    }

    //展示心形动画
    private void showHeartAnim() {
        mHeartLayout.post(new Runnable() {
            @Override
            public void run() {
                mHeartLayout.addHeart(getRandomColor());
            }
        });
    }
}
