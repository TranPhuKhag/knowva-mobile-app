package com.prm392.knowva_mobile.repository;

import android.content.Context;
import com.prm392.knowva_mobile.model.FlashcardSet;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.view.Home.HomeScreenItem;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

public class HomeRepository {
    private FlashcardRepository flashcardRepository;

    public HomeRepository(Context context) {
        this.flashcardRepository = new FlashcardRepository(context);
    }

    // Phương thức cung cấp dữ liệu (hiện tại là mock data)
    public List<HomeScreenItem> getHomeItems(String userName) {
        List<HomeScreenItem> items = new ArrayList<>();

        // Dữ liệu giả cho các danh sách
        List<FlashcardSet> recentSets = new ArrayList<>();
        recentSets.add(new FlashcardSet("1", "Lịch sử Đảng", "Bộ Giáo dục", 150));
        recentSets.add(new FlashcardSet("2", "Giải tích I", "PGS. TS. ABC", 200));
        recentSets.add(new FlashcardSet("7", "Kinh tế vi mô", "Khoa Kinh tế", 115));

        List<FlashcardSet> authorSets = new ArrayList<>();
        authorSets.add(new FlashcardSet("3", "React Native", "John Doe", 80));
        authorSets.add(new FlashcardSet("4", "NodeJS cơ bản", "Jane Smith", 120));

        // 1. Thêm Banner
        items.add(new HomeScreenItem.Banner(userName));

        // 2. Thêm Tiêu đề và danh sách "Tiếp tục học"
        items.add(new HomeScreenItem.Header("Tiếp tục học"));
        items.add(new HomeScreenItem.ContinueLearning(recentSets));

        // 3. Thêm Tiêu đề và danh sách "Tác giả nổi bật"
        items.add(new HomeScreenItem.Header("Tác giả nổi bật"));
        items.add(new HomeScreenItem.Authors(authorSets));

        // 4. Thêm Tiêu đề (phần gợi ý sẽ load từ API)
        items.add(new HomeScreenItem.Header("Gợi ý cho bạn"));

        return items;
    }

    // Gọi API để lấy tất cả flashcard sets công khai
    public Call<List<MyFlashcardSetResponse>> getAllSets() {
        return flashcardRepository.getAllSets();
    }
}