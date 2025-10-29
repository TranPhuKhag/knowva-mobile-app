package com.prm392.knowva_mobile.repository;

import android.content.Context;

import com.prm392.knowva_mobile.factory.APIClient;
import com.prm392.knowva_mobile.model.FlashcardSet;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.service.FlashcardService;
import com.prm392.knowva_mobile.view.flashcard.model.CreateSetRequest;
import com.prm392.knowva_mobile.view.flashcard.model.UpdateSetRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class FlashcardRepository {
    private FlashcardService service;

    public FlashcardRepository(Context context) {
        this.service = APIClient.getClient(context).create(FlashcardService.class);
    }

    public Call<List<MyFlashcardSetResponse>> getMySets() {
        return service.getMySets();
    }

    public Call<List<MyFlashcardSetResponse>> getAllSets() {
        return service.getAllSets();
    }

    public Call<Void> createSet(CreateSetRequest body) {
        return service.createSet(body);
    }

    public void getSetById(long id, Callback<FlashcardSet> callback) {
        service.getSetById(id).enqueue(callback);
    }

    public Call<Void> deleteSet(long id) {
        return service.deleteSet(id);
    }

    public Call<FlashcardSet> updateSet(long id, UpdateSetRequest body) {
        return service.updateSet(id, body);
    }
}
