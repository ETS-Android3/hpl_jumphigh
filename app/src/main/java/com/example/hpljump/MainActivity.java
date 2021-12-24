package com.example.hpljump;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView introduction1;
    Button start_button;
    boolean voice_instruction = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_introductionscreen);
        // get our html content
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        String htmlAsString = getString(R.string.instructions);      // used by WebView
        Spanned htmlAsSpanned = Html.fromHtml(htmlAsString); // used by TextView

        // by ID we can use each component which id is assign in xml file
        // use findViewById() to get the Button
        start_button = (Button)findViewById(R.id.start);
        introduction1 = (TextView)findViewById(R.id.introtext);
        introduction1.setText(htmlAsSpanned);

        // In question1 get the TextView use by findViewById()
        // In TextView set question Answer for message
//        introduction1.setText("Q 1 - How to pass the data between activities in Android?\n"
//                + "\n"
//                + "Ans- Intent");
        Switch sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    voice_instruction = true;
                } else {
                    // The toggle is disabled
                    voice_instruction = false;
                }
            }
        });
        // Add_button add clicklistener
        start_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd ',' HH:mm:ss ");
                String currentDateandTime = sdf.format(new Date());

                // Intents are objects of the android.content.Intent type. Your code can send them
                // to the Android system defining the components you are targeting.
                // Intent to start an activity called SecondActivity with the following code:

                Intent intent = new Intent(MainActivity.this, Accelerometer.class);
                //Sending data to next activity using putExtra method
                intent.putExtra("DATETIME", currentDateandTime);
                intent.putExtra("voice_instruction",voice_instruction);
                // start the activity connect to the specified class
                startActivity(intent);
            }
        });
    }
}