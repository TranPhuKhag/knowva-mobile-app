package com.prm392.knowva_mobile.view.quiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.repository.FlashcardRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateQuizActivity extends AppCompatActivity {

    private FlashcardRepository flashcardRepository;
    private GenerateQuizAdapter adapter;
    private RecyclerView rvSets;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_quiz);

        flashcardRepository = new FlashcardRepository(this);

        // --- Toolbar ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- RecyclerView ---
        rvSets = findViewById(R.id.rv_my_sets_for_quiz);
        rvSets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenerateQuizAdapter(this::showConfirmationDialog);
        rvSets.setAdapter(adapter);

        // --- Load Data ---
        fetchMySets();
    }

    private void fetchMySets() {
        // Chúng ta sẽ lấy các bộ flashcard của người dùng
        flashcardRepository.getMySets().enqueue(new Callback<List<MyFlashcardSetResponse>>() {
            @Override
            public void onResponse(Call<List<MyFlashcardSetResponse>> call, Response<List<MyFlashcardSetResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.submit(response.body());
                } else {
                    Toast.makeText(GenerateQuizActivity.this, "Không thể tải danh sách", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MyFlashcardSetResponse>> call, Throwable t) {
                Toast.makeText(GenerateQuizActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmationDialog(MyFlashcardSetResponse set) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Tạo Quiz")
                .setMessage("Bạn có muốn tạo quiz từ bộ \"" + set.title + "\"?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Tạo", (dialog, which) -> generateQuiz(set))
                .show();
    }

    private void generateQuiz(MyFlashcardSetResponse set) {
        Toast.makeText(this, "Đang tạo quiz...", Toast.LENGTH_SHORT).show();

        flashcardRepository.generateQuiz(set.id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GenerateQuizActivity.this, "Đã gửi yêu cầu tạo quiz!", Toast.LENGTH_SHORT).show();
                    // TODO: Chuyển người dùng đến màn hình "Quiz của tôi"
                    finish(); // Tạm thời đóng activity
                } else {
                    Toast.makeText(GenerateQuizActivity.this, "Tạo quiz thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(GenerateQuizActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}