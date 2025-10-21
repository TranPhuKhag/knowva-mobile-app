package com.prm392.knowva_mobile.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.repository.AuthRepository;
import com.prm392.knowva_mobile.repository.HomeRepository;
import com.prm392.knowva_mobile.view.Home.HomeAdapter;
import com.prm392.knowva_mobile.view.Home.HomeScreenItem;
import com.prm392.knowva_mobile.view.flashcard.FlashcardBottomSheet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private AuthRepository authRepository;
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

        // Khởi tạo AuthRepository
        authRepository = new AuthRepository(this);

        // Ánh xạ View
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        topAppBar = findViewById(R.id.topAppBar);
        recyclerView = findViewById(R.id.rv_home_activity);

        // Thiết lập Toolbar và Drawer
        setSupportActionBar(topAppBar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Sidebar menu event
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Toast.makeText(HomeActivity.this, "Trang chủ", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(HomeActivity.this, "Hồ sơ", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_settings) {
                Toast.makeText(HomeActivity.this, "Cài đặt", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // RecyclerView
        homeRepository = new HomeRepository();
        setupRecyclerView();
        loadData();

        // Bottom Navigation
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

        // Xử lý nút back với OnBackPressedCallback
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

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeAdapter = new HomeAdapter();
        recyclerView.setAdapter(homeAdapter);
    }

    private void loadData() {
        List<HomeScreenItem> items = homeRepository.getHomeItems();
        homeAdapter.setItems(items);
    }

    private void performLogout() {
        authRepository.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                clearLocalDataAndGoToLogin();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Lỗi mạng, đăng xuất...", Toast.LENGTH_SHORT).show();
                clearLocalDataAndGoToLogin();
            }
        });
    }

    private void clearLocalDataAndGoToLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
