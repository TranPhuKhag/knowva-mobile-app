package com.prm392.knowva_mobile.view.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.repository.QuizRepository;
import com.prm392.knowva_mobile.view.HomeActivity;
import com.prm392.knowva_mobile.view.flashcard.FlashcardBottomSheet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuizzesActivity extends AppCompatActivity {

    private final SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private final SimpleDateFormat monthFmt = new SimpleDateFormat("'In' MMMM yyyy", Locale.US);

    private QuizRepository quizRepository;
    private MyQuizzesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quizzes);

        quizRepository = new QuizRepository(this);

        // --- Toolbar ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- RecyclerView ---
        RecyclerView rv = findViewById(R.id.rv_my_quizzes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyQuizzesAdapter();
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyQuizzesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyQuizSetResponse set) {
                // Mở trang chi tiết (như cũ)
                Intent intent = new Intent(MyQuizzesActivity.this, QuizDetailActivity.class);
                intent.putExtra(QuizDetailActivity.QUIZ_ID_KEY, set.id);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(MyQuizSetResponse set, int position) {
                // Hiển thị dialog xác nhận
                showDeleteConfirmation(set, position);
            }
        });

        // --- Bottom Navigation ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.menu_bottom_quiz); // Đánh dấu Quiz là mục đang chọn

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_bottom_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            if (id == R.id.menu_bottom_flashcard) {
                new FlashcardBottomSheet().show(getSupportFragmentManager(), "FlashcardBottomSheet");
                return false; // Không đổi trạng thái selected
            }
            if (id == R.id.menu_bottom_quiz) {
                // Đã ở đây rồi, nhưng nếu user nhấn lại, ta mở bottom sheet
                new QuizBottomSheet().show(getSupportFragmentManager(), "QuizBottomSheet");
                return false; // Không đổi trạng thái selected
            }
            return false;
        });

        // --- Tải dữ liệu ---
//        fetchMyQuizzes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi quay lại màn hình này
        // để nhận được các thay đổi mới nhất sau khi edit.
        fetchMyQuizzes();
    }

    private void showDeleteConfirmation(MyQuizSetResponse set, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa bộ quiz \"" + set.title + "\"?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // 1. Xóa ngay lập tức khỏi UI
                    adapter.removeItem(position);

                    // 2. Gọi API để xóa
                    callDeleteApi(set.id);
                })
                .show();
    }

    private void callDeleteApi(long quizId) {
        quizRepository.deleteQuizSet(quizId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MyQuizzesActivity.this, "Đã xóa quiz", Toast.LENGTH_SHORT).show();
                    // UI đã được cập nhật rồi, không cần làm gì thêm
                } else {
                    Toast.makeText(MyQuizzesActivity.this, "Lỗi khi xóa, đang làm mới...", Toast.LENGTH_SHORT).show();
                    fetchMyQuizzes(); // Tải lại danh sách nếu API lỗi
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MyQuizzesActivity.this, "Lỗi mạng, đang làm mới...", Toast.LENGTH_SHORT).show();
                fetchMyQuizzes(); // Tải lại danh sách nếu mạng lỗi
            }
        });
    }

    private void fetchMyQuizzes() {
        quizRepository.getMyQuizSets().enqueue(new Callback<List<MyQuizSetResponse>>() {
            @Override
            public void onResponse(Call<List<MyQuizSetResponse>> call, Response<List<MyQuizSetResponse>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MyQuizzesActivity.this, "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                List<Object> items = groupByMonth(response.body());
                adapter.submit(items);
            }

            @Override
            public void onFailure(Call<List<MyQuizSetResponse>> call, Throwable t) {
                Log.e("MyQuizzesActivity", "Network error: " + t.getMessage(), t);
                Toast.makeText(MyQuizzesActivity.this, "Mạng lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper để nhóm theo tháng
    private List<Object> groupByMonth(List<MyQuizSetResponse> src) {
        // Sắp xếp theo ngày tạo (mới nhất trước)
        Collections.sort(src, (a, b) -> {
            try {
                Date dateA = iso.parse(a.createdAt);
                Date dateB = iso.parse(b.createdAt);
                return dateB.compareTo(dateA);
            } catch (Exception e) {
                return 0;
            }
        });

        Map<String, List<MyQuizSetResponse>> map = new LinkedHashMap<>();
        for (MyQuizSetResponse s : src) {
            String key = "Unknown Date";
            try {
                if (s.createdAt != null) {
                    key = monthFmt.format(iso.parse(s.createdAt));
                }
            } catch (ParseException ignored) {}

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        List<Object> out = new ArrayList<>();
        for (Map.Entry<String, List<MyQuizSetResponse>> e : map.entrySet()) {
            out.add(e.getKey()); // Thêm header tháng
            out.addAll(e.getValue()); // Thêm các quiz set trong tháng đó
        }
        return out;
    }
}