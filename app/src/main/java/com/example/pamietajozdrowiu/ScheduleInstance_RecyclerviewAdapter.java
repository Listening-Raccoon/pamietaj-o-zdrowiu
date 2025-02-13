package com.example.pamietajozdrowiu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleInstance_RecyclerviewAdapter extends RecyclerView.Adapter<ScheduleInstance_RecyclerviewAdapter.ViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    private List<ScheduleInstanceModel> instances;

    public ScheduleInstance_RecyclerviewAdapter(List<ScheduleInstanceModel> instances, RecyclerViewInterface recyclerViewInterface) {
        this.instances = instances;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public ScheduleInstance_RecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instances_recyclerview_item, parent, false);

        return new ScheduleInstance_RecyclerviewAdapter.ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleInstance_RecyclerviewAdapter.ViewHolder holder, int position) {
        holder.nameTextView.setText(instances.get(position).getName());
        holder.timeTextView.setText(instances.get(position).getTime());
        //holder.instanceIdTextView.setText(instances.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return instances.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, timeTextView, instanceIdTextView;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_textview);
            timeTextView = itemView.findViewById(R.id.time_textview);
            //instanceIdTextView = itemView.findViewById(R.id.instance_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onInstanceClick(position);
                        }
                    }
                }
            });
        }
    }
}
