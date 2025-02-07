package com.example.pamietajozdrowiu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PantryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar loadingDataProgressBar;
    private TextView nothingToDisplayTextView;
    private RecyclerView recyclerView;
    private List<PantryParentItemModel> parentItems;
    private PantryParentItem_RecyclerviewAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        loadingDataProgressBar = findViewById(R.id.loading_data_progressbar);
        nothingToDisplayTextView = findViewById(R.id.nothing_to_display_textview);
        recyclerView = findViewById(R.id.pantry_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        UpdateRecyclerView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void UpdateRecyclerView() {
        loadingDataProgressBar.setVisibility(View.VISIBLE);
        nothingToDisplayTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);



        firestore.collection("userData").document(currentUser.getUid()).collection("medicine").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final List<PantryChildItemModel> childItems = new ArrayList<>();
                        final List<PantryParentItemModel> parentItems = new ArrayList<>();
                        final List<String> groups = new ArrayList<>();



                        for (DocumentSnapshot document: task.getResult().getDocuments()) {
                            String id = document.getString("id");
                            String name = document.getString("name");
                            String amount = document.getLong("amount").toString();
                            String group = document.getString("group");

                            childItems.add(new PantryChildItemModel(name, amount, id, group));
                            if (!Objects.equals(group, "") && !groups.contains(group)) {
                                groups.add(group);
                            }
                        }

                        if (childItems.isEmpty()) {
                            nothingToDisplayTextView.setVisibility(View.VISIBLE);
                            loadingDataProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        List<PantryChildItemModel> unassignedChildItems = new ArrayList<>();
                        for (PantryChildItemModel childItem: childItems) {
                            if (childItem.getGroup().isEmpty()) {
                                unassignedChildItems.add(childItem);
                            }
                        }
                        parentItems.add(new PantryParentItemModel(unassignedChildItems, "Unassigned"));

                        for (String group: groups) {
                            List<PantryChildItemModel> groupChildItems = new ArrayList<>();

                            for (PantryChildItemModel childItem: childItems) {
                                if (childItem.getGroup().equals(group)) {
                                    groupChildItems.add(childItem);
                                }
                            }
                            parentItems.add(new PantryParentItemModel(groupChildItems, group));
                        }
                        adapter = new PantryParentItem_RecyclerviewAdapter(parentItems);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        loadingDataProgressBar.setVisibility(View.GONE);
                    }
                });
    }
}