package com.stanley.fastmhd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChoiceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private int id0;
    private int id1;
    private TextView text;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private boolean vDone = false;
    private ArrayList<Integer> vychodzie;
    private ArrayList<Integer> konecne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        vychodzie = extras.getIntegerArrayList("VYCHODZIE_Z");
        konecne = extras.getIntegerArrayList("KONECNE_Z");

        text = findViewById(R.id.zastavkyText);
        text.setText(R.string.vyberV);
        adapter = new ArrayAdapter<>(this, R.layout.text_layout);

        for (int i = 0; i < vychodzie.size(); i++) {
            adapter.add(Scraper.zastavky[vychodzie.get(i)].meno);
        }

        list = findViewById(R.id.zastavkyVyber);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (!vDone) {
            id0 = position;
            text.setText(R.string.vyberK);
            adapter = new ArrayAdapter<>(this, R.layout.text_layout);
            for (int i = 0; i < konecne.size(); i++) {
                adapter.add(Scraper.zastavky[konecne.get(i)].meno);
            }
            list.setAdapter(adapter);
            vDone = true;
        } else {
            id1 = position;
            Intent intent = new Intent();
            intent.putExtra("V_INT", vychodzie.get(id0));
            intent.putExtra("K_INT", konecne.get(id1));
            setResult(RESULT_OK, intent);
            finish();

        }
    }

}
