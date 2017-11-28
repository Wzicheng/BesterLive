package com.neusoft.besterlive.control.activity;

import android.content.DialogInterface;
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
import com.neusoft.besterlive.control.fragment.EditProfileFragment;
import com.neusoft.besterlive.control.http.request.SendGiftRequest;
import com.neusoft.besterlive.view.BottomControlView;
import com.neusoft.besterlive.view.ChatView;
import com.neusoft.besterlive.view.DanMuView;
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
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tyrantgit.widget.HeartLayout;

import static com.tencent.qalsdk.base.a.ca;
import static com.tencent.qalsdk.base.a.cu;

/**
 * Created by Wzich on 2017/11/12.
 */

public class WatcherLiveActivity extends AppCompatActivity {
    private SizeChangeRelativeLayout mActivityHostLive;
    private AVRootView mLiveView;
    private TitleView mTitleView;
    private BottomControlView mBottomControlView;
    private MsgListView mMsgListView;
    private HeartLayout mHeartLayout;
    private GiftRepeatView mGiftRepeateView;
    private DanMuView mDanMuView;
    private ChatView mChatView;
    private GiftFullView mGiftFullView;

    private Timer heartTimer = new Timer();
    private Random colorRandom = new Random();

    private int roomId;
    private String userId;
    private String hostId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_live);
        initView();
        joinRoom();
    }

    private void joinRoom() {
        roomId = getIntent().getIntExtra("roomId",-1);
        if (roomId < 0){
            Toast.makeText(this, "房间号异常", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        hostId = getIntent().getStringExtra("hostId");
        //加入房间配置项
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(hostId)
                .controlRole("Guest")   //设置角色
                .autoCamera(false) //是否自动打开摄像头
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO
                    | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)//设置权限
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)//是否开始半自动接收
                .autoMic(false);//是否自动打开mic

        ILVLiveManager.getInstance().joinRoom(roomId, memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //根据hostId获取主播信息
                final List<String> userId = new ArrayList<>();
                userId.add(hostId);
                TIMFriendshipManager.getInstance().getUsersProfile(userId, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(WatcherLiveActivity.this, "获取主播信息失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> userProfiles) {
                        mTitleView.setHost(userProfiles.get(0));
                    }
                });
                mTitleView.addNewWatcher(BesterApplication.getApp().getSelfProfile());

                ILVCustomCmd customCmd = new ILVCustomCmd();
                customCmd.setType(ILVText.ILVTextType.eGroupMsg);
                customCmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_ENTER);
                customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
                ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {

                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {

                    }
                });

                //加入房间成功，显示心形欢迎动画
                heartTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        showHeartAnim();
                    }
                },0,1000);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //加入房间失败
                Toast.makeText(WatcherLiveActivity.this, "加入房间失败", Toast.LENGTH_SHORT).show();
                quitRoom();
            }
        });
    }

    //显示心形动画
    private void showHeartAnim() {
        mHeartLayout.post(new Runnable() {
            @Override
            public void run() {
                mHeartLayout.addHeart(getRandomColor());
            }
        });
    }

    //获取心形动画随机颜色
    private int getRandomColor() {
        int randomColor = Color.rgb(colorRandom.nextInt(255),colorRandom.nextInt(255)
                ,colorRandom.nextInt(255));
        return randomColor;
    }

    private void initView() {
        //对整个RelativeLayout进行监听
        mActivityHostLive = (SizeChangeRelativeLayout) findViewById(R.id.activity_watch_live);
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

        //心形点赞窗口
        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        mHeartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击后发送心形礼物
                ILVCustomCmd customCmd = new ILVCustomCmd();
                customCmd.setCmd(IMConstants.CMD_MGS_GIFT);
                customCmd.setType(ILVText.ILVTextType.eGroupMsg);
                customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
                GiftSelectDialog.GiftCmdInfo giftCmdInfo = new GiftSelectDialog.GiftCmdInfo();
                giftCmdInfo.giftId = GiftInfo.Gift_Heart.giftId;
                customCmd.setParam(new Gson().toJson(giftCmdInfo));

                sendGiftMsg(customCmd);
            }
        });

        //礼物显示窗口
        mGiftRepeateView = (GiftRepeatView) findViewById(R.id.gift_repeate_view);

        //弹幕显示窗口
        mDanMuView = (DanMuView) findViewById(R.id.danmu_view);

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

                switch (cmd.getCmd()){
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
                            ,GiftSelectDialog.GiftCmdInfo.class);
                        if (cmdInfo == null){
                            return;
                        }
                        GiftInfo giftInfo = GiftInfo.getGiftById(cmdInfo.giftId);
                        if (giftInfo.giftId == GiftInfo.Gift_Heart.giftId){
                            mHeartLayout.addHeart(getRandomColor());
                        } else if (giftInfo.type == GiftInfo.Type.ContinueGift){
                            mGiftRepeateView.showGiftMsg(giftInfo,cmdInfo.repeatId,BesterApplication.getApp().getSelfProfile());
                        } else if (giftInfo.type == GiftInfo.Type.FullScreenGift){
                            mGiftFullView.showGift(giftInfo,BesterApplication.getApp().getSelfProfile());
                        }
                    case ILVLiveConstants.ILVLIVE_CMD_ENTER:
                        //新用户加入房间
                        mTitleView.addNewWatcher(userProfile);
                        break;

                    case ILVLiveConstants.ILVLIVE_CMD_LEAVE:
                        //有用户退出房间
                        if (userProfile.getIdentifier().equals(hostId)){
                            //主播退出房间，关闭直播窗口
                            quitRoom();
                        } else {
                            mTitleView.userQuitRoom(userProfile);
                        }
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
        mBottomControlView.isHost(false);
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
                //显示礼物操作栏
                mBottomControlView.setVisibility(View.INVISIBLE);
                GiftSelectDialog giftSelectDialog = new GiftSelectDialog(WatcherLiveActivity.this);
                giftSelectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mBottomControlView.setVisibility(View.VISIBLE);
                    }
                });
                giftSelectDialog.setOnGiftSendListener(new GiftSelectDialog.OnGiftSendListener() {
                    @Override
                    public void onGiftSend(ILVCustomCmd customCmd) {
                        sendGiftMsg(customCmd);
                    }
                });
                giftSelectDialog.show();
            }

            @Override
            public void onOperateClick(View view) {

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

    private void sendGiftMsg(final ILVCustomCmd customCmd) {
        //发送礼物消息
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
            @Override
            public void onSuccess(TIMMessage data) {
                // 显示动画效果
                GiftSelectDialog.GiftCmdInfo cmdInfo =
                        new Gson().fromJson(customCmd.getParam(), GiftSelectDialog.GiftCmdInfo.class);
                if (cmdInfo == null) {
                    return;
                }
                GiftInfo giftInfo = GiftInfo.getGiftById(cmdInfo.giftId);
                if (giftInfo.giftId == GiftInfo.Gift_Heart.giftId){
                    mHeartLayout.addHeart(getRandomColor());
                } else if (giftInfo.type == GiftInfo.Type.ContinueGift){
                    sendGift(giftInfo);
                    mGiftRepeateView.showGiftMsg(giftInfo,cmdInfo.repeatId,BesterApplication.getApp().getSelfProfile());
                } else if (giftInfo.type == GiftInfo.Type.FullScreenGift){
                    sendGift(giftInfo);
                    mGiftFullView.showGift(giftInfo,BesterApplication.getApp().getSelfProfile());
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(WatcherLiveActivity.this, "发送礼物失败,原因：" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendGift(GiftInfo giftInfo) {
        SendGiftRequest request = new SendGiftRequest();
        request.setOnResultListener(new BaseRequest.OnResultListener<UserInfo>() {
            @Override
            public void onFail(int code, String msg) {

            }

            @Override
            public void onSuccess(UserInfo data) {
                //更新等级信息
                TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_LEVEL,
                        (data.level + "").getBytes(), new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {

                            }

                            @Override
                            public void onSuccess() {
                                saveSelfInfo();
                            }
                        });
                TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_SEND,
                        (data.sendNums + "").getBytes(), new TIMCallBack() {
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
        SendGiftRequest.SendGiftParam param = new SendGiftRequest.SendGiftParam();
        param.userId = BesterApplication.getApp().getSelfProfile().getIdentifier();
        param.exp = giftInfo.expValue;
        request.request(param);
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
        //避免内存泄漏
        heartTimer.cancel();
        quitRoom();
    }

    private void quitRoom() {
        //退出房间
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        customCmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_LEAVE);
        customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ;
                        Toast.makeText(WatcherLiveActivity.this, "退出房间成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        Toast.makeText(WatcherLiveActivity.this, "退出房间失败", Toast.LENGTH_SHORT).show();
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
}
