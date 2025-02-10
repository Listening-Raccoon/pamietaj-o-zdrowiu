package com.example.pamietajozdrowiu;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LogActivity extends AppCompatActivity {

    Toolbar toolbar;
    CalendarView calendarView;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;

    List<ImageView> ratings;
    int selectedRating;

    HashMap<Date, Integer> downloadedRatings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log);

        ratings = List.of(findViewById(R.id.log_rating_1),
                findViewById(R.id.log_rating_2),
                findViewById(R.id.log_rating_3),
                findViewById(R.id.log_rating_4),
                findViewById(R.id.log_rating_5));

        selectedRating = -1;

        ratings.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView rating: ratings) {
                    rating.getBackground().mutate().setColorFilter(getResources()
                            .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
                }
                v.getBackground().mutate().setColorFilter(getResources()
                        .getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
                selectedRating = 0;
            }
        });

        ratings.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView rating: ratings) {
                    rating.getBackground().mutate().setColorFilter(getResources()
                            .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
                }
                v.getBackground().mutate().setColorFilter(getResources()
                        .getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
                selectedRating = 1;
            }
        });

        ratings.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView rating: ratings) {
                    rating.getBackground().mutate().setColorFilter(getResources()
                            .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
                }
                v.getBackground().mutate().setColorFilter(getResources()
                        .getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
                selectedRating = 2;
            }
        });

        ratings.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView rating: ratings) {
                    rating.getBackground().mutate().setColorFilter(getResources()
                            .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
                }
                v.getBackground().mutate().setColorFilter(getResources()
                        .getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
                selectedRating = 3;
            }
        });

        ratings.get(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView rating: ratings) {
                    rating.getBackground().mutate().setColorFilter(getResources()
                            .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
                }
                v.getBackground().mutate().setColorFilter(getResources()
                        .getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
                selectedRating = 4;
            }
        });

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        calendarView = findViewById(R.id.calendar_view);

        calendarView.setMaxDate(System.currentTimeMillis() - 1000);


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

    @Override
    protected void onResume() {
        super.onResume();
        selectedRating = -1;
        for (ImageView rating: ratings) {
            rating.getBackground().mutate().setColorFilter(getResources()
                    .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void SetRating(int position) {
        for (ImageView rating: ratings) {
            rating.getBackground().mutate().setColorFilter(getResources()
                    .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
        }
        ratings.get(position).getBackground().mutate().setColorFilter(getResources()
                .getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
    }

    private void GetRatingsFromDatabase() {
        firestore.collection("userData").document(currentUser.getUid()).collection("ratings").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        
                    }
                });
    }
}