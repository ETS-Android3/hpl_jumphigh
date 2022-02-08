package com.example.hpljump;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class results extends AppCompatActivity {
    TextView results1;
    private String currentDateandTime;
    private static final String TAG = "results";
    private String data;
    private double avg_tflight;
    private double stdev_tflight;
    private double avg_sampling_rate;
    private double takoff_thr;
    private double landing_thr;
    private double avg_height;
    private double stdev_height;
    private double avg_vi;
    private double summintakeoff;
    private double minpeak;
    private int jumpcount;
    Button historybutton;
    Button exitbutton;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_results);

        exitbutton = (Button)findViewById(R.id.exitbutton);
        exitbutton.setOnClickListener(new View.OnClickListener() {

          public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(), MainActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.putExtra("EXIT", true);
              startActivity(intent);


              // Intents are objects of the android.content.Intent type. Your code can send them
              // to the Android system defining the components you are targeting.
              // Intent to start an activity called SecondActivity with the following code:


          }

        });

        historybutton = (Button) findViewById(R.id.historybutton);
        //historybutton.setAlpha(.5f);
        //historybutton.setEnabled(false);


        historybutton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v)
                {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd ',' HH:mm:ss ");
//                String currentDateandTime = sdf.format(new Date());
                    // Intents are objects of the android.content.Intent type. Your code can send them
                    // to the Android system defining the components you are targeting.
                    // Intent to start an activity called SecondActivity with the following code:
                    // Log.d(TAG, "Results button Clicked. ");
                    Intent intent = new Intent(results.this, history1.class);
                    //Sending data to next activity using putExtra method
                    //    intent.putExtra("DATETIME", currentDateandTime);
                    // start the activity connect to the specified class
                    // intent.putExtra("ACCDATA", data.toString());
                    intent.putExtra("DATETIME", currentDateandTime);

                    intent.putExtra("jumpcount", jumpcount);
                    intent.putExtra("avg_tflight", avg_tflight);
                    intent.putExtra("stdev_tflight", stdev_tflight);
                    intent.putExtra("avg_sampling_rate", avg_sampling_rate);
                    intent.putExtra("takoff_thr", takoff_thr);
                    intent.putExtra("landing_thr", landing_thr);
                    intent.putExtra("avg_height", avg_height);
                    intent.putExtra("stdev_height", stdev_height);
                    intent.putExtra("avg_vi", avg_vi);
                    intent.putExtra("summintakeoff", summintakeoff);
                    intent.putExtra("minpeak", minpeak);
                    startActivity(intent);

                }



        });

        //Log.d(TAG, "Result screen is shown. ");
      Bundle bunble=getIntent().getExtras();

        if(bunble!=null){
            //Getting the value stored in the name "NAME"
            //data=bunble.getString("ACCDATA");
            jumpcount=bunble.getInt("jumpcount" );
            avg_tflight=bunble.getDouble("avg_tflight");
            stdev_tflight=bunble.getDouble("stdev_tflight");
            avg_sampling_rate=bunble.getDouble("avg_sampling_rate");
            takoff_thr=bunble.getDouble("takoff_thr");
            landing_thr=bunble.getDouble("landing_thr");
            avg_height=bunble.getDouble("avg_height");
            stdev_height=bunble.getDouble("stdev_height");
            avg_vi=bunble.getDouble("avg_vi");
            summintakeoff=bunble.getDouble("summintakeoff");
            minpeak=bunble.getDouble("minpeak");
            currentDateandTime=bunble.getString("DATETIME");
        }


        String htmlAsString =
                "        <h2>Jump Performance Results </h2>\n" +
                "        <ol>\n" +
                        "            <li> Number of Jumps: "+jumpcount+"</li>\n" +
                        "            <li> Average Sampling Rate: "+String.format("%.2f",avg_sampling_rate)+" Hz</li>\n" +
                        "            <li> Average Jump height: "+String.format("%.2f", avg_height)+" m</li>\n" +
                        "            <li> Jump height Standard Deviation: "+String.format("%.2f",stdev_height)+" m</li>\n" +
                        "            <li> Average Time of Flight: "+String.format("%.2f",avg_tflight)+" s</li>\n" +
                        "            <li> Time of Flight Standard Deviation: "+String.format("%.2f",stdev_tflight)+" s</li>\n" +
                        "            <li> Average Initial Velocity: "+String.format("%.2f",avg_vi)+" m/s</li>\n" +

                "            <li> Takeoff Threshold: "+String.format("%.2f",takoff_thr)+" m/s/s</li>\n" +
                        "            <li> Landing Threshold: "+String.format("%.2f",landing_thr)+" m/s/s</li>\n" +
                        "            <li> Avg Peak acceleration: "+String.format("%.2f",summintakeoff/jumpcount)+" m/s/s</li>\n" +
                        "            <li> Peak acceleration: "+String.format("%.2f",minpeak)+" m/s/s</li>\n" +

            //    "            <li>Time of propulsion: 5 sec (tbd)</li>\n" +
                "        </ol>\n" ;      // used by WebView
        Spanned htmlAsSpanned = Html.fromHtml(htmlAsString,0); // used by TextView

        // by ID we can use each component which id is assign in xml file
        // use findViewById() to get the Button
//        start_button = (Button)findViewById(R.id.start);
        results1 = (TextView)findViewById(R.id.resultstext);
        results1.setText(htmlAsSpanned);


    }
/*
    public void savedata(View view){

        try{
            //saving the file into device
            String Datetitle = currentDateandTime.substring(0,currentDateandTime.indexOf(","));
            String Timetitle = currentDateandTime.substring(currentDateandTime.indexOf(",")+1, currentDateandTime.length());
            Datetitle =Datetitle.replaceAll(                    "[^a-zA-Z0-9]", "");
            Timetitle =Timetitle.replaceAll(                    "[^a-zA-Z0-9]", "");
            String filename = "JumpAcceleratorProfile-ID-"+Datetitle + '-' + Timetitle + ".csv";
            //String afilename;
            //afilename=filename.replaceAll("[]:","").replaceAll("\.","").replaceAll("\ ","");
//            filename=filename.replaceAll(".","");
//            filename=filename.replaceAll(" ","");
            Log.d(TAG,Datetitle);
            Log.d(TAG,Timetitle);
            Log.d(TAG,filename);
            FileOutputStream out = openFileOutput(filename, Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), filename);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            if (filelocation.exists()) {
                Uri path = FileProvider.getUriForFile(context, "com.example.hpljump.fileprovider", filelocation);
                fileIntent.setType("text/csv");
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, filename);
                //fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                Intent chooser = Intent.createChooser(fileIntent, "Send mail");

                List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivity(chooser);
                //startActivity(Intent.createChooser(fileIntent, "Send mail"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }*/



    public void savehistory(View view){

        try{
            //saving the file into device


            //String Datetitle = currentDateandTime.substring(0,currentDateandTime.indexOf(","));
            //String Timetitle = currentDateandTime.substring(currentDateandTime.indexOf(",")+1, currentDateandTime.length());
            //Datetitle =Datetitle.replaceAll(                    "[^a-zA-Z0-9]", "");
            //Timetitle =Timetitle.replaceAll(                    "[^a-zA-Z0-9]", "");
            //String filename = "JumpAcceleratorProfile-ID-"+Datetitle + '-' + Timetitle + ".csv";
            String historyfilename = "JumpAccelerator-History-ID.csv";
            //currentDateandTime=bunble.getString("DATETIME");
            //voice_instruction = bunble.getBoolean("voice_instruction");
            //appending the value to the contents of textView1
            //data.append("ID, Date, Time");
            // todo: get support for the idx in future.
            //data.append("\n"+ "IDX,"+ currentDateandTime);
            StringBuilder new_history = new StringBuilder();
            StringBuilder cont_history = new StringBuilder();
            new_history.append("\n"+ "ID, Date, Time,"+ "Number of Jumps, Average Sampling Rate(Hz)," +
                    "Average Jump height(m),Jump height Standard Deviation(m),Average Time of Flight(s),"+
                    "Time of Flight Standard Deviation(s),Average Initial Velocity(m/s),Takeoff Threshold(m/s/s),"+
                    "Landing Threshold(m/s/s),Avg Peak acceleration(m/s/s),Peak acceleration(m/s/s),Time of propulsion(s))" );

            cont_history.append("\n" + "IDX,"+String.valueOf(currentDateandTime) + ',' + jumpcount + ',' + String.format("%.2f",avg_sampling_rate) + ',' +
                    String.format("%.2f", avg_height)+ ','+String.format("%.2f",stdev_height)+ ',' +String.format("%.2f",avg_tflight)+','+String.format("%.2f",stdev_tflight)+',' +
                    String.format("%.2f",avg_vi)+ ','+String.format("%.2f",takoff_thr)+ ',' +String.format("%.2f",landing_thr)+','+String.format("%.2f",summintakeoff/jumpcount)+',' +
                    String.format("%.2f",minpeak)+ ','+String.format("%.2f",100));

            //exporting
            Context context = getApplicationContext();

            File tmpDir = new File(historyfilename);
            boolean exists = tmpDir.exists();
            int noOfLines;
            int history_n=5;
            String line;
            String [] history_s =new String[history_n];

            if(!exists) {
                FileOutputStream fos = new FileOutputStream(historyfilename);
                fos.write(new_history.toString().getBytes());
                fos.write(cont_history.toString().getBytes());
                fos.flush();
                fos.close();
            }
            else {
                FileOutputStream fos = new FileOutputStream(historyfilename,true);
                fos.write(cont_history.toString().getBytes());
                fos.flush();
                fos.close();



                try (LineNumberReader reader = new LineNumberReader(new FileReader(historyfilename))) {
                    reader.skip(Integer.MAX_VALUE);
                    noOfLines = reader.getLineNumber() + 1;
                }

                FileInputStream fileInputStream = context.openFileInput(historyfilename);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);



                StringBuilder sb = new StringBuilder();
                int line_i=0;
                int h_i=0;
                while ((line = bufferedReader.readLine()) != null) {
                    if ((line_i>0) && ((noOfLines>history_n) && (line_i>noOfLines-history_n)||(noOfLines<history_n)))
                    {
                        history_s[h_i]=line;
                        h_i++;
                    }

                    //sb.append(line);
                    line_i++;

                }

                //fos.write(new_history.toString().getBytes());


            }


            //exporting
            //Context context = getApplicationContext();
/*            File filelocation = new File(getFilesDir(), historyfilename);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            if (filelocation.exists()) {
                Uri path = FileProvider.getUriForFile(context, "com.example.hpljump.fileprovider", filelocation);
                fileIntent.setType("text/csv");
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, historyfilename);
                //fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                Intent chooser = Intent.createChooser(fileIntent, "Send mail");

                List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivity(chooser);
                //startActivity(Intent.createChooser(fileIntent, "Send mail"));
            }*/
        }
        catch(Exception e){
            e.printStackTrace();
        }

//        stopData=true;
//        plotData=false;
//        // Intents are objects of the android.content.Intent type. Your code can send them
//        // to the Android system defining the components you are targeting.
//        // Intent to start an activity called SecondActivity with the following code:
//        Log.d(TAG, "Results button Clicked. ");
//        Intent intent = new Intent(Accelerometer.this, results.class);
//        //Sending data to next activity using putExtra method
//        //    intent.putExtra("DATETIME", currentDateandTime);
//        // start the activity connect to the specified class
//        // intent.putExtra("ACCDATA", data.toString());
//        //intent.putExtra("DATETIME", currentDateandTime);
//
//        intent.putExtra("jumpcount", jumpcount);
//        intent.putExtra("avg_tflight", avg_tflight);
//        intent.putExtra("stdev_tflight", stdev_tflight);
//        intent.putExtra("avg_sampling_rate", avg_sampling_rate);
//        intent.putExtra("takoff_thr", takoff_thr);
//        intent.putExtra("landing_thr", landing_thr);
//        intent.putExtra("avg_height", avg_height);
//        intent.putExtra("stdev_height", stdev_height);
//        intent.putExtra("avg_vi", avg_vi);
//        startActivity(intent);

    }

}