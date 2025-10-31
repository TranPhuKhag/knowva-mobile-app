package com.prm392.knowva_mobile.model.response.quiz;

import java.io.Serializable;
import java.util.List;

// Model cho response của API /review
public class QuizReviewResponse implements Serializable {
    public long attemptId;
    public double score;
    public List<ReviewItem> reviews;

    public static class ReviewItem implements Serializable {
        public long questionId;
        public long userAnswerId;
        public long correctAnswerId;
        public boolean correct;
        public List<AnswerOnly> answers; // Chỉ chứa id và text
    }

    public static class AnswerOnly implements Serializable {
        public long id;
        public String answerText;
    }
}