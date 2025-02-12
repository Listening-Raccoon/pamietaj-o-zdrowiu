package com.example.pamietajozdrowiu;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    CardView instanceCreate;
    FloatingActionButton addButton;
    ProgressBar progressBar;
    TextView nothingToDisplayTextView;
    RecyclerView recyclerView;

    Animation openAnimation;
    Animation closeAnimation;

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

        openAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_open);
        closeAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_close);

        instanceCreate.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        nothingToDisplayTextView.setVisibility(View.GONE);




        addButton.setOnClickListener(new View.OnClickListener() {
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

