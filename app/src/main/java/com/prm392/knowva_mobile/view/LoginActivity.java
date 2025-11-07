package com.prm392.knowva_mobile.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.User;
import com.prm392.knowva_mobile.model.response.AuthResponse;
import com.prm392.knowva_mobile.model.request.SignIn;
import com.prm392.knowva_mobile.repository.AuthRepository;
import com.prm392.knowva_mobile.manager.SessionManager;
import com.prm392.knowva_mobile.model.response.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister; // Đây là "Forgot Password"
    private TextView tvGoToRegister;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvRegister = findViewById(R.id.txtRegister); // "Forgot password"
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        sessionManager = new SessionManager(getApplicationContext());

        authRepository = new AuthRepository(this);

        tvRegister.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Chức năng quên mật khẩu sắp có!", Toast.LENGTH_SHORT).show();
        });

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        SignIn signInData = new SignIn(email, password);

        authRepository.signIn(signInData).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    String token = authResponse.getToken();
                    sessionManager.saveAuthToken(token);
                    fetchUserProfileAndProceed();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserProfileAndProceed() {
        authRepository.getUserProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                UserProfileResponse profile = null;
                if (response.isSuccessful() && response.body() != null) {
                    profile = response.body();
                    sessionManager.saveUserProfile(profile);

                    // --- LOGIC ĐIỀU HƯỚNG MỚI ---
                    if (profile.isVerified()) {
                        // 1. Đã xác thực -> Vào Home
                        goToHomeActivity();
                    } else {
                        // 2. Chưa xác thực -> Vào màn hình Verify
                        goToVerifyActivity(profile.getEmail());
                    }
                    // --- KẾT THÚC LOGIC MỚI ---

                } else {
                    Log.e("LoginActivity", "Failed to fetch profile: " + response.code());
                    sessionManager.saveUserProfile(null);
                    goToHomeActivity(); // Failsafe: Vẫn cho vào home
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e("LoginActivity", "Network error fetching profile: " + t.getMessage());
                sessionManager.saveUserProfile(null);
                goToHomeActivity(); // Failsafe: Vẫn cho vào home
            }
        });
    }

    // --- HÀM ĐIỀU HƯỚNG (GIỮ NGUYÊN) ---
    private void goToHomeActivity() {
        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // --- HÀM ĐIỀU HƯỚNG MỚI ---
    private void goToVerifyActivity(String email) {
        Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
        intent.putExtra("email", email);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}