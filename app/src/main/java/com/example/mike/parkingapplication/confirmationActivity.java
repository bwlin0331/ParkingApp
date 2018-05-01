package com.example.mike.parkingapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.pinpoint.analytics.monetization.AmazonMonetizationEventBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class confirmationActivity extends AppCompatActivity {
    TextView tt, tv, tv2;
    DynamoDBMapper dynamoDBMapper;
    double price;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        tt = (TextView)findViewById(R.id.textView4);
        tv = (TextView)findViewById(R.id.textView5);
        tv2 = (TextView)findViewById(R.id.textView6);
        String st = getIntent().getStringExtra("StartString");
        String et = getIntent().getStringExtra("EndString");

        tv.setText(st + " to " + et);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        price = ((double)getIntent().getLongExtra("Duration", 0))*0.083;
        price = price*(-1);
        DecimalFormat df = new DecimalFormat("0.00");
        tv2.setText("Price: " + df.format(price) + "$");
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.reserveFB);
        final FloatingActionButton esc = (FloatingActionButton) findViewById(R.id.cancelBT);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GarageScheduleDO scheduleItem = new GarageScheduleDO();
                scheduleItem.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
                scheduleItem.setParkID(getIntent().getDoubleExtra("ParkID",0));
                scheduleItem.setStartTime(getIntent().getDoubleExtra("StartTime", 0));
                scheduleItem.setEndTime(getIntent().getDoubleExtra("EndTime", 0));
                Calendar cs = Calendar.getInstance();
                cs.setTimeInMillis((long)(scheduleItem.getStartTime()*1000));
                Calendar ce = Calendar.getInstance();
                ce.setTimeInMillis((long)(scheduleItem.getEndTime()*1000));
                //homeActivity.ro.setVisibility(View.GONE);
                homeActivity.va.showPrevious();
                homeActivity.tvr.setText("From " + cs.getTime().toString() + " To " + ce.getTime().toString() + "\n"
                        + "ParkID: " + scheduleItem.getParkID().intValue());
                homeActivity.cv.setDate(cs.getTimeInMillis());
                final Thread t = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        dynamoDBMapper.save(scheduleItem);
                    }
                });
                t.start();
                tt.setText("Reservation Confirmed");
                Snackbar.make(view, "Reservation Success", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                logMonetizationEvent();
                fab.setClickable(false);

                //generate code here
            }
        });

        esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void logMonetizationEvent() {
        MainActivity.pinpointManager.getSessionClient().startSession();

        final AnalyticsEvent event =
                AmazonMonetizationEventBuilder.create(MainActivity.pinpointManager.getAnalyticsClient())
                        .withCurrency("USD")
                        .withItemPrice(price)
                        .withProductId("Parking Reservation")
                        .withQuantity(1.0).build();

        MainActivity.pinpointManager.getAnalyticsClient().recordEvent(event);
        MainActivity.pinpointManager.getSessionClient().stopSession();
        MainActivity.pinpointManager.getAnalyticsClient().submitEvents();
    }
}
