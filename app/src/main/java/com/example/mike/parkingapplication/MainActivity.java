package com.example.mike.parkingapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.amazonaws.mobile.client.AWSMobileClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AWSMobileClient.getInstance().initialize(this).execute();
        noAcc();
        login();
    }

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
