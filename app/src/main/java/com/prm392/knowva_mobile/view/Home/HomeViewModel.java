package com.prm392.knowva_mobile.view.Home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.knowva_mobile.model.FlashcardSet;
// import com.example.app.data.model.FlashcardSet; // Import model của bạn

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<HomeScreenItem>> _homeItems = new MutableLiveData<>();
    public LiveData<List<HomeScreenItem>> homeItems = _homeItems;

    public LiveData<List<HomeScreenItem>> getHomeItems() {
        return homeItems;
    }

    public HomeViewModel() {
        loadHomeScreenData();
    }

    private void loadHomeScreenData() {
        // Trong dự án thực tế, bạn sẽ gọi Repository/DataSource ở đây
        // Đây là dữ liệu giả (mock data)
        List<HomeScreenItem> items = new ArrayList<>();
        List<FlashcardSet> recentSets = new ArrayList<>();
        // TODO: Thêm dữ liệu thật cho recentSets và recommendedSets

        // 1. Thêm Banner
        items.add(new HomeScreenItem.Banner("Nguoi dung"));

        // 2. Thêm Carousel
        items.add(new HomeScreenItem.ContinueLearning(recentSets));

        // 3. Thêm Header
        items.add(new HomeScreenItem.Header("Gợi ý cho bạn"));

        // 4. Thêm các item gợi ý
        // items.add(new HomeScreenItem.RecommendedSet(new FlashcardSet(...)));

        _homeItems.setValue(items);
    }
}
