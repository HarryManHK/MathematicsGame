package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MyRecord extends AppCompatActivity {
    private ListView list;
    private SQLiteDatabase db;
    private String sql;
    private Button btCloseRec,btClearRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_record);
        list = findViewById(R.id.list);
        btCloseRec = findViewById(R.id.btCloseRec);
        btClearRec = findViewById(R.id.btClearRec);

        showRecord();

        // Set OnClickListener for the close button
        btCloseRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRec();
            }
        });

        // Set OnClickListener for the clear record button
        btClearRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRec();
            }
        });
    }

    public void showRecord(){
        // Open the database
        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/eBidDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

        // Get the number of rows in the GamesLog table
        int countDB = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM GamesLog;", null);
        if (cursor.moveToFirst()) {
            countDB = cursor.getInt(0);
        }
        cursor.close();

        String[] items = new String[countDB];

        // Fetch all rows from the GamesLog table
        String fetchSql = "SELECT playDate, playTime, duration, correctCount FROM GamesLog";
        Cursor dataCursor = db.rawQuery(fetchSql, null);

        // Populate the items array with data from the cursor
        int I = 0;
        if (dataCursor.moveToFirst()) {
            do {
                String playDate = dataCursor.getString(dataCursor.getColumnIndexOrThrow("playDate"));
                String playTime = dataCursor.getString(dataCursor.getColumnIndexOrThrow("playTime"));
                String duration = dataCursor.getString(dataCursor.getColumnIndexOrThrow("duration"));
                String correctCount = dataCursor.getString(dataCursor.getColumnIndexOrThrow("correctCount"));

                items[I] = playDate + " " + playTime + " " + duration + " " + correctCount;
                I++;
            } while (dataCursor.moveToNext());
        }
        dataCursor.close();

        // Create an ArrayAdapter using the items array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        // Set the adapter to the ListView
        list.setAdapter(adapter);

        db.close();
    }

    public void closeRec() {
        finish(); // close this page
    }

    public void clearRec() {
        // Open the database
        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/eBidDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

        // Delete all records from the GamesLog table
        db.delete("GamesLog", null, null);

        showRecord();

        db.close();
    }
}