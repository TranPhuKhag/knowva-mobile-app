package com.prm392.knowva_mobile.features.home.presentation.state;

import com.prm392.knowva_mobile.data.model.FlashcardSet;

import java.util.List;

// Lớp cha trừu tượng, tương đương với Sealed Class trong Kotlin
public abstract class HomeScreenItem {
    // Private constructor để ngăn các lớp bên ngoài kế thừa
    private HomeScreenItem() {}

    // 1. Dữ liệu cho banner
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

    // 2. Dữ liệu cho danh sách cuộn ngang
    public static final class ContinueLearning extends HomeScreenItem {
        public final List<FlashcardSet> sets; // Giả sử bạn có class FlashcardSet

        public ContinueLearning(List<FlashcardSet> sets) {
            this.sets = sets;
        }
    }

    // 3. Dữ liệu cho tiêu đề
    public static final class Header extends HomeScreenItem {
        public final String title;

        public Header(String title) {
            this.title = title;
        }
    }

    public static final class Authors extends HomeScreenItem {
        public final List<FlashcardSet> sets; // Giả sử bạn có class FlashcardSet

        public Authors(List<FlashcardSet> sets) {
            this.sets = sets;
        }
    }

    // 4. Dữ liệu cho item gợi ý
    public static final class RecommendedSet extends HomeScreenItem {
        public final FlashcardSet set; // Giả sử bạn có class FlashcardSet

        public RecommendedSet(FlashcardSet set) {
            this.set = set;
        }
    }
}
