package com.example.bookwormadventuresdeluxe2.NotificationUtility;

/**
 * Returns a retrofit client for HTTP requests
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url)
    {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
