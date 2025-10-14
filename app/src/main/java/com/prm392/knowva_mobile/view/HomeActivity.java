package com.prm392.knowva_mobile.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.features.home.presentation.ui.adapter.HomeAdapter;
import com.prm392.knowva_mobile.features.home.presentation.viewmodel.HomeViewModel;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel viewModel;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gắn layout cho Activity này
        setContentView(R.layout.activity_home);

        // --- Tìm và thiết lập Toolbar ---
        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar); // Đặt toolbar này làm action bar chính

        // Bắt sự kiện click cho icon menu (hamburger icon)
        topAppBar.setNavigationOnClickListener(v -> {
            // Xử lý mở menu ở đây, ví dụ: mở Navigation Drawer
            Toast.makeText(this, "Menu icon clicked!", Toast.LENGTH_SHORT).show();
        });

        // 1. Tìm RecyclerView từ layout
        recyclerView = findViewById(R.id.rv_home_activity); // Dùng ID mới để tránh nhầm lẫn

        // 2. Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 3. Setup RecyclerView và Adapter
        setupRecyclerView();

        // 4. Lắng nghe dữ liệu từ ViewModel
        observeViewModel();
    }

    // PHƯƠNG THỨC NÀY ĐỂ XỬ LÝ CLICK VÀO CÁC ICON TRÊN TOOLBAR
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_add) {
            // Xử lý khi nhấn nút dấu cộng
            Toast.makeText(this, "Nút dấu cộng được nhấn!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_profile) {
            // Xử lý khi nhấn nút avatar
            Toast.makeText(this, "Nút avatar được nhấn!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu, menu);
        return true;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeAdapter = new HomeAdapter(); // Dùng lại HomeAdapter đã có
        recyclerView.setAdapter(homeAdapter);
    }

    private void observeViewModel() {
        viewModel.getHomeItems().observe(this, homeScreenItems -> {
            if (homeScreenItems != null) {
                homeAdapter.setItems(homeScreenItems);
            }
        });
    }
}