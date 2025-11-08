package com.prm392.knowva_mobile.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.factory.APIClient;
import com.prm392.knowva_mobile.manager.SessionManager;
import com.prm392.knowva_mobile.model.request.UpdateUserRequest;
import com.prm392.knowva_mobile.model.response.UserProfileResponse;
import com.prm392.knowva_mobile.service.UserService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etUsername, etEmail, etPhone, etBirthdate;
    private AutoCompleteTextView etGender;
    private MaterialButton btnUpdate;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private UserService userService;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        sessionManager = new SessionManager(this);
        userService = APIClient.getClient(this).create(UserService.class);
        calendar = Calendar.getInstance();

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_update_profile);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize views
        etFullName = findViewById(R.id.et_update_fullname);
        etUsername = findViewById(R.id.et_update_username);
        etEmail = findViewById(R.id.et_update_email);
        etPhone = findViewById(R.id.et_update_phone);
        etBirthdate = findViewById(R.id.et_update_birthdate);
        etGender = findViewById(R.id.et_update_gender);
        btnUpdate = findViewById(R.id.btn_update_profile);
        progressBar = findViewById(R.id.progress_update);

        // Setup gender dropdown
        String[] genderOptions = {"MALE", "FEMALE", "OTHER"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        etGender.setAdapter(genderAdapter);

        // Load current user data
        loadCurrentUserData();

        // DatePicker for birthdate
        etBirthdate.setOnClickListener(v -> showDatePicker());

        // Update button click
        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void loadCurrentUserData() {
        etFullName.setText(sessionManager.getFullName());
        etUsername.setText(sessionManager.getUsername());
        etEmail.setText(sessionManager.getEmail());
        etPhone.setText(sessionManager.getPhoneNumber());
        etBirthdate.setText(sessionManager.getBirthdate());
        etGender.setText(sessionManager.getGender(), false);
    }

    private void showDatePicker() {
        String currentDate = etBirthdate.getText() != null ? etBirthdate.getText().toString() : "";
        
        // Parse current date if exists
        if (!currentDate.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar.setTime(sdf.parse(currentDate));
            } catch (Exception e) {
                // Use current date
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                etBirthdate.setText(sdf.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void updateProfile() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String birthdate = etBirthdate.getText() != null ? etBirthdate.getText().toString().trim() : "";
        String gender = etGender.getText() != null ? etGender.getText().toString().trim() : "";

        // Validation
        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            etFullName.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            etUsername.setError("Vui lòng nhập tên người dùng");
            etUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        // Create request
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName(fullName);
        request.setUsername(username);
        request.setEmail(email);
        request.setPhoneNumber(phone);
        request.setBirthdate(birthdate);
        request.setGender(gender);

        // Show loading
        btnUpdate.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Call API
        long userId = sessionManager.getUserId();
        userService.updateUser(userId, request).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                btnUpdate.setEnabled(true);
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse updatedProfile = response.body();
                    
                    // Update SessionManager with new data
                    sessionManager.saveUserProfile(updatedProfile);
                    
                    Toast.makeText(UpdateProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Return to previous activity
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Cập nhật thất bại: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                btnUpdate.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UpdateProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

