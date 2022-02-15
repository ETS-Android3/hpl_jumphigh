  package com.example.hpljump;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private int history_n=5;
    private ArrayList<String> Datetitle_s_al= new ArrayList<>();
    private String [] Datetitle_s=new String[history_n];
    private String [] Timetitle_s=new String[history_n];
    private int [] jumpcount_s=new int[history_n];
    private double [] avg_sampling_rate_s=new double[history_n];
    private double [] avg_height_s=new double[history_n];
    private double [] stdev_height_s=new double[history_n];
    private double [] avg_tflight_s =new double[history_n];
    private double [] stdev_tflight_s=new double[history_n];
    private double [] avg_vi_s=new double[history_n];
    private double [] takoff_thr_s=new double[history_n];
    private double [] landing_thr_s=new double[history_n];
    private double [] summintakeoff_s=new double[history_n];
    private double [] minpeak_s=new double[history_n];

    private boolean drawn=false;
    private Button historybutton;
    Button exitbutton;
    TextView debug1;
    private String [] history_s =new String[history_n];

    // variable for our bar chart
    BarChart barChart;

    // variable for our bar data set.
    BarDataSet barDataSet1, barDataSet2;

    // array list for storing entries.
    ArrayList barEntries;

    // creating a string array for displaying days.
    String[] days= new String[history_n];;// = new String[]{"1/16/22 13:46", "1/16/22 13:51", "1/16/22 13:52", "1/16/22 13:53", "1/16/22 13:55", "1/16/22 13:57"};


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("history_n",history_n);
        savedInstanceState.putStringArray("Datetitle_s", Datetitle_s);
        savedInstanceState.putStringArray("Timetitle_s", Timetitle_s);
        savedInstanceState.putDoubleArray("avg_height_s", avg_height_s);
        savedInstanceState.putDoubleArray("avg_vi_s", avg_vi_s);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        history_n = savedInstanceState.getInt("history_n");
        Datetitle_s = savedInstanceState.getStringArray("Datetitle_s");
        Timetitle_s = savedInstanceState.getStringArray("Timetitle_s");
        avg_height_s = savedInstanceState.getDoubleArray("avg_height_s");
        avg_vi_s = savedInstanceState.getDoubleArray("avg_vi_s");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_history);

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
        debug1 = (TextView)findViewById(R.id.debugtext);

        if (savedInstanceState == null) {
            savehistory();
            Datetitle_s_al=new ArrayList( Arrays.asList( Datetitle_s ) );
        }
        else
        {
            history_n = savedInstanceState.getInt("history_n");
            Datetitle_s = savedInstanceState.getStringArray("Datetitle_s");
            Timetitle_s = savedInstanceState.getStringArray("Timetitle_s");
            avg_height_s = savedInstanceState.getDoubleArray("avg_height_s");
            avg_vi_s = savedInstanceState.getDoubleArray("avg_vi_s");
        }

        // initializing variable for bar chart.
        barChart = findViewById(R.id.chart);
        for (int i=0;i<history_n;i++)
            days[i]=Datetitle_s[i]+Timetitle_s[i];
        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntriesOne(), "Average Jump Height (m)");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        barDataSet2 = new BarDataSet(getBarEntriesTwo(), "Average Initial Velocity (m/s)");
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
        xAxis.setGranularity(1f);

        // below line is to enable
        // granularity to our x axis.
        xAxis.setGranularityEnabled(true);
        // below line is to make our
        // bar chart as draggable.
        barChart.setDragEnabled(true);

        // below line is to make visible
        // range for our bar chart.
        barChart.setVisibleXRangeMaximum(3.5f);
        xAxis.setAxisMaximum(5.0f);

        // below line is to add bar
        // space to our chart.
        float barSpace = 0.1f;

        // below line is use to add group
        // spacing to our bar chart.
        float groupSpace = 0.2f;

        // we are setting width of
        // bar in below line.
        data.setBarWidth(0.3f);

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
//        barEntries.add(new BarEntry(1f, 0.17f));
//        barEntries.add(new BarEntry(2f, 0.15f));
//        barEntries.add(new BarEntry(3f, 0.18f));
//        barEntries.add(new BarEntry(4f, 0.14f));
//        barEntries.add(new BarEntry(5f, 0.15f));
//        barEntries.add(new BarEntry(6f, 0.13f));
        for (int i=0;i<history_n;i++)
            barEntries.add(new BarEntry((float)i, (float) avg_height_s[i]));
            //barEntries.add(new BarEntry(1f, (float) takoff_thr_s[i]));
        return barEntries;
    }

    // array list for second set.
    private ArrayList<BarEntry> getBarEntriesTwo() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
//        barEntries.add(new BarEntry(1f, 0.04f));
//        barEntries.add(new BarEntry(2f, 0.04f));
//        barEntries.add(new BarEntry(3f, 0.03f));
//        barEntries.add(new BarEntry(4f, 0.05f));
//        barEntries.add(new BarEntry(5f, 0.02f));
//        barEntries.add(new BarEntry(6f, 0.03f));
        for (int i=0;i<history_n;i++)
            barEntries.add(new BarEntry((float)i, (float) avg_vi_s[i]));
            //barEntries.add(new BarEntry(1f, (float) landing_thr_s[i]));
        return barEntries;
    }




    public void savehistory(){
        StringBuilder new_history = new StringBuilder();
        StringBuilder cont_history = new StringBuilder();
        StringBuilder cleaned_history = new StringBuilder();
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

            new_history.append( "ID, Date, Time,"+ "Number of Jumps, Average Sampling Rate(Hz)," +
                    "Average Jump height(m),Jump height Standard Deviation(m),Average Time of Flight(s),"+
                    "Time of Flight Standard Deviation(s),Average Initial Velocity(m/s),Takeoff Threshold(m/s/s),"+
                    "Landing Threshold(m/s/s),Avg Peak acceleration(m/s/s),Peak acceleration(m/s/s),Time of propulsion(s))" );
            float tmp;
            if (jumpcount==0)
                tmp=0;
            else
                tmp=(float)summintakeoff/jumpcount;

            //avg_height = Math.random() * 0.2;
            //jumpcount =3;
            cont_history.append("\n" + "IDX,"+String.valueOf(currentDateandTime) + "," + String.valueOf(jumpcount) + "," + String.format("%.2f",avg_sampling_rate) + "," );
            cont_history.append(        String.format("%.2f", avg_height)+ ","+String.format("%.2f",stdev_height)+ "," +String.format("%.2f",avg_tflight)+","+String.format("%.2f",stdev_tflight)+",");

            cont_history.append(        String.format("%.2f",avg_vi)+ ","+String.format("%.2f",takoff_thr)+ "," +String.format("%.2f",landing_thr)+","+String.format("%.2f",tmp)+",");
            cont_history.append(        String.format("%.2f",minpeak)+ ","+String.format("%.2f",0.5));

            //exporting
            Context context = getApplicationContext();

            //String root = Environment.getExternalStorageDirectory().toString();
            //File tmpDir1 = new File(root + "/hpl_saved_history");
            //if (!tmpDir1.exists()) {
            //    tmpDir1.mkdirs();
            //}
            //File tmpDir = new File(tmpDir1, historyfilename);
            File tmpDir = new File(context.getFilesDir(), historyfilename);

            boolean exists = tmpDir.exists();
            int noOfLines;

            String line,last_line = null;
            if(!exists) {
                tmpDir.createNewFile();
                FileOutputStream fos = new FileOutputStream(tmpDir);
                debug1.setText(new_history.toString());
                fos.write(new_history.toString().getBytes());
                debug1.setText(cont_history.toString());
                fos.write(cont_history.toString().getBytes());
                fos.flush();
                fos.close();
            }
            else {
                try (LineNumberReader reader = new LineNumberReader(new FileReader(tmpDir))) {
                    reader.skip(Integer.MAX_VALUE);
                    noOfLines = reader.getLineNumber() + 1;
                }

                FileInputStream fileInputStream = new FileInputStream(tmpDir);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                boolean badline = false;
                StringBuilder sb = new StringBuilder();
                int line_i=0;
                int h_i=0;
                String[] lastvalues=null;//= last_line.split(",");
                while ((line = bufferedReader.readLine()) != null) {
                    String[] values = line.split(",");

                    if ((line_i==0)  && !(values[0].equals("ID")))
                    {
                        cleaned_history.setLength(0);
                        cleaned_history.append(new_history);
                        badline = true;
                    }
                    if (values[0].equals("ID")) {
                        if (line_i==0) {
                            cleaned_history.setLength(0);
                            cleaned_history.append(line);
                        }
                        else
                        {
                            badline = true;
                        }
                        line_i++;
                        last_line = line;
                        lastvalues = last_line.split(",");
                        continue;
                    }
                        if ((line_i>0)&&(!values[2].equals(lastvalues[2])) && !line.equals("") && (Double.parseDouble(values[5]) > 0.01) && (Integer.parseInt(values[3]) > 1)) {
                            if ((line_i > 0) && ((noOfLines > history_n) && (line_i > noOfLines - history_n) || (noOfLines < history_n))) {
                                history_s[h_i] = line;

                                Datetitle_s[h_i] = values[1];
                                Timetitle_s[h_i] = values[2];
                                jumpcount_s[h_i] = Integer.parseInt(values[3]);
                                avg_sampling_rate_s[h_i] = Double.parseDouble(values[4]);
                                avg_height_s[h_i] = Double.parseDouble(values[5]);
                                stdev_height_s[h_i] = Double.parseDouble(values[6]);
                                avg_tflight_s[h_i] = Double.parseDouble(values[7]);
                                stdev_tflight_s[h_i] = Double.parseDouble(values[8]);
                                avg_vi_s[h_i] = Double.parseDouble(values[9]);
                                takoff_thr_s[h_i] = Double.parseDouble(values[10]);
                                landing_thr_s[h_i] = Double.parseDouble(values[11]);
                                summintakeoff_s[h_i] = Double.parseDouble(values[12]);
                                minpeak_s[h_i] = Double.parseDouble(values[13]);
                                h_i++;
                            }

                            cleaned_history.append(line);
                        } else {
                            badline = true;
                        }
                        //sb.append(line);
                        line_i++;
                        last_line = line;
                        lastvalues = last_line.split(",");
                    }

                fileInputStream.close();

                if (line_i==0) {
                    //cleaned_history.setLength(0);
                    //cleaned_history.append(line);
                    last_line=new_history.toString();
                    FileOutputStream fos = new FileOutputStream(tmpDir);

                    fos.write(new_history.toString().getBytes());
                    fos.flush();
                    fos.close();
                }
                if (badline)
                {
                    FileOutputStream fos = new FileOutputStream(tmpDir);
                    debug1.setText(cleaned_history.toString());
                    fos.write(cleaned_history.toString().getBytes());
                    fos.flush();
                    fos.close();
                }
                lastvalues = last_line.split(",");
                String newline=cont_history.toString().trim();
                String[] newvalues = newline.split(",");
                if ((!lastvalues[2].equals(newvalues[2])) && !newline.equals("") && (Double.parseDouble(newvalues[5]) > 0.01) && (Integer.parseInt(newvalues[3]) > 1)) {

                    //history_s[h_i] = line;

                    Datetitle_s[h_i] = newvalues[1];
                    Timetitle_s[h_i] = newvalues[2];
                    jumpcount_s[h_i] = Integer.parseInt(newvalues[3]);
                    avg_sampling_rate_s[h_i] = Double.parseDouble(newvalues[4]);
                    avg_height_s[h_i] = Double.parseDouble(newvalues[5]);
                    stdev_height_s[h_i] = Double.parseDouble(newvalues[6]);
                    avg_tflight_s[h_i] = Double.parseDouble(newvalues[7]);
                    stdev_tflight_s[h_i] = Double.parseDouble(newvalues[8]);
                    avg_vi_s[h_i] = Double.parseDouble(newvalues[9]);
                    takoff_thr_s[h_i] = Double.parseDouble(newvalues[10]);
                    landing_thr_s[h_i] = Double.parseDouble(newvalues[11]);
                    summintakeoff_s[h_i] = Double.parseDouble(newvalues[12]);
                    minpeak_s[h_i] = Double.parseDouble(newvalues[13]);

                    debug1.setText(cont_history.toString());
                    FileOutputStream fos = new FileOutputStream(tmpDir, true);
                    fos.write(cont_history.toString().getBytes());
                    fos.flush();
                    fos.close();
                }


            }

            //fos.write(new_history.toString().getBytes());





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




      public void savehistory(View view){

          try{
              //saving the file into device
              Context context = getApplicationContext();
              StringBuilder save_history = new StringBuilder();
              String historyfilename = "JumpAccelerator-History-ID.csv";
              //String root = Environment.getExternalStorageDirectory().toString();
              //File tmpDir1 = new File(root + "/hpl_saved_history");
              //if (!tmpDir1.exists()) {
              //    tmpDir1.mkdirs();
              //}
              //File tmpDir = new File(tmpDir1, historyfilename);
              File tmpDir = new File(context.getFilesDir(), historyfilename);

              boolean exists = tmpDir.exists();


              String line= null;
              if(exists) {

                  FileInputStream fileInputStream = new FileInputStream(tmpDir);
                  InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                  BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                  boolean badline = false;
                  StringBuilder sb = new StringBuilder();
                  int line_i=0;
                  int h_i=0;
                  String[] lastvalues=null;//= last_line.split(",");
                  while ((line = bufferedReader.readLine()) != null) {

                          save_history.append(line);
                          save_history.append("\n");
                  }

                  fileInputStream.close();

                  String filename = "JumpAcceleratorProfile-ID-History.csv";
                  //String historyfilename = "JumpAccelerator-History-ID.csv";
                  //String afilename;
                  //afilename=filename.replaceAll("[]:","").replaceAll("\.","").replaceAll("\ ","");
    //            filename=filename.replaceAll(".","");
    //            filename=filename.replaceAll(" ","");
                  //  Log.d(TAG,Datetitle);
                  //  Log.d(TAG,Timetitle);
                  // Log.d(TAG,filename);
                  FileOutputStream out = openFileOutput(filename, Context.MODE_PRIVATE);
                  out.write((save_history.toString()).getBytes());
                  out.close();

                  //exporting
                  //Context context = getApplicationContext();
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