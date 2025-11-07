package com.prm392.knowva_mobile.model.response.quiz;

import java.io.Serializable;
import java.util.List;

// Đại diện cho một câu hỏi trong lượt làm
public class QuizAttemptQuestion implements Serializable {
    public long id;
    public String questionText;
    public String imageUrl;
    public int timeLimit;
    public int order;
    public List<Answer> answers;

    // Dùng chung cho cả /start và /review
    public static class Answer implements Serializable {
        public long id;
        public String answerText;

        // Các trường này sẽ LÀ NULL khi /start
        // và CÓ GIÁ TRỊ khi /review
        public Boolean isCorrect;
        public Boolean isSelected; // Đánh dấu câu trả lời của người dùng
    }
}