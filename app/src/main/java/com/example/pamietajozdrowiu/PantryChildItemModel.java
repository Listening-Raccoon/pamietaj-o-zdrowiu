package com.example.pamietajozdrowiu;

public class PantryChildItemModel {
    private final String nameTextView;
    private final String amountTextView;
    private final String id;
    private final String group;

    public PantryChildItemModel(String nameTextView, String amountTextView, String id, String group) {
        this.nameTextView = nameTextView;
        this.amountTextView = amountTextView;
        this.id = id;
        this.group = group;
    }

    public String getNameTextView() {
        return nameTextView;
    }

    public String getAmountTextView() {
        return amountTextView;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }
}
