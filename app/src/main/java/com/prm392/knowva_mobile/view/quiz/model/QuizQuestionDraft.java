package com.prm392.knowva_mobile.view.quiz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Dữ liệu nháp cho một câu hỏi
public class QuizQuestionDraft implements Serializable {
    public String questionText = "";
    public List<QuizAnswerDraft> answers = new ArrayList<>();

    public QuizQuestionDraft() {
        // Mặc định thêm 2 câu trả lời
        answers.add(new QuizAnswerDraft());
        answers.add(new QuizAnswerDraft());
    }

    public boolean isValid() {
        if (questionText == null || questionText.trim().isEmpty()) {
            return false;
        }
        // Phải có ít nhất 2 câu trả lời hợp lệ
        int validAnswers = 0;
        // Và ít nhất 1 câu đúng
        boolean hasCorrect = false;
        for (QuizAnswerDraft answer : answers) {
            if (answer.isValid()) {
                validAnswers++;
            }
            if (answer.isCorrect) {
                hasCorrect = true;
            }
        }
        return validAnswers >= 2 && hasCorrect;
    }
}