package com.stanley.fastmhd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ConnectionsListActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections_list);
        ListView zoznamSpojov = findViewById(R.id.zoznamSpojov);
        ZoznamSpojovAdapter adapter = new ZoznamSpojovAdapter(this, R.layout.zoznam_spojov_layout,
                Logic.spoje);
        zoznamSpojov.setAdapter(adapter);

    }
}
