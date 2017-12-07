package com.neusoft.besterlive.control.http.request;

import com.neusoft.besterlive.control.http.response.GetGiftResponseObject;
import com.neusoft.besterlive.utils.BaseRequest;

/**
 * Created by Wzich on 2017/11/27.
 */

public class GetGiftRequest extends BaseRequest {
//    private static final String host = "http://besterlive.butterfly.mopaasapp.com/userServlet?action=getGift&";
    private static final String host = "http://39.108.8.161/BesterLive/userServlet?action=getGift&";

    public static class GetGiftParam{
        public String userId;
        public int exp;

        private static final String Param_User_id = "userId";
        private static final String Param_Exp = "exp";

        @Override
        public String toString() {
            return  Param_User_id + "=" + userId +
                    "&" + Param_Exp + "=" + exp;
        }
    }

    public void request(GetGiftParam param){
        String url = host + param.toString();
        request(url);
    }

    @Override
    public Object onSuccess(String body) {
        GetGiftResponseObject getGiftResponseObejct = gson.fromJson(body
                ,GetGiftResponseObject.class);
        return getGiftResponseObejct.data;
    }
}
