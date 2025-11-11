package com.prm392.knowva_mobile.factory;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static String baseURL = "https://api.knowva.me/api/";
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        // Thêm logging interceptor để debug
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
            Log.d("API_LOG", message)
        );
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)      // THÊM connectTimeout
                .callTimeout(30, TimeUnit.SECONDS)         // Giảm từ 90s xuống 30s
                .readTimeout(30, TimeUnit.SECONDS)         // Giảm từ 90s xuống 30s
                .writeTimeout(30, TimeUnit.SECONDS)        // Giảm từ 90s xuống 30s
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new AuthInterceptor(context))
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
