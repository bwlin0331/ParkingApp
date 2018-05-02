package com.example.mike.parkingapplication;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class QR_Code extends AppCompatActivity {
    EditText text;
    Button gen_btn;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr__code);
        image = (ImageView) findViewById(R.id.image);


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {

            BitMatrix bitMatrix = multiFormatWriter.encode(IdentityManager.getDefaultIdentityManager().getCachedUserID(), BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}