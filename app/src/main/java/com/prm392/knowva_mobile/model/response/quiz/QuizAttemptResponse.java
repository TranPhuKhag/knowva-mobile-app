package com.prm392.knowva_mobile.model.response.quiz;

import java.io.Serializable;
import java.util.List;

// Lớp Wrapper cho toàn bộ response của API /start
public class QuizAttemptResponse implements Serializable {
    public QuizAttempt attempt;
    public List<QuizAttemptQuestion> questions;
}