package com.prm392.knowva_mobile.view.flashcard;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.Flashcard;
import com.prm392.knowva_mobile.model.response.ExamResultResponse;
import com.prm392.knowva_mobile.repository.FlashcardRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamModeActivity extends AppCompatActivity {

    private ViewPager2 vpCards;
    private TextView tvExamQuestion, tvExamScore, tvCorrect, tvIncorrect, tvCould;
    private TextInputEditText edtExamAnswer;
    private MaterialButton btnSubmitExam, btnPrevious, btnNext;
    private ProgressBar progressExam;
    private View boxExamResult;

    private FlashcardRepository repo;
    private long setId;
    private List<Flashcard> cards;
    private int currentPosition = 0;

    // Lưu trạng thái exam theo flashcard ID
    private static class ExamState {
        String lastAnswer = "";
        ExamResultResponse result;
    }
    private final Map<Long, ExamState> examStates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_mode);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setTitle("Exam Mode");

        // Initialize views
        vpCards = findViewById(R.id.vpCards);
        tvExamQuestion = findViewById(R.id.tvExamQuestion);
        edtExamAnswer = findViewById(R.id.edtExamAnswer);
        btnSubmitExam = findViewById(R.id.btnSubmitExam);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        progressExam = findViewById(R.id.progressExam);
        boxExamResult = findViewById(R.id.boxExamResult);
        tvExamScore = findViewById(R.id.tvExamScore);
        tvCorrect = findViewById(R.id.tvWhatWasCorrect);
        tvIncorrect = findViewById(R.id.tvWhatWasIncorrect);
        tvCould = findViewById(R.id.tvWhatCouldInclude);

        repo = new FlashcardRepository(this);

        // Get data from intent
        setId = getIntent().getLongExtra("set_id", -1);
        String cardsJson = getIntent().getStringExtra("cards_json");

        if (cardsJson != null && !cardsJson.isEmpty()) {
            cards = new Gson().fromJson(cardsJson, new TypeToken<List<Flashcard>>(){}.getType());
            if (cards != null && !cards.isEmpty()) {
                setupExamMode();
            } else {
                Toast.makeText(this, "No flashcards available", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Failed to load flashcards", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupExamMode() {
        // Setup ViewPager for flashcard display
        FlashcardPagerAdapter adapter = new FlashcardPagerAdapter(cards);
        vpCards.setAdapter(adapter);

        vpCards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                updateExamForPosition(position);
                updateNavigationButtons();
            }
        });

        // Navigation buttons
        btnPrevious.setOnClickListener(v -> {
            if (currentPosition > 0) {
                vpCards.setCurrentItem(currentPosition - 1, true);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPosition < cards.size() - 1) {
                vpCards.setCurrentItem(currentPosition + 1, true);
            }
        });

        // Initialize first card
        updateExamForPosition(0);
        updateNavigationButtons();
    }

    private void updateExamForPosition(int position) {
        if (position < 0 || position >= cards.size()) return;

        Flashcard card = cards.get(position);
        tvExamQuestion.setText(card.getFront());

        // Restore previous answer and result if exists
        ExamState state = examStates.get(card.getId());
        if (state != null) {
            edtExamAnswer.setText(state.lastAnswer);
            if (state.result != null) {
                showExamResult(state.result);
            } else {
                hideExamResult();
            }
        } else {
            edtExamAnswer.setText("");
            hideExamResult();
        }

        btnSubmitExam.setOnClickListener(v -> submitAnswer(card));
    }

    private void updateNavigationButtons() {
        btnPrevious.setEnabled(currentPosition > 0);
        btnNext.setEnabled(currentPosition < cards.size() - 1);
    }

    private void submitAnswer(Flashcard card) {
        String answer = edtExamAnswer.getText() == null ? "" : edtExamAnswer.getText().toString().trim();
        if (answer.isEmpty()) {
            Toast.makeText(this, "Please enter your answer", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitExam.setEnabled(false);
        progressExam.setVisibility(View.VISIBLE);

        ExamState state = examStates.get(card.getId());
        if (state == null) {
            state = new ExamState();
            examStates.put(card.getId(), state);
        }
        state.lastAnswer = answer;

        final ExamState finalState = state;
        repo.submitExam(setId, card.getId(), answer).enqueue(new Callback<ExamResultResponse>() {
            @Override
            public void onResponse(Call<ExamResultResponse> call, Response<ExamResultResponse> res) {
                btnSubmitExam.setEnabled(true);
                progressExam.setVisibility(View.GONE);

                if (res.isSuccessful() && res.body() != null) {
                    ExamResultResponse result = res.body();
                    finalState.result = result;
                    showExamResult(result);
                } else {
                    Toast.makeText(ExamModeActivity.this, "Grading failed (" + res.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ExamResultResponse> call, Throwable t) {
                btnSubmitExam.setEnabled(true);
                progressExam.setVisibility(View.GONE);
                Toast.makeText(ExamModeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showExamResult(ExamResultResponse result) {
        boxExamResult.setVisibility(View.VISIBLE);
        tvExamScore.setText("Score: " + result.getScore());

        // Hiển thị "What was correct" nếu có nội dung
        if (result.getWhatWasCorrect() != null && !result.getWhatWasCorrect().trim().isEmpty()) {
            findViewById(R.id.cardCorrect).setVisibility(View.VISIBLE);
            tvCorrect.setText(result.getWhatWasCorrect());
        } else {
            findViewById(R.id.cardCorrect).setVisibility(View.GONE);
        }

        // Hiển thị "What was incorrect" nếu có nội dung
        if (result.getWhatWasIncorrect() != null && !result.getWhatWasIncorrect().trim().isEmpty()) {
            findViewById(R.id.cardIncorrect).setVisibility(View.VISIBLE);
            tvIncorrect.setText(result.getWhatWasIncorrect());
        } else {
            findViewById(R.id.cardIncorrect).setVisibility(View.GONE);
        }

        // Hiển thị "What could have included" nếu có nội dung
        if (result.getWhatCouldHaveIncluded() != null && !result.getWhatCouldHaveIncluded().trim().isEmpty()) {
            findViewById(R.id.cardCouldInclude).setVisibility(View.VISIBLE);
            tvCould.setText(result.getWhatCouldHaveIncluded());
        } else {
            findViewById(R.id.cardCouldInclude).setVisibility(View.GONE);
        }
    }

    private void hideExamResult() {
        boxExamResult.setVisibility(View.GONE);
        tvExamScore.setText("");
        tvCorrect.setText("");
        tvIncorrect.setText("");
        tvCould.setText("");

        // Ẩn tất cả các card feedback
        findViewById(R.id.cardCorrect).setVisibility(View.GONE);
        findViewById(R.id.cardIncorrect).setVisibility(View.GONE);
        findViewById(R.id.cardCouldInclude).setVisibility(View.GONE);
    }
}
