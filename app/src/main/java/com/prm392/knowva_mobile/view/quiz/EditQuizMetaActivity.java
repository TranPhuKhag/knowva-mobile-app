package com.prm392.knowva_mobile.view.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.request.quiz.CreateQuizRequest;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.repository.QuizRepository;
import com.prm392.knowva_mobile.view.quiz.model.QuizAnswerDraft;
import com.prm392.knowva_mobile.view.quiz.model.QuizQuestionDraft;
import com.prm392.knowva_mobile.repository.QuizRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditQuizMetaActivity extends AppCompatActivity {
    // (Giữ nguyên các mảng Enum)
    private final String[] QUESTION_TYPES = {"MULTIPLE_CHOICE", "TRUE_FALSE", "MIXED"};
    private final String[] CATEGORIES = {"MATHEMATICS", "PHYSICS", "CHEMISTRY", "BIOLOGY", "COMPUTER_SCIENCE", "HISTORY", "GEOGRAPHY", "LITERATURE", "LANGUAGE", "BUSINESS", "ECONOMICS", "PSYCHOLOGY", "MEDICINE", "LAW", "ENGINEERING", "ARTS", "MUSIC", "OTHER"};

    private AutoCompleteTextView actQuestionType, actCategory;
    private RadioButton rbPublic, rbPrivate;
    private TextInputEditText etTimeLimit;
    private QuizRepository quizRepository;

    private String title, desc;
    private List<QuizQuestionDraft> questionsDraft;
    private MyQuizSetResponse originalQuiz;
    private long quizId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tái sử dụng layout
        setContentView(R.layout.activity_create_quiz_meta);

        quizRepository = new QuizRepository(this);

        // Nhận dữ liệu
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        quizId = getIntent().getLongExtra("quiz_id", -1);
        originalQuiz = new Gson().fromJson(getIntent().getStringExtra("original_quiz_json"), MyQuizSetResponse.class);
        Type listType = new TypeToken<List<QuizQuestionDraft>>() {}.getType();
        questionsDraft = new Gson().fromJson(getIntent().getStringExtra("questions_json"), listType);

        // Ánh xạ Views
        rbPublic = findViewById(R.id.rb_public);
        rbPrivate = findViewById(R.id.rb_private); // Thêm
        etTimeLimit = findViewById(R.id.et_time_limit);
        actQuestionType = findViewById(R.id.act_question_type);
        actCategory = findViewById(R.id.act_category);

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cập nhật Cài đặt Quiz"); // Đổi tiêu đề
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                submitUpdate(); // Gọi hàm submit mới
                return true;
            }
            return false;
        });

        // Setup Dropdown Menus
        ArrayAdapter<String> qtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, QUESTION_TYPES);
        actQuestionType.setAdapter(qtAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        actCategory.setAdapter(catAdapter);

        // --- NẠP DỮ LIỆU CŨ ---
        prefillMeta();
    }

    private void prefillMeta() {
        if (originalQuiz == null) return;

        if ("PRIVATE".equals(originalQuiz.description)) {
            rbPrivate.setChecked(true);
        } else {
            rbPublic.setChecked(true);
        }

        etTimeLimit.setText(String.valueOf(originalQuiz.timeLimit));
        // Đặt text cho AutoCompleteTextView (tham số thứ 2 là false để không filter)
        actQuestionType.setText(originalQuiz.questionType, false);
        actCategory.setText(originalQuiz.category, false);
    }

    private void submitUpdate() {
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

        // Chuyển đổi DTO (giống hệt Create)
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

        // --- GỌI API UPDATE ---
        Toast.makeText(this, "Đang cập nhật quiz...", Toast.LENGTH_SHORT).show();
        quizRepository.updateQuizSet(quizId, request).enqueue(new Callback<MyQuizSetResponse>() {
            @Override
            public void onResponse(Call<MyQuizSetResponse> call, Response<MyQuizSetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditQuizMetaActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // Gửi kết quả về QuizDetailActivity
                    Intent intent = new Intent(EditQuizMetaActivity.this, MyQuizzesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish(); // Đóng Activity này

                    // TODO: Cần 1 cơ chế để đóng cả EditQuizActivity
                    // (Tạm thời người dùng sẽ quay lại EditQuizActivity, rồi tự back)

                } else {
                    String errorMsg = "Lỗi (" + response.code() + ")";
                    // (Code xử lý lỗi tương tự)
                    Toast.makeText(EditQuizMetaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyQuizSetResponse> call, Throwable t) {
                Toast.makeText(EditQuizMetaActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String textOf(EditText editText, String defaultValue) {
        String text = editText.getText() == null ? "" : editText.getText().toString().trim();
        return text.isEmpty() ? defaultValue : text;
    }
}