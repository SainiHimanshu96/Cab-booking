package com.unclecabby.unclecabby.Remote;

import com.unclecabby.unclecabby.Model.FCMResponse;
import com.unclecabby.unclecabby.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAr1kxeNo:APA91bFm712JITywFdodQDa46ANNGNqgNLbk1HSGM0bP4kg53jq2xPgcPfiw6Sp2wLC2CLJtjenUEh26ahRUKWf6SkjvSoK6eZw1qjI9GZoi7O7rL8eM4GU81wfXzYHYVjItY6W-eC3Q"
    })

    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);

}
