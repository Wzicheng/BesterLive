package com.neusoft.besterlive.control.http.request;

import com.neusoft.besterlive.utils.BaseRequest;

/**
 * Created by Wzich on 2017/11/29.
 */

public class QuitRoomRequest extends BaseRequest {
//    private static final String host = "http://besterlive.butterfly.mopaasapp.com/roomServlet?action=quitRoom&";
    private static final String host = "http://39.108.8.161/BesterLive/roomServlet?action=quitRoom&";

    public static class QuitRoomParam{
        public String userId;
        public int roomId;

        private static final String Param_User_id = "userId";
        private static final String Param_Room_id = "roomId";

        @Override
        public String toString() {
            return  Param_User_id + "=" + userId +
                    "&" + Param_Room_id + "=" +roomId;
        }
    }

    public void request(QuitRoomParam param){
        String url = host + param.toString();
        request(url);
    }

    @Override
    public Object onSuccess(String body) {
        return null;
    }
}
