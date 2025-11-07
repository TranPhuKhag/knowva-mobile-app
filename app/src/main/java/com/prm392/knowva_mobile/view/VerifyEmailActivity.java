package com.prm392.knowva_mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.manager.SessionManager;
import com.prm392.knowva_mobile.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private EditText edtOtp;
    private Button btnVerify;
    private TextView tvResendOtp, tvVerifyMessage;
    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        edtOtp = findViewById(R.id.edtOtp);
        btnVerify = findViewById(R.id.btnVerify);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvVerifyMessage = findViewById(R.id.tvVerifyMessage);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: No email provided.", Toast.LENGTH_LONG).show();
            // Quay về Login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        tvVerifyMessage.setText("An OTP has been sent to " + userEmail + ". Please enter it below.");

        // Tự động gửi OTP khi vào màn hình
        sendOtp();

        btnVerify.setOnClickListener(v -> verifyEmail());
        tvResendOtp.setOnClickListener(v -> sendOtp());
    }

    private void sendOtp() {
        if (userEmail == null) return;

        tvResendOtp.setEnabled(false);
        Toast.makeText(this, "Sending OTP to " + userEmail + "...", Toast.LENGTH_SHORT).show();

        authRepository.sendVerifyOtp(userEmail).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyEmailActivity.this, "OTP sent!", Toast.LENGTH_SHORT).show();
                } else {
                    // --- ĐÃ SỬA LỖI TẠI ĐÂY ---
                    Toast.makeText(VerifyEmailActivity.this, "Failed to send OTP.", Toast.LENGTH_SHORT).show();
                }
                tvResendOtp.setEnabled(true);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(VerifyEmailActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                tvResendOtp.setEnabled(true);
            }
        });
    }

    private void verifyEmail() {
        String otp = edtOtp.getText().toString().trim();
        if (otp.isEmpty() || otp.length() < 6) {
            Toast.makeText(this, "Please enter a valid 6-digit OTP.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnVerify.setEnabled(false);
        btnVerify.setText("Verifying...");

        authRepository.verifyEmail(userEmail, otp).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyEmailActivity.this, "Email verified successfully!", Toast.LENGTH_SHORT).show();

                    // Cập nhật trạng thái đã xác thực trong Session (nếu cần)
                    // (Hoặc có thể bỏ qua, vì lần fetch profile sau sẽ tự cập nhật)

                    // Chuyển tới Home
                    Intent intent = new Intent(VerifyEmailActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Verification failed. Invalid or expired OTP.", Toast.LENGTH_LONG).show();
                    btnVerify.setEnabled(true);
                    btnVerify.setText("Verify & Continue");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(VerifyEmailActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnVerify.setEnabled(true);
                btnVerify.setText("Verify & Continue");
            }
        });
    }
}