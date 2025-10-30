package com.prm392.knowva_mobile.view.quiz.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizDetailAdapter extends RecyclerView.Adapter<QuizDetailAdapter.VH> {

    private List<MyQuizSetResponse.Question> questions;
    private final LayoutInflater inflater;

    public QuizDetailAdapter(Context context) {
        this.questions = Collections.emptyList();
        this.inflater = LayoutInflater.from(context);
    }

    public void submitList(List<MyQuizSetResponse.Question> newQuestions) {
        this.questions = (newQuestions != null) ? newQuestions : Collections.emptyList();
        // Sắp xếp câu hỏi theo thứ tự (order)
        Collections.sort(this.questions, (a, b) -> Integer.compare(a.order, b.order));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_quiz_question_detail, parent, false);
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

    // Lớp ViewHolder
    class VH extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        ImageView imgQuestion;
        LinearLayout llAnswersContainer;

        VH(@NonNull View v) {
            super(v);
            tvQuestionText = v.findViewById(R.id.tv_question_text);
            imgQuestion = v.findViewById(R.id.img_question);
            llAnswersContainer = v.findViewById(R.id.ll_answers_container);
        }

        void bind(MyQuizSetResponse.Question q, int questionNumber) {
            // 1. Bind câu hỏi
            tvQuestionText.setText(String.format(Locale.getDefault(),
                    "Câu %d: %s", questionNumber, q.questionText));

            // 2. Bind ảnh (nếu có)
            if (q.imageUrl != null && !q.imageUrl.isEmpty()) {
                imgQuestion.setVisibility(View.VISIBLE);
                Glide.with(imgQuestion.getContext()).load(q.imageUrl).into(imgQuestion);
            } else {
                imgQuestion.setVisibility(View.GONE);
            }

            // 3. Bind câu trả lời
            llAnswersContainer.removeAllViews(); // Xóa các view cũ
            if (q.answers != null) {
                for (MyQuizSetResponse.Answer ans : q.answers) {
                    // Tạo một TextView mới cho mỗi câu trả lời
                    TextView tvAns = new TextView(llAnswersContainer.getContext());
                    tvAns.setText(ans.answerText);
                    tvAns.setTextSize(16f);
                    tvAns.setPadding(8, 8, 8, 8);

                    // Đánh dấu câu trả lời đúng
                    if (ans.isCorrect) {
                        tvAns.setTypeface(null, Typeface.BOLD);
                        tvAns.setTextColor(itemView.getContext().getColor(R.color.primary));
                        // Thêm icon check
                        tvAns.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_check, 0, 0, 0);
                        tvAns.setCompoundDrawablePadding(16);
                    } else {
                        tvAns.setTypeface(null, Typeface.NORMAL);
                        tvAns.setTextColor(itemView.getContext().getColor(R.color.on_surface_variant));
                    }

                    llAnswersContainer.addView(tvAns);
                }
            }
        }
    }
}