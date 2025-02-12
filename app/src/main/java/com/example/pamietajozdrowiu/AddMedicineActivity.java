package com.example.pamietajozdrowiu;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.pamietajozdrowiu.databinding.ActivityAddMedicineBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddMedicineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    ActivityAddMedicineBinding binding;
    LinearLayout newMedicineContainer, fromListContainer, fromPrescriptionContainer;
    Toolbar toolbar;
    EditText medicineNameEditText;
    TextView timeOfTakingTextView, startDateTextView, endDateTextview;
    Button addButton;
    Spinner dropdownMenuSpinner;
    RadioGroup frequencyRadioGroup;
    GridLayout daysOfWeekContainer;
    CheckBox mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox, fridayCheckbox, saturdayCheckbox, sundayCheckbox;
    List<CheckBox> checkboxes;
    ProgressBar progressBar;
    int year, month, day, hour, minute;
    int startDate, endDate;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        toolbar = findViewById(R.id.toolbar);

        //VIEWS FROM NEW MEDICINE LAYOUT


        //VIEWS FROM LIST LAYOUT


        //VIEWS FROM PRESCRIPTION LAYOUT




        medicineNameEditText = findViewById(R.id.medicine_name_nm);
        timeOfTakingTextView = findViewById(R.id.time_of_taking_nm);
        startDateTextView = findViewById(R.id.start_date_nm);
        endDateTextview = findViewById(R.id.end_date_nm);
        addButton = findViewById(R.id.add_button);
        newMedicineContainer = findViewById(R.id.add_new_medicine_container);
        fromPrescriptionContainer = findViewById(R.id.add_from_prescription_container);
        fromListContainer = findViewById(R.id.add_from_list_container);
        frequencyRadioGroup = findViewById(R.id.add_medicine_radioGroup_nm);
        daysOfWeekContainer = findViewById(R.id.days_of_week_container_nm);
        progressBar = findViewById(R.id.progress_bar);

        mondayCheckbox = findViewById(R.id.monday_checkbox_nm);
        tuesdayCheckbox = findViewById(R.id.tuesday_checkbox_nm);
        wednesdayCheckbox = findViewById(R.id.wednesday_checkbox_nm);
        thursdayCheckbox = findViewById(R.id.thursday_checkbox_nm);
        fridayCheckbox = findViewById(R.id.friday_checkbox_nm);
        saturdayCheckbox = findViewById(R.id.saturday_checkbox_nm);
        sundayCheckbox = findViewById(R.id.sunday_checkbox_nm);

        checkboxes = List.of(mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox, fridayCheckbox, saturdayCheckbox, sundayCheckbox);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        progressBar.setVisibility(View.GONE);

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddMedicineActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        startDateTextView.setText(formatDate(year, month, day));
                        endDateTextview.setEnabled(true);
                        startDate = (year*10000 + month*100 + day);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        endDateTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddMedicineActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        endDate = (year*10000 + month*100 + day);
                        if (endDate >= startDate)
                            endDateTextview.setText(formatDate(year, month, day));
                        else
                            Toast.makeText(AddMedicineActivity.this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        
        timeOfTakingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddMedicineActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        timeOfTakingTextView.setText(formatTime(i, i1));
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ScheduleInstanceModel> schedule = new ArrayList<>();

                if (!isFormValid()) {
                    Toast.makeText(AddMedicineActivity.this,
                            R.string.fill_all_fields,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    schedule = createSchedule();
                    progressBar.setVisibility(View.VISIBLE);

                    AddMedicineName(schedule.get(0));

                    schedule.forEach(instance -> AddScheduleInstance(instance));
                    Toast.makeText(AddMedicineActivity.this,
                            "Success",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        dropdownMenuSpinner = findViewById(R.id.medicine_source_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.medicine_source, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenuSpinner.setAdapter(adapter);

        dropdownMenuSpinner.setOnItemSelectedListener(this);


        frequencyRadioGroup.setOnCheckedChangeListener(this);

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            newMedicineContainer.setVisibility(View.VISIBLE);
            fromListContainer.setVisibility(View.GONE);
            fromPrescriptionContainer.setVisibility(View.GONE);
        } else if (i == 1) {
            newMedicineContainer.setVisibility(View.GONE);
            fromListContainer.setVisibility(View.VISIBLE);
            fromPrescriptionContainer.setVisibility(View.GONE);
        }else if (i == 2) {
            newMedicineContainer.setVisibility(View.GONE);
            fromListContainer.setVisibility(View.GONE);
            fromPrescriptionContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        newMedicineContainer.setVisibility(View.VISIBLE);
        fromPrescriptionContainer.setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == R.id.everyday_radiobutton_nm) {
            daysOfWeekContainer.setVisibility(View.GONE);
        }
        else if (i == R.id.custom_radiobutton_nm) {
            daysOfWeekContainer.setVisibility(View.VISIBLE);
        }

    }

    private boolean isFormValid() {
        setEditTextColor(medicineNameEditText);
        setTextViewColor(timeOfTakingTextView);
        setTextViewColor(startDateTextView);
        setTextViewColor(endDateTextview);


        return isEditTextValid(medicineNameEditText) &&
                isTextViewValid(timeOfTakingTextView) &&
                isTextViewValid(startDateTextView) &&
                isTextViewValid(endDateTextview) &&
                areCheckboxesValid();
    }

    private boolean areCheckboxesValid() {
        if (frequencyRadioGroup.getCheckedRadioButtonId() == R.id.custom_radiobutton_nm) {
            for (CheckBox checkBox : checkboxes) {
                if (checkBox.isChecked())
                    return true;
            }
            return false;
        }
        return true;
    }

    private boolean isEditTextValid(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    private boolean isTextViewValid(TextView textView) {
        return !textView.getText().toString().isEmpty();
    }

    private void setEditTextColor(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.getBackground().mutate().setColorFilter(getResources()
                    .getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            editText.setHintTextColor(getResources()
                    .getColor(android.R.color.holo_red_light));
        }
        else {
            editText.getBackground().mutate().setColorFilter(getResources()
                    .getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
            editText.setHintTextColor(getResources()
                    .getColor(android.R.color.darker_gray));
        }
    }

    private void setTextViewColor(TextView textView) {
        if (textView.getText().toString().isEmpty()) {
            textView.setHintTextColor(getResources()
                    .getColor(android.R.color.holo_red_light));
        }
        else {
            textView.setHintTextColor(getResources()
                    .getColor(android.R.color.darker_gray));
        }
    }

    private String formatDate(int year, int month, int day) {
        return year + "/" + (month<10 ? "0":"") + (month+1) + "/" + (day<10 ? "0":"") + day;
    }



    private String formatTime(int hour, int minute) {
        return (hour<10 ? "0":"") + hour + ":" + (minute<10 ? "0":"") + minute;
    }

    private List<ScheduleInstanceModel> createSchedule () {

        List<ScheduleInstanceModel> schedule = new ArrayList<>();
        List<Integer> daysOfWeek = getDaysOfWeek();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        String timeOfTaking = timeOfTakingTextView.getText().toString();
        String medicineName = medicineNameEditText.getText().toString().toLowerCase();


        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        try {
            startDate.setTime(Objects.requireNonNull(dateFormatter.parse(startDateTextView.getText().toString())));
            endDate.setTime(Objects.requireNonNull(dateFormatter.parse(endDateTextview.getText().toString())));

            do {
                if (daysOfWeek.contains(startDate.get(Calendar.DAY_OF_WEEK))) {
                    String date = dateFormatter.format(startDate.getTime());
                    schedule.add(new ScheduleInstanceModel(medicineName, date, timeOfTaking));
                }
                startDate.add(Calendar.DATE, 1);
            } while (!startDate.getTime().after(endDate.getTime()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return schedule;
    }

    private List<Integer> getDaysOfWeek() {
        List<Integer> daysOfWeek = new ArrayList<>();

        if (frequencyRadioGroup.getCheckedRadioButtonId() == R.id.custom_radiobutton_nm) {

            if (mondayCheckbox.isChecked())
                daysOfWeek.add(Calendar.MONDAY);
            if (tuesdayCheckbox.isChecked())
                daysOfWeek.add(Calendar.TUESDAY);
            if (wednesdayCheckbox.isChecked())
                daysOfWeek.add(Calendar.WEDNESDAY);
            if (thursdayCheckbox.isChecked())
                daysOfWeek.add(Calendar.THURSDAY);
            if (fridayCheckbox.isChecked())
                daysOfWeek.add(Calendar.FRIDAY);
            if (saturdayCheckbox.isChecked())
                daysOfWeek.add(Calendar.SATURDAY);
            if (sundayCheckbox.isChecked())
                daysOfWeek.add(Calendar.SUNDAY);
        }
        else if (frequencyRadioGroup.getCheckedRadioButtonId() == R.id.everyday_radiobutton_nm) {
            daysOfWeek.add(Calendar.MONDAY);
            daysOfWeek.add(Calendar.TUESDAY);
            daysOfWeek.add(Calendar.WEDNESDAY);
            daysOfWeek.add(Calendar.THURSDAY);
            daysOfWeek.add(Calendar.FRIDAY);
            daysOfWeek.add(Calendar.SATURDAY);
            daysOfWeek.add(Calendar.SUNDAY);

        }
        return daysOfWeek;
    }

    private void AddScheduleInstance(ScheduleInstanceModel instance) {
        Map<String, Object> map = new HashMap<>();
        String id = UUID.randomUUID().toString();
        map.put("id", id);
        map.put("name", instance.getName());
        map.put("date", instance.getDate());
        map.put("time", instance.getTime());


        firestore.collection("userData")
                .document(currentUser.getUid())
                .collection("schedule")
                .document(id).set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void AddMedicineName(ScheduleInstanceModel instance) {
        final List<MedicineModel> medicineNames = new ArrayList<>();

        firestore.collection("userData")
                .document(currentUser.getUid())
                .collection("medicine").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    boolean found = false;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String id = document.getString("id");
                            String name = document.getString("name");

                            medicineNames.add(new MedicineModel(id, name));
                        }

                        for (MedicineModel medicineName : medicineNames) {
                            if (medicineName.getName().equals(instance.getName())) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            Map<String, Object> map = new HashMap<>();
                            String id = UUID.randomUUID().toString();
                            map.put("id", id);
                            map.put("name", instance.getName());
                            map.put("group", "");
                            map.put("amount", 0);
                            AddNameToDatabase(map);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void AddNameToDatabase(Map<String, Object> map) {
        firestore.collection("userData")
                .document(currentUser.getUid())
                .collection("medicine")
                .document((String) map.get("id")).set(map);
    }
}