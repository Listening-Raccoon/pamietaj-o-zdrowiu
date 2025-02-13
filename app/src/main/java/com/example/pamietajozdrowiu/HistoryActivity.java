package com.example.pamietajozdrowiu;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    CardView instanceCreate;
    FloatingActionButton addButton;
    ProgressBar progressBar;
    TextView nothingToDisplayTextView, dateTextView;
    RecyclerView recyclerView;
    EditText titleEditText, contentEditText;
    ImageView saveImageView;

    Animation openAnimation;
    Animation closeAnimation;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;

    List<HistoryInstanceModel> historyInstances;
    HistoryInstance_RecyclerviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        toolbar = findViewById(R.id.toolbar);
        instanceCreate = findViewById(R.id.history_instance_create);
        addButton = findViewById(R.id.history_add_button);
        progressBar = findViewById(R.id.loading_data_progressbar);
        nothingToDisplayTextView = findViewById(R.id.nothing_to_display_textview);
        recyclerView = findViewById(R.id.history_recyclerview);
        titleEditText = findViewById(R.id.history_instance_title);
        contentEditText = findViewById(R.id.history_instance_content);
        saveImageView = findViewById(R.id.history_instance_save);
        dateTextView = findViewById(R.id.history_instance_date);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        openAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_open);
        closeAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_close);

        instanceCreate.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        nothingToDisplayTextView.setVisibility(View.GONE);




        addButton.setOnClickListener(new View.OnClickListener() {
            final Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            final String dateString = dateFormat.format(date);


            @Override
            public void onClick(View v) {
                if (instanceCreate.getVisibility() == View.GONE) {
                    addButton.startAnimation(openAnimation);
                    instanceCreate.setVisibility(View.VISIBLE);
                }
                else if (instanceCreate.getVisibility() == View.VISIBLE) {
                    addButton.startAnimation(closeAnimation);
                    instanceCreate.setVisibility(View.GONE);
                }
            }
        });

        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleEditText.getText().toString().isEmpty()) {
                    titleEditText.getBackground().mutate().setColorFilter(getResources()
                            .getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                    titleEditText.setHintTextColor(getResources()
                            .getColor(android.R.color.holo_red_light));
                }
                else {
                    titleEditText.getBackground().mutate().setColorFilter(getResources()
                            .getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
                    titleEditText.setHintTextColor(getResources()
                            .getColor(android.R.color.darker_gray));
                    SaveInstanceToDatabase();
                    instanceCreate.setVisibility(View.GONE);
                    addButton.startAnimation(closeAnimation);
                }
            }
        });

        GetHistoryFromDatabase();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void SaveInstanceToDatabase() {
        String id = UUID.randomUUID().toString();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = dateFormat.format(date);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", titleEditText.getText().toString());
        map.put("content", contentEditText.getText().toString());
        map.put("date", dateString);

        firestore.collection("userData")
                .document(currentUser.getUid())
                .collection("history").document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        GetHistoryFromDatabase();
                    }
                });
    }

    private void GetHistoryFromDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        nothingToDisplayTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        historyInstances = new ArrayList<>();

        firestore.collection("userData").document(currentUser.getUid()).collection("history").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String id = document.getString("id");
                            String title = document.getString("title");
                            String content = document.getString("content");
                            String date = document.getString("date");

                            historyInstances.add(new HistoryInstanceModel(id, date, title, content));
                        }

                        FillRecyclerView();
                    }
                });
    }

    private void FillRecyclerView() {
        progressBar.setVisibility(View.GONE);
        if (historyInstances.isEmpty()) {
            nothingToDisplayTextView.setVisibility(View.VISIBLE);
        }
        else {
            adapter = new HistoryInstance_RecyclerviewAdapter(historyInstances);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

