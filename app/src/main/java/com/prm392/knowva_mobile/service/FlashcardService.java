package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.FlashcardSet;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.view.flashcard.model.CreateSetRequest;
import com.prm392.knowva_mobile.view.flashcard.model.UpdateSetRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FlashcardService {
    @GET("flashcard-sets/my-flashcard-sets")
    Call<List<MyFlashcardSetResponse>> getMySets();

    @GET("flashcard-sets/all")
    Call<List<MyFlashcardSetResponse>> getAllSets();

    @POST("flashcard-sets/save")
    Call<Void> createSet(@Body CreateSetRequest body);

    @GET("flashcard-sets/{id}")
    Call<FlashcardSet> getSetById(@Path("id") long id);

    @DELETE("flashcard-sets/{id}")
    Call<Void> deleteSet(@Path("id") long id);

    @PUT("flashcard-sets/{id}")
    Call<FlashcardSet> updateSet(
        @Path("id") long id,
        @Body UpdateSetRequest body
    );
}
