package com.haibin.calendarviewproject;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil{
    public static void sendOkHttpRequest(String address, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        Request request= new Request.Builder().url(address).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        //client.newcall(request).enqueue(callback);
    }
}