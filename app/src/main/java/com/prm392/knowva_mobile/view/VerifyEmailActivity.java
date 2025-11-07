package com.prm392.knowva_mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "VerifyEmailActivity";
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

        Log.d(TAG, "onCreate: VerifyEmailActivity started");

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        edtOtp = findViewById(R.id.edtOtp);
        btnVerify = findViewById(R.id.btnVerify);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvVerifyMessage = findViewById(R.id.tvVerifyMessage);

        userEmail = getIntent().getStringExtra("email");
        Log.d(TAG, "onCreate: Email received: " + userEmail);

        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "onCreate: No email provided");
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
        Log.d(TAG, "onCreate: Auto-sending OTP on screen load");
        sendOtp();

        btnVerify.setOnClickListener(v -> {
            Log.d(TAG, "Verify button clicked");
            verifyEmail();
        });

        tvResendOtp.setOnClickListener(v -> {
            Log.d(TAG, "Resend OTP button clicked");
            sendOtp();
        });
    }

    private void sendOtp() {
        if (userEmail == null) {
            Log.e(TAG, "sendOtp: userEmail is null, cannot send OTP");
            return;
        }

        Log.d(TAG, "sendOtp: Sending OTP to email: " + userEmail);
        tvResendOtp.setEnabled(false);
        Toast.makeText(this, "Sending OTP to " + userEmail + "...", Toast.LENGTH_SHORT).show();

        authRepository.sendVerifyOtp(userEmail).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "sendOtp onResponse: Response code = " + response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG, "sendOtp: OTP sent successfully");
                    Toast.makeText(VerifyEmailActivity.this, "OTP sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "sendOtp: Failed to send OTP. Response code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "sendOtp: Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "sendOtp: Error reading error body", e);
                    }
                    Toast.makeText(VerifyEmailActivity.this, "Failed to send OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
                tvResendOtp.setEnabled(true);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "sendOtp onFailure: Network Error: " + t.getMessage(), t);
                Toast.makeText(VerifyEmailActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                tvResendOtp.setEnabled(true);
            }
        });
    }

    private void verifyEmail() {
        String otp = edtOtp.getText().toString().trim();
        Log.d(TAG, "verifyEmail: Attempting to verify OTP (length: " + otp.length() + ")");

        if (otp.isEmpty() || otp.length() < 6) {
            Log.w(TAG, "verifyEmail: Invalid OTP length");
            Toast.makeText(this, "Please enter a valid 6-digit OTP.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "verifyEmail: Verifying OTP for email: " + userEmail);
        btnVerify.setEnabled(false);
        btnVerify.setText("Verifying...");

        authRepository.verifyEmail(userEmail, otp).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "verifyEmail onResponse: Response code = " + response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG, "verifyEmail: Email verified successfully");
                    Toast.makeText(VerifyEmailActivity.this, "Email verified successfully!", Toast.LENGTH_SHORT).show();

                    // Chuyển tới Home
                    Log.d(TAG, "verifyEmail: Navigating to HomeActivity");
                    Intent intent = new Intent(VerifyEmailActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "verifyEmail: Verification failed with code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "verifyEmail: Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "verifyEmail: Error reading error body", e);
                    }
                    Toast.makeText(VerifyEmailActivity.this, "Verification failed. Invalid or expired OTP.", Toast.LENGTH_LONG).show();
                    btnVerify.setEnabled(true);
                    btnVerify.setText("Verify & Continue");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "verifyEmail onFailure: Network Error: " + t.getMessage(), t);
                Toast.makeText(VerifyEmailActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnVerify.setEnabled(true);
                btnVerify.setText("Verify & Continue");
            }
        });
    }
}