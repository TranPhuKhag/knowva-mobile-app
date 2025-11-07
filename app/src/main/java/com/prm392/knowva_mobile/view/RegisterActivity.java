package com.prm392.knowva_mobile.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.AuthResponse;
import com.prm392.knowva_mobile.model.request.SignUp;
import com.prm392.knowva_mobile.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnSignUp;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        authRepository = new AuthRepository(this);

        btnSignUp.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSignUp.setEnabled(false);
        btnSignUp.setText("Registering...");

        SignUp signUpData = new SignUp(username, email, password);

        authRepository.signUp(signUpData).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Đăng ký thành công, lưu token
                    String token = response.body().getToken();
                    saveToken(token);

                    Toast.makeText(RegisterActivity.this, "Register successful! Please verify your email.", Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình xác thực email
                    Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
                    intent.putExtra("email", email); // Gửi email sang
                    // Xóa tất cả activity cũ và bắt đầu task mới
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Sign Up failed. Email or username might already exist.", Toast.LENGTH_SHORT).show();
                    btnSignUp.setEnabled(true);
                    btnSignUp.setText("Register");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSignUp.setEnabled(true);
                btnSignUp.setText("Register");
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.apply();
    }
}