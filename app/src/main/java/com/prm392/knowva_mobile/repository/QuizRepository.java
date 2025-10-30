package com.prm392.knowva_mobile.repository;

import android.content.Context;

import com.prm392.knowva_mobile.factory.APIClient;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.service.QuizService;
import com.prm392.knowva_mobile.model.request.quiz.CreateQuizRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class QuizRepository {
    private QuizService service;

    public QuizRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        this.service = retrofit.create(QuizService.class);
    }

    public Call<List<MyQuizSetResponse>> getMyQuizSets() {
        return service.getMyQuizSets();
    }

    public Call<Void> saveQuizSet(CreateQuizRequest body) {
        return service.saveQuizSet(body);
    }

    public Call<MyQuizSetResponse> getQuizSetById(long id) {
        return service.getQuizSetById(id);
    }

    public Call<MyQuizSetResponse> updateQuizSet(long id, CreateQuizRequest body) {
        return service.updateQuizSet(id, body);
    }
}