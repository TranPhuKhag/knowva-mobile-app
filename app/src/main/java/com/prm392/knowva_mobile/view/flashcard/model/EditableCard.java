package com.prm392.knowva_mobile.view.flashcard.model;

public class EditableCard {
    public long id;           // giữ id cũ; thẻ mới = 0
    public String front = "";
    public String back  = "";
    public String imageUrl;   // optional
    public int order;         // cập nhật theo vị trí adapter (1..N)

    public EditableCard() {
        this.id = 0;
        this.front = "";
        this.back = "";
        this.imageUrl = null;
        this.order = 0;
    }
}

