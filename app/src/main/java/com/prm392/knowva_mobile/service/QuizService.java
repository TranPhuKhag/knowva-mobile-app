package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.model.request.quiz.CreateQuizRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface QuizService {
    @GET("quiz-sets/my-quiz-sets")
    Call<List<MyQuizSetResponse>> getMyQuizSets();

    @POST("quiz-sets/save")
    Call<Void> saveQuizSet(@Body CreateQuizRequest body);

    @GET("quiz-sets/{id}")
    Call<MyQuizSetResponse> getQuizSetById(@Path("id") long id);

    @PUT("quiz-sets/{quizSetId}")
    Call<MyQuizSetResponse> updateQuizSet(
            @Path("quizSetId") long quizSetId,
            @Body CreateQuizRequest body
    );
}
