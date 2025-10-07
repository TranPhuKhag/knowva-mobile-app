package com.prm392.knowva_mobile.factory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        if (path.endsWith("login") || path.endsWith("register")) {
            return chain.proceed(originalRequest);
        }

        // Với các request khác, lấy token và thêm vào header
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("access_token", null);

        // Nếu không có token cho các request cần xác thực, ghi log và vẫn gửi đi
        if (token == null) {
            Log.w("AuthInterceptor", "Token is null for authenticated request: " + path);
            return chain.proceed(originalRequest);
        }

        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}