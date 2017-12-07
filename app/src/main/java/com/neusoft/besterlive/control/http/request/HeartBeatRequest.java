package com.neusoft.besterlive.control.http.request;

import com.neusoft.besterlive.utils.BaseRequest;

/**
 * Created by Wzich on 2017/11/29.
 */

public class HeartBeatRequest extends BaseRequest {
//    private static final String host = "http://besterlive.butterfly.mopaasapp.com/roomServlet?action=heartBeat&";
    private static final String host = "http://39.108.8.161/BesterLive/roomServlet?action=heartBeat&";

    public static class HeartBeatParam{
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

    public void request(HeartBeatParam param){
        String url = host + param.toString();
        request(url);
    }

    @Override
    public Object onSuccess(String body) {
        return null;
    }
}
