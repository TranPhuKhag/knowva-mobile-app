package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.model.request.quiz.CreateQuizRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface QuizService {
    @GET("quiz-sets/my-quiz-sets")
    Call<List<MyQuizSetResponse>> getMyQuizSets();

    @POST("quiz-sets/save")
    Call<Void> saveQuizSet(@Body CreateQuizRequest body);
}
