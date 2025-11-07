package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.view.Home.HomeScreenItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HomeService {
    // Ví dụ về một endpoint để lấy dữ liệu cho màn hình chính
    @GET("home")
    Call<List<HomeScreenItem>> getHomeData();
}