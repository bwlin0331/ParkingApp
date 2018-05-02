package com.example.mike.parkingapplication;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDexApplication;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewAnimator;


import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;


import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.pinpoint.analytics.monetization.AmazonMonetizationEventBuilder;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class homeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //public static PinpointManager pinpointManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private AWSConfiguration awsConfiguration;
    List<GarageScheduleDO> dataP;
    TextView tv;
    DatePicker dp;
    NumberPicker np,np2;
    Calendar startTime;
    BarChart chart;
    ArrayList<String> xlabel;
    //public static variables
    public static ViewAnimator va;
    public static CalendarView cv;
    public static TextView tvr;
    GarageScheduleDO reservation;
    final int capacity;
    {
        capacity = 5;
    }

    int hour, minute, spot;
    String format, time1, time2;
    Button search;
    ProgressBar pb;
    boolean found;
    DynamoDBMapper dynamoDBMapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //date
        chart = (BarChart) findViewById(R.id.barchart);
        chart.animateY(1000);
        xlabel = new ArrayList<String>();
        xlabel.add("12:00 am");
        xlabel.add("1:00 am");
        xlabel.add("2:00 am");
        xlabel.add("3:00 am");
        xlabel.add("4:00 am");
        xlabel.add("5:00 am");
        xlabel.add("6:00 am");
        xlabel.add("7:00 am");
        xlabel.add("8:00 am");
        xlabel.add("9:00 am");
        xlabel.add("10:00 am");
        xlabel.add("11:00 am");
        xlabel.add("12:00 pm");
        xlabel.add("1:00 pm");
        xlabel.add("2:00 pm");
        xlabel.add("3:00 pm");
        xlabel.add("4:00 pm");
        xlabel.add("5:00 pm");
        xlabel.add("6:00 pm");
        xlabel.add("7:00 pm");
        xlabel.add("8:00 pm");
        xlabel.add("9:00 pm");
        xlabel.add("10:00 pm");
        xlabel.add("11:00 pm");
        dp = (DatePicker) findViewById(R.id.datePicker);
        tvr = (TextView) findViewById(R.id.textView9);
        //number picker
        np = (NumberPicker) findViewById(R.id.numberPicker);
        np2 = (NumberPicker)findViewById(R.id.numberPicker2);
        //pb = (ProgressBar)findViewById(R.id.pB);
        va = (ViewAnimator) findViewById(R.id.viewAnimator);
        cv = (CalendarView) findViewById(R.id.calendarView);
        cv.setEnabled(false);
        np.setMinValue(0);
        np.setMaxValue(24);
        np.setWrapSelectorWheel(true);
        np2.setMinValue(0);
        np2.setMaxValue(59);
        np.setWrapSelectorWheel(true);
        //build timing variables
        tv = (TextView) findViewById(R.id.TimeText);
        time1 = tv.getText().toString();
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                // Initialize a new time picker dialog fragment
                DialogFragment dFragment = new TimePickerFragment();

                // Show the time picker dialog fragment

                dFragment.show(getFragmentManager(),"Time Picker");

            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Toast.makeText(getBaseContext(), "Graph Update", Toast.LENGTH_LONG).show();
                final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                Calendar start = new GregorianCalendar(year,month,dayOfMonth,0,0);
                Calendar end = new GregorianCalendar(year,month,dayOfMonth+1,0,0);
                String startatt = String.valueOf(start.getTimeInMillis()/1000);
                String endatt = String.valueOf(end.getTimeInMillis()/1000);
                scanExpression.addFilterCondition("StartTime",
                        new Condition()
                                .withComparisonOperator(ComparisonOperator.LE)
                                .withAttributeValueList(new AttributeValue().withN(endatt)));
                scanExpression.addFilterCondition("EndTime",
                        new Condition()
                                .withComparisonOperator(ComparisonOperator.GE)
                                .withAttributeValueList(new AttributeValue().withN(startatt)));

                final Thread t = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        dataP = dynamoDBMapper.scan(GarageScheduleDO.class,scanExpression);
                       //System.out.println(scanResult.size());
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                float x[] = new float[24];
                for(int i = 0; i < 24; i++){
                    x[i] = 0f;
                }
                for(GarageScheduleDO next: dataP){
                    for(int i = 0; i < 24; i++){
                        Calendar temp = new GregorianCalendar(year,month,dayOfMonth,i,0);
                        double time = temp.getTimeInMillis()/1000.0;
                        if(time >= next.getStartTime() && time <= next.getEndTime()){
                            x[i] = x[i] + 1.0f;
                        }
                    }
                }
                ArrayList<BarEntry> valueset = new ArrayList<>();
                for(int i = 0; i < 24; i++){
                    valueset.add(new BarEntry(i,x[i]));
                }
                BarDataSet dataSet = new BarDataSet(valueset,"Busy hours");
                BarData data = new BarData(dataSet);

                chart.setData(data);
                chart.animateXY(2000,2000);
                chart.invalidate();
                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return xlabel.get((int)value);
                    }
                });
            }
        });

        // build out database instance
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();


        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                reservation = dynamoDBMapper.load(
                        GarageScheduleDO.class,
                        IdentityManager.getDefaultIdentityManager().getCachedUserID());
                // Item read
                // Log.d("News Item:", newsItem.toString());
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(reservation == null){
            va.showNext();
        }else{
            double st = reservation.getStartTime()*1000;
            Calendar std = Calendar.getInstance();
            std.setTimeInMillis((long)st);
            double et = reservation.getEndTime()*1000;
            Calendar etd = Calendar.getInstance();
            etd.setTimeInMillis((long)et);
            int parkID = reservation.getParkID().intValue();
            tvr.setText("From " + std.getTime().toString() + " To " + etd.getTime().toString() + "\n"
                        + "ParkID: " + parkID);
            cv.setDate((long)st);

        }



        /*DynamoDBQueryExpression query = new DynamoDBQueryExpression()
                .withHashKeyValues(note)*/


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(homeActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkTimes();
        cancel();
        checkIn();

    }

    public void logEvent() {
        MainActivity.pinpointManager.getSessionClient().startSession();
        final AnalyticsEvent event =
                MainActivity.pinpointManager.getAnalyticsClient().createEvent("Parking")
                        .withAttribute("Parker", "Parked")
                        .withMetric("Spot", Math.random());

        MainActivity.pinpointManager.getAnalyticsClient().recordEvent(event);
        MainActivity.pinpointManager.getSessionClient().stopSession();
        MainActivity.pinpointManager.getAnalyticsClient().submitEvents();
    }
    public void cancel(){
        Button cb = (Button)findViewById(R.id.button3);


        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                final GarageScheduleDO item = new GarageScheduleDO();
                item.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dynamoDBMapper.delete(item);

                        // Item deleted
                    }
                }).start();
                va.showNext();
                Toast.makeText(getBaseContext(), "Cancelled Reservation", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void checkIn() {
            Button but = (Button)findViewById(R.id.button4);

            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(homeActivity.this, QR_Code.class));
                }
            });
    }

    public void checkTimes(){
        search = (Button)findViewById(R.id.button2);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logEvent();
                time1 = tv.getText().toString();
                //pb.setVisibility(View.VISIBLE);
               // System.out.println(time1);
                int hour1, hour2, min1, min2, tot1, tot2;
                final int ind1 = time1.indexOf(':');
                int ind2 = time1.lastIndexOf(':');
                hour1 = Integer.parseInt(time1.substring(0,ind1));
                //System.out.println(hour1);
                min1 = Integer.parseInt(time1.substring(ind1+1,ind2));
               // System.out.println(min1);

                if(time1.substring(ind2+1).equals("PM")){

                    hour1 += 12;

                }


                //System.out.println(np.getValue() + " " + min1);
                //System.out.println(hour2 + " " + min2);
                //tot1 = hour1*60 + min1;


                //System.out.println(tot1 + " " + tot2);
                Calendar start = new GregorianCalendar(dp.getYear(),dp.getMonth(),dp.getDayOfMonth(),hour1,min1);
                Calendar end = new GregorianCalendar(dp.getYear(),dp.getMonth(),dp.getDayOfMonth(),hour1+np.getValue(),min1+np2.getValue());

                System.out.println(start.getTime().toString());
                System.out.println(end.getTime().toString());
                String startatt = String.valueOf(start.getTimeInMillis()/1000);
                String endatt = String.valueOf(end.getTimeInMillis()/1000);


                final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                scanExpression.addFilterCondition("StartTime",
                        new Condition()
                        .withComparisonOperator(ComparisonOperator.LE)
                        .withAttributeValueList(new AttributeValue().withN(endatt)));
                scanExpression.addFilterCondition("EndTime",
                        new Condition()
                        .withComparisonOperator(ComparisonOperator.GE)
                        .withAttributeValueList(new AttributeValue().withN(startatt)));

                //dynamoDBMapper.scan()
                /*final GarageScheduleDO schedule = new GarageScheduleDO();
                schedule.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
                schedule.setStartTime(Double.parseDouble(startatt));
                schedule.setEndTime(Double.parseDouble(endatt));*/
                final int[] size = new int[1];
                final Thread t = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        List<GarageScheduleDO> scanResult = dynamoDBMapper.scan(GarageScheduleDO.class,scanExpression);
                        Set<Integer> spotList = new HashSet<Integer>();
                        //System.out.println(scanResult.size());
                        if(scanResult.size() >= capacity){
                            found = false;
                            return;
                        }
                        else {
                            found = true;
                            size[0] = scanResult.size();
                            for (GarageScheduleDO scan : scanResult) {
                                spotList.add(scan.getParkID().intValue());
                            }
                            int i;
                            for(i = 1; i <= capacity; i++){
                                if(!spotList.contains(i)){
                                    spot = i;
                                    break;
                                }
                            }

                            //schedule.setSpotID((double)i);
                            //dynamoDBMapper.save(schedule);
                        }
                    }
                 });
                 t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //pb.setVisibility(View.INVISIBLE);
                if(!found){
                    Toast.makeText(getBaseContext(), "There are no available parking spots at this time", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Parking Spot Found", Toast.LENGTH_LONG).show();
                    long duration = (start.getTimeInMillis() - end.getTimeInMillis())/60000; //minutes duration

                    Intent transition = new Intent(getBaseContext(), confirmationActivity.class);
                    transition.putExtra("ParkID", (double)spot);
                    transition.putExtra("StartTime", Double.parseDouble(startatt));
                    transition.putExtra("EndTime", Double.parseDouble(endatt));
                    transition.putExtra("StartString", start.getTime().toString());
                    transition.putExtra("EndString",end.getTime().toString());
                    transition.putExtra("Duration",duration);
                    startActivity(transition);
                }

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.myAcc){
            startActivity(new Intent(homeActivity.this, accountActivity.class));
        }
        if(id == R.id.currRes){
            startActivity(new Intent(homeActivity.this, QR_Code.class));
        }
        if(id == R.id.orderHistory){
            Toast.makeText(this, "This is order", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.purchaseHistory){
            startActivity(new Intent(homeActivity.this, purchasehistoryActivity.class));
        }
        if(id == R.id.settings){
            startActivity(new Intent(homeActivity.this, settingsActivity.class));
        }
        if(id == R.id.logout){

            //AWSMobileClient.getInstance().getCredentialsProvider().refresh();
            IdentityManager.getDefaultIdentityManager().signOut();


            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        return false;
    }

}
