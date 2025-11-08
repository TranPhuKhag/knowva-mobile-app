package com.prm392.knowva_mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.manager.SessionManager;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.repository.AuthRepository;
import com.prm392.knowva_mobile.repository.HomeRepository;
import com.prm392.knowva_mobile.view.Home.HomeAdapter;
import com.prm392.knowva_mobile.view.Home.HomeScreenItem;
import com.prm392.knowva_mobile.view.flashcard.FlashcardBottomSheet;
import com.prm392.knowva_mobile.view.quiz.QuizBottomSheet;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;

import java.util.ArrayList;
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
    private SessionManager sessionManager;
    private List<HomeScreenItem> baseHomeItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);
        authRepository = new AuthRepository(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        topAppBar = findViewById(R.id.topAppBar);
        recyclerView = findViewById(R.id.rv_home_activity);

        setSupportActionBar(topAppBar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        updateNavHeader();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Toast.makeText(HomeActivity.this, "Trang chủ", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_logout) {
                performLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        homeRepository = new HomeRepository(this);
        setupRecyclerView();
        loadData();

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
                new QuizBottomSheet().show(getSupportFragmentManager(), "QuizBottomSheet");
                return true;
            }
            return false;
        });

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

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = headerView.findViewById(R.id.tv_user_email);
        ImageView ivAvatar = headerView.findViewById(R.id.iv_avatar);

        String name = sessionManager.getFullName();
        String email = sessionManager.getEmail();
        String avatarUrl = sessionManager.getAvatarUrl();

        tvUserName.setText(name);
        tvUserEmail.setText(email);

        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .circleCrop()
                .into(ivAvatar);
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
        String realUserName = sessionManager.getFullName();
        // 1. Tải các item CỐ ĐỊNH (Banner, Header "Flashcard gợi ý")
        baseHomeItems = homeRepository.getHomeItems(realUserName);
        homeAdapter.setItems(baseHomeItems); // Hiển thị tạm thời

        // 2. Tải các item ĐỘNG (từ API)
        loadAllQuizzes(); // Gọi API quiz
        loadSuggestedSets();
    }

    private void loadAllQuizzes() {
        homeRepository.getAllQuizSets().enqueue(new Callback<List<MyQuizSetResponse>>() {
            @Override
            public void onResponse(Call<List<MyQuizSetResponse>> call, Response<List<MyQuizSetResponse>> res) {
                if (res.isSuccessful() && res.body() != null && !res.body().isEmpty()) {
                    List<MyQuizSetResponse> allQuizzes = res.body();

                    // Tìm vị trí của "Flashcard gợi ý"
                    int flashcardHeaderIndex = -1;
                    for (int i = 0; i < baseHomeItems.size(); i++) {
                        HomeScreenItem item = baseHomeItems.get(i);
                        if (item instanceof HomeScreenItem.Header && ((HomeScreenItem.Header) item).title.equals("Flashcard gợi ý")) {
                            flashcardHeaderIndex = i;
                            break;
                        }
                    }

                    // Chèn Header và List Quiz vào *trước* "Flashcard gợi ý"
                    int insertionIndex = (flashcardHeaderIndex != -1) ? flashcardHeaderIndex : baseHomeItems.size();

                    baseHomeItems.add(insertionIndex, new HomeScreenItem.Header("Quiz gợi ý"));
                    baseHomeItems.add(insertionIndex + 1, new HomeScreenItem.QuizSets(allQuizzes));

                    homeAdapter.notifyDataSetChanged(); // Cập nhật UI
                }
            }
            @Override
            public void onFailure(Call<List<MyQuizSetResponse>> call, Throwable t) {
                // Bỏ qua lỗi
            }
        });
    }

    private void loadSuggestedSets() {
        homeRepository.getAllSets().enqueue(new Callback<List<MyFlashcardSetResponse>>() {
            @Override
            public void onResponse(Call<List<MyFlashcardSetResponse>> call, Response<List<MyFlashcardSetResponse>> res) {
                if (!res.isSuccessful() || res.body() == null) return;

                List<MyFlashcardSetResponse> all = res.body();
                int limit = Math.min(4, all.size());
                List<MyFlashcardSetResponse> suggestedList = all.subList(0, limit);

                baseHomeItems.add(new HomeScreenItem.SuggestedSets(suggestedList));
                homeAdapter.notifyDataSetChanged(); // Cập nhật UI
            }

            @Override
            public void onFailure(Call<List<MyFlashcardSetResponse>> call, Throwable t) {
            }
        });
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
        sessionManager.logoutUser();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
