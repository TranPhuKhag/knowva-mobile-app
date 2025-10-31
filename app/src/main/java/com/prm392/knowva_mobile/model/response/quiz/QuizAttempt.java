package com.prm392.knowva_mobile.model.response.quiz;

import java.io.Serializable;

// Đại diện cho đối tượng "attempt"
public class QuizAttempt implements Serializable {
    public long id;
    public QuizSet quizSet;
    public double score;
    public String startedAt;
    public String completedAt;

    // Chỉ cần các trường tối thiểu,
    // (Bỏ qua 'user' và 'owner' bên trong để cho đơn giản)
    public static class QuizSet implements Serializable {
        public long id;
        public String title;
        public int timeLimit;
    }
}