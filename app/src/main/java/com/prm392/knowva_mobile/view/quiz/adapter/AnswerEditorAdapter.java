package com.prm392.knowva_mobile.view.quiz.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.view.quiz.model.QuizAnswerDraft;

import java.util.List;

public class AnswerEditorAdapter extends RecyclerView.Adapter<AnswerEditorAdapter.VH> {

    private final List<QuizAnswerDraft> data;
    private final Runnable onDataChanged;

    public AnswerEditorAdapter(List<QuizAnswerDraft> data, Runnable onDataChanged) {
        this.data = data;
        this.onDataChanged = onDataChanged;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer_editor, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void removeAt(int position) {
        if (position >= 0 && position < data.size()) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
            onDataChanged.run();
        }
    }

    // Class VH (ViewHolder)
    class VH extends RecyclerView.ViewHolder {
        CheckBox cbCorrect;
        TextInputEditText edtAnswerText;
        ImageButton btnRemove;
        TextWatcher textWatcher;

        VH(@NonNull View v) {
            super(v);
            cbCorrect = v.findViewById(R.id.cb_correct);
            edtAnswerText = v.findViewById(R.id.edt_answer_text);
            btnRemove = v.findViewById(R.id.btn_remove_answer);
        }

        void bind(QuizAnswerDraft answer) {
            // Gỡ listener cũ
            if (textWatcher != null) {
                edtAnswerText.removeTextChangedListener(textWatcher);
            }
            if (cbCorrect.getTag() instanceof CheckBox.OnCheckedChangeListener) {
                cbCorrect.setOnCheckedChangeListener(null);
            }

            // Đặt dữ liệu
            edtAnswerText.setText(answer.answerText);
            cbCorrect.setChecked(answer.isCorrect);

            // Tạo listener mới
            textWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    answer.answerText = s.toString();
                    onDataChanged.run();
                }
                @Override public void afterTextChanged(Editable s) {}
            };

            CheckBox.OnCheckedChangeListener checkListener = (buttonView, isChecked) -> {
                answer.isCorrect = isChecked;
                // Đảm bảo chỉ 1 câu trả lời đúng (bỏ chọn các câu khác)
                if (isChecked) {
                    for (int i = 0; i < data.size(); i++) {
                        if (i != getAdapterPosition()) {
                            data.get(i).isCorrect = false;
                            notifyItemChanged(i, "payload_uncheck"); // Chỉ cập nhật checkbox
                        }
                    }
                }
                onDataChanged.run();
            };

            // Gán listener mới
            edtAnswerText.addTextChangedListener(textWatcher);
            cbCorrect.setOnCheckedChangeListener(checkListener);
            cbCorrect.setTag(checkListener); // Lưu listener để gỡ sau

            btnRemove.setOnClickListener(v -> removeAt(getAdapterPosition()));
        }
    }

    // Tối ưu hóa việc bỏ chọn
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && "payload_uncheck".equals(payloads.get(0))) {
            holder.cbCorrect.setChecked(data.get(position).isCorrect);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }
}