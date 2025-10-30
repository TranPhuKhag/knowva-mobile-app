package com.prm392.knowva_mobile.model.request.quiz;

import java.util.List;

// Đây là POJO cho JSON body cuối cùng
public class CreateQuizRequest {
    public String title;
    public String description;
    public String sourceType = "TEXT"; // Mặc định là TEXT vì tạo thủ công
    public String language = "VIETNAMESE";
    public String questionType = "MULTIPLE_CHOICE";
    public int maxQuestions;
    public String visibility = "PUBLIC";
    public String category = "OTHER";
    public int timeLimit = 0; // 0 = không giới hạn
    public List<Question> questions;

    public static class Question {
        public String questionText;
        public String imageUrl;
        public int timeLimit = 0;
        public int order;
        public List<Answer> answers;
    }

    public static class Answer {
        public String answerText;
        public boolean isCorrect;
    }
}