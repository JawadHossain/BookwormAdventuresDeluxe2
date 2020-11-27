package com.example.bookwormadventuresdeluxe2.Utilities.NotificationUtility;

/**
 * Interface to define retrofit client api end points.
 */

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.example.bookwormadventuresdeluxe2.Utilities.NotificationUtility.CloudMessagingServerKeyConstant.cloudMessagingServerKey;

public interface NotificationAPI
{
    /* Setup post request headers */
    @Headers(
            {
                    cloudMessagingServerKey,
                    "Content-Type:application/json"
            }
    )
    @POST("fcm/send")
    Call<RetrofitResponse> sendNotification(@Body NotificationSender body);
}
