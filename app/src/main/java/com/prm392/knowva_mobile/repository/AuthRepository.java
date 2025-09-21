package com.prm392.knowva_mobile.repository;

import android.content.Context;

import com.prm392.knowva_mobile.factory.APIClient;
import com.prm392.knowva_mobile.model.response.AuthResponse;
import com.prm392.knowva_mobile.model.request.SignIn;
import com.prm392.knowva_mobile.model.request.SignUp;
import com.prm392.knowva_mobile.service.AuthService;

import retrofit2.Call;
import retrofit2.Retrofit;

public class AuthRepository {
    private AuthService authService;

    public AuthRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        authService = retrofit.create(AuthService.class);
    }

    public Call<AuthResponse> signIn(SignIn account) {
        return authService.signIn(account);
    }

    public Call<AuthResponse> signUp(SignUp account){
        return authService.signUp(account);
    }
}