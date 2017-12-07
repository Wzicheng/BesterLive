package com.neusoft.besterlive.control.http.request;

import com.neusoft.besterlive.control.http.response.GetWatchersResponseObject;
import com.neusoft.besterlive.utils.BaseRequest;

/**
 * Created by Wzich on 2017/12/1.
 */

public class GetWatchersRequest extends BaseRequest {
//    private static final String host = "http://besterlive.butterfly.mopaasapp.com/roomServlet?action=getWatchers&";
    private static final String host = "http://39.108.8.161/BesterLive/roomServlet?action=getWatchers&";

    public static class GetWatchersParam{
        public int roomId;

        private static final String Param_Room_id = "roomId";

        @Override
        public String toString() {
            return Param_Room_id + "=" +roomId;
        }
    }

    public void request(GetWatchersParam param) {
        request(host + param.toString());
    }

    @Override
    public Object onSuccess(String body) {
        GetWatchersResponseObject responseObj = gson.fromJson(body, GetWatchersResponseObject.class);
        return responseObj.data;
    }
}
