package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.request.UpdateUserRequest;
import com.prm392.knowva_mobile.model.response.UserProfileResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @GET("users/me") // Endpoint để lấy thông tin người dùng hiện tại
    Call<UserProfileResponse> getUserProfile();

    @PUT("users/{id}/update") // Endpoint để cập nhật thông tin người dùng
    Call<UserProfileResponse> updateUser(@Path("id") long userId, @Body UpdateUserRequest request);
}