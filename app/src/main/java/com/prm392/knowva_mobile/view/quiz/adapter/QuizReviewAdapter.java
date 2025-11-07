package com.prm392.knowva_mobile.view.quiz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.QuizAttemptQuestion;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizReviewAdapter extends RecyclerView.Adapter<QuizReviewAdapter.VH> {

    private List<QuizAttemptQuestion> questions;
    private final LayoutInflater inflater;
    private final Context context;

    public QuizReviewAdapter(Context context) {
        this.context = context;
        this.questions = Collections.emptyList();
        this.inflater = LayoutInflater.from(context);
    }

    public void submitList(List<QuizAttemptQuestion> newQuestions) {
        this.questions = (newQuestions != null) ? newQuestions : Collections.emptyList();
        Collections.sort(this.questions, (a, b) -> Integer.compare(a.order, b.order));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_quiz_review_question, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        h.bind(questions.get(pos), pos + 1);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        LinearLayout llAnswersContainer;

        VH(@NonNull View v) {
            super(v);
            tvQuestionText = v.findViewById(R.id.tv_question_text);
            llAnswersContainer = v.findViewById(R.id.ll_answers_container);
        }

        void bind(QuizAttemptQuestion q, int questionNumber) {
            tvQuestionText.setText(String.format(Locale.getDefault(),
                    "Câu %d: %s", questionNumber, q.questionText));

            llAnswersContainer.removeAllViews();
            if (q.answers == null) return;

            for (QuizAttemptQuestion.Answer ans : q.answers) {
                TextView tvAns = new TextView(context);
                tvAns.setText(ans.answerText);
                tvAns.setTextSize(16f);
                tvAns.setPadding(8, 8, 8, 8);

                boolean isSelected = ans.isSelected != null && ans.isSelected;
                boolean isCorrect = ans.isCorrect != null && ans.isCorrect;

                if (isCorrect) {
                    // Câu trả lời đúng
                    tvAns.setTypeface(null, Typeface.BOLD);
                    tvAns.setTextColor(Color.parseColor("#16a34a")); // Màu xanh lá
                    tvAns.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                } else if (isSelected) {
                    // Câu trả lời sai (do người dùng chọn)
                    tvAns.setTypeface(null, Typeface.BOLD);
                    tvAns.setTextColor(Color.parseColor("#dc2626")); // Màu đỏ
                    tvAns.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close, 0, 0, 0);
                } else {
                    // Câu trả lời bình thường (không chọn, không đúng)
                    tvAns.setTypeface(null, Typeface.NORMAL);
                    tvAns.setTextColor(context.getColor(R.color.on_surface_variant));
                }

                tvAns.setCompoundDrawablePadding(16);
                llAnswersContainer.addView(tvAns);
            }
        }
    }
}