package com.example.pamietajozdrowiu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CalendarActivity extends AppCompatActivity  implements RecyclerViewInterface{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    CalendarView calendarView;
    Toolbar toolbar;

    TextView nothingToDisplayTextView;
    ProgressBar loadingDataProgressBar;
    RecyclerView recyclerView;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;

    String selectedDateString = "";
    Date selectedDate;

    Dialog detailsDialog;
    Button okButton;

    List<ScheduleInstanceModel> instancesForDetails = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendar_view);
        recyclerView = findViewById(R.id.instances_recyclerview);
        nothingToDisplayTextView = findViewById(R.id.nothing_to_display_textview);
        loadingDataProgressBar = findViewById(R.id.loading_data_progressbar);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                if (id == R.id.new_medicine) {
                    Intent intent = new Intent(CalendarActivity.this, AddMedicineActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                else if (id == R.id.nav_pantry) {
                    Intent intent = new Intent(CalendarActivity.this, PantryActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                else if (id == R.id.nav_medical_log) {
                    return true;
                }
                else if (id == R.id.nav_medical_history) {
                    return true;
                }
                else if (id == R.id.nav_settings) {
                    return true;
                }
                else if (id == R.id.nav_about) {
                    return true;
                }
                else if (id == R.id.nav_profile) {
                    return true;
                }
                else if (id == R.id.nav_logout) {
                    auth.signOut();
                    startActivity(new Intent(CalendarActivity.this, LoginActivity.class));
                    return true;
                }

                return false;
            }
        });


        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        selectedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        selectedDateString = dateFormat.format(selectedDate);

        UpdateRecyclerView(selectedDateString);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String month = (i1 + 1) < 10 ? "0" + (i1 + 1) : Integer.toString(i1 + 1);
                String day = i2 < 10 ? "0" + i2 : Integer.toString(i2);
                selectedDateString = i + "/" + month + "/" + day;

                UpdateRecyclerView(selectedDateString);

            }
        });

        detailsDialog = new Dialog(CalendarActivity.this);
        detailsDialog.setContentView(R.layout.instance_details_dialog);
        detailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        detailsDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_dialog_background);
        detailsDialog.setCancelable(true);

        okButton = detailsDialog.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsDialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        selectedDateString = dateFormat.format(selectedDate);
        calendarView.setDate(new Date().getTime());
        navigationView.setCheckedItem(R.id.nav_home);
        UpdateRecyclerView(selectedDateString);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    private void UpdateRecyclerView(String selectedDate) {
        loadingDataProgressBar.setVisibility(View.VISIBLE);
        nothingToDisplayTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);



        firestore.collection("userData").document(currentUser.getUid()).collection("schedule").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        final List<ScheduleInstanceModel> instances = new ArrayList<>();
                        final List<ScheduleInstanceModel> instancesToDisplay = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String id = document.getString("id");
                            String name = document.getString("name");
                            String date = document.getString("date");
                            String time = document.getString("time");

                            instances.add(new ScheduleInstanceModel(id, name, date, time));
                        }

                        for (ScheduleInstanceModel instance : instances) {
                            if (instance.getDate().equals(selectedDate)) {
                                instancesToDisplay.add(instance);
                            }
                        }

                        if (!instancesToDisplay.isEmpty()) {

                            instancesToDisplay.sort(new Comparator<ScheduleInstanceModel>() {
                                @Override
                                public int compare(ScheduleInstanceModel o1, ScheduleInstanceModel o2) {
                                    Date date1 = new Date(o1.getDate());
                                    Date date2 = new Date(o2.getDate());

                                    return date1.compareTo(date2);
                                }
                            });

                            ScheduleInstance_RecyclerviewAdapter adapter = new ScheduleInstance_RecyclerviewAdapter(CalendarActivity.this, instancesToDisplay, CalendarActivity.this);

                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(CalendarActivity.this));
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        else {
                            nothingToDisplayTextView.setVisibility(View.VISIBLE);
                        }

                        instancesForDetails = instancesToDisplay;

                        loadingDataProgressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void onInstanceClick(int position) {
        TextView name = detailsDialog.findViewById(R.id.details_name_content_textView);
        name.setText(instancesForDetails.get(position).getName());

        TextView date = detailsDialog.findViewById(R.id.details_date_content_textView);
        date.setText(instancesForDetails.get(position).getDate());

        TextView time = detailsDialog.findViewById(R.id.details_time_content_textView);
        time.setText(instancesForDetails.get(position).getTime());

        detailsDialog.show();
    }
}