package com.prm392.knowva_mobile.view.quiz.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.view.quiz.model.QuizAnswerDraft;
import com.prm392.knowva_mobile.view.quiz.model.QuizQuestionDraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestionEditorAdapter extends RecyclerView.Adapter<QuestionEditorAdapter.VH> {

    private final List<QuizQuestionDraft> data = new ArrayList<>();
    private final Runnable onDataChanged;

    public QuestionEditorAdapter(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    public void addEmptyQuestion() {
        data.add(new QuizQuestionDraft());
        notifyItemInserted(data.size() - 1);
        onDataChanged.run();
    }

    public List<QuizQuestionDraft> getData() {
        return data;
    }

    private void removeAt(int position) {
        if (position >= 0 && position < data.size()) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
            onDataChanged.run();
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_editor, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView tvHeader;
        TextInputEditText edtQuestionText;
        ImageButton btnRemoveQuestion;
        Button btnAddAnswer;
        RecyclerView rvAnswers;
        AnswerEditorAdapter answerAdapter;
        TextWatcher textWatcher;

        VH(@NonNull View v) {
            super(v);
            tvHeader = v.findViewById(R.id.tv_question_header);
            edtQuestionText = v.findViewById(R.id.edt_question_text);
            btnRemoveQuestion = v.findViewById(R.id.btn_remove_question);
            btnAddAnswer = v.findViewById(R.id.btn_add_answer);
            rvAnswers = v.findViewById(R.id.rv_answers);
        }

        void bind(QuizQuestionDraft question, int position) {
            tvHeader.setText(String.format(Locale.getDefault(), "Câu hỏi %d", position + 1));

            // Gỡ listener cũ
            if (textWatcher != null) {
                edtQuestionText.removeTextChangedListener(textWatcher);
            }

            // Đặt text
            edtQuestionText.setText(question.questionText);

            // Gán listener mới
            textWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    question.questionText = s.toString();
                    onDataChanged.run();
                }
                @Override public void afterTextChanged(Editable s) {}
            };
            edtQuestionText.addTextChangedListener(textWatcher);

            // Nút xóa câu hỏi
            btnRemoveQuestion.setOnClickListener(v -> removeAt(getAdapterPosition()));

            // Setup nested RecyclerView cho câu trả lời
            if (answerAdapter == null) {
                answerAdapter = new AnswerEditorAdapter(question.answers, onDataChanged);
                rvAnswers.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                rvAnswers.setAdapter(answerAdapter);
            } else {
                answerAdapter.notifyDataSetChanged();
            }

            // Nút thêm câu trả lời
            btnAddAnswer.setOnClickListener(v -> {
                question.answers.add(new QuizAnswerDraft());
                answerAdapter.notifyItemInserted(question.answers.size() - 1);
                onDataChanged.run();
            });
        }
    }
}