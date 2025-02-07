package com.example.pamietajozdrowiu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryParentItem_RecyclerviewAdapter extends RecyclerView.Adapter<PantryParentItem_RecyclerviewAdapter.ViewHolder> {

    final private List<PantryParentItemModel> parentItems;
    private List<PantryChildItemModel> childItems = new ArrayList<>();

    public PantryParentItem_RecyclerviewAdapter(List<PantryParentItemModel> parentItems) {
        this.parentItems = parentItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pantry_parent_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryParentItemModel parentItem = parentItems.get(position);
        holder.sectionNameTextView.setText(parentItem.getSectionName());

        boolean isExpanded = parentItem.isExpanded();
        holder.childLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        if (isExpanded) {
            holder.sectionArrowImage.setVisibility(View.GONE);
            holder.sectionArrowImage.setImageResource(R.drawable.baseline_arrow_up_24);
            holder.sectionArrowImage.setVisibility(View.VISIBLE);
        } else {
            holder.sectionArrowImage.setVisibility(View.GONE);
            holder.sectionArrowImage.setImageResource(R.drawable.baseline_arrow_down_24);
            holder.sectionArrowImage.setVisibility(View.VISIBLE);
        }

        PantryChildItem_RecyclerviewAdapter childAdapter = new PantryChildItem_RecyclerviewAdapter(childItems);
        holder.childRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.childRecyclerView.setHasFixedSize(true);
        holder.childRecyclerView.setAdapter(childAdapter);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentItem.setExpanded(!isExpanded);
                childItems = parentItem.getChildItems();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return parentItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout parentLayout;
        private final RelativeLayout childLayout;
        private final TextView sectionNameTextView;
        private final ImageView sectionArrowImage;
        private final RecyclerView childRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            childLayout = itemView.findViewById(R.id.child_layout);
            sectionNameTextView = itemView.findViewById(R.id.section_name_textview);
            sectionArrowImage = itemView.findViewById(R.id.section_arrow_image);
            childRecyclerView = itemView.findViewById(R.id.child_recyclerview);
        }
    }
}
