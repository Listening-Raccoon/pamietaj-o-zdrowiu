package com.example.pamietajozdrowiu;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HistoryInstance_RecyclerviewAdapter extends RecyclerView.Adapter<HistoryInstance_RecyclerviewAdapter.ViewHolder> {
    List<HistoryInstanceModel> historyInstances;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;

    public HistoryInstance_RecyclerviewAdapter(List<HistoryInstanceModel> historyInstances) {
        this.historyInstances = historyInstances;
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_instance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTextView.setText(historyInstances.get(position).getTitle());
        holder.dateTextView.setText(historyInstances.get(position).getDate());
        holder.contentTextView.setText(historyInstances.get(position).getContent());

        holder.contentTextView.setVisibility(View.GONE);
        holder.warningTextView.setVisibility(View.GONE);

        holder.titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.contentTextView.getVisibility() == View.GONE) {
                    holder.contentTextView.setVisibility(View.VISIBLE);
                }
                else if (holder.contentTextView.getVisibility() == View.VISIBLE) {
                    holder.contentTextView.setVisibility(View.GONE);
                }
            }
        });

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.deleteClickedOnce) {
                    holder.warningTextView.setVisibility(View.VISIBLE);
                    holder.deleteClickedOnce = true;
                }
                else {
                    DeleteInstanceFromDatabase(holder.getAdapterPosition());
                    historyInstances.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    holder.warningTextView.setVisibility(View.GONE);
                    holder.deleteClickedOnce = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyInstances.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView contentTextView;
        TextView warningTextView;
        ImageView deleteImageView;
        boolean deleteClickedOnce;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.history_instance_title);
            dateTextView = itemView.findViewById(R.id.history_instance_date);
            contentTextView = itemView.findViewById(R.id.history_instance_content);
            deleteImageView = itemView.findViewById(R.id.history_instance_delete);
            warningTextView = itemView.findViewById(R.id.history_warning_textview);

            deleteClickedOnce = false;
        }
    }

    private void DeleteInstanceFromDatabase(int adapterPosition) {
        firestore.collection("userData")
                .document(currentUser.getUid())
                .collection("history")
                .document(historyInstances.get(adapterPosition).getId()).delete();
    }
}
