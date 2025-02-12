package com.example.pamietajozdrowiu;

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

            contentTextView.setVisibility(View.GONE);
            warningTextView.setVisibility(View.GONE);


            deleteClickedOnce = false;

            titleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (contentTextView.getVisibility() == View.GONE) {
                        contentTextView.setVisibility(View.VISIBLE);
                    }
                    else if (contentTextView.getVisibility() == View.VISIBLE) {
                        contentTextView.setVisibility(View.GONE);
                    }
                }
            });

            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!deleteClickedOnce) {
                        warningTextView.setVisibility(View.VISIBLE);
                        deleteClickedOnce = true;
                    }
                    else {
                        DeleteInstanceFromDatabase(getAdapterPosition());
                        historyInstances.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        warningTextView.setVisibility(View.GONE);
                        deleteClickedOnce = false;
                    }
                }
            });
        }
    }

    private void DeleteInstanceFromDatabase(int adapterPosition) {
        firestore.collection("userData")
                .document(currentUser.getUid())
                .collection("history")
                .document(historyInstances.get(adapterPosition).getId()).delete();
    }
}
