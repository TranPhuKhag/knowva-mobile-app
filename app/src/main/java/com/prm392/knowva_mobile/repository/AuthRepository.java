package com.prm392.knowva_mobile.repository;

import android.content.Context;
import com.prm392.knowva_mobile.factory.APIClient;
import com.prm392.knowva_mobile.model.response.AuthResponse;
import com.prm392.knowva_mobile.model.request.SignIn;
import com.prm392.knowva_mobile.model.request.SignUp;
import com.prm392.knowva_mobile.model.response.UserProfileResponse;
import com.prm392.knowva_mobile.service.AuthService;
import com.prm392.knowva_mobile.service.UserService;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AuthRepository {
    private AuthService authService;
    private UserService userService;

    public AuthRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        authService = retrofit.create(AuthService.class);
        userService = retrofit.create(UserService.class);
    }

    public Call<AuthResponse> signIn(SignIn account) {
        return authService.signIn(account);
    }

    public Call<AuthResponse> signUp(SignUp account) {
        return authService.signUp(account);
    }

    public Call<Void> logout() {
        return authService.logout();
    }

    public Call<UserProfileResponse> getUserProfile() {
        return userService.getUserProfile();
    }

    // --- THÊM MỚI ---
    public Call<Void> sendVerifyOtp(String email) {
        return authService.sendVerifyOtp(email);
    }

    public Call<Void> verifyEmail(String email, String otp) {
        return authService.verifyEmail(email, otp);
    }
    // --- KẾT THÚC THÊM MỚI ---
}