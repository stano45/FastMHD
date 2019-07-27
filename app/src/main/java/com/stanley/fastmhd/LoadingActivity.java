package com.stanley.fastmhd;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Starts data download

        AsyncTask s = new ScraperAsync().execute();

    }


    public class ScraperAsync extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Scraper.parseZastavky();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent i = new Intent(LoadingActivity.this, MainActivity.class);
            finish();
            startActivity(i);
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);
            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(LoadingActivity.this, values[0], Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(LoadingActivity.this, "No internet.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
