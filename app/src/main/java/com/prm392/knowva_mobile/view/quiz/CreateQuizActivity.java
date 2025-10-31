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
import com.prm392.knowva_mobile.view.quiz.adapter.QuestionEditorAdapter;
import com.prm392.knowva_mobile.view.quiz.model.QuizQuestionDraft;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etDesc;
    private RecyclerView rvQuestions;
    private QuestionEditorAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Dùng onBackPressed()
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

        // Mặc định thêm 1 câu hỏi trống
        adapter.addEmptyQuestion();

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

    // Callback này được gọi mỗi khi text trong adapter thay đổi
    private void onDataChanged() {
        // Có thể dùng để bật/tắt nút "Done" trong tương lai
    }

    private boolean hasAnyInput() {
        if (!TextUtils.isEmpty(etTitle.getText()) || !TextUtils.isEmpty(etDesc.getText())) {
            return true;
        }
        for (QuizQuestionDraft q : adapter.getData()) {
            if (!TextUtils.isEmpty(q.questionText)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!hasAnyInput()) {
            super.onBackPressed();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setMessage("Bạn có chắc muốn hủy bản nháp quiz này?")
                .setNegativeButton("Tiếp tục sửa", null)
                .setPositiveButton("Hủy bỏ", (d, w) -> finish())
                .show();
    }

    private void onClickDone() {
        // 1. Validate Tiêu đề
        String title = valueOf(etTitle);
        if (title.isEmpty()) {
            etTitle.setError("Tiêu đề là bắt buộc");
            etTitle.requestFocus();
            return;
        }

        // 2. Filter các câu hỏi hợp lệ
        List<QuizQuestionDraft> validQuestions = new ArrayList<>();
        for (QuizQuestionDraft q : adapter.getData()) {
            if (q.isValid()) {
                validQuestions.add(q);
            }
        }

        // 3. Validate số lượng câu hỏi
        if (validQuestions.isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Thiếu câu hỏi")
                    .setMessage("Bạn phải thêm ít nhất 1 câu hỏi hợp lệ.\n(Một câu hỏi hợp lệ phải có nội dung, ít nhất 2 câu trả lời, và 1 câu trả lời đúng)")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // 4. Chuyển sang trang Meta
        Intent intent = new Intent(this, CreateQuizMetaActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("desc", valueOf(etDesc));
        intent.putExtra("questions_json", new Gson().toJson(validQuestions));
        startActivity(intent);
    }

    private String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}