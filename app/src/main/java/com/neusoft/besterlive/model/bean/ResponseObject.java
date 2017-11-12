package com.neusoft.besterlive.model.bean;

/**
 * Created by Wzich on 2017/11/11.
 */

public class ResponseObject {
    public static String CODE_SUCCESS = "1";
    public static String CODE_ERROR = "0";

    private String code;
    private String errCode;
    private String errMsg;

    public boolean isSuccess(){
        return CODE_SUCCESS.equals(code);
    }

    public String getErrorCode(){
        return errCode;
    }

    public String getErrorMsg(){
        return errMsg;
    }
}
