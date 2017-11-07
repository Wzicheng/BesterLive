package com.neusoft.besterlive.model.bean;

import com.tencent.TIMFriendshipManager;

/**
 * 用户信息字段
 * Created by Wzich on 2017/10/29.
 */

public class CustomProfile {
    //自定义字段
    private static final String PREFIX = "Tag_Profile_Custom_";
    public static final String CUSTOM_RENZHENG = PREFIX + "renzheng";
    public static final String CUSTOM_LEVEL = PREFIX + "level";
    public static final String CUSTOM_GET = PREFIX + "getNums";
    public static final String CUSTOM_SEND = PREFIX + "sendNums";

    //腾讯基本字段(生日，头像路径，性别，语言，地区，昵称，个性签名，备注，群组)
    public static final long AllBaseInfo =
            TIMFriendshipManager.TIM_PROFILE_FLAG_BIRTHDAY |
            TIMFriendshipManager.TIM_PROFILE_FLAG_FACE_URL |
            TIMFriendshipManager.TIM_PROFILE_FLAG_GENDER |
            TIMFriendshipManager.TIM_PROFILE_FLAG_LANGUAGE |
            TIMFriendshipManager.TIM_PROFILE_FLAG_LOCATION |
            TIMFriendshipManager.TIM_PROFILE_FLAG_NICK |
            TIMFriendshipManager.TIM_PROFILE_FLAG_SELF_SIGNATURE |
            TIMFriendshipManager.TIM_PROFILE_FLAG_REMARK |
            TIMFriendshipManager.TIM_PROFILE_FLAG_GROUP;
}