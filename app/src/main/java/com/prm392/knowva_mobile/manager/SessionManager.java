package com.prm392.knowva_mobile.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.prm392.knowva_mobile.model.response.UserProfileResponse;

public class SessionManager {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_USER_TOKEN = "access_token";
    // --- Thêm các Key mới ---
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_FULL_NAME = "user_full_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_BIRTHDATE = "user_birthdate";
    private static final String KEY_USER_GENDER = "user_gender";
    private static final String KEY_USER_AVATAR_URL = "user_avatar_url";
    private static final String KEY_IS_VERIFIED = "user_is_verified";
    private static final String KEY_VIP_DAYS = "user_vip_days";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_STATS_FC_SETS = "stats_fc_sets";
    private static final String KEY_STATS_Q_SETS = "stats_q_sets";
    private static final String KEY_STATS_FC_ATTEMPTS = "stats_fc_attempts";
    private static final String KEY_STATS_Q_ATTEMPTS = "stats_q_attempts";
    private static final String KEY_STATS_AVG_SCORE = "stats_avg_score";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // --- Cập nhật hàm lưu session ---
    public void saveAuthToken(String token) {
        editor.putString(KEY_USER_TOKEN, token);
        editor.apply();
    }

    // --- Hàm mới để lưu thông tin Profile ---
    public void saveUserProfile(UserProfileResponse profile) {
        if (profile == null) {
            // Trường hợp không lấy được profile, có thể lưu giá trị mặc định hoặc null
            editor.putLong(KEY_USER_ID, -1);
            editor.putString(KEY_USER_FULL_NAME, "Người dùng");
            editor.putString(KEY_USER_EMAIL, "");
            editor.remove(KEY_USER_PHONE); // Xóa nếu không có
            editor.remove(KEY_USER_BIRTHDATE);
            editor.remove(KEY_USER_GENDER);
            editor.remove(KEY_USER_AVATAR_URL);
            editor.putBoolean(KEY_IS_VERIFIED, false);
            editor.remove(KEY_VIP_DAYS);
            editor.putString(KEY_ROLE, "REGULAR");
            editor.putInt(KEY_STATS_FC_SETS, 0);
            editor.putInt(KEY_STATS_Q_SETS, 0);
            editor.putInt(KEY_STATS_FC_ATTEMPTS, 0);
            editor.putInt(KEY_STATS_Q_ATTEMPTS, 0);
            editor.remove(KEY_STATS_AVG_SCORE);
        } else {
            editor.putLong(KEY_USER_ID, profile.getId());
            editor.putString(KEY_USER_FULL_NAME, profile.getFullName());
            editor.putString(KEY_USER_EMAIL, profile.getEmail());
            editor.putString(KEY_USER_PHONE, profile.getPhoneNumber());
            editor.putString(KEY_USER_BIRTHDATE, profile.getBirthdate());
            editor.putString(KEY_USER_GENDER, profile.getGender());
            editor.putString(KEY_USER_AVATAR_URL, profile.getAvatarUrl());
            editor.putBoolean(KEY_IS_VERIFIED, profile.isVerified());
            if (profile.getVipDaysLeft() != null) {
                editor.putInt(KEY_VIP_DAYS, profile.getVipDaysLeft());
            } else {
                editor.remove(KEY_VIP_DAYS); // Xóa nếu null
            }
            editor.putString(KEY_ROLE, profile.getRole());

            UserProfileResponse.Stats stats = profile.getStats();
            if (stats != null) {
                editor.putInt(KEY_STATS_FC_SETS, stats.getTotalFlashcardSets());
                editor.putInt(KEY_STATS_Q_SETS, stats.getTotalQuizSets());
                editor.putInt(KEY_STATS_FC_ATTEMPTS, stats.getTotalFlashcardAttempts());
                editor.putInt(KEY_STATS_Q_ATTEMPTS, stats.getTotalQuizAttempts());
                if (stats.getAverageQuizScore() != null) {
                    // SharedPreferences không lưu Double, lưu dạng Float hoặc String
                    editor.putFloat(KEY_STATS_AVG_SCORE, stats.getAverageQuizScore().floatValue());
                } else {
                    editor.remove(KEY_STATS_AVG_SCORE);
                }
            } else {
                // Xóa các key stats nếu stats là null
                editor.remove(KEY_STATS_FC_SETS);
                editor.remove(KEY_STATS_Q_SETS);
                editor.remove(KEY_STATS_FC_ATTEMPTS);
                editor.remove(KEY_STATS_Q_ATTEMPTS);
                editor.remove(KEY_STATS_AVG_SCORE);
            }
        }
        editor.apply();
    }

    public String getUserToken() { return sharedPreferences.getString(KEY_USER_TOKEN, null); }
    public long getUserId() { return sharedPreferences.getLong(KEY_USER_ID, -1); }
    public String getFullName() { return sharedPreferences.getString(KEY_USER_FULL_NAME, "Người dùng"); }
    public String getEmail() { return sharedPreferences.getString(KEY_USER_EMAIL, ""); }
    public String getPhoneNumber() { return sharedPreferences.getString(KEY_USER_PHONE, null); }
    public String getBirthdate() { return sharedPreferences.getString(KEY_USER_BIRTHDATE, null); }
    public String getGender() { return sharedPreferences.getString(KEY_USER_GENDER, null); }
    public String getAvatarUrl() { return sharedPreferences.getString(KEY_USER_AVATAR_URL, null); }
    public boolean isVerified() { return sharedPreferences.getBoolean(KEY_IS_VERIFIED, false); }
    public int getVipDaysLeft() { return sharedPreferences.getInt(KEY_VIP_DAYS, -1); } // -1 nếu không có
    public String getRole() { return sharedPreferences.getString(KEY_ROLE, "REGULAR"); }
    public int getTotalFlashcardSets() { return sharedPreferences.getInt(KEY_STATS_FC_SETS, 0); }
    public int getTotalQuizSets() { return sharedPreferences.getInt(KEY_STATS_Q_SETS, 0); }
    public int getTotalFlashcardAttempts() { return sharedPreferences.getInt(KEY_STATS_FC_ATTEMPTS, 0); }
    public int getTotalQuizAttempts() { return sharedPreferences.getInt(KEY_STATS_Q_ATTEMPTS, 0); }
    public float getAverageQuizScore() { return sharedPreferences.getFloat(KEY_STATS_AVG_SCORE, -1f); } // -1f nếu không có
    public void logoutUser() {
        editor.clear(); // Xóa tất cả dữ liệu
        editor.apply();
    }
    public boolean isLoggedIn() {
        return getUserToken() != null;
    }
}