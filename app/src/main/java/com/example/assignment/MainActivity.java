package com.example.assignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private String sql;
    private Button btPlay, btRanking, btYourRecord, btClose;
    private static int roundOfGame = 10; //default is 10 round

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btPlay = findViewById(R.id.btPlay);
        btRanking = findViewById(R.id.btRanking);
        btYourRecord = findViewById(R.id.btYourRecord);
        btClose = findViewById(R.id.btClose);

        // Set OnClickListener for the play button
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        btRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameRanking();
            }
        });

        btYourRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourRecord();
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // arguments: groupId, itemId, orderId, title
        menu.add(0, 1, 1, "About");
        menu.add(0, 2, 2, "Round Of Game");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    //called whatever ab item is selected
    public boolean onOptionsItemSelected(MenuItem item){
        return (applyMenuOption(item) ||
                super.onOptionsItemSelected(item));
    }

    private boolean applyMenuOption(MenuItem item) {
        int menuItemId = item.getItemId();

        if(menuItemId == 1) {
            openDialog();//Open About message window
        }else if(menuItemId == 2) {
            showInputDialog();
        }
        return false;
    }

    //About
    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This is a Mathematic Game.You can set the rounds you want to play.\nVersion 1.0\nAuthor: Harry Man\nGithub: HarryManHK");
        builder.setTitle("About");
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener()   {
            @Override
            public void onClick(DialogInterface dialog, int which)    {
            }
        }).show();
    }


    //set play of round
    private void showInputDialog() {
        // Create the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input, null);
        final EditText etRoundOfGame = dialogView.findViewById(R.id.etRoundOfGame);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set round of game")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etRoundOfGame.getText().toString().equals("")){
                            roundOfGame = 10; //user has input anything
                            Toast.makeText(MainActivity.this, "Default round of game is 10, as you input nothing.", Toast.LENGTH_SHORT).show();
                        }else{
                            roundOfGame = Integer.parseInt(etRoundOfGame.getText().toString());
                            Toast.makeText(MainActivity.this, "Updated round of game to " + roundOfGame, Toast.LENGTH_SHORT).show();
                        }
                        // Open the database
                        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/eBidDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
                        // Create the table if it doesn't exist
                        sql = "CREATE TABLE IF NOT EXISTS UserSetting (roundOfGame int);";
                        db.execSQL(sql);
                        //empty the record
                        db.delete("UserSetting", null, null);
                        // Insert the result into gameLog table
                        ContentValues values = new ContentValues();
                        values.put("roundOfGame", roundOfGame);
                        db.insert("UserSetting", null, values);
                        Log.d("roundOfGame","inputed"+roundOfGame);
                        Cursor cursor = db.rawQuery("SELECT roundOfGame FROM UserSetting;", null);
                        if (cursor.moveToFirst()) {
                            roundOfGame = cursor.getInt(cursor.getColumnIndex("roundOfGame"));
                        }
                        cursor.close();

                        db.close();
                        Log.d("roundOfGame","roundOfGame is "+ roundOfGame +"in menu page.");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        // Show the dialog
        builder.create().show();
    }


    public void play() {
        //open the Gameplay UI
        Intent intent = new Intent(this, GamePlay.class);
        intent.putExtra("RoundOfGame",roundOfGame);
        startActivity(intent);
    }

    public void gameRanking() {
        //open the Game Ranking UI
        Intent intent = new Intent(this, GameRanking.class);
        startActivity(intent);
    }

    public void yourRecord() {
        // Open the database
        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/eBidDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        // Create the table if it doesn't exist
        sql = "CREATE TABLE IF NOT EXISTS GamesLog (gameID INTEGER PRIMARY KEY AUTOINCREMENT, playDate text, playTime text, duration text, correctCount text);";
        db.execSQL(sql);
        db.close();

        //open the MyRecord UI
        Intent intent = new Intent(this, MyRecord.class);
        startActivity(intent);
    }

    public void close() {
        finish(); //close this page
    }
}