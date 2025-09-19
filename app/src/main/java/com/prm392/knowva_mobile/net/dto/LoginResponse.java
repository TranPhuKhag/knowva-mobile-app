package com.prm392.knowva_mobile.net.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    // BE trả JWT token (đặt tên phòng trường hợp khác nhau)
    @SerializedName(value = "token", alternate = {"accessToken","access_token","jwt"})
    public String token;

    public UserDto user; // có thể null nếu BE không trả user
}
