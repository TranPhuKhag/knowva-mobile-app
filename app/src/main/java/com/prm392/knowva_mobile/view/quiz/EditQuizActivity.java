package com.prm392.knowva_mobile.view.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.view.quiz.adapter.QuestionEditorAdapter;
import com.prm392.knowva_mobile.view.quiz.model.QuizAnswerDraft;
import com.prm392.knowva_mobile.view.quiz.model.QuizQuestionDraft;

import java.util.ArrayList;
import java.util.List;

public class EditQuizActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etDesc;
    private RecyclerView rvQuestions;
    private QuestionEditorAdapter adapter;

    private MyQuizSetResponse originalQuiz;
    private ArrayList<QuizQuestionDraft> questionsDraft = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tái sử dụng layout của CreateQuizActivity
        setContentView(R.layout.activity_create_quiz);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chỉnh sửa Quiz"); // Đổi tiêu đề
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                onClickDone();
                return true;
            }
            return false;
        });

        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_desc);
        rvQuestions = findViewById(R.id.rv_questions);

        setupRecyclerView();

        // Lấy dữ liệu quiz gốc
        String quizJson = getIntent().getStringExtra("quiz_json");
        if (quizJson == null) {
            finish();
            return;
        }
        originalQuiz = new Gson().fromJson(quizJson, MyQuizSetResponse.class);
        prefillData();

        findViewById(R.id.fab_add_question).setOnClickListener(v -> {
            adapter.addEmptyQuestion();
            rvQuestions.smoothScrollToPosition(adapter.getItemCount() - 1);
        });
    }

    private void setupRecyclerView() {
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionEditorAdapter(this::onDataChanged);
        rvQuestions.setAdapter(adapter);
    }

    private void prefillData() {
        etTitle.setText(originalQuiz.title);
        etDesc.setText(originalQuiz.description);

        // Chuyển đổi từ Response sang Draft
        for (MyQuizSetResponse.Question q : originalQuiz.questions) {
            QuizQuestionDraft draft = new QuizQuestionDraft();
            draft.questionText = q.questionText;
            draft.answers.clear(); // Xóa 2 câu trả lời rỗng mặc định
            if (q.answers != null) {
                for (MyQuizSetResponse.Answer a : q.answers) {
                    QuizAnswerDraft ansDraft = new QuizAnswerDraft();
                    ansDraft.answerText = a.answerText;
                    ansDraft.isCorrect = a.isCorrect;
                    draft.answers.add(ansDraft);
                }
            }
            questionsDraft.add(draft);
        }
        adapter.submitList(questionsDraft);
    }

    private void onDataChanged() {}

    @Override
    public void onBackPressed() {
        // Cảnh báo nếu có thay đổi
        new MaterialAlertDialogBuilder(this)
                .setMessage("Bạn có chắc muốn hủy các thay đổi?")
                .setNegativeButton("Tiếp tục sửa", null)
                .setPositiveButton("Hủy bỏ", (d, w) -> finish())
                .show();
    }

    private void onClickDone() {
        String title = valueOf(etTitle);
        if (title.isEmpty()) {
            etTitle.setError("Tiêu đề là bắt buộc");
            return;
        }

        List<QuizQuestionDraft> validQuestions = new ArrayList<>();
        for (QuizQuestionDraft q : adapter.getData()) {
            if (q.isValid()) {
                validQuestions.add(q);
            }
        }

        if (validQuestions.isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Thiếu câu hỏi")
                    .setMessage("Bạn phải có ít nhất 1 câu hỏi hợp lệ.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Chuyển sang trang Meta
        Intent intent = new Intent(this, EditQuizMetaActivity.class);
        intent.putExtra("quiz_id", originalQuiz.id);
        intent.putExtra("original_quiz_json", new Gson().toJson(originalQuiz));
        intent.putExtra("title", title);
        intent.putExtra("desc", valueOf(etDesc));
        intent.putExtra("questions_json", new Gson().toJson(validQuestions));
        startActivity(intent);
    }

    private String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}