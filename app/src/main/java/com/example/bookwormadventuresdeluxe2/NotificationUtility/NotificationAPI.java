package com.example.bookwormadventuresdeluxe2.NotificationUtility;

/**
 * Interface to define retrofit client api end points.
 */

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.example.bookwormadventuresdeluxe2.NotificationUtility.CloudMessagingServerKeyConstant.cloudMessagingServerKey;

public interface NotificationAPI
{
    /* Setup post request headers */
    // Todo: Hide FCM server key in secrets.xml
    @Headers(
            {
                    cloudMessagingServerKey,
                    "Content-Type:application/json"
            }
    )
    @POST("fcm/send")
    Call<RetrofitResponse> sendNotification(@Body NotificationSender body);
}
