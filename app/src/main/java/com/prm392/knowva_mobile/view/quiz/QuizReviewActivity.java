package com.prm392.knowva_mobile.view.quiz;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttemptQuestion;
import com.prm392.knowva_mobile.repository.QuizRepository;
import com.prm392.knowva_mobile.view.quiz.adapter.QuizReviewAdapter;
import com.google.gson.Gson;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttemptResponse;
import com.prm392.knowva_mobile.model.response.quiz.QuizReviewResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizReviewActivity extends AppCompatActivity {

    private long mAttemptId;
    private QuizRepository quizRepository;
    private QuizReviewAdapter adapter;
    private List<QuizAttemptQuestion> originalQuestions;
    private ImageView imgScoreFeedback;
    private TextView tvScoreResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_review);

        mAttemptId = getIntent().getLongExtra("attempt_id", -1);
        String json = getIntent().getStringExtra(QuizAttemptActivity.ATTEMPT_RESPONSE_JSON);
        double score = getIntent().getDoubleExtra("score", -1.0);
        if (mAttemptId == -1 || json == null) {
            Toast.makeText(this, "Lỗi không tìm thấy bài làm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        originalQuestions = new Gson().fromJson(json, QuizAttemptResponse.class).questions;

        quizRepository = new QuizRepository(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // Đóng và quay về

        RecyclerView rv = findViewById(R.id.rv_review_questions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuizReviewAdapter(this);
        rv.setAdapter(adapter);

        imgScoreFeedback = findViewById(R.id.img_score_feedback);
        tvScoreResult = findViewById(R.id.tv_score_result);

        if (score != -1.0) {
            displayScore(score);
        }

        fetchReview();
    }

    private void displayScore(double score) {
        // Làm tròn điểm về số nguyên
        int intScore = (int) Math.round(score);

        tvScoreResult.setText(String.format(Locale.getDefault(), "Điểm của bạn: %d", intScore));
        tvScoreResult.setVisibility(View.VISIBLE);

        // Logic hiển thị ảnh
        if (intScore < 50) {
            imgScoreFeedback.setImageResource(R.drawable.angry);
        } else if (intScore <= 80) { // 50 <= score <= 80
            imgScoreFeedback.setImageResource(R.drawable.better);
        } else { // > 80
            imgScoreFeedback.setImageResource(R.drawable.icon_super);
        }
        imgScoreFeedback.setVisibility(View.VISIBLE);
    }

    private void fetchReview() {
        // Gọi API review mới
        quizRepository.reviewAttempt(mAttemptId).enqueue(new Callback<QuizReviewResponse>() {
            @Override
            public void onResponse(Call<QuizReviewResponse> call, Response<QuizReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Trộn 2 list
                    List<QuizAttemptQuestion> mergedList = mergeReviewData(originalQuestions, response.body());
                    adapter.submitList(mergedList);

                    if (tvScoreResult.getVisibility() == View.GONE) {
                        displayScore(response.body().score);
                    }
                } else {
                    Toast.makeText(QuizReviewActivity.this, "Lỗi tải kết quả: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<QuizReviewResponse> call, Throwable t) {
                Toast.makeText(QuizReviewActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<QuizAttemptQuestion> mergeReviewData(List<QuizAttemptQuestion> questions, QuizReviewResponse reviewData) {
        // Tạo Map để tra cứu review item theo questionId
        Map<Long, QuizReviewResponse.ReviewItem> reviewMap = reviewData.reviews.stream()
                .collect(Collectors.toMap(r -> r.questionId, r -> r));

        for (QuizAttemptQuestion q : questions) {
            QuizReviewResponse.ReviewItem review = reviewMap.get(q.id);
            if (review == null) continue; // Bỏ qua nếu câu hỏi không có trong review

            // Duyệt qua các câu trả lời của câu hỏi gốc
            for (QuizAttemptQuestion.Answer ans : q.answers) {
                // Đánh dấu câu người dùng đã chọn
                if (ans.id == review.userAnswerId) {
                    ans.isSelected = true;
                }
                // Đánh dấu câu trả lời đúng
                if (ans.id == review.correctAnswerId) {
                    ans.isCorrect = true;
                }
            }
        }
        return questions;
    }
}