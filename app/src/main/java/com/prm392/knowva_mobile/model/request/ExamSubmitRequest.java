package com.prm392.knowva_mobile.model.request;

public class ExamSubmitRequest {
    public long flashcardId;
    public String userAnswer;

    public ExamSubmitRequest(long flashcardId, String userAnswer) {
        this.flashcardId = flashcardId;
        this.userAnswer = userAnswer;
    }
}

