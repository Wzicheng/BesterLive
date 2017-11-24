package com.neusoft.besterlive;

import android.app.Application;

import com.neusoft.besterlive.model.bean.CustomProfile;
import com.neusoft.besterlive.utils.QnUploadHelper;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wzich on 2017/10/27.
 */

public class BesterApplication extends Application {
    private static BesterApplication app;
    private static TIMUserProfile mTIMUserProfile;
    private static ILVLiveConfig liveConfig;

    //AK密钥
    private String accessKey = "Z7dCSZ6mSiHVHg_-qMloEjMpL_xv3A7W4fst7wzR";
    //SK密钥
    private String secretKey = "3rgyMqlWJt-hjKzS6pMNkfc39xl6WL2w0GeO_yOG";
    //域名
    private String domain = "http://oymnwdkwz.bkt.clouddn.com/";
    //存储空间名
    private String bucketName = "bester-app";

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initLiveSdk();
        init7niu();
    }

    private void init7niu() {
        QnUploadHelper.init(accessKey,secretKey,domain,bucketName);
    }

    private void initLiveSdk() {
        //初始化SDK
        ILiveSDK.getInstance().initSdk(this, 1400046908, 18599);
        liveConfig = new ILVLiveConfig();
        //初始化直播场景
        ILVLiveManager.getInstance().init(liveConfig);

        //用户信息字段的配置
        List<String> custom = new ArrayList<>();
        custom.add(CustomProfile.CUSTOM_RENZHENG);
        custom.add(CustomProfile.CUSTOM_LEVEL);
        custom.add(CustomProfile.CUSTOM_GET);
        custom.add(CustomProfile.CUSTOM_SEND);
        TIMManager.getInstance().initFriendshipSettings(CustomProfile.AllBaseInfo,custom);
    }

    public static BesterApplication getApp() {
        return app;
    }

    /**
     * 保存获取的用户字段信息
     * @param timUserProfile
     */
    public void saveSelfProfile(TIMUserProfile timUserProfile) {
        mTIMUserProfile = timUserProfile;
    }

    public TIMUserProfile getSelfProfile(){
         return mTIMUserProfile;
    }

    public ILVLiveConfig getLiveConfig(){
        return liveConfig;
    }
}
