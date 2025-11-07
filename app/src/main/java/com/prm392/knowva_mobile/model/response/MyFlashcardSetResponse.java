package com.prm392.knowva_mobile.model.response;

import java.util.List;

public class MyFlashcardSetResponse {
    public long id;
    public long userId;
    public String username;
    public String title;
    public String description;
    public String sourceType;
    public String language;
    public String cardType;
    public String visibility;
    public String category;
    public List<Card> flashcards;
    public String accessToken;
    public String createdAt;
    public String updatedAt;

    public static class Card {
        public long id;
        public String front;
        public String back;
        public String imageUrl;
        public int order;
    }
}
