package com.prm392.knowva_mobile.net;

import com.prm392.knowva_mobile.net.dto.LoginRequest;
import com.prm392.knowva_mobile.net.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest body);
}
