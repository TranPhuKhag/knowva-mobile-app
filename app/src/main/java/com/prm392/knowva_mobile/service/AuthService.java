package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.response.AuthResponse;
import com.prm392.knowva_mobile.model.request.SignIn;
import com.prm392.knowva_mobile.model.request.SignUp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query; // Import @Query

public interface AuthService {
    @POST("login")
    Call<AuthResponse> signIn(@Body SignIn signIn);

    @POST("register")
    Call<AuthResponse> signUp(@Body SignUp signUp);

    @POST("logout")
    Call<Void> logout();

    // --- THÊM MỚI ---
    @POST("send-verify-otp")
    Call<Void> sendVerifyOtp(@Query("email") String email);

    @POST("verify-email")
    Call<Void> verifyEmail(@Query("email") String email, @Query("otp") String otp);
    // --- KẾT THÚC THÊM MỚI ---
}