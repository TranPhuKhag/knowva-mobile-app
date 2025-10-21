package com.prm392.knowva_mobile.view.flashcard.model;

import java.util.List;

public class CreateSetRequest {
    public String title;
    public String description;
    public String sourceType;   // "PDF"
    public String language;     // "VIETNAMESE"
    public String cardType;     // "STANDARD"
    public String visibility;   // "PUBLIC" | "PRIVATE"
    public String category;     // "HISTORY" ...
    public List<Card> flashcards;

    public static class Card {
        public String front;
        public String back;
        public int order;
    }
}

