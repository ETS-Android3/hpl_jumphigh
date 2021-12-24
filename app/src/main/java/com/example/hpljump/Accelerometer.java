package com.example.hpljump;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {
//hi
    private static final String TAG = "Accelerometer";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double minpeak =0.0;
    private double summintakeoff = 0.0;
    private static final int X_AXIS_INDEX = 0;
    private static final int Y_AXIS_INDEX = 1;
    private static final int Z_AXIS_INDEX = 2;
    private StringBuilder data = new StringBuilder();
    // declare our Line chart
    private LineChart mChart;
    private LineData mData;
    private Thread mThread;
    private boolean plotData = true;
    private String currentDateandTime;
    TextView xvalue, yvalue, zvalue;
    private boolean stopData = true;
    private Button resultbutton;
    private Button savebutton;
    private MediaPlayer mMediaPlayer;
    private ArrayList<Double> dataarr = new ArrayList<Double>();
    private ArrayList<Long> tsarr= new ArrayList<Long>();
    private ArrayList<Double> tflightarr = new ArrayList<Double>();
    private ArrayList<Double> heightarr = new ArrayList<Double>();
    private double avg_tflight;
    private double stdev_tflight;
    private double avg_sampling_rate;
    private double takoff_thr;
    private double landing_thr;
    private double avg_height;
    private double stdev_height;
    private double avg_vi;
    private int jumpcount;
    private int enable_plot=1;
    private boolean voice_instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//put media name in .raw
 //       final MediaPlayer mp = MediaPlayer.create(this,R.raw.jumpcountdown);
  //      mp.start();
  //      mMediaPlayer = new MediaPlayer();


 /*       mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener(){
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // ... react appropriately ...
                // The MediaPlayer has moved to the Error state, must be reset!
                // Then return true if the error has been handled
                mMediaPlayer.reset();
                return false;
            }
        });*/
       // mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        xvalue = (TextView) findViewById(R.id.xvalue);
        yvalue = (TextView) findViewById(R.id.yvalue);
        zvalue = (TextView) findViewById(R.id.zvalue);


        Bundle bunble=getIntent().getExtras();

        if(bunble!=null){
            //Getting the value stored in the name "NAME"
            currentDateandTime=bunble.getString("DATETIME");
            voice_instruction = bunble.getBoolean("voice_instruction");
            //appending the value to the contents of textView1
            data.append("ID, Date, Time");
            // todo: get support for the idx in future.
            data.append("\n"+ "IDX,"+ currentDateandTime);
            data.append("\n");
            data.append("\n"+ "Accelerometer Data");
            data.append("\n"+ "Millisecond,"+ "X axis (m/s^2), Y axis (m/s^2), Z axis (m/s^2), Resultant (m/s^2)" );
        }
        if(voice_instruction){
            try {

                mMediaPlayer = MediaPlayer.create(this,R.raw.jumpcountdownupdatelong);
                //mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        mMediaPlayer.start();
                    }
                });
            } catch (Exception e) {
                mMediaPlayer.reset();
                //Log.d(TAG,"Mediaplayer is absent");
            }
        }

        else{
                //mMediaPlayer = MediaPlayer.create(this,R.raw.jumpcountdownshort);
        }
        if(voice_instruction) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 5 seconds
                    stopData = false;
                }
            }, 30000);
        }
        else
        {
            stopData = false;
        }
        resultbutton = (Button) findViewById(R.id.resultbutton);
        resultbutton.setAlpha(.5f);
        resultbutton.setEnabled(false);

        savebutton = (Button) findViewById(R.id.savebutton);
        resultbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd ',' HH:mm:ss ");
//                String currentDateandTime = sdf.format(new Date());
                stopData=true;
                enable_plot=0;
                // Intents are objects of the android.content.Intent type. Your code can send them
                // to the Android system defining the components you are targeting.
                // Intent to start an activity called SecondActivity with the following code:
               // Log.d(TAG, "Results button Clicked. ");
                Intent intent = new Intent(Accelerometer.this, results.class);
                //Sending data to next activity using putExtra method
            //    intent.putExtra("DATETIME", currentDateandTime);
                // start the activity connect to the specified class
               // intent.putExtra("ACCDATA", data.toString());
                //intent.putExtra("DATETIME", currentDateandTime);

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
        // Instantiating the SensorManager Class
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Instantiating the object  of sensor class by calling the getDefaultsensor() method
        // of the SensorManager class
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Check if Accelerometer is available or not
        if (mAccelerometer != null) {
           // Log.d(TAG,"accelerometer is present");
            // Now register the listener and override two methods onAccuracyCHanged
            // and onSensorChanged
        } else {
           // Log.d(TAG,"accelerometer is absent");
        }

        // Instantiating the Charts
        mChart = (LineChart) findViewById(R.id.chart1);
        // Enable Description text
        mChart.getDescription().setEnabled(true);
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setGridBackgroundColor(Color.LTGRAY);
        //mChart.enableFiltering()

        mData = new LineData();
        mData.setValueTextColor(Color.BLUE);
        // add empty data
        mChart.setData(mData);



        startPlot(mChart);

    }

    private void startPlot(LineChart mChart) {
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);


        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();
    }

    private void feedMultiple() {
        if(mThread != null) {
            mThread.interrupt();
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (enable_plot==1)
                        plotData = true;
                    try {
                        mThread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
       // Log.d(TAG, "onSensorChanged: X: "+ sensorEvent.values[X_AXIS_INDEX] + "Y: " +
       //         sensorEvent.values[Y_AXIS_INDEX] + "Z: " + sensorEvent.values[Z_AXIS_INDEX]);
        if (enable_plot==1) {
            xvalue.setText("     X-axis     " + sensorEvent.values[X_AXIS_INDEX]);
            yvalue.setText("     Y-axis     " + sensorEvent.values[Y_AXIS_INDEX]);
            zvalue.setText("     Z-axis     " + sensorEvent.values[Z_AXIS_INDEX]);
        }
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd ',' HH:mm:ss ");
        //String newDateandTime = sdf.format(new Date());
        long millisec = SystemClock.elapsedRealtime();
        float xval=sensorEvent.values[X_AXIS_INDEX];
        float yval=sensorEvent.values[Y_AXIS_INDEX];
        float zval=sensorEvent.values[Z_AXIS_INDEX];
        double rval=((Math.signum(xval)*Math.sqrt(xval*xval+yval*yval+zval*zval))*-1)-9.81 ;

        if (!stopData) {
            data.append("\n" + String.valueOf(millisec) + ',' + sensorEvent.values[X_AXIS_INDEX] + ',' + sensorEvent.values[Y_AXIS_INDEX] + ',' + sensorEvent.values[Z_AXIS_INDEX]+ ',' + rval);
            tsarr.add(millisec);
            dataarr.add(rval);
        }
        if (plotData && (enable_plot==1)) {
            addEntry(sensorEvent);
            plotData = false;
        }

    }

    private void addEntry(SensorEvent sensorEvent) {
        LineData data = mChart.getData();
 
        if (data != null) {
            ILineDataSet xData = data.getDataSetByIndex(X_AXIS_INDEX);
            ILineDataSet yData = data.getDataSetByIndex(Y_AXIS_INDEX);
            ILineDataSet zData = data.getDataSetByIndex(Z_AXIS_INDEX);

            if (xData == null) {
                xData = createSetX();
                mChart.getData().addDataSet(xData);
            }
            if (yData == null) {
                yData = createSetY();
                mChart.getData().addDataSet(yData);
            }
            if (zData == null) {
                zData = createSetZ();
                mChart.getData().addDataSet(zData);
            }

            if (xData != null ) {
                mChart.getData().addEntry(new Entry(xData.getEntryCount(), sensorEvent.values[X_AXIS_INDEX] + 5), X_AXIS_INDEX);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.setVisibleXRangeMaximum(150);
                mChart.moveViewToX(mChart.getData().getEntryCount());
                mChart.moveViewToX(mChart.getData().getXMax());
            }
            if (yData != null) {
                mChart.getData().addEntry(new Entry(yData.getEntryCount(), sensorEvent.values[Y_AXIS_INDEX]),  Y_AXIS_INDEX);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.setVisibleXRangeMaximum(150);
                mChart.moveViewToX(mChart.getData().getEntryCount());
                mChart.moveViewToX(mChart.getData().getXMax());

            }
            if (zData != null) {
                mChart.getData().addEntry(new Entry(zData.getEntryCount(), sensorEvent.values[Z_AXIS_INDEX]), Z_AXIS_INDEX);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.setVisibleXRangeMaximum(150);
                mChart.moveViewToX(mChart.getData().getEntryCount());
                mChart.moveViewToX(mChart.getData().getXMax());
            }

        }
    }



   private ILineDataSet createSetX() {
        LineDataSet set = new LineDataSet(null, "Real Time X-Axis Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setFormLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private ILineDataSet createSetY() {
        LineDataSet set = new LineDataSet(null, "Real Time Y-Axis Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setFormLineWidth(3f);
        set.setColor(Color.CYAN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private ILineDataSet createSetZ() {
        LineDataSet set = new LineDataSet(null, "Real Time Z-Axis Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setFormLineWidth(3f);
        set.setColor(Color.YELLOW);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }



    @Override
    protected void onPause() {
        // unregister listener to make sure battery is not drained because of this
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(Accelerometer.this);
        mThread.interrupt();
        super.onDestroy();

        if (mMediaPlayer != null) mMediaPlayer.release();

    }



    public void savedata(View view){

        try{
            //saving the file into device
            stopData=true;
            enable_plot=0;

            calresult();

            String Datetitle = currentDateandTime.substring(0,currentDateandTime.indexOf(","));
            String Timetitle = currentDateandTime.substring(currentDateandTime.indexOf(",")+1, currentDateandTime.length());
            Datetitle =Datetitle.replaceAll(                    "[^a-zA-Z0-9]", "");
            Timetitle =Timetitle.replaceAll(                    "[^a-zA-Z0-9]", "");
            String filename = "JumpAcceleratorProfile-ID-"+Datetitle + '-' + Timetitle + ".csv";
            //String afilename;
            //afilename=filename.replaceAll("[]:","").replaceAll("\.","").replaceAll("\ ","");
//            filename=filename.replaceAll(".","");
//            filename=filename.replaceAll(" ","");
          //  Log.d(TAG,Datetitle);
          //  Log.d(TAG,Timetitle);
           // Log.d(TAG,filename);
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
        resultbutton.setAlpha(1.0f);
        resultbutton.setEnabled(true);
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


    private void calresult() {
        //average sampling rate
        int num_samples = tsarr.size();

        avg_sampling_rate = (num_samples*1000)/(tsarr.get(num_samples-1)-tsarr.get(0));
        preprocess_inverse(num_samples,avg_sampling_rate);
        // sampling rate stddev
        //# jumps
        takoff_thr=-3.5-9.81;
        landing_thr=19.0;
        double [] min_takeoff= new double [20];
        long [] t_takeoff= new long [20];
        long [] t_landing= new long [20];
        double tflight_sum=0,height_sum=0;
        int takeoffind=0;
        double [] f_mintakeoff = new double[20];
        long t_landing_tmp=0;
        long t_takeoff_tmp=0;
        minpeak =0.0;
        summintakeoff = 0.0;
        jumpcount=0;
        boolean takeoff=false;
        data.append("\n,,,,,,,"+ "Jump results");
        data.append("\n,,,,,,,"+ "Jump,"+ "takeoff time(s), Landing time(s)" );
        int jump_inc = 3;
        int jump_skipped = 1;
        ArrayList<Double> tempdataarr = new ArrayList<Double>();
        for (int i=0;i<num_samples;i++)
        {
            if ((dataarr.get(i)<takoff_thr) && (takeoff==false) ){

                if((tsarr.get(i)-tsarr.get(0))<4500){
                    continue;
                }

                takeoff=true;
                t_takeoff_tmp=tsarr.get(i);
                takeoffind = i;
                tempdataarr.clear();
            }
            if(takeoff==true){
                tempdataarr.add(dataarr.get(i));

            }

            if ((dataarr.get(i)>landing_thr) && (takeoff==true) ){



                t_landing_tmp=tsarr.get(i);
                if(t_landing_tmp-t_takeoff_tmp>2000){
                    //fix all 0 issue professor reported 11/2/21
                    takeoff=false;
                    tempdataarr.clear();
                    continue;
                }
                if(t_landing_tmp-t_takeoff_tmp<100){
                    //fix all 0 issue professor reported 11/2/21
                    takeoff=false;
                    tempdataarr.add(dataarr.get(i));
                    continue;
                }

                double min = Collections.min(tempdataarr);
                int min_ind=tempdataarr.indexOf(Collections.min(tempdataarr));
                int ctakeoff = takeoffind + min_ind;
                t_takeoff[jumpcount] = tsarr.get(ctakeoff);
                min_takeoff[jumpcount]=min;
                double min_dist_0=10000;
                int min_dist_0_ind=0;
                int skip_time=150;
                double min_landing=12;
                for (int j=min_ind;j<tempdataarr.size();j++) {
                    if ((tempdataarr.get(j)>min_landing) && ((tsarr.get(takeoffind +j)-t_takeoff[jumpcount])>100))
                    {
                        //if ((tsarr.get(takeoffind+j) -tsarr.get(takeoffind))>skip_time) {
                        // avoid the initial pulse shown in professor's trace 11/2/21
                            min_dist_0_ind = j;
                            break;
                        //}
                    }

                }
                int clanding = takeoffind + min_dist_0_ind;
                t_landing[jumpcount] = tsarr.get(clanding);



                takeoff=false;
                data.append("\n,,,,,,1st rnd,"+ String.valueOf(jumpcount)+","+ String.valueOf(t_takeoff[jumpcount] )+","+String.valueOf(t_landing[jumpcount] )+"," + String.valueOf(min_takeoff[jumpcount] )+","+String.valueOf(t_landing[jumpcount]));
                jumpcount++;
                tempdataarr.clear();
            }
        }
        data.append("\n----------------------------------");
        double [] temp_arr=new double [20];
        for (int i = 0; i < 20; i++)
            temp_arr[i] = min_takeoff[i];
        Arrays.sort(temp_arr);
        jumpcount=0;
        long [] f_t_takeoff =new long [20];
        long [] f_t_landing =new long [20];
        int maxjumpcnt=20;
        for (int i = 0; i < 20; i++) {
            if (temp_arr[i]==0)
            {
                maxjumpcnt=i;
                break;
            }

        }
        //maxjumpcnt=Math.min(maxjumpcnt,jump_inc+jump_skipped);
        for (int i = 0; i < maxjumpcnt; i++) {
            if (min_takeoff[i] > temp_arr[maxjumpcnt])
            {
                t_takeoff[i]=0;
                t_landing[i]=0;

            }
            else
            {
                f_t_takeoff[jumpcount]=t_takeoff[i];
                f_t_landing[jumpcount]=t_landing[i];
                f_mintakeoff[jumpcount]= min_takeoff[i];
                summintakeoff += min_takeoff[i];
                if(min_takeoff[i]<minpeak){
                    minpeak = min_takeoff[i];
                }
                data.append("\n,,,,,,2nd round,"+ String.valueOf(jumpcount)+","+ String.valueOf(f_t_takeoff[jumpcount] )+","+String.valueOf(f_t_landing[jumpcount] )+","+ String.valueOf(f_mintakeoff[jumpcount] ) +","+ String.valueOf(t_takeoff[jumpcount] )+","+String.valueOf(t_landing[jumpcount])+","+String.valueOf(maxjumpcnt));
                jumpcount++;
            }
        }


        for (int i=0;i<jump_inc;i++)
        {
            if (i+1 <=jump_skipped && i+1>jump_inc+jump_skipped){


            }
            else{
                double tflight = (f_t_landing[i] - f_t_takeoff[i])/1000.0;
                tflightarr.add(tflight);

                tflight_sum+=tflight;
                double height = (tflight / 2.0) * (tflight / 2.0) * 9.81 / 2.0;

                heightarr.add(height);

                height_sum+=height;
            }

        }

        avg_tflight=Math.round((tflight_sum/tflightarr.size())*100.0)/100.0;
        avg_height=Math.round((height_sum/heightarr.size())*100.0)/100.0;
//        Vi =  (9.81 m/s/s)*(Tflight/2) = 1.91m/s
//        Height = (Vi*Tflight/2)-(1/2)*(9.81 m/s/s)*((Tflight/2)*(Tflight/2)) = 0.18m

        avg_vi=Math.round((avg_tflight*9.81/2)*100.0)/100.0;
        double temp = 0;
        double temp2 = 0;
        for (int i=0;i<tflightarr.size();i++)
        {
            Double val = tflightarr.get(i);
            double squrDiffToMean = Math.pow(val - avg_tflight, 2);

            // Step 3:
            temp += squrDiffToMean;
            double val1= heightarr.get(i);
            squrDiffToMean = Math.pow(val1 - avg_height, 2);

            // Step 3:
            temp2 += squrDiffToMean;
        }

        // Step 4:
        double meanOfDiffs = (double) temp / (double) (tflightarr.size());
        double meanOfDiffsh = (double) temp2 / (double) (heightarr.size());
        // Step 5:
        stdev_tflight = Math.round((Math.sqrt(meanOfDiffs))*100.0)/100.0;
        stdev_height = Math.round((Math.sqrt(meanOfDiffsh))*100.0)/100.0;


        String htmlAsString =
                "        ,,,,,,,Jump Performance Results \n" +
                        "        \n" +
                        "            ,,,,,,, Number of Jumps: ,"+jumpcount+",\n" +
                        "            ,,,,,,, Average Sampling Rate: ,"+String.format("%.2f",avg_sampling_rate)+", Hz\n" +
                        "            ,,,,,,,Average Jump height: ,"+String.format("%.2f",avg_height)+" m\n" +
                        "            ,,,,,,,Jump height Standard Deviation: ,"+String.format("%.2f",stdev_height)+" m\n" +
                        "            ,,,,,,,Average Time of Flight: ,"+String.format("%.2f",avg_tflight)+" s\n" +
                        "            ,,,,,,, Time of Flight Standard Deviation: ,"+String.format("%.2f",stdev_tflight)+" s\n" +
                        "            ,,,,,,, Average Initial Velocity: ,"+String.format("%.2f",avg_vi)+" m/s\n" +

                        "            ,,,,,,, Takeoff Threshold: ,"+String.format("%.2f",takoff_thr)+" m/s/s\n" +
                        "            ,,,,,,,Landing Threshold: ,"+String.format("%.2f",landing_thr)+" m/s/s\n" +
                        "            ,,,,,,,Avg Peak acceleration: ,"+String.format("%.2f",summintakeoff/jumpcount)+" m/s/s\n" +
                        "            ,,,,,,,Peak acceleration: ,"+String.format("%.2f",minpeak)+" m/s/s\n" +

                        "            ,,,,,,,Time of propulsion: 5, sec (tbd)\n" +
                        "        \n" ;
        data.append("\n\n" + htmlAsString);
                // #average TFlight
        // #Tflight stddev
        // #average Vi
        // #average Height
//       # My interpretation of how we detect TFlight manually
//       # find the resultant acceleration
//       # find the first sample in which the acceleration is larger than the takeoff threshold. (eg. 5 m/s^2) Record the timestamp of this takeoff sample and call it t_takeoff.
//       # find the first sample after the above takeoff sample  in which the acceleration is smaller than the landing threshold (eg. -30 m/s^2) Record the timestamp of this landing sample and call it t_landing.
//       # TFlight = T_landing -T_takeoff

        // #Height stddevkhb

//       # Number of Jumps
//     #   Avg Sampling Rate
//     #           TFlight
//      #  Vi
//     #   Jump Height
//      #  average
//      #  standard deviation
//        Time of Propulsion
//        Peak Acceleration
//        Parameters
//                Thres_takeoff
//        thres_landing
//


    }

    private void preprocess_inverse(int num_samples, double avg_sampling_rate) {
        double idle_time=4500; // assuming time 4.5s is the idle time
       // if (Math.abs(Collections.max(dataarr))<Math.abs(Collections.min(dataarr)))
            //11/17 sometimes it does not work
        long init_time=tsarr.get(0);
        int idlecnt=0;
        double sumacc=0;
        double idle_acc_flip=-15;
        for (int i=0; i<tsarr.size();i++)
        {
            if (((tsarr.get(i)-init_time)>idle_time) && ((tsarr.get(i)-init_time)<(idle_time+500))) {
                sumacc += dataarr.get(i);
                idlecnt+=1;
            }

        }
        data.append("\n\n sumacc "+String.format("%.2f",sumacc)+"idlecnt "+String.format("%d",idlecnt)+"." );
        if ((sumacc/(double)idlecnt)<idle_acc_flip)
        {
            for (int i=0; i<dataarr.size();i++) {
                dataarr.set(i,(dataarr.get(i)+9.81)*(-1)-9.81);
            }
            data.append("\n\n Data Flipped." );
        }
    }
}