package com.prm392.knowva_mobile.view.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.request.quiz.CreateQuizRequest;
import com.prm392.knowva_mobile.repository.QuizRepository;
import com.prm392.knowva_mobile.view.HomeActivity;
import com.prm392.knowva_mobile.view.quiz.model.QuizAnswerDraft;
import com.prm392.knowva_mobile.view.quiz.model.QuizQuestionDraft;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateQuizMetaActivity extends AppCompatActivity {
    // Enum values từ API
    private final String[] QUESTION_TYPES = {"MULTIPLE_CHOICE", "TRUE_FALSE", "MIXED"};
    private final String[] CATEGORIES = {"MATHEMATICS", "PHYSICS", "CHEMISTRY", "BIOLOGY", "COMPUTER_SCIENCE", "HISTORY", "GEOGRAPHY", "LITERATURE", "LANGUAGE", "BUSINESS", "ECONOMICS", "PSYCHOLOGY", "MEDICINE", "LAW", "ENGINEERING", "ARTS", "MUSIC", "OTHER"};

    private AutoCompleteTextView actQuestionType, actCategory;
    private RadioButton rbPublic;
    private TextInputEditText etTimeLimit;
    private QuizRepository quizRepository;

    private String title, desc;
    private List<QuizQuestionDraft> questionsDraft;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz_meta);

        quizRepository = new QuizRepository(this);

        // Nhận dữ liệu từ CreateQuizActivity
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        Type listType = new TypeToken<List<QuizQuestionDraft>>() {}.getType();
        questionsDraft = new Gson().fromJson(getIntent().getStringExtra("questions_json"), listType);

        // Ánh xạ Views
        rbPublic = findViewById(R.id.rb_public);
        etTimeLimit = findViewById(R.id.et_time_limit);
        actQuestionType = findViewById(R.id.act_question_type);
        actCategory = findViewById(R.id.act_category);

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                submitQuiz();
                return true;
            }
            return false;
        });

        // Setup Dropdown Menus
        ArrayAdapter<String> qtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, QUESTION_TYPES);
        actQuestionType.setAdapter(qtAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        actCategory.setAdapter(catAdapter);
    }

    private void submitQuiz() {
        CreateQuizRequest request = new CreateQuizRequest();
        request.title = title;
        request.description = desc;
        request.visibility = rbPublic.isChecked() ? "PUBLIC" : "PRIVATE";
        request.questionType = textOf(actQuestionType, "MULTIPLE_CHOICE");
        request.category = textOf(actCategory, "OTHER");

        try {
            request.timeLimit = Integer.parseInt(textOf(etTimeLimit, "0"));
        } catch (NumberFormatException e) {
            request.timeLimit = 0;
        }

        // Chuyển đổi DTO nháp sang DTO của request
        request.questions = new ArrayList<>();
        int order = 1;
        for (QuizQuestionDraft draft : questionsDraft) {
            CreateQuizRequest.Question q = new CreateQuizRequest.Question();
            q.questionText = draft.questionText;
            q.order = order++;
            q.answers = new ArrayList<>();
            for (QuizAnswerDraft ansDraft : draft.answers) {
                if (ansDraft.isValid()) {
                    CreateQuizRequest.Answer a = new CreateQuizRequest.Answer();
                    a.answerText = ansDraft.answerText;
                    a.isCorrect = ansDraft.isCorrect;
                    q.answers.add(a);
                }
            }
            request.questions.add(q);
        }
        request.maxQuestions = request.questions.size();

        // Gọi API
        Toast.makeText(this, "Đang tạo quiz...", Toast.LENGTH_SHORT).show();
        quizRepository.saveQuizSet(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateQuizMetaActivity.this, "Tạo quiz thành công!", Toast.LENGTH_SHORT).show();
                    // Về Home và xóa các activity tạo quiz/flashcard
                    Intent intent = new Intent(CreateQuizMetaActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Lỗi (" + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) { /* Bỏ qua */ }
                    Log.e("CreateQuizMeta", errorMsg);
                    Toast.makeText(CreateQuizMetaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CreateQuizMeta", "Lỗi mạng: " + t.getMessage(), t);
                Toast.makeText(CreateQuizMetaActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String textOf(EditText editText, String defaultValue) {
        String text = editText.getText() == null ? "" : editText.getText().toString().trim();
        return text.isEmpty() ? defaultValue : text;
    }
}