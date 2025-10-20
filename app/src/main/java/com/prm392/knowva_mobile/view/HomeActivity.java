package com.prm392.knowva_mobile.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.repository.HomeRepository;
import com.prm392.knowva_mobile.view.Home.HomeAdapter;
import com.prm392.knowva_mobile.view.Home.HomeScreenItem;
import com.prm392.knowva_mobile.view.flashcard.FlashcardBottomSheet;

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
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // --- Khởi tạo và hiển thị dữ liệu cho RecyclerView ---
        homeRepository = new HomeRepository();
        setupRecyclerView();
        loadData();

        // --- Thiết lập Bottom Navigation ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.menu_bottom_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_bottom_home) {
                setTitle("knowva-mobile");
                return true;
            }
            if (id == R.id.menu_bottom_flashcard) {
                new FlashcardBottomSheet().show(getSupportFragmentManager(), "FlashcardBottomSheet");
                return false;
            }
            if (id == R.id.menu_bottom_quiz) {
                Toast.makeText(this, "Quiz", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // --- Xử lý nút back ---
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(HomeActivity.this, "Đang tìm: " + query, Toast.LENGTH_SHORT).show();
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Toast.makeText(this, "Chuyển sang trang tạo Flashcard", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}