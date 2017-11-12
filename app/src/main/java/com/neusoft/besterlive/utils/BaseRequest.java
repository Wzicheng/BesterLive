package com.neusoft.besterlive.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;
import com.neusoft.besterlive.model.bean.ResponseObject;
import com.neusoft.besterlive.model.bean.RoomInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Wzich on 2017/11/11.
 */

public abstract class BaseRequest {
    private static final int WHAT_FAIL = 0;
    private static final int WHAT_SUCCESS = 1;
    private OnResultListener onResultListener;

    private static OkHttpClient httpClient = new OkHttpClient();
    protected static Gson gson = new Gson();

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == WHAT_SUCCESS){
                if (onResultListener != null){
                    onResultListener.onSuccess(msg.obj);
                }
            } else if (what == WHAT_FAIL){
                if (onResultListener != null){
                    onResultListener.onFail(msg.arg1, (String) msg.obj);
                }
            }
        }
    };

    public void request(String url){
        final Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = handler.obtainMessage();
                message.what = WHAT_FAIL;
                message.arg1 = -100;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("onResponse","flag:" + response.isSuccessful());
                if (response.isSuccessful()){
                   //请求成功，将服务器返回的数据进行解析
                    String responseBody = response.body().string();
                    ResponseObject responseObject = gson.fromJson(responseBody,ResponseObject.class);
                    if (responseObject == null){
                        //服务器返回数据异常
                        Message message = handler.obtainMessage();
                        message.what = WHAT_FAIL;
                        message.arg1 = -101;
                        message.obj = "服务器返回数据异常";
                        handler.sendMessage(message);
                    } else {
                        if (responseObject.isSuccess()){
                            Object data = onSuccess(responseBody);
                            Message message = handler.obtainMessage();
                            message.what = WHAT_SUCCESS;
                            message.obj = data;
                            handler.sendMessage(message);

                        } else {
                            Message message = handler.obtainMessage();
                            message.what = WHAT_FAIL;
                            message.arg1 = Integer.valueOf(responseObject.getErrorCode());
                            message.obj = responseObject.getErrorMsg();
                            handler.sendMessage(message);
                        }
                    }
                } else {
                    //请求失败
                    Message message = handler.obtainMessage();
                    message.what = WHAT_FAIL;
                    message.arg1 = response.code();
                    message.obj = "服务器异常";
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void setOnResultListener(OnResultListener listener){
        onResultListener = listener;

    }

    public interface OnResultListener<T>{
        void onFail(int code,String msg);
        void onSuccess(T data);
    }

    public abstract Object onSuccess(String body);
}
