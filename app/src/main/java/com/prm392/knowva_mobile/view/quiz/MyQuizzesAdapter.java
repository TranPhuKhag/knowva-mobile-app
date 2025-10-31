package com.prm392.knowva_mobile.view.quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;

import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyQuizzesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Object> data = new ArrayList<>();
    private static final int T_HEADER = 0, T_ITEM = 1;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MyQuizSetResponse set);
        void onDeleteClick(MyQuizSetResponse set, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<Object> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < data.size()) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemViewType(int pos) {
        // Cẩn thận: kiểm tra data.get(pos) có null không
        if (pos < 0 || pos >= data.size() || data.get(pos) == null) {
            return T_ITEM; // Hoặc một kiểu mặc định
        }
        return (data.get(pos) instanceof String) ? T_HEADER : T_ITEM;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        if (vt == T_HEADER) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_month_header, p, false);
            return new VHHeader(v);
        } else {
            // Tái sử dụng layout của flashcard
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_my_quiz_set, p, false);
            return new VHItem(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        if (getItemViewType(pos) == T_HEADER) {
            ((VHHeader) h).tv.setText((String) data.get(pos));
        } else {
            MyQuizSetResponse s = (MyQuizSetResponse) data.get(pos);
            VHItem vh = (VHItem) h;
            vh.title.setText(s.title);
            // Hiển thị số lượng câu hỏi
            vh.terms.setText(String.format(Locale.getDefault(), "%d questions", s.getQuestionCount()));
            vh.username.setText(s.username);

            vh.btnDelete.setVisibility(View.VISIBLE);

            vh.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(s);
                }
            });

            vh.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    // Lấy vị trí adapter hiện tại
                    listener.onDeleteClick(s, h.getAdapterPosition());
                }
            });
        }
    }

    static class VHHeader extends RecyclerView.ViewHolder {
        TextView tv;
        VHHeader(View v) {
            super(v);
            tv = v.findViewById(R.id.tv_month);
        }
    }

    static class VHItem extends RecyclerView.ViewHolder {
        TextView title, terms, username;
        ImageButton btnDelete;
        VHItem(View v) {
            super(v);
            title = v.findViewById(R.id.tv_title);
            terms = v.findViewById(R.id.tv_terms);
            username = v.findViewById(R.id.tv_username);
            btnDelete = v.findViewById(R.id.btn_delete_quiz);
        }
    }
}