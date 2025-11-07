package com.prm392.knowva_mobile.model.request.quiz;

// POJO cho body của API /submit
public class SubmitAnswerRequest {
    public long questionId;
    public long selectedAnswerId;

    public SubmitAnswerRequest(long questionId, long selectedAnswerId) {
        this.questionId = questionId;
        this.selectedAnswerId = selectedAnswerId;
    }
}