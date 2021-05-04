package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button start;
    public static List<String>catList=new ArrayList<>();
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore=FirebaseFirestore.getInstance();
        start=(Button) findViewById(R.id.buttonStart);
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "please wait a moment", Toast.LENGTH_SHORT).show();
                openCategory();
                loadData();


            }
        });
    }
    public void openCategory() {
        Intent intent = new Intent(this, CategoryActivity.class);
        startActivity(intent);
    }
    private void loadData(){
        catList.clear();
        firestore.collection("QUIZ").document("Categories").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc=task.getResult();
                    if(doc.exists()){
                        long count=(long)doc.get("COUNT");
                        for (int i=1;i<=count;i++){
                            String catName=doc.getString("CAT"+String.valueOf(i));
                            catList.add(catName);
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this,"no category exists",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}