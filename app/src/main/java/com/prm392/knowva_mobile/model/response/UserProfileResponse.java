package com.prm392.knowva_mobile.model.response;

public class UserProfileResponse {
    private long id;
    private String fullName;
    private String username;    // Thêm username
    private String phoneNumber; // Thêm
    private String birthdate;   // Thêm
    private String gender;      // Thêm
    private String email;
    private String avatarUrl;
    private boolean isVerified; // Thêm
    private Integer vipDaysLeft; // Thêm (Integer để có thể null)
    private Stats stats;         // Thêm
    private String role;        // Thêm

    // --- Getters ---
    public long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }  // Thêm getter
    public String getPhoneNumber() { return phoneNumber; }
    public String getBirthdate() { return birthdate; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }
    public String getAvatarUrl() { return avatarUrl; }
    public boolean isVerified() { return isVerified; }
    public Integer getVipDaysLeft() { return vipDaysLeft; }
    public Stats getStats() { return stats; }
    public String getRole() { return role; }

    // --- Inner class for Stats ---
    public static class Stats {
        private int totalFlashcardSets;
        private int totalQuizSets;
        private int totalFlashcardAttempts;
        private int totalQuizAttempts;
        private Double averageQuizScore; // Double để có thể null

        // --- Getters for Stats ---
        public int getTotalFlashcardSets() { return totalFlashcardSets; }
        public int getTotalQuizSets() { return totalQuizSets; }
        public int getTotalFlashcardAttempts() { return totalFlashcardAttempts; }
        public int getTotalQuizAttempts() { return totalQuizAttempts; }
        public Double getAverageQuizScore() { return averageQuizScore; }
    }
}