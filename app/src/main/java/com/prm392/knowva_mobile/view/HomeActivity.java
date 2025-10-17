package com.prm392.knowva_mobile.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.repository.HomeRepository;
import com.prm392.knowva_mobile.view.Home.HomeAdapter;
import com.prm392.knowva_mobile.view.Home.HomeScreenItem;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar topAppBar;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private HomeRepository homeRepository;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- Ánh xạ các view từ layout ---
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        topAppBar = findViewById(R.id.topAppBar);
        recyclerView = findViewById(R.id.rv_home_activity);

        // --- Thiết lập Toolbar và Navigation Drawer ---
        setSupportActionBar(topAppBar);
        // Dòng này sẽ tự động kết nối icon 3 gạch với sidebar
        toggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- Xử lý sự kiện click item trong sidebar ---
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Toast.makeText(HomeActivity.this, "Trang chủ", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(HomeActivity.this, "Hồ sơ", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_settings) {
                Toast.makeText(HomeActivity.this, "Cài đặt", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_logout) {
                Toast.makeText(HomeActivity.this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                // Ví dụ: quay về màn hình Login
                // Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                // startActivity(intent);
                // finish();
            }
            // Đóng sidebar sau khi click
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // --- Khởi tạo và hiển thị dữ liệu cho RecyclerView ---
        homeRepository = new HomeRepository();
        setupRecyclerView();
        loadData();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeAdapter = new HomeAdapter();
        recyclerView.setAdapter(homeAdapter);
    }

    private void loadData() {
        List<HomeScreenItem> items = homeRepository.getHomeItems();
        homeAdapter.setItems(items);
    }

    // --- Xử lý nút back của hệ thống (quan trọng) ---
    // Nếu sidebar đang mở, nhấn back sẽ đóng sidebar thay vì thoát app
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Các phương thức xử lý menu trên Toolbar (action_add, action_profile) có thể giữ nguyên
    // nếu bạn vẫn muốn chúng hiển thị trên Toolbar.
}