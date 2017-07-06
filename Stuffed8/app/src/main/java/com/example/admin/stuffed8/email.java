package com.example.admin.stuffed8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.*;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class email extends AppCompatActivity {

    Button buttonSendingEmail = null;
    String subjectString, s, gps, contentString,usermail;
    File pictureFile;
    Uri path;
    String[] mailList = { "raju88apr@gmail.com","kvaishali1344@gmail.com","rajuanto1912@gmail.com",
             "saadmaanmahmid1990@gmail.com", "deepak.munjal15@gmail.com" };
    Random r = new Random();
    int i1 = (r.nextInt(4 - 0) + 0);

    boolean isSendingmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        subjectString = "Sending my image and location";
        //passing the image path information from camera activity
        s = getIntent().getStringExtra("pathtopic");
        //Passing the location information from GPS activity
        gps = getIntent().getStringExtra("gpsval");
        contentString = gps;
        usermail = getIntent().getStringExtra("useremailid");
        pictureFile = new File(s);
        //View the taken picture here
        if (pictureFile.exists()) {
            Bitmap pictureBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(pictureBitmap);
        }

        path = Uri.fromFile(pictureFile);
        buttonClick();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    buttonSendingEmail.performClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 5000);
    }

    public void buttonClick() {
        buttonSendingEmail = (Button) findViewById(R.id.send);
        buttonSendingEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the following piece of code opens an email client using intent
                Intent sendingEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                        mailList[i1],
                        null));
                sendingEmailIntent.putExtra(Intent.EXTRA_CC,new String[] { usermail });
                sendingEmailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectString);
                sendingEmailIntent.putExtra(Intent.EXTRA_TEXT, contentString);
                sendingEmailIntent.putExtra(Intent.EXTRA_STREAM, path);
                //To choose the available mail app in the mobile device
                //startActivity(Intent.createChooser(sendingEmailIntent, "Sending email"));
                //Default - will target to GMAIL app in the mobile device
                startActivity(sendingEmailIntent);
                isSendingmail = true;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (isSendingmail) {
            finish();
        }
    }
}

