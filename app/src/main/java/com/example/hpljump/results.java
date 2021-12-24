package com.example.hpljump;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    Button exitbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_results);

        exitbutton = (Button)findViewById(R.id.exitbutton);
        exitbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);


                // Intents are objects of the android.content.Intent type. Your code can send them
                // to the Android system defining the components you are targeting.
                // Intent to start an activity called SecondActivity with the following code:


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
            //currentDateandTime=bunble.getString("DATETIME");
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
}