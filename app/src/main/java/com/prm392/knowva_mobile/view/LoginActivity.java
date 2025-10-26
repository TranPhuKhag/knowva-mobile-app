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
    private TextView tvRegister;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvRegister = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);

        sessionManager = new SessionManager(getApplicationContext());

        authRepository = new AuthRepository(this);

        tvRegister.setOnClickListener(v -> {
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
//                    User user = authResponse.getUser();
//                    // Lấy token từ đối tượng AuthResponse
//                    String token = response.body().getToken();
//                    saveUserInfo(token, user);
//                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                    finish();
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

//    private void saveUserInfo(String token, User user) {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // Lưu token
//        editor.putString("access_token", token);
//
//        // Lưu thêm thông tin của User (nếu user không null)
//        if (user != null) {
//            editor.putString("user_name", user.getUsername());
//            editor.putString("user_email", user.getEmail());
//            // Bạn có thể lưu thêm bất kỳ thông tin nào khác từ User object
//        }
//        editor.apply();
//    }

    // --- HÀM MỚI ĐỂ LẤY PROFILE ---
    private void fetchUserProfileAndProceed() {
        authRepository.getUserProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                UserProfileResponse profile = null; // Khởi tạo là null
                if (response.isSuccessful() && response.body() != null) {
                    profile = response.body(); // Lấy profile nếu thành công
                } else {
                    Log.e("LoginActivity", "Failed to fetch profile: " + response.code());
                }
                // Luôn gọi saveUserProfile, kể cả khi profile là null
                sessionManager.saveUserProfile(profile);
                goToHomeActivity(); // Chuyển sang Home
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e("LoginActivity", "Network error fetching profile: " + t.getMessage());
                sessionManager.saveUserProfile(null); // Lưu giá trị mặc định/null
                goToHomeActivity(); // Chuyển sang Home
            }
        });
    }

    private void goToHomeActivity() {
        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}