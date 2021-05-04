package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetsActivity extends AppCompatActivity {
    private GridView sets_grid;
    private FirebaseFirestore firestore;
    public static int category_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);
        Toolbar toolbar = findViewById(R.id.set_toolbar);
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra("CATEGORY");
        category_id=getIntent().getIntExtra("CATEGORY_ID",1);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sets_grid = findViewById(R.id.sets_gridview);
        firestore=FirebaseFirestore.getInstance();
        loadSets();


    }
    public void loadSets(){

        firestore.collection("QUIZ").document("CAT"+String.valueOf(category_id)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        long sets = (long) doc.get("SETS");
                        SetsAdapter adapter = new SetsAdapter((int) sets);
                        sets_grid.setAdapter(adapter);
                    } else {
                        Toast.makeText(SetsActivity.this, "no DOC exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SetsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }});

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            SetsActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}