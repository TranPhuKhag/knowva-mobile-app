package com.prm392.knowva_mobile.model.response.quiz;

import java.util.List;

// Giả định cấu trúc response tương tự như MyFlashcardSetResponse
public class MyQuizSetResponse {
    public long id;
    public long userId;
    public String username;
    public String title;
    public String description;
    public String createdAt;
    public String updatedAt;
    public List<Question> questions; // Giả định có danh sách câu hỏi

    public static class Question {
        public long id;
        public String questionText;
        // Thêm các trường khác nếu cần
    }

    // Helper để lấy số lượng câu hỏi
    public int getQuestionCount() {
        return (questions != null) ? questions.size() : 0;
    }
}