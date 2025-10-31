package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.model.request.quiz.CreateQuizRequest;
import com.prm392.knowva_mobile.model.request.quiz.SubmitAnswerRequest;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttemptQuestion;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttemptResponse;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttempt;
import com.prm392.knowva_mobile.model.response.quiz.QuizReviewResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.DELETE;
public interface QuizService {

    @Headers("Cache-Control: no-cache")
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

    @DELETE("quiz-sets/{id}")
    Call<Void> deleteQuizSet(@Path("id") long id);
    @GET("quiz-attempts/{quizSetId}/start") // Đổi @POST thành @GET
    Call<QuizAttemptResponse> startAttempt(
            @Path("quizSetId") long quizSetId
            // Xóa @Body Object emptyBody
    );

    @POST("quiz-attempts/{attemptId}/submit")
    Call<QuizAttempt> submitAttempt(
            @Path("attemptId") long attemptId,
            @Body List<SubmitAnswerRequest> body
    );

    @GET("quiz-attempts/{attemptId}/review")
    Call<QuizReviewResponse> reviewAttempt(@Path("attemptId") long attemptId);
}
