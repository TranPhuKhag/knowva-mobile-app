package com.prm392.knowva_mobile.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;

import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.auth.TokenStore;
import com.prm392.knowva_mobile.net.ApiClient;
import com.prm392.knowva_mobile.net.AuthApi;
import com.prm392.knowva_mobile.net.dto.LoginRequest;
import com.prm392.knowva_mobile.net.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPass  = findViewById(R.id.etPassword);
        Button   btn     = findViewById(R.id.btnLogin);

        AuthApi api = ApiClient.get().create(AuthApi.class);
        TokenStore tokenStore = new TokenStore(this);

        btn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass  = etPass.getText().toString();

            btn.setEnabled(false);

            api.login(new LoginRequest(email, pass)).enqueue(new Callback<LoginResponse>() {
                @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                    btn.setEnabled(true);
                    if (res.isSuccessful() && res.body()!=null) {
                        String token = res.body().token;
                        if (token != null) {

                            tokenStore.save(token);
                            Toast.makeText(LoginActivity.this, "Đăng nhập OK", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Thiếu token trong phản hồi", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login fail: " + res.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                    btn.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
