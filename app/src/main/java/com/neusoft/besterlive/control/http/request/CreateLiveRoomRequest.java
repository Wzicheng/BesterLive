package com.neusoft.besterlive.control.http.request;

import com.neusoft.besterlive.control.http.response.CreateLiveRoomResponseObject;
import com.neusoft.besterlive.utils.BaseRequest;

/**
 * Created by Wzich on 2017/11/11.
 */

public class CreateLiveRoomRequest extends BaseRequest {
    private static final String host = "http://besterlive.butterfly.mopaasapp.com/roomServlet?action=createLiveRoom&";

    public static class CreateLiveRoomParam{
        public String userId;
        public String userName;
        public String userAvatar;
        public String liveTitle;
        public String liveCover;

        private static final String Param_User_id = "userId";
        private static final String Param_User_name = "userName";
        private static final String Param_User_avatar = "userAvatar";
        private static final String Param_Live_cover = "liveCover";
        private static final String Param_Live_title = "liveTitle";

        @Override
        public String toString() {
            return  Param_User_id + "=" + userId +
                    "&" + Param_User_name + "=" + userName +
                    "&" + Param_User_avatar + "=" + userAvatar +
                    "&" + Param_Live_cover + "=" + liveCover +
                    "&" + Param_Live_title + "=" + liveTitle;
        }
    }

    public void request(CreateLiveRoomParam param){
        String url = host + param.toString();
        request(url);
    }

    @Override
    public Object onSuccess(String body) {
        CreateLiveRoomResponseObject responseObject = gson.fromJson(body, CreateLiveRoomResponseObject.class);
        return responseObject.data;
    }
}
