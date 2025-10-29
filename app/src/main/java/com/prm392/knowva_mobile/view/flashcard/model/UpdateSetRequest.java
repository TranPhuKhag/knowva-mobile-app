package com.prm392.knowva_mobile.view.flashcard.model;

import java.util.List;

public class UpdateSetRequest {
    public String title;
    public String description;
    public String sourceType;
    public String language;
    public String cardType;
    public String visibility;
    public String category;
    public List<Card> flashcards;

    public static class Card {
        public long id;         // thẻ mới => 0
        public String front;
        public String back;
        public String imageUrl; // có thể null
        public int order;       // 1..N (theo vị trí)
    }
}

