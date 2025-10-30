package com.prm392.knowva_mobile.view.quiz.model;

import java.io.Serializable;

// Dữ liệu nháp cho một câu trả lời
public class QuizAnswerDraft implements Serializable {
    public String answerText = "";
    public boolean isCorrect = false;

    public boolean isValid() {
        return answerText != null && !answerText.trim().isEmpty();
    }
}