package com.example.pamietajozdrowiu;

import java.util.List;

public class PantryParentItemModel {

    final private List<PantryChildItemModel> childItems;
    final private String sectionName;
    private boolean isExpanded;

    public PantryParentItemModel(List<PantryChildItemModel> childItems, String sectionName) {
        this.childItems = childItems;
        this.sectionName = sectionName;
        this.isExpanded = false;
    }

    public List<PantryChildItemModel> getChildItems() {
        return childItems;
    }

    public String getSectionName() {
        return sectionName;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
