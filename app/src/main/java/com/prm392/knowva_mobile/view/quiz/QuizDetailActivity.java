package com.prm392.knowva_mobile.view.quiz;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.repository.QuizRepository;
import com.prm392.knowva_mobile.view.quiz.adapter.QuizDetailAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.prm392.knowva_mobile.manager.SessionManager;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizDetailActivity extends AppCompatActivity {

    public static final String QUIZ_ID_KEY = "quiz_id";

    private QuizRepository quizRepository;
    private QuizDetailAdapter adapter;

    private TextView tvSetTitle, tvSetDescription, tvUsername, tvQuestionsCount;
    private RecyclerView rvQuestions;
    private Button btnStartQuiz;
    private long quizId;

    private SessionManager sessionManager;
    private MyQuizSetResponse mCurrentQuiz;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);

        quizId = getIntent().getLongExtra(QUIZ_ID_KEY, -1);
        if (quizId == -1) {
            Toast.makeText(this, "ID Quiz không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        quizRepository = new QuizRepository(this);
        sessionManager = new SessionManager(this);

        // --- Ánh xạ View ---
        tvSetTitle = findViewById(R.id.tvSetTitle);
        tvSetDescription = findViewById(R.id.tvSetDescription);
        tvUsername = findViewById(R.id.tvUsername);
        tvQuestionsCount = findViewById(R.id.tvQuestionsCount);
        rvQuestions = findViewById(R.id.rv_questions);
        btnStartQuiz = findViewById(R.id.btn_start_quiz);

        // --- Toolbar ---
        toolbar = findViewById(R.id.toolbar); // Gán vào biến toàn cục
        setSupportActionBar(toolbar); // Quan trọng: Đặt làm Action Bar
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- RecyclerView ---
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuizDetailAdapter(this);
        rvQuestions.setAdapter(adapter);

        // --- Nút bắt đầu ---
        btnStartQuiz.setOnClickListener(v -> {
            // TODO: Chuyển sang màn hình làm quiz
            Toast.makeText(this, "Bắt đầu làm quiz (chưa implement)", Toast.LENGTH_SHORT).show();
        });

        // --- Tải dữ liệu ---
        fetchQuizDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Chỉ hiển thị menu nếu là chủ sở hữu
        if (mCurrentQuiz != null &&
                mCurrentQuiz.username != null &&
                sessionManager.getFullName() != null &&
                mCurrentQuiz.username.equals(sessionManager.getFullName())) {

            getMenuInflater().inflate(R.menu.menu_quiz_detail_owner, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.menu_quiz_more) {
            openOwnerBottomSheet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openOwnerBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_quiz_owner_actions, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        view.findViewById(R.id.bs_quiz_edit).setOnClickListener(v -> {
            dialog.dismiss();
            openEditActivity();
        });

        dialog.show();
    }

    private void openEditActivity() {
        if (mCurrentQuiz == null) return;

        Intent i = new Intent(this, EditQuizActivity.class);
        i.putExtra("quiz_json", new Gson().toJson(mCurrentQuiz));
        startActivity(i);
    }

    private void fetchQuizDetails() {
        quizRepository.getQuizSetById(quizId).enqueue(new Callback<MyQuizSetResponse>() {
            @Override
            public void onResponse(Call<MyQuizSetResponse> call, Response<MyQuizSetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                } else {
                    Toast.makeText(QuizDetailActivity.this, "Không thể tải chi tiết quiz: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyQuizSetResponse> call, Throwable t) {
                Toast.makeText(QuizDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindData(MyQuizSetResponse quiz) {
        mCurrentQuiz = quiz;

        // Bind thông tin meta
        tvSetTitle.setText(quiz.title);
        tvUsername.setText(quiz.username);

        if (!TextUtils.isEmpty(quiz.description)) {
            tvSetDescription.setText(quiz.description);
            tvSetDescription.setVisibility(View.VISIBLE);
        } else {
            tvSetDescription.setVisibility(View.GONE);
        }

        int count = quiz.getQuestionCount();
        tvQuestionsCount.setText(String.format(Locale.getDefault(), "%d câu hỏi", count));

        // Bind danh sách câu hỏi
        adapter.submitList(quiz.questions);
        invalidateOptionsMenu();
    }
}