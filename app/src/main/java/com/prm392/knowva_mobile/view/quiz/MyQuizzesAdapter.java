package com.prm392.knowva_mobile.view.quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<Object> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int pos) {
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
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_my_flashcard_set, p, false);
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

            vh.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(s);
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
        VHItem(View v) {
            super(v);
            title = v.findViewById(R.id.tv_title);
            terms = v.findViewById(R.id.tv_terms);
            username = v.findViewById(R.id.tv_username);
        }
    }
}