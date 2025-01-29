package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameResult extends AppCompatActivity {
    private TextView tvResult, tvTime, tvGRFinsih;
    private Button btContinue;
    private SQLiteDatabase db;
    private String sql;
    private static String playDate;
    private static String playTime;
    private static String duration;
    private static String correctCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        tvGRFinsih = findViewById(R.id.tvGRFinish);
        tvResult = findViewById(R.id.tvResult);
        tvTime = findViewById(R.id.tvTime);
        btContinue = findViewById(R.id.btContinue);

        tvGRFinsih.setTextColor(0xFF00FF00);// to green color

        // Open the database
        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/eBidDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        // Create the table if it doesn't exist
        sql = "CREATE TABLE IF NOT EXISTS GamesLog (gameID INTEGER PRIMARY KEY AUTOINCREMENT, playDate text, playTime text, duration text, correctCount text);";
        db.execSQL(sql);

        // Adept the GmaePlay page data
        Intent intent = getIntent();
        tvResult.setText(intent.getStringExtra("Result"));
        tvTime.setText(intent.getStringExtra("Time"));
        playDate = intent.getStringExtra("Play Date");
        playTime = intent.getStringExtra("Play Time");
        duration = intent.getStringExtra("Time");
        correctCount = intent.getStringExtra("Result");

        // Insert the result into gameLog table
        ContentValues values = new ContentValues();
        values.put("playDate", playDate);
        values.put("playTime", playTime);
        values.put("duration", duration);
        values.put("correctCount", correctCount);

        db.insert("GamesLog", null, values);
        db.close();
    }

    public void Continue(View view) {
        Intent intent = new Intent(this, GamePlay.class);
        startActivity(intent);
        finish();//close this page
    }

    public void BackToMenu(View view) {
        finish();//close this page
    }
}