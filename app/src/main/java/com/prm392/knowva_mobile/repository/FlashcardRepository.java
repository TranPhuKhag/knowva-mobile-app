package com.prm392.knowva_mobile.repository;

import android.content.Context;

import com.prm392.knowva_mobile.factory.APIClient;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.service.FlashcardService;
import com.prm392.knowva_mobile.view.flashcard.model.CreateSetRequest;

import java.util.List;

import retrofit2.Call;

public class FlashcardRepository {
    private FlashcardService service;

    public FlashcardRepository(Context context) {
        this.service = APIClient.getClient(context).create(FlashcardService.class);
    }

    public Call<List<MyFlashcardSetResponse>> getMySets() {
        return service.getMySets();
    }

    public Call<Void> createSet(CreateSetRequest body) {
        return service.createSet(body);
    }
}
