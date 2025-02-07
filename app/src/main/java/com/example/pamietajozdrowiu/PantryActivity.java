package com.example.pamietajozdrowiu;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity {

    Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<PantryParentItemModel> parentItems;
    private PantryParentItem_RecyclerviewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry);

        recyclerView = findViewById(R.id.pantry_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        parentItems = new ArrayList<>();

        List<PantryChildItemModel> childItems1 = new ArrayList<>();
        childItems1.add(new PantryChildItemModel("Name1", "53"));
        childItems1.add(new PantryChildItemModel("Name2", "26"));
        childItems1.add(new PantryChildItemModel("Name3", "11"));

        List<PantryChildItemModel> childItems2 = new ArrayList<>();
        childItems2.add(new PantryChildItemModel("Name4", "66"));
        childItems2.add(new PantryChildItemModel("Name5", "39"));
        childItems2.add(new PantryChildItemModel("Name6", "31"));

        parentItems.add(new PantryParentItemModel(childItems1, "Section1"));
        parentItems.add(new PantryParentItemModel(childItems2, "Section2"));

        adapter = new PantryParentItem_RecyclerviewAdapter(parentItems);
        recyclerView.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar);



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