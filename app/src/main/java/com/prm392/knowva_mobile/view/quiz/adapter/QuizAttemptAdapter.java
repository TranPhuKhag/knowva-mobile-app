package com.prm392.knowva_mobile.view.quiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttemptQuestion;
import java.util.List;

public class QuizAttemptAdapter extends RecyclerView.Adapter<QuizAttemptAdapter.VH> {

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(long questionId, long answerId);
    }

    private final List<QuizAttemptQuestion> questions;
    private final OnAnswerSelectedListener listener;
    private final Context context;

    public QuizAttemptAdapter(Context context, List<QuizAttemptQuestion> questions, OnAnswerSelectedListener listener) {
        this.context = context;
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_quiz_attempt_question, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        h.bind(questions.get(pos));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        ImageView imgQuestion;
        RadioGroup rgAnswers;

        VH(@NonNull View v) {
            super(v);
            tvQuestionText = v.findViewById(R.id.tv_question_text);
            imgQuestion = v.findViewById(R.id.img_question);
            rgAnswers = v.findViewById(R.id.rg_answers);
        }

        void bind(QuizAttemptQuestion q) {
            tvQuestionText.setText(q.questionText);

            if (q.imageUrl != null && !q.imageUrl.isEmpty()) {
                imgQuestion.setVisibility(View.VISIBLE);
                Glide.with(context).load(q.imageUrl).into(imgQuestion);
            } else {
                imgQuestion.setVisibility(View.GONE);
            }

            // Xóa các RadioButton cũ và tạo mới
            rgAnswers.removeAllViews();
            for (QuizAttemptQuestion.Answer ans : q.answers) {
                RadioButton rb = new RadioButton(context);
                rb.setText(ans.answerText);
                rb.setId((int) ans.id); // Dùng ID làm View ID
                rb.setTextSize(18f);
                rb.setPadding(16, 16, 16, 16);
                rgAnswers.addView(rb);
            }

            rgAnswers.setOnCheckedChangeListener((group, checkedId) -> {
                if (listener != null) {
                    listener.onAnswerSelected(q.id, checkedId); // checkedId là answerId
                }
            });
        }
    }
}