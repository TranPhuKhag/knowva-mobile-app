package com.prm392.knowva_mobile.model.response;

public class ExamResultResponse {
    private int score; // 0..100
    private String whatWasCorrect;
    private String whatWasIncorrect;
    private String whatCouldHaveIncluded;

    public int getScore() {
        return score;
    }

    public String getWhatWasCorrect() {
        return whatWasCorrect;
    }

    public String getWhatWasIncorrect() {
        return whatWasIncorrect;
    }

    public String getWhatCouldHaveIncluded() {
        return whatCouldHaveIncluded;
    }
}

