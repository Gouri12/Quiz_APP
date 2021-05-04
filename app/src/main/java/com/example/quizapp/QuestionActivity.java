package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.quizapp.SetsActivity.category_id;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView question,questionCount,timer;
    private Button opt1,opt2,opt3,opt4;
    private List<Question> questionList;
    int questionNum;
    private CountDownTimer countDown;
    private int score;
    private FirebaseFirestore firestore;
    private int setNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        question=findViewById(R.id.question);
        questionCount=findViewById(R.id.quest_no);
        timer=findViewById(R.id.counter);
        opt1=findViewById(R.id.option1);
        opt2=findViewById(R.id.option2);
        opt3=findViewById(R.id.option3);
        opt4=findViewById(R.id.option4);
        opt1.setOnClickListener(this);
        opt2.setOnClickListener(this);
        opt3.setOnClickListener(this);
        opt4.setOnClickListener(this);
        questionList=new ArrayList<>();
        setNo=getIntent().getIntExtra("SETNO",1);
        firestore=FirebaseFirestore.getInstance();
        getQuestionsList();
        score=0;




    }
    private void getQuestionsList(){
        questionList.clear();
        firestore.collection("QUIZ").document("CAT"+String.valueOf(category_id)).collection("SET"+String.valueOf(setNo)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot questions=task.getResult();
                    for(QueryDocumentSnapshot doc:questions){
                        questionList.add(new Question(doc.getString("QUESTION"),doc.getString("A"),doc.getString("B"),doc.getString("C"),doc.getString("D"),Integer.valueOf(doc.getString("ANSWER"))
                        ));
                    }
                    setQuestion();

              } else{
                    Toast.makeText(QuestionActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }});


    }
    private void setQuestion(){
        timer.setText(String.valueOf(10));
        question.setText(questionList.get(0).getQuestion());
        opt1.setText(questionList.get(0).getOptA());
        opt2.setText(questionList.get(0).getOptB());
        opt3.setText(questionList.get(0).getOptC());
        opt4.setText(questionList.get(0).getOptD());
        questionCount.setText(String.valueOf(1)+"/"+String.valueOf(questionList.size()));
        startTimer();
        questionNum=0;
    }
    private void startTimer(){
         countDown=new CountDownTimer(12000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished<10000)
                  timer.setText(String.valueOf(millisUntilFinished/1000));

            }

            @Override
            public void onFinish() {
                changeQuestion();

            }
        };
        countDown.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        int selectedOption=0;
        switch (v.getId()){
            case R.id.option1:
                selectedOption=1;
                break;
            case R.id.option2:
                selectedOption=2;
                break;
            case R.id.option3:
                selectedOption=3;
                break;
            case R.id.option4:
                selectedOption=4;
                break;
            default:
        }
        countDown.cancel();
        checkAnswer(selectedOption,v);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private  void checkAnswer(int selectedOption, View view)
    {
        if(selectedOption==questionList.get(questionNum).getCorrectAns()){
            score++;
            //Right
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }
        else {
            //wrong
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            switch (questionList.get(questionNum).getCorrectAns()){
                case 1:
                    opt1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    opt2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    opt3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    opt4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }
        }
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        },2000);

    }
    private void changeQuestion(){
        if(questionNum<questionList.size()-1){
            questionNum++;
            playAnim(question,0,0);
            playAnim(opt1,0,1);
            playAnim(opt2,0,2);
            playAnim(opt3,0,3);
            playAnim(opt4,0,4);

            questionCount.setText(String.valueOf(questionNum+1) + "/" +String.valueOf(questionList.size()));
            timer.setText(String.valueOf(10));
            startTimer();

        }
        else{
            //score
            Intent intent=new Intent(QuestionActivity.this,ScoreActivity.class);
            intent.putExtra("SCORE",String.valueOf(score)+"/"+String.valueOf(questionList.size()));
            startActivity(intent);
            QuestionActivity.this.finish();
        }
    }
    private void playAnim(View view,final int value,int viewNum){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animation) {
                      if(value==0){
                        switch (viewNum) {
                            case 0:
                                ((TextView)view).setText(questionList.get(questionNum).getQuestion());
                                break;
                            case 1:
                                ((Button)view).setText(questionList.get(questionNum).getOptA());
                            case 2:
                                ((Button)view).setText(questionList.get(questionNum).getOptB());
                            case 3:
                                ((Button)view).setText(questionList.get(questionNum).getOptC());
                            case 4:
                                ((Button)view).setText(questionList.get(questionNum).getOptD());
                        }

                        if(viewNum!=0){
                            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                        }

                        playAnim(view,1,viewNum);
                      }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }
}