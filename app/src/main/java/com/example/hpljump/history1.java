package com.example.hpljump;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class history1 extends AppCompatActivity {
    //TextView results1;
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
    private Button historybutton;
    Button exitbutton;

    // variable for our bar chart
    BarChart barChart;

    // variable for our bar data set.
    BarDataSet barDataSet1, barDataSet2;

    // array list for storing entries.
    ArrayList barEntries;

    // creating a string array for displaying days.
    String[] days = new String[]{"1/16/22 13:46", "1/16/22 13:51", "1/16/22 13:52", "1/16/22 13:53", "1/16/22 13:55", "1/16/22 13:57"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_history);

        // initializing variable for bar chart.
        barChart = findViewById(R.id.chart);

        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntriesOne(), "Average Jump Height (m)");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        barDataSet2 = new BarDataSet(getBarEntriesTwo(), "Jump height Standard Deviation (m)");
        barDataSet2.setColor(Color.BLUE);

        // below line is to add bar data set to our bar data.
        BarData data = new BarData(barDataSet1, barDataSet2);

        // after adding data to our bar data we
        // are setting that data to our bar chart.
        barChart.setData(data);

        // below line is to remove description
        // label of our bar chart.
        barChart.getDescription().setEnabled(false);

        // below line is to get x axis
        // of our bar chart.
        XAxis xAxis = barChart.getXAxis();

        // below line is to set value formatter to our x-axis and
        // we are adding our days to our x axis.
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(true);

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // below line is to set granularity
        // to our x axis labels.
        xAxis.setGranularity(1);

        // below line is to enable
        // granularity to our x axis.
        xAxis.setGranularityEnabled(true);
        // below line is to make our
        // bar chart as draggable.
        barChart.setDragEnabled(true);

        // below line is to make visible
        // range for our bar chart.
        barChart.setVisibleXRangeMaximum(3);

        // below line is to add bar
        // space to our chart.
        float barSpace = 0.1f;

        // below line is use to add group
        // spacing to our bar chart.
        float groupSpace = 0.5f;

        // we are setting width of
        // bar in below line.
        data.setBarWidth(0.15f);

        // below line is to set minimum
        // axis to our chart.
        barChart.getXAxis().setAxisMinimum(0);

        // below line is to
        // animate our chart.
        barChart.animate();

        // below line is to group bars
        // and add spacing to it.
        barChart.groupBars(0, groupSpace, barSpace);

        // below line is to invalidate
        // our bar chart.
        barChart.invalidate();


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
        //results1 = (TextView)findViewById(R.id.resultstext);
        //results1.setText(htmlAsSpanned);


    }

    // array list for first set
    private ArrayList<BarEntry> getBarEntriesOne() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, 0.17f));
        barEntries.add(new BarEntry(2f, 0.15f));
        barEntries.add(new BarEntry(3f, 0.18f));
        barEntries.add(new BarEntry(4f, 0.14f));
        barEntries.add(new BarEntry(5f, 0.15f));
        barEntries.add(new BarEntry(6f, 0.13f));
        return barEntries;
    }

    // array list for second set.
    private ArrayList<BarEntry> getBarEntriesTwo() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, 0.04f));
        barEntries.add(new BarEntry(2f, 0.04f));
        barEntries.add(new BarEntry(3f, 0.03f));
        barEntries.add(new BarEntry(4f, 0.05f));
        barEntries.add(new BarEntry(5f, 0.02f));
        barEntries.add(new BarEntry(6f, 0.03f));
        return barEntries;
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