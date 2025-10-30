package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuizService {
    @GET("quiz-sets/my-quiz-sets")
    Call<List<MyQuizSetResponse>> getMyQuizSets();
}