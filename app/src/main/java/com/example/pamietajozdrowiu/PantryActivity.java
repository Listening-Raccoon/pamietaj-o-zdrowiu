package com.example.pamietajozdrowiu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import org.jetbrains.annotations.Async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PantryActivity extends AppCompatActivity implements RecyclerViewInterface{

    private Toolbar toolbar;
    private ProgressBar loadingDataProgressBar;
    private TextView nothingToDisplayTextView;
    private RecyclerView recyclerView;
    private List<PantryParentItemModel> parentItems;
    private PantryParentItem_RecyclerviewAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;

    Dialog optionsDialog;
    Button confirmButton;
    Button deleteButton;

    int currentChildPosition;
    List<PantryChildItemModel> currentChildItems;
    boolean deleteButtonClickedOnce = false;

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


        optionsDialog = new Dialog(PantryActivity.this);
        optionsDialog.setContentView(R.layout.pantry_child_options_dialog);
        optionsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionsDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_dialog_background);
        optionsDialog.setCancelable(true);

        confirmButton = optionsDialog.findViewById(R.id.pantry_dialog_confirm_button);
        deleteButton = optionsDialog.findViewById(R.id.pantry_dialog_delete_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText dialogAmount = optionsDialog.findViewById(R.id.pantry_dialog_amount_edittext);
                int amountInt = Integer.parseInt(dialogAmount.getText().toString());

                final EditText dialogGroup = optionsDialog.findViewById(R.id.pantry_dialog_group_edittext);

                HashMap<String, Object> data = new HashMap<>();

                data.put("amount", amountInt);
                data.put("group", dialogGroup.getText().toString().isBlank() ? "" : dialogGroup.getText().toString());
                data.put("name", currentChildItems.get(currentChildPosition).getName());
                data.put("id", currentChildItems.get(currentChildPosition).getId());

                firestore.collection("userData")
                        .document(currentUser.getUid())
                        .collection("medicine")
                        .document(currentChildItems.get(currentChildPosition).getId()).set(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UpdateRecyclerView();
                                optionsDialog.dismiss();
                            }
                        });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deleteButtonClickedOnce) {
                    TextView warningTextView = optionsDialog.findViewById(R.id.pantry_dialog_warning_textview);
                    warningTextView.setVisibility(View.VISIBLE);

                    deleteButtonClickedOnce = true;
                } else {
                    DeleteMedicine(currentChildItems.get(currentChildPosition));
                }
            }
        });
    }

    private void DeleteMedicine(PantryChildItemModel item) {

        firestore.collection("userData").document(currentUser.getUid()).collection("schedule").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final List<ScheduleInstanceModel> instances = new ArrayList<>();
                        final List<ScheduleInstanceModel> instancesToDelete = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String id = document.getString("id");
                            String name = document.getString("name");
                            String date = document.getString("date");
                            String time = document.getString("time");

                            instances.add(new ScheduleInstanceModel(id, name, date, time));
                        }

                        for (ScheduleInstanceModel instance : instances) {
                            if (instance.getName().equals(item.getName())) {
                                instancesToDelete.add(instance);
                            }
                        }

                        DeleteScheduleInstances(instancesToDelete);

                        firestore.collection("userData")
                                .document(currentUser.getUid())
                                .collection("medicine").document(item.getId()).delete();

                        UpdateRecyclerView();
                        optionsDialog.dismiss();
                    }
                });

    }

    private void DeleteScheduleInstances(List<ScheduleInstanceModel> instances) {
        for (ScheduleInstanceModel instance : instances) {
            firestore.collection("userData")
                    .document(currentUser.getUid())
                    .collection("schedule").document(instance.getId()).delete();
        }
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
                        adapter = new PantryParentItem_RecyclerviewAdapter(parentItems, PantryActivity.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        loadingDataProgressBar.setVisibility(View.GONE);
                    }
                });
    }



    @Override
    public void onInstanceClick(int position) {

    }

    @Override
    public void onInstanceClick(int position, List<PantryChildItemModel> childItems) {
        currentChildPosition = position;
        currentChildItems = childItems;

        deleteButtonClickedOnce = false;

        TextView warningTextView = optionsDialog.findViewById(R.id.pantry_dialog_warning_textview);
        warningTextView.setVisibility(View.GONE);

        EditText groupEditText = optionsDialog.findViewById(R.id.pantry_dialog_group_edittext);
        groupEditText.setText(childItems.get(position).getGroup());

        final TextView name = optionsDialog.findViewById(R.id.pantry_dialog_name_textview);
        name.setText(childItems.get(position).getName());

        final EditText amount = optionsDialog.findViewById(R.id.pantry_dialog_amount_edittext);
        amount.setText(childItems.get(position).getAmount());

        Button subButton = optionsDialog.findViewById(R.id.pantry_dialog_sub_button);
        Button addButton = optionsDialog.findViewById(R.id.pantry_dialog_add_button);

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amountInt = Integer.parseInt(amount.getText().toString());
                amountInt--;

                if (amountInt < 0) {
                    amountInt = 0;
                }

                amount.setText(String.valueOf(amountInt));
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amountInt = Integer.parseInt(amount.getText().toString());
                amountInt++;

                amount.setText(String.valueOf(amountInt));
            }
        });

        optionsDialog.show();
    }
}