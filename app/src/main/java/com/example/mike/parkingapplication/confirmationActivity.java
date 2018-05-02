package com.example.mike.parkingapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.pinpoint.analytics.monetization.AmazonMonetizationEventBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class confirmationActivity extends AppCompatActivity {
    TextView tt, tv, tv2,tv3;
    DynamoDBMapper dynamoDBMapper;
    PurchaseHistoryDO pc;
    double price;
    String licenceplate;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        selectLicencePlateDialog();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        tt = (TextView)findViewById(R.id.textView4);
        tv = (TextView)findViewById(R.id.textView5);
        tv2 = (TextView)findViewById(R.id.textView6);
        tv3 = (TextView)findViewById(R.id.textView7);
        String st = getIntent().getStringExtra("StartString");
        String et = getIntent().getStringExtra("EndString");

        tv.setText(st + " to " + et);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Map<String, ?> allEntries = this.getSharedPreferences("LicencePlate", Context.MODE_PRIVATE).getAll();
        if(allEntries.size() == 0){
            addLicencePlateDialog();
        }else{
            licenceplate = allEntries.entrySet().iterator().next().getKey();
            tv3.setText(licenceplate);
        }
        //selectLicencePlateDialog();
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
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLicencePlateDialog();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GarageScheduleDO scheduleItem = new GarageScheduleDO();
                scheduleItem.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
                scheduleItem.setParkID(getIntent().getDoubleExtra("ParkID",0));
                scheduleItem.setStartTime(getIntent().getDoubleExtra("StartTime", 0));
                scheduleItem.setEndTime(getIntent().getDoubleExtra("EndTime", 0));
                scheduleItem.setCheckedIn(Boolean.FALSE);
                scheduleItem.setPrice(price);
                scheduleItem.setLicencePlate(licenceplate);

                final Thread tb = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pc = dynamoDBMapper.load(
                                PurchaseHistoryDO.class,
                                IdentityManager.getDefaultIdentityManager().getCachedUserID());
                        // Item read
                        // Log.d("News Item:", newsItem.toString());
                    }
                });
                tb.start();
                try {
                    tb.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(pc == null){
                    pc = new PurchaseHistoryDO();
                    pc.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
                    DecimalFormat df = new DecimalFormat("0.00");
                    final double p = price;
                    List<String> prc = Arrays.asList(df.format(p));
                    final String s = Calendar.getInstance().getTime().toString();
                    List<String> dt = Arrays.asList(s);

                    //price.add(df.format(price));
                    //date.add(Calendar.getInstance().toString());
                    pc.setDate(dt);
                    pc.setPrice(prc);
                }else{
                    List<String> prc = pc.getPrice();
                    List<String> dt = pc.getDate();
                    DecimalFormat df = new DecimalFormat("0.00");
                    final double p = price;
                    prc.add(df.format(p));
                    dt.add(Calendar.getInstance().getTime().toString());
                    pc.setDate(dt);
                    pc.setPrice(prc);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dynamoDBMapper.save(pc);
                    }
                }).start();

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
    public void addLicencePlateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Enter Your Licence Plate");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor edit =  getSharedPreferences("LicencePlate",Context.MODE_PRIVATE).edit();
                edit.putString(input.getText().toString(), "");
                // Save your string in SharedPref
                edit.commit();
                selectLicencePlateDialog();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                // go back
            }
        });

        builder.show();
    }
    public void selectLicencePlateDialog(){
        final Map<String, ?> allEntries = this.getSharedPreferences("LicencePlate", Context.MODE_PRIVATE).getAll();
        final CharSequence LP[] = new CharSequence[allEntries.size()+2];
        int i = 0;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            LP[i] = entry.getKey();//Licence Plate
            i++;
        }
        LP[i] = "Add New Licence Plate";
        LP[i+1] = "Clear All";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a licence plate");
        builder.setItems(LP, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                if(which == allEntries.size()){
                    addLicencePlateDialog();
                    dialog.dismiss();
                }else if(which == (allEntries.size()+1))
                {
                    SharedPreferences.Editor edit =  getSharedPreferences("LicencePlate",Context.MODE_PRIVATE).edit();
                    edit.clear();
                    edit.commit();

                    addLicencePlateDialog();
                    dialog.dismiss();
                }else{
                    licenceplate = LP[which].toString();
                    tv3.setText(licenceplate);
                    //dialog.dismiss();
                }

            }
        });
        builder.show();
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
