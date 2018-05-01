package com.example.mike.parkingapplication;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.AmazonWebServiceClient.*;
import com.amazonaws.*;

import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;

public class MainActivity extends AppCompatActivity {
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AWSMobileClient.getInstance().initialize(this).execute();
        noAcc();
        login();
    }*/
    // Declare a DynamoDBMapper object
    public static PinpointManager pinpointManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()

                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();*/
        // Add a call to initialize AWSMobileClient

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                AuthUIConfiguration config =
                        new AuthUIConfiguration.Builder()
                                .userPools(true)  // true? show the Email and Password UI
                                .logoResId(R.drawable.logo) // Change the logo
                                //.backgroundColor(Color) // Change the backgroundColor
                                .isBackgroundColorFullScreen(true) // Full screen backgroundColor the backgroundColor full screenff
                                .fontFamily("sans-serif-light") // Apply sans-serif-light as the global font
                                .canCancel(false)
                                .build();
                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(MainActivity.this, SignInUI.class);

                signin.login(MainActivity.this, homeActivity.class).authUIConfiguration(config).execute();
            }
        }).execute();
        PinpointConfiguration config = new PinpointConfiguration(
                MainActivity.this,
                AWSMobileClient.getInstance().getCredentialsProvider(),
                AWSMobileClient.getInstance().getConfiguration()
        );
        pinpointManager = new PinpointManager(config);
        pinpointManager.getSessionClient().startSession();
        pinpointManager.getAnalyticsClient().submitEvents();
        //finish();
    }


    public void OnDestroy(){
        pinpointManager.getSessionClient().stopSession();
        pinpointManager.getAnalyticsClient().submitEvents();
        super.onDestroy();
    }
}
/*
    private void noAcc(){
        TextView noAcc = findViewById(R.id.tvNoAcc);
        noAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                startActivity(new Intent(MainActivity.this, accountInfoActivity.class));
            }
        });
    }

    private void login(){
        Button login = findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, homeActivity.class));
            }
        });
    }
}
*/