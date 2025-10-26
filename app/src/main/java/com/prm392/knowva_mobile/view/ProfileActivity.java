package com.prm392.knowva_mobile.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.UserProfileResponse;
import com.prm392.knowva_mobile.repository.AuthRepository;
import com.prm392.knowva_mobile.manager.SessionManager;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextInputEditText etFullName, etEmail, etPhone;
    private ProgressBar progressBar;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        // --- Toolbar ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        // Bật nút back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Ánh xạ View
        ImageView ivAvatar = findViewById(R.id.iv_profile_avatar);
        TextView tvFullName = findViewById(R.id.tv_profile_fullname);
        TextView tvEmail = findViewById(R.id.tv_profile_email);
        TextView tvPhone = findViewById(R.id.tv_profile_phone);
        TextView tvBirthdate = findViewById(R.id.tv_profile_birthdate);
        TextView tvGender = findViewById(R.id.tv_profile_gender);
        TextView tvVerified = findViewById(R.id.tv_profile_verified);
        TextView tvRole = findViewById(R.id.tv_profile_role);
        TextView tvVip = findViewById(R.id.tv_profile_vip);
        TextView tvFcSets = findViewById(R.id.tv_stats_fc_sets);
        TextView tvQSets = findViewById(R.id.tv_stats_q_sets);
        TextView tvFcAttempts = findViewById(R.id.tv_stats_fc_attempts);
        TextView tvQAttempts = findViewById(R.id.tv_stats_q_attempts);
        TextView tvAvgScore = findViewById(R.id.tv_stats_avg_score);

        // --- Lấy và hiển thị dữ liệu từ SessionManager ---
        tvFullName.setText(formatField("Họ tên", sessionManager.getFullName()));
        tvEmail.setText(formatField("Email", sessionManager.getEmail()));
        tvPhone.setText(formatField("SĐT", sessionManager.getPhoneNumber()));
        tvBirthdate.setText(formatField("Ngày sinh", sessionManager.getBirthdate()));
        tvGender.setText(formatField("Giới tính", sessionManager.getGender()));
        tvVerified.setText(String.format("Xác thực: %s", sessionManager.isVerified() ? "Đã xác thực" : "Chưa xác thực"));
        tvRole.setText(formatField("Vai trò", sessionManager.getRole()));
        int vipDays = sessionManager.getVipDaysLeft();
        tvVip.setText(String.format("Ngày VIP còn lại: %s", vipDays >= 0 ? String.valueOf(vipDays) : "N/A"));

        tvFcSets.setText(String.format(Locale.getDefault(), "Bộ Flashcard: %d", sessionManager.getTotalFlashcardSets()));
        tvQSets.setText(String.format(Locale.getDefault(), "Bộ Quiz: %d", sessionManager.getTotalQuizSets()));
        tvFcAttempts.setText(String.format(Locale.getDefault(), "Lượt học Flashcard: %d", sessionManager.getTotalFlashcardAttempts()));
        tvQAttempts.setText(String.format(Locale.getDefault(), "Lượt làm Quiz: %d", sessionManager.getTotalQuizAttempts()));
        float avgScore = sessionManager.getAverageQuizScore();
        tvAvgScore.setText(String.format("Điểm Quiz TB: %s", avgScore >= 0 ? String.format(Locale.getDefault(), "%.1f", avgScore) : "N/A"));

        // Load Avatar
        Glide.with(this)
                .load(sessionManager.getAvatarUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .circleCrop()
                .into(ivAvatar);
    }

    // Hàm helper để định dạng và xử lý giá trị null/rỗng
    private String formatField(String label, String value) {
        return String.format("%s: %s", label, TextUtils.isEmpty(value) ? "N/A" : value);
    }

    // Xử lý nút back trên toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchUserProfile() {
        progressBar.setVisibility(View.VISIBLE); // Hiển thị loading

        authRepository.getUserProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                progressBar.setVisibility(View.GONE); // Ẩn loading

                if (response.isSuccessful() && response.body() != null) {
                    displayProfile(response.body());
                } else {
                    // Xử lý lỗi
                    String errorMsg = "Không thể tải hồ sơ: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += "\n" + response.errorBody().string();
                        }
                    } catch (Exception e) { /* Ignore */ }
                    Log.e("ProfileActivity", errorMsg);
                    Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    // Có thể đóng Activity nếu không tải được profile
                    // finish();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE); // Ẩn loading
                Log.e("ProfileActivity", "Lỗi mạng khi tải hồ sơ: " + t.getMessage(), t);
                Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Có thể đóng Activity nếu không tải được profile
                // finish();
            }
        });
    }

    private void displayProfile(UserProfileResponse profile) {
        etFullName.setText(profile.getFullName());
        etEmail.setText(profile.getEmail());
        // etPhone.setText(profile.getPhoneNumber()); // Cần thêm getPhoneNumber() vào model nếu API trả về

        Glide.with(this)
                .load(profile.getAvatarUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .circleCrop()
                .into(ivAvatar);
    }
}