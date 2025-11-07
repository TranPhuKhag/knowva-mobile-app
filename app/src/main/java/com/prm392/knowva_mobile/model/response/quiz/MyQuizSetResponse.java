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

    public int timeLimit;
    public String questionType;
    public String category;
    public String visibility;
    public String sourceType;
    public String language;
    public static class Question {
        public long id;
        public String questionText;
        public String imageUrl;
        public int order;
        public List<Answer> answers;
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