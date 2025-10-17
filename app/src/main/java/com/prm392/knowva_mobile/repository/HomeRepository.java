package com.prm392.knowva_mobile.repository;

import com.prm392.knowva_mobile.model.FlashcardSet;
import com.prm392.knowva_mobile.view.Home.HomeScreenItem;
import java.util.ArrayList;
import java.util.List;

public class HomeRepository {
    // Trong thực tế, bạn sẽ khởi tạo HomeService ở đây
    // private HomeService homeService;

    public HomeRepository() {
        // Retrofit retrofit = APIClient.getClient(context);
        // homeService = retrofit.create(HomeService.class);
    }

    // Phương thức cung cấp dữ liệu (hiện tại là mock data)
    public List<HomeScreenItem> getHomeItems() {
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
        items.add(new HomeScreenItem.Banner("Van Hao", 7, 25, 50));

        // 2. Thêm Tiêu đề và danh sách "Tiếp tục học"
        items.add(new HomeScreenItem.Header("Tiếp tục học"));
        items.add(new HomeScreenItem.ContinueLearning(recentSets));

        // 3. Thêm Tiêu đề và danh sách "Tác giả nổi bật"
        items.add(new HomeScreenItem.Header("Tác giả nổi bật"));
        items.add(new HomeScreenItem.Authors(authorSets));

        // 4. Thêm Tiêu đề và các item gợi ý
        items.add(new HomeScreenItem.Header("Gợi ý cho bạn"));
        items.add(new HomeScreenItem.RecommendedSet(new FlashcardSet("5", "Toán cao cấp", "Nguyễn Văn A", 99)));
        items.add(new HomeScreenItem.RecommendedSet(new FlashcardSet("6", "Lập trình Android", "Trần Thị B", 110)));

        return items;
    }
}