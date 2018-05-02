package com.example.mike.parkingapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.List;

public class purchasehistoryActivity extends AppCompatActivity {
    PurchaseHistoryDO pc;
    DynamoDBMapper dynamoDBMapper;
    ListView listView;
    ArrayList<String> al;
    ArrayAdapter<String> ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchasehistory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView)findViewById(R.id.lv);

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

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
        al = new ArrayList<String>();

        if(pc != null) {
            List<String> dates = pc.getDate();
            List<String> price = pc.getPrice();
            for (int i = 0; i < dates.size(); i++) {
                al.add(String.valueOf(i) + " : " + dates.get(i) + "\n$" + price.get(i));
            }
        }

        ad = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, al);
        listView.setAdapter(ad);

    }

}
