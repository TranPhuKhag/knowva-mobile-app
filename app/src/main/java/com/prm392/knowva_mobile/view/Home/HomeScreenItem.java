package com.prm392.knowva_mobile.view.Home;

import com.prm392.knowva_mobile.model.FlashcardSet;
import java.util.List;

// Lớp cha trừu tượng để định nghĩa các loại item trên màn hình chính
public abstract class HomeScreenItem {
    private HomeScreenItem() {} // Ngăn kế thừa từ bên ngoài

    // 1. Dữ liệu cho banner chào mừng
    public static final class Banner extends HomeScreenItem {
        public final String userName;
        public final int streak;
        public final int dailyProgress;
        public final int dailyGoal;

        public Banner(String userName, int streak, int dailyProgress, int dailyGoal) {
            this.userName = userName;
            this.streak = streak;
            this.dailyProgress = dailyProgress;
            this.dailyGoal = dailyGoal;
        }
    }

    // 2. Dữ liệu cho danh sách "Tiếp tục học"
    public static final class ContinueLearning extends HomeScreenItem {
        public final List<FlashcardSet> sets;
        public ContinueLearning(List<FlashcardSet> sets) { this.sets = sets; }
    }

    // 3. Dữ liệu cho các tiêu đề (ví dụ: "Gợi ý cho bạn")
    public static final class Header extends HomeScreenItem {
        public final String title;
        public Header(String title) { this.title = title; }
    }

    // 4. Dữ liệu cho danh sách "Tác giả nổi bật"
    public static final class Authors extends HomeScreenItem {
        public final List<FlashcardSet> sets;
        public Authors(List<FlashcardSet> sets) { this.sets = sets; }
    }

    // 5. Dữ liệu cho một bộ flashcard được gợi ý
    public static final class RecommendedSet extends HomeScreenItem {
        public final FlashcardSet set;
        public RecommendedSet(FlashcardSet set) { this.set = set; }
    }
}