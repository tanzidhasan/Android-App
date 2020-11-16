package com.example.nirob.Fragment;

import com.example.nirob.Notification.MyResponse;
import com.example.nirob.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAo7NR5UQ:APA91bHMwrd2vdoZaBQ2FSKkvJWSdsCfY61_0t2H_ND0zZuzfVQGRGmX0g8IyqZKlc2UrTUrXHpoLrNVODp1yVCi8Ki2QmyyGoX_H41pPnYOH14T5PKaSVGFHKhdwYspGYY2kmXXgzFH"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
