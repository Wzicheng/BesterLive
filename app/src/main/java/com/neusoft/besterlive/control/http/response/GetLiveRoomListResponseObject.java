package com.neusoft.besterlive.control.http.response;

import com.neusoft.besterlive.model.bean.ResponseObject;
import com.neusoft.besterlive.model.bean.RoomInfo;

import java.util.List;

/**
 * Created by Wzich on 2017/11/12.
 */

public class GetLiveRoomListResponseObject extends ResponseObject{
    public List<RoomInfo> data;
}
