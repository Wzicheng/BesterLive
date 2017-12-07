package com.neusoft.besterlive.control.http.request;

import com.neusoft.besterlive.control.http.response.GetLiveRoomListResponseObject;
import com.neusoft.besterlive.utils.BaseRequest;

/**
 * Created by Wzich on 2017/11/12.
 */

public class GetLiveRoomRequest extends BaseRequest {
//    private static final String host = "http://besterlive.butterfly.mopaasapp.com/roomServlet?action=getLiveRoomList&";
    private static final String host = "http://39.108.8.161/BesterLive/roomServlet?action=getLiveRoomList&";

    public static class GetLiveRoomListParam {
        public int pageIndex;

        private static final String Param_page_index = "pageIndex";

        @Override
        public String toString() {
            return Param_page_index + "=" + pageIndex;
        }
    }

    public void request(GetLiveRoomListParam param){
        String url = host + param.toString();
        request(url);
    }

    @Override
    public Object onSuccess(String body) {
        GetLiveRoomListResponseObject responseObject = gson.fromJson(body,GetLiveRoomListResponseObject.class);
        return responseObject.data;
    }
}
