package com.example.pamietajozdrowiu;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LogActivity extends AppCompatActivity {

    Toolbar toolbar;
    CalendarView calendarView;
    ProgressBar progressBar;
    TextView nothingToDisplayTextView;
    LinearLayout ratingsLayout;
    Button saveButton;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;

    List<ImageView> ratings;
    int selectedRating;

    String selectedDate;

    HashMap<String, Integer> downloadedRatings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log);
        downloadedRatings = new HashMap<>();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        calendarView = findViewById(R.id.calendar_view);

        calendarView.setMaxDate(System.currentTimeMillis() - 1000);

        ratingsLayout = findViewById(R.id.log_ratings_layout);
        ratings = List.of(findViewById(R.id.log_rating_1),
                findViewById(R.id.log_rating_2),
                findViewById(R.id.log_rating_3),
                findViewById(R.id.log_rating_4),
                findViewById(R.id.log_rating_5));

        for (ImageView rating: ratings) {
            rating.setClickable(true);
        }

        progressBar = findViewById(R.id.loading_data_progressbar);
        nothingToDisplayTextView = findViewById(R.id.nothing_to_display_textview);

        saveButton = findViewById(R.id.log_save_button);

        selectedRating = -1;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        selectedDate = dateFormat.format(date);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedRating != -1) {
                    for (ImageView rating: ratings) {
                        rating.setClickable(false);
                    }
                    SaveRating(selectedDate, selectedRating);
                    downloadedRatings.put(selectedDate, selectedRating);
                    saveButton.setVisibility(View.GONE);
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String month = (i1 + 1) < 10 ? "0" + (i1 + 1) : Integer.toString(i1 + 1);
                String day = i2 < 10 ? "0" + i2 : Integer.toString(i2);
                selectedDate = i + "/" + month + "/" + day;

                if (!downloadedRatings.isEmpty() && downloadedRatings.containsKey(selectedDate)) {
                    for (ImageView rating: ratings) {
                        rating.setClickable(false);
                    }
                }

                SetRatingOfDay();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar.setVisibility(View.VISIBLE);
        nothingToDisplayTextView.setVisibility(View.GONE);
        ratingsLayout.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        GetRatingsFromDatabase();
    }

    private void SaveRating(String date, int rating) {
        HashMap<String, Object> map = new HashMap<>();
        String id = UUID.randomUUID().toString();
        map.put("id", id);
        map.put("date", date);
        map.put("rating", rating);

        firestore.collection("userData")
                .document(currentUser.getUid())
                .collection("ratings").document(id).set(map);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarView.setDate(new Date().getTime());
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        selectedDate = dateFormat.format(date);
        selectedRating = -1;
        for (ImageView rating: ratings) {
            rating.getBackground().mutate().setColorFilter(getResources()
                    .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
        }
        for (ImageView rating: ratings) {
            rating.setClickable(true);
        }
        GetRatingsFromDatabase();
    }

    private void SetRating(int position) {
        for (ImageView rating: ratings) {
            rating.getBackground().mutate().setColorFilter(getResources()
                    .getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
        }
        ratings.get(position).getBackground().mutate().setColorFilter(getResources()
                .getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
    }

    private void SetRatingOfDay() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String todayDate = dateFormat.format(date);

        if (selectedDate.equals(todayDate)) {
            if (downloadedRatings.containsKey(selectedDate)) {
                progressBar.setVisibility(View.GONE);
                nothingToDisplayTextView.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                ratingsLayout.setVisibility(View.VISIBLE);
                SetRating(downloadedRatings.get(selectedDate));
                for (ImageView rating: ratings) {
                    rating.setClickable(false);
                }
            }
            else {
                progressBar.setVisibility(View.GONE);
                nothingToDisplayTextView.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
                ratingsLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (downloadedRatings.containsKey(selectedDate)) {
                progressBar.setVisibility(View.GONE);
                nothingToDisplayTextView.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                ratingsLayout.setVisibility(View.VISIBLE);
                SetRating(downloadedRatings.get(selectedDate));
                for (ImageView rating: ratings) {
                    rating.setClickable(true);
                }
            }
            else {
                progressBar.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                ratingsLayout.setVisibility(View.GONE);
                nothingToDisplayTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void GetRatingsFromDatabase() {
        firestore.collection("userData").document(currentUser.getUid()).collection("ratings").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String date = document.getString("date");
                            int rating = document.getLong("rating").intValue();

                            downloadedRatings.put(date, rating);
                        }

                        SetRatingOfDay();
                    }
                });
    }
}