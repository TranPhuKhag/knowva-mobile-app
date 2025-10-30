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
    public List<Question> questions;

    public static class Question {
        public long id;
        public String questionText;
        public String imageUrl; // <-- THÊM TRƯỜNG NÀY
        public int order;       // <-- THÊM TRƯỜNG NÀY
        public List<Answer> answers; // <-- THÊM TRƯỜNG NÀY
    }

    public static class Answer {
        public long id; // Giả sử API trả về ID
        public String answerText;
        public boolean isCorrect;
    }

    // Helper để lấy số lượng câu hỏi
    public int getQuestionCount() {
        return (questions != null) ? questions.size() : 0;
    }
}