package com.example.pamietajozdrowiu;

import java.util.List;

public interface RecyclerViewInterface {
    void onInstanceClick(int position);

    void onInstanceClick(int position, List<PantryChildItemModel> childItems);
}