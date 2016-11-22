package com.example.administrator.email;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Intent getIntent = getIntent();
        String message = getIntent.getStringExtra("Message");
        TextView show= (TextView) findViewById(R.id.show_message);
        show.setText(message);
    }
}
