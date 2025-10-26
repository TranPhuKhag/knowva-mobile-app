package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.response.UserProfileResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {
    @GET("users/me") // Endpoint để lấy thông tin người dùng hiện tại
    Call<UserProfileResponse> getUserProfile();
}