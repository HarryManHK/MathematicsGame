package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameRanking extends AppCompatActivity {
    private DownloadTask task = null;
    private Button btCloseRanking;
    private ListView lvRankinglist;
    private String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ranking);

        btCloseRanking = findViewById(R.id.btCloseRanking);
        lvRankinglist = findViewById(R.id.lvRankinglist);
        // lvRankinglist.setOnItemClickListener(this);

        if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            task = new DownloadTask();
            task.execute("https://ranking-mobileasignment-wlicpnigvf.cn-hongkong.fcapp.run");
        }
    }

    public void closeRanking(View view) {
        finish();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... values) {
            InputStream inputStream = null;
            String result = "";
            URL url;
            try {
                url = new URL(values[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
                result = stringBuilder.toString();
                return result;
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Assuming the result is a JSON array string
                JSONArray camArray = new JSONArray(result);

                listItems = new String[camArray.length()];

                for (int i = 0; i < camArray.length(); i++) {
                    JSONObject item = camArray.getJSONObject(i);
                    listItems[i] = "Rank"+(i+1)+", "+item.getString("Name")+", "+item.getInt("Correct")+" Correct, "+item.getInt("Time")+" sec";
                    Log.d("JSONArray result", listItems[i]);
                }

                lvRankinglist.setAdapter(new ArrayAdapter<>(GameRanking.this, android.R.layout.simple_list_item_1, listItems));
            } catch (Exception e) {
                Log.d("JSONArray result error:", e.toString());
            }
        }
    }
}