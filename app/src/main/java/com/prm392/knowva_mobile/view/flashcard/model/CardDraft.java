package com.prm392.knowva_mobile.view.flashcard.model;

public class CardDraft {
    public String front = "";
    public String back = "";

    public boolean isValid() {
        return front != null && !front.trim().isEmpty()
                && back != null && !back.trim().isEmpty();
    }
}

