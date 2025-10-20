package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.view.flashcard.model.CreateSetRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FlashcardService {
    @GET("flashcard-sets/my-flashcard-sets")
    Call<List<MyFlashcardSetResponse>> getMySets();

    @POST("flashcard-sets/save")
    Call<Void> createSet(@Body CreateSetRequest body);
}
