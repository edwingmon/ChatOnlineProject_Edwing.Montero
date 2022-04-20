package com.example.damxat.Interfices;

import android.provider.SyncStateContract;

import com.example.damxat.Constants.Constants;
import com.example.damxat.Model.PushNotification;
import com.example.damxat.Model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterfice {
    @Headers({"Authorization: key=" + Constants.SERVER_KEY, "Content-Type:" + Constants.CONTENT_TYPE})
    @POST("fcm/send")
    Call<ResponseModel> postNotification(@Body PushNotification data);
}
