package com.prm392.knowva_mobile.view.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import java.util.ArrayList;
import java.util.List;

public class SuggestedSetAdapter extends RecyclerView.Adapter<SuggestedSetAdapter.VH> {

    public interface OnItemClick {
        void onClick(MyFlashcardSetResponse set);
    }

    private final List<MyFlashcardSetResponse> data = new ArrayList<>();
    private final OnItemClick listener;

    public SuggestedSetAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void submit(List<MyFlashcardSetResponse> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggest_set, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        MyFlashcardSetResponse s = data.get(pos);
        h.tvTitle.setText(s.title);
        h.tvAuthor.setText(s.username);
        int terms = (s.flashcards == null) ? 0 : s.flashcards.size();
        h.tvTerms.setText(terms + " terms");

        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(s);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvTerms;

        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvAuthor = v.findViewById(R.id.tvAuthor);
            tvTerms = v.findViewById(R.id.tvTerms);
        }
    }
}
