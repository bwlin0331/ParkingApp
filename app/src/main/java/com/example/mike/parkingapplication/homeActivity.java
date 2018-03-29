package com.example.mike.parkingapplication;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Thread.sleep;


public class homeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private AWSConfiguration awsConfiguration;
    TextView tv;
    DatePicker dp;
    NumberPicker np,np2;
    Calendar startTime;
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
        dp = (DatePicker) findViewById(R.id.datePicker);

        //number picker
        np = (NumberPicker) findViewById(R.id.numberPicker);
        np2 = (NumberPicker)findViewById(R.id.numberPicker2);
        pb = (ProgressBar)findViewById(R.id.pB);

        np.setMinValue(0);
        np.setMaxValue(24);
        np.setWrapSelectorWheel(true);
        np2.setMinValue(15);
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


        // build out database instance
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(homeActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkTimes();
    }


    public void checkTimes(){
        search = (Button)findViewById(R.id.button2);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time1 = tv.getText().toString();
                pb.setVisibility(View.VISIBLE);
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

                final Thread t = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        List<GarageScheduleDO> scanResult = dynamoDBMapper.scan(GarageScheduleDO.class,scanExpression);
                        Set<Integer> spotList = new HashSet<Integer>();
                        System.out.println(scanResult.size());
                        if(scanResult.size() >= capacity){
                            found = false;
                            return;
                        }
                        else {
                            found = true;

                            for (GarageScheduleDO scan : scanResult) {
                                spotList.add(scan.getSpotID().intValue());
                            }
                            int i;
                            for(i = 1; i < capacity; i++){
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
                while(t.isAlive()){
                    SystemClock.sleep(250);
                }
                pb.setVisibility(View.INVISIBLE);
                if(!found){
                    Toast.makeText(getBaseContext(), "There are no available parking spots at this time", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Parking Spot Found", Toast.LENGTH_LONG).show();
                    Intent transition = new Intent(getBaseContext(), confirmationActivity.class);
                    transition.putExtra("ParkID", spot);
                    transition.putExtra("StartTime", Double.parseDouble(startatt));
                    transition.putExtra("EndTime", Double.parseDouble(endatt));
                    transition.putExtra("StartString", start.getTime().toString());
                    transition.putExtra("EndString",end.getTime().toString());
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
            Toast.makeText(this, "This is current", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.orderHistory){
            Toast.makeText(this, "This is order", Toast.LENGTH_SHORT).show();
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
