package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class GamePlay extends AppCompatActivity {
    private static int roundOfGame = 10; //default is 10 round
    private SQLiteDatabase db;
    private String sql;
    private TextView tvTimer,tvNumOfQuestion, tvQuestion, tvCorretOrWrong, tvShowAns;
    private Button btDone,btToNextQuestion;
    private EditText etPlayerAns;
    //private static String [][] questionArray= new String[roundOfGame][2];
    private static ArrayList<String> questionArrayList = new ArrayList<>();
    private static ArrayList<String> answerArrayList = new ArrayList<>();
    private static int countQuestion = 0;
    private static int countCorrectQuestion = 0;
    private static int countWrongQuestion = 0;
    private static boolean playerAnsStatus; //true is correct false is wrong
    private static int seconds;
    private static long millisecond, startTime, timeBuff, updateTime = 0L;
    private Handler handler;
    private static String currentDate;
    private static String currentTime;

    //timer
    private final Runnable runnable =  new Runnable() {
        @Override
        public void run() {
            millisecond = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff +millisecond;
            seconds = (int)(updateTime/1000);
            tvTimer.setText("Time: "+seconds+" sec");
            handler.postDelayed(this,0);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        tvTimer = findViewById(R.id.tvTimer);
        tvNumOfQuestion = findViewById(R.id.tvNumOfQuestion);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCorretOrWrong = findViewById(R.id.tvCorretOrWrong);
        tvShowAns = findViewById(R.id.tvShowAns);
        btDone = findViewById(R.id.btDone);
        btToNextQuestion = findViewById(R.id.btToNextQuestion);
        etPlayerAns = findViewById(R.id.etPlayerAns);

        //get the RoundOfGame data in the database
        // Open the database
        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/eBidDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //find to round of game in database
        try {
            Cursor cursor = db.rawQuery("SELECT roundOfGame FROM UserSetting;", null);
            if (cursor.moveToFirst()) {
                roundOfGame = cursor.getInt(cursor.getColumnIndex("roundOfGame"));
                cursor.close();
            }
        }catch (Exception e){
            //user has not set the round of game
            //Default set roundOfGame = 10

            // Create the table if it doesn't exist
            sql = "CREATE TABLE IF NOT EXISTS UserSetting (roundOfGame int);";
            db.execSQL(sql);
            //empty the record
            db.delete("UserSetting", null, null);
            // Insert the result into gameLog table
            ContentValues values = new ContentValues();
            roundOfGame = 10;
            values.put("roundOfGame", roundOfGame);
            db.insert("UserSetting", null, values);
            Log.d("roundOfGame","inputed"+roundOfGame);
            Toast.makeText(GamePlay.this, "Default round of game is 10, as you input nothing.", Toast.LENGTH_SHORT).show();
        }
        db.close();
        Log.d("roundOfGame","roundOfGame is "+roundOfGame+"in GamePlay page.");
        //Log.d("roundOfGame","roundOfGame is "+cursor.getString(cursor.getColumnIndexOrThrow("roundOfGame")) +"in GamePlay page.");

        //clear the data --> if the player quit the game, the game is not end.
        countWrongQuestion = 0;
        countCorrectQuestion = 0;
        countQuestion = 0;
        //clear the timer
        millisecond = 0L;
        startTime = 0L;
        timeBuff = 0L;
        //clear the array list data
        questionArrayList.clear();
        answerArrayList.clear();

        // Set the initial visibility to GONE
        tvCorretOrWrong.setVisibility(View.GONE);
        tvShowAns.setVisibility(View.GONE);
        btToNextQuestion.setVisibility(View.GONE);

        //create 10 questions
        generateQuestion();
        tvNumOfQuestion.setText("Question 1");// set the first question
        //tvQuestion.setText(questionArray[countQuestion][0]);//print the first question
        tvQuestion.setText(questionArrayList.get(0));//print the first question

        //start the timer
        tvTimer.setText("Time: 0 sec");
        handler = new Handler(Looper.getMainLooper());
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable,0);

        // Get current date
        SimpleDateFormat playDate = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = playDate.format(new Date());

        // Get current time
        SimpleDateFormat playTime = new SimpleDateFormat("HH:mm");
        currentTime = playTime.format(new Date());
    }

    public static void generateQuestion(){
        String [] operatorsArray = {"+","-","*","/"};
        int count = 0;

        //generate 10 question
        while (count<roundOfGame){
            Random rd = new Random();
            int randomOperatorIndex = rd.nextInt(4);
            String operator = operatorsArray[randomOperatorIndex];
            if (operator.equals("+")){
                int num1 = (int) (Math.random() * ((100 - 1) + 1)) + 1;
                int num2 = (int) (Math.random() * ((100 - 1) + 1)) + 1;
                //questionArray[count][0] = num1 + operator + num2;//save the question
                questionArrayList.add(num1 + operator + num2);
                int ans = num1 + num2;
                //questionArray[count][1] = String.valueOf(ans);//save the answer
                answerArrayList.add(String.valueOf(ans));
            }else if (operator.equals("-")){
                while (true){
                    int num1 = (int) (Math.random() * ((100 - 1) + 1)) + 1;
                    int num2 = (int) (Math.random() * ((100 - 1) + 1)) + 1;
                    int ans = num1 - num2;
                    if (ans >= 0){
                        //questionArray[count][0] = num1 + operator + num2;
                        questionArrayList.add(num1 + operator + num2);
                        //questionArray[count][1] = String.valueOf(ans);
                        answerArrayList.add(String.valueOf(ans));
                        break; //End loop
                    }
                }
            }else if (operator.equals("*")){
                int num1 = (int) (Math.random() * ((100 - 1) + 1)) + 1;
                int num2 = (int) (Math.random() * ((100 - 1) + 1)) + 1;
                int ans = num1 * num2;
                //questionArray[count][0] = num1 + operator + num2;
                questionArrayList.add(num1 + operator + num2);
                //questionArray[count][1] = String.valueOf(ans);
                answerArrayList.add(String.valueOf(ans));
            }else if (operator.equals("/")){
                while (true){
                    int num1 = (int) (Math.random() * ((100 - 1) + 1)) + 1;
                    int num2 = (int) (Math.random() * ((100 - 1) + 1)) + 1;
                    if(num1%num2==0){
                        //questionArray[count][0] = num1 + operator + num2;
                        questionArrayList.add(num1 + operator + num2);
                        int ans = num1 / num2;
                        //questionArray[count][1] = String.valueOf(ans);
                        answerArrayList.add(String.valueOf(ans));
                        break;//End loop
                    }
                }
            }
            count++;
        }
    }

    public void Done(View view) {
        //stop the timer
        timeBuff += millisecond;
        handler.removeCallbacks(runnable);

        String playerAnswer = etPlayerAns.getText().toString();

        if(playerAnswer.equals("")){
            tvCorretOrWrong.setText("You must be input the answer!");
            tvCorretOrWrong.setTextColor(0xFFFF0000);//green color
            tvCorretOrWrong.setVisibility(View.VISIBLE);

        }else if (Integer.parseInt(playerAnswer)<0) {
            // user click one more time the DONE button
            // nothing to do...
            //}else if (questionArray[countQuestion][1].equals(playerAnswer)){//correct answer
        }else if (answerArrayList.get(countQuestion).equals(playerAnswer)){//correct answer
            tvCorretOrWrong.setText("CORRECT!");
            tvCorretOrWrong.setTextColor(0xFF00FF00);//green color

            // Show the TextViews and Button when DONE is clicked
            tvCorretOrWrong.setVisibility(View.VISIBLE);
            btToNextQuestion.setVisibility(View.VISIBLE);

            playerAnsStatus=true;
        }else{//wrong answer
            tvCorretOrWrong.setText("WRONG!");
            tvCorretOrWrong.setTextColor(0xFFFF0000);//red color

            //tvShowAns.setText("Answer is "+questionArray[countQuestion][1] +" !");
            tvShowAns.setText("Answer is "+answerArrayList.get(countQuestion) +" !");

            // Show the TextViews and Button when DONE is clicked
            tvCorretOrWrong.setVisibility(View.VISIBLE);
            tvShowAns.setVisibility(View.VISIBLE);
            btToNextQuestion.setVisibility(View.VISIBLE);

            playerAnswer = String.valueOf(-1);

            playerAnsStatus=false;
        }
    }
    public void toNextQuestion(View view){

        countQuestion++;
        if (playerAnsStatus){
            countCorrectQuestion++;
        }else{
            countWrongQuestion++;
        }

        //hide the button
        tvCorretOrWrong.setVisibility(View.GONE);
        tvShowAns.setVisibility(View.GONE);
        btToNextQuestion.setVisibility(View.GONE);
        etPlayerAns.setText(null);//clear the text box

        if(countQuestion<roundOfGame){
            tvNumOfQuestion.setText("Question "+(countQuestion+1));
            //tvQuestion.setText(questionArray[countQuestion][0]);//print the next question
            tvQuestion.setText(questionArrayList.get(countQuestion));//print the next question

            //start the timer
            //handler = new Handler(Looper.getMainLooper());
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable,0);
        }else{
            Intent intent = new Intent(this, GameResult.class);
            intent.putExtra("Result","Correct " + countCorrectQuestion+", Wrong: "+countWrongQuestion);
            intent.putExtra("Time","Time: " + seconds + " sec");
            intent.putExtra("Play Date",currentDate);
            intent.putExtra("Play Time",currentTime);
            countWrongQuestion = 0;//this round is end ,clear the data
            countCorrectQuestion = 0;//this round is end ,clear the data
            countQuestion = 0;//this round is end ,clear the data

            //clear the timer
            millisecond = 0L;
            startTime = 0L;
            timeBuff = 0L;

            startActivity(intent);
            finish();//close this page
        }
    }
}