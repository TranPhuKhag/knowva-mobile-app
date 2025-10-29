package com.prm392.knowva_mobile.view.flashcard;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.Flashcard;
import java.util.Collections;
import java.util.List;

public class FlashcardPagerAdapter extends RecyclerView.Adapter<FlashcardPagerAdapter.VH> {
    private final List<Flashcard> cards;

    public FlashcardPagerAdapter(List<Flashcard> cards) {
        this.cards = cards != null ? cards : Collections.emptyList();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Flashcard c = cards.get(pos);
        h.bind(c);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private final View faceFront;
        private final View faceBack;
        private final TextView tvFront;
        private final TextView tvBack;
        private final ImageView imgFront;
        private boolean isFront = true;
        private boolean isFlipping = false;

        VH(@NonNull View itemView) {
            super(itemView);
            faceFront = itemView.findViewById(R.id.faceFront);
            faceBack = itemView.findViewById(R.id.faceBack);
            tvFront = itemView.findViewById(R.id.tvFront);
            tvBack = itemView.findViewById(R.id.tvBack);
            imgFront = itemView.findViewById(R.id.imgFront);

            float scale = itemView.getResources().getDisplayMetrics().density;
            itemView.setCameraDistance(8000 * scale);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flip();
                }
            });

            View.OnTouchListener passThroughTouch = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            };

            faceFront.setOnTouchListener(passThroughTouch);
            faceBack.setOnTouchListener(passThroughTouch);
        }

        void bind(Flashcard c) {
            tvFront.setText(c.getFront());
            tvBack.setText(c.getBack());

            if (c.getImageUrl() != null && !c.getImageUrl().isEmpty()) {
                imgFront.setVisibility(View.VISIBLE);
                Glide.with(imgFront.getContext()).load(c.getImageUrl()).into(imgFront);
            } else {
                imgFront.setVisibility(View.GONE);
            }

            isFront = true;
            isFlipping = false;
            faceFront.setVisibility(View.VISIBLE);
            faceBack.setVisibility(View.GONE);
            faceFront.setRotationY(0);
            faceBack.setRotationY(0);
        }

        private void flip() {
            if (isFlipping) {
                return;
            }

            isFlipping = true;
            final View visible = isFront ? faceFront : faceBack;
            final View hidden = isFront ? faceBack : faceFront;

            visible.animate()
                .rotationY(90f)
                .setDuration(150)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        visible.setVisibility(View.GONE);
                        hidden.setRotationY(-90f);
                        hidden.setVisibility(View.VISIBLE);
                        hidden.animate()
                            .rotationY(0f)
                            .setDuration(150)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    isFront = !isFront;
                                    isFlipping = false;
                                }
                            })
                            .start();
                    }
                })
                .start();
        }
    }
}

