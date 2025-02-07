package com.example.pamietajozdrowiu;

public class PantryChildItemModel {
    private final String nameTextView;
    private final String amountTextView;

    public PantryChildItemModel(String nameTextView, String amountTextView) {
        this.nameTextView = nameTextView;
        this.amountTextView = amountTextView;
    }

    public String getNameTextView() {
        return nameTextView;
    }

    public String getAmountTextView() {
        return amountTextView;
    }
}
