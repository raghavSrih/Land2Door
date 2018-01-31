package com.example.hoag.land2door;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String msgFarm = intent.getStringExtra(MainActivity.EXTRA_FARM);
        String msgAbout = intent.getStringExtra(MainActivity.EXTRA_ABOUT);
        String msgName = intent.getStringExtra(MainActivity.EXTRA_NAME);
        String msgPhone = intent.getStringExtra(MainActivity.EXTRA_PHONE);
        String msgMobile = intent.getStringExtra(MainActivity.EXTRA_MOBILE);
        String msgEmail = intent.getStringExtra(MainActivity.EXTRA_EMAIL);

        // Capture the layout's TextView and set the string as its text
        TextView textFarm = findViewById(R.id.viewFarm);
        textFarm.setText(msgFarm);
        TextView textAbout = findViewById(R.id.viewAbout);
        textAbout.setText(msgAbout);
        TextView textFarmer = findViewById(R.id.viewFarmer);
        textFarmer.setText(msgName);
        TextView textPhone = findViewById(R.id.viewPhone);
        textPhone.setText(msgPhone);
        TextView textMobile = findViewById(R.id.viewMobile);
        textMobile.setText(msgMobile);
        TextView textEmail = findViewById(R.id.viewEmail);
        textEmail.setText(msgEmail);
    }
}
