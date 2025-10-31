package com.prm392.knowva_mobile.view.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.request.quiz.SubmitAnswerRequest;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttemptResponse;
import com.prm392.knowva_mobile.repository.QuizRepository;
import com.prm392.knowva_mobile.view.quiz.adapter.QuizAttemptAdapter;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttempt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizAttemptActivity extends AppCompatActivity {

    public static final String ATTEMPT_RESPONSE_JSON = "attempt_response_json";

    private QuizRepository quizRepository;
    private ViewPager2 vpQuestions;
    private MaterialToolbar toolbar;
    private Button btnSubmit, btnPrev;
    private TextView tvTimer;

    private QuizAttemptResponse attemptResponse;
    private QuizAttemptAdapter adapter;
    private CountDownTimer timer;

    private long mAttemptId;
    private final Map<Long, Long> selectedAnswers = new HashMap<>(); // questionId -> answerId

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_attempt);

        quizRepository = new QuizRepository(this);

        // --- Nhận dữ liệu ---
        String json = getIntent().getStringExtra(ATTEMPT_RESPONSE_JSON);
        if (json == null) {
            Toast.makeText(this, "Lỗi khi bắt đầu quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        attemptResponse = new Gson().fromJson(json, QuizAttemptResponse.class);
        mAttemptId = attemptResponse.attempt.id;
        int totalQuestions = attemptResponse.questions.size();

        // --- Ánh xạ Views ---
        toolbar = findViewById(R.id.toolbar);
        vpQuestions = findViewById(R.id.vp_questions);
        btnSubmit = findViewById(R.id.btn_submit);
        btnPrev = findViewById(R.id.btn_prev);
        tvTimer = findViewById(R.id.tv_timer);

        toolbar.setNavigationOnClickListener(v -> confirmExit());
        toolbar.setTitle(String.format(Locale.getDefault(), "Câu 1/%d", totalQuestions));

        // --- Setup ViewPager ---
        adapter = new QuizAttemptAdapter(this, attemptResponse.questions, (questionId, answerId) -> {
            selectedAnswers.put(questionId, answerId);
        });
        vpQuestions.setAdapter(adapter);
        vpQuestions.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(String.format(Locale.getDefault(), "Câu %d/%d", position + 1, totalQuestions));
                btnPrev.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
            }
        });

        btnPrev.setOnClickListener(v -> vpQuestions.setCurrentItem(vpQuestions.getCurrentItem() - 1, true));
        btnSubmit.setOnClickListener(v -> confirmSubmit());

        // --- Setup Timer ---
        int timeLimitSeconds = attemptResponse.attempt.quizSet.timeLimit;
        if (timeLimitSeconds > 0) {
            startTimer(timeLimitSeconds * 1000L);
        } else {
            tvTimer.setText("Không giới hạn");
        }
    }

    private void startTimer(long durationMillis) {
        timer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                tvTimer.setText(time);
            }
            @Override
            public void onFinish() {
                tvTimer.setText("Hết giờ!");
                Toast.makeText(QuizAttemptActivity.this, "Hết giờ! Đang tự động nộp bài...", Toast.LENGTH_LONG).show();
                submitQuiz();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    private void confirmExit() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Thoát bài làm?")
                .setMessage("Bạn có chắc muốn thoát? Kết quả sẽ không được lưu.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Thoát", (d, w) -> finish())
                .show();
    }

    private void confirmSubmit() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Nộp bài")
                .setMessage("Bạn có chắc muốn nộp bài?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Nộp", (d, w) -> submitQuiz())
                .show();
    }

    private void submitQuiz() {
        if (timer != null) {
            timer.cancel();
        }

        List<SubmitAnswerRequest> body = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : selectedAnswers.entrySet()) {
            body.add(new SubmitAnswerRequest(entry.getKey(), entry.getValue()));
        }

        Toast.makeText(this, "Đang nộp bài...", Toast.LENGTH_SHORT).show();

        quizRepository.submitAttempt(mAttemptId, body).enqueue(new Callback<QuizAttempt>() {
            @Override
            public void onResponse(Call<QuizAttempt> call, Response<QuizAttempt> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QuizAttempt result = response.body();
                    goToReview(mAttemptId, result.score);
                } else {
                    Toast.makeText(QuizAttemptActivity.this, "Lỗi khi nộp bài: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QuizAttempt> call, Throwable t) {
                // Dùng QuizAttemptActivity.this vì đang ở trong 1 inner class (Callback)
                Toast.makeText(QuizAttemptActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToReview(long attemptId, double score) { // Sửa lại hàm để nhận attemptId
        Intent intent = new Intent(this, QuizReviewActivity.class);
        intent.putExtra("attempt_id", attemptId);
        intent.putExtra(ATTEMPT_RESPONSE_JSON, new Gson().toJson(attemptResponse));
        intent.putExtra("score", score);
        startActivity(intent);
        finish(); // Đóng màn hình làm bài
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel(); // Hủy timer khi Activity bị hủy
        }
    }
}