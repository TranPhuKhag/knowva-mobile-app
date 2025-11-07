package com.prm392.knowva_mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.AuthResponse;
import com.prm392.knowva_mobile.model.request.SignIn;
import com.prm392.knowva_mobile.repository.AuthRepository;
import com.prm392.knowva_mobile.manager.SessionManager;
import com.prm392.knowva_mobile.model.response.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvGoToRegister;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: LoginActivity started");

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvRegister = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        sessionManager = new SessionManager(getApplicationContext());
        authRepository = new AuthRepository(this);

        tvRegister.setOnClickListener(v -> {
            Log.d(TAG, "Forgot password clicked");
            Toast.makeText(LoginActivity.this, "Chức năng quên mật khẩu sắp có!", Toast.LENGTH_SHORT).show();
        });

        tvGoToRegister.setOnClickListener(v -> {
            Log.d(TAG, "Go to Register clicked");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            login();
        });
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        Log.d(TAG, "login: Attempting login with email: " + email);

        if (email.isEmpty() || password.isEmpty()) {
            Log.w(TAG, "login: Email or password is empty");
            Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        SignIn signInData = new SignIn(email, password);

        authRepository.signIn(signInData).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Log.d(TAG, "signIn onResponse: Response code = " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    String token = authResponse.getToken();
                    Log.d(TAG, "signIn onResponse: Login successful, token received");
                    sessionManager.saveAuthToken(token);
                    fetchUserProfileAndProceed();
                } else {
                    Log.e(TAG, "signIn onResponse: Login failed with code " + response.code());
                    Toast.makeText(LoginActivity.this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Log in");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "signIn onFailure: Network Error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnLogin.setEnabled(true);
                btnLogin.setText("Log in");
            }
        });
    }

    private void fetchUserProfileAndProceed() {
        Log.d(TAG, "fetchUserProfileAndProceed: Fetching user profile");

        authRepository.getUserProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                Log.d(TAG, "getUserProfile onResponse: Response code = " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();
                    sessionManager.saveUserProfile(profile);
                    Log.d(TAG, "getUserProfile: Profile saved. isVerified = " + profile.isVerified());

                    if (profile.isVerified()) {
                        Log.d(TAG, "User is verified, going to HomeActivity");
                        goToHomeActivity();
                    } else {
                        Log.d(TAG, "User is not verified, going to VerifyEmailActivity");
                        goToVerifyActivity(profile.getEmail());
                    }
                } else {
                    Log.e(TAG, "getUserProfile: Failed to fetch profile: " + response.code());
                    sessionManager.saveUserProfile(null);
                    goToHomeActivity();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "getUserProfile onFailure: Network error: " + t.getMessage(), t);
                sessionManager.saveUserProfile(null);
                goToHomeActivity();
            }
        });
    }

    private void goToHomeActivity() {
        Log.d(TAG, "goToHomeActivity: Navigating to HomeActivity");
        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
        try {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Log.d(TAG, "goToHomeActivity: Successfully started HomeActivity");
        } catch (Exception e) {
            Log.e(TAG, "goToHomeActivity: Error starting HomeActivity: " + e.getMessage(), e);
            Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goToVerifyActivity(String email) {
        Log.d(TAG, "goToVerifyActivity: Navigating to VerifyEmailActivity with email: " + email);
        Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_LONG).show();
        try {
            Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
            intent.putExtra("email", email);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Log.d(TAG, "goToVerifyActivity: Successfully started VerifyEmailActivity");
        } catch (Exception e) {
            Log.e(TAG, "goToVerifyActivity: Error starting VerifyEmailActivity: " + e.getMessage(), e);
            Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
