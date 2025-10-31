package com.prm392.knowva_mobile.repository;

import android.content.Context;
import com.prm392.knowva_mobile.model.FlashcardSet;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.view.Home.HomeScreenItem;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;

public class HomeRepository {
    private FlashcardRepository flashcardRepository;
    private QuizRepository quizRepository;
    public HomeRepository(Context context) {
        this.flashcardRepository = new FlashcardRepository(context);
        this.quizRepository = new QuizRepository(context);
    }

    // Phương thức cung cấp dữ liệu (hiện tại là mock data)
    public List<HomeScreenItem> getHomeItems(String userName) {
        List<HomeScreenItem> items = new ArrayList<>();

        items.add(new HomeScreenItem.Banner(userName));

        items.add(new HomeScreenItem.Header("Flashcard gợi ý"));

        return items;
    }

    // Gọi API để lấy tất cả flashcard sets công khai
    public Call<List<MyFlashcardSetResponse>> getAllSets() {
        return flashcardRepository.getAllSets();
    }
    public Call<List<MyQuizSetResponse>> getAllQuizSets() {
        return quizRepository.getAllQuizSets();
    }
}