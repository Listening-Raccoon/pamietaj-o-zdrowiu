package com.example.pamietajozdrowiu;

public class MedicineModel {
    final String id;
    final String name;
    int amount;

    public MedicineModel(String id, String name, int amount) {
        this.name = name;
        this.amount = amount;
        this.id = id;

    }

    public MedicineModel(String id, String name) {
        this.name = name;
        this.amount = 0;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
