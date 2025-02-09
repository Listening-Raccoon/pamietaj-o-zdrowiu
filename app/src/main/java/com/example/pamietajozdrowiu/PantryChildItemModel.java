package com.example.pamietajozdrowiu;

public class PantryChildItemModel {
    private final String name;
    private String amount;
    private final String id;
    private final String group;

    public PantryChildItemModel(String name, String amount, String id, String group) {
        this.name = name;
        this.amount = amount;
        this.id = id;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
