package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.response.AuthResponse;
import com.prm392.knowva_mobile.model.request.SignIn;
import com.prm392.knowva_mobile.model.request.SignUp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("login") // Endpoint mới
    Call<AuthResponse> signIn(@Body SignIn signIn);

    @POST("register") // Endpoint mới
    Call<AuthResponse> signUp(@Body SignUp signUp);

    @POST("logout")
    Call<Void> logout();
}