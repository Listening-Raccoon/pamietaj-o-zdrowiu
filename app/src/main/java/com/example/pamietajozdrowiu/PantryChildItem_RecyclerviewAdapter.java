package com.example.pamietajozdrowiu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryChildItem_RecyclerviewAdapter extends RecyclerView.Adapter<PantryChildItem_RecyclerviewAdapter.ViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    private final List<PantryChildItemModel> childItems;

    public PantryChildItem_RecyclerviewAdapter(List<PantryChildItemModel> childItems, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.childItems = childItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pantry_child_item, parent, false);
        return new ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTextView.setText(childItems.get(position).getName());
        holder.amountTextView.setText(childItems.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return childItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout childLayout;
        TextView nameTextView;
        TextView amountTextView;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.pantry_child_item_name_text);
            amountTextView = itemView.findViewById(R.id.pantry_child_item_amount_text);
            childLayout = itemView.findViewById(R.id.child_layout);

            childLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onInstanceClick(position, childItems);
                        }
                    }
                }
            });
        }
    }
}
