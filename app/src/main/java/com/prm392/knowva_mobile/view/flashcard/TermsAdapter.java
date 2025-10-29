package com.prm392.knowva_mobile.view.flashcard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.Flashcard;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class TermsAdapter extends RecyclerView.Adapter<TermsAdapter.VH> {

    public interface OnItemClick {
        void onClick(int position);
    }

    private final List<Flashcard> data;
    private final OnItemClick onItemClick;
    private int selected = 0;

    public TermsAdapter(List<Flashcard> data, OnItemClick onItemClick) {
        this.data = data;
        this.onItemClick = onItemClick;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_term, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Flashcard c = data.get(pos);
        h.tvFront.setText(c.getFront());
        h.tvBack.setText(c.getBack());

        if (c.getImageUrl() != null && !c.getImageUrl().isEmpty()) {
            h.img.setVisibility(View.VISIBLE);
            Glide.with(h.img.getContext()).load(c.getImageUrl()).into(h.img);
        } else {
            h.img.setVisibility(View.GONE);
        }

        // highlight item đang xem
        boolean isSelected = pos == selected;
        h.card.setStrokeWidth(isSelected ? 2 : 1);
        h.card.setStrokeColor(h.card.getContext()
                .getResources()
                .getColor(isSelected ? R.color.primary : R.color.outline, null));

        final int position = pos;
        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClick != null) {
                    onItemClick.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setSelected(int position) {
        int old = selected;
        selected = position;
        notifyItemChanged(old);
        notifyItemChanged(selected);
    }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvFront, tvBack;
        ImageView img;

        VH(@NonNull View v) {
            super(v);
            card = (MaterialCardView) v;
            tvFront = v.findViewById(R.id.tvFrontTerm);
            tvBack = v.findViewById(R.id.tvBackTerm);
            img = v.findViewById(R.id.imgTerm);
        }
    }
}
