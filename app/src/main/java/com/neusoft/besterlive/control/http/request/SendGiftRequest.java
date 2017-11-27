package com.neusoft.besterlive.control.http.request;

import com.neusoft.besterlive.control.http.response.SendGiftResponseObject;
import com.neusoft.besterlive.utils.BaseRequest;

/**
 * Created by Wzich on 2017/11/27.
 */

public class SendGiftRequest extends BaseRequest {
    private static final String host = "http://besterlive.butterfly.mopaasapp.com/userServlet?action=sendGift&";

    public static class SendGiftParam{
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

    public void request(SendGiftParam param){
        String url = host + param.toString();
        request(url);
    }

    @Override
    public Object onSuccess(String body) {
        SendGiftResponseObject giftResponseObejct = gson.fromJson(body
                ,SendGiftResponseObject.class);
        return giftResponseObejct.data;
    }
}
