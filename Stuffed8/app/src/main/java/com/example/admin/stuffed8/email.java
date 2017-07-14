package com.example.admin.stuffed8;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class email extends AppCompatActivity implements View.OnClickListener {

    //Variables Declaration
    private Button buttonSendingEmail;
    String  s,usermail;
    String gps, contentString;
    String subjectString = "Sending my image and location";
    private String[] pmail3;
    private String[] smail3;
    private String[] usermail3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        //passing the image path information from camera activity
        s = getIntent().getStringExtra("pathtopic");

        //Passing the location information from GPS activity
        gps = getIntent().getStringExtra("gpsval");
        contentString = gps;

        //Logged user mail id
        usermail = getIntent().getStringExtra("useremailid");

        /*
        //pictureFile = new File(s);
        //View the taken picture here
        if (pictureFile.exists()) {
            Bitmap pictureBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(pictureBitmap);
        }
           */

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
        }, 4000);
    }

    private void sendEmail() {

       //Storing the email JSON data
       pmail3 = LoginActivity.pmail2;
       smail3 = LoginActivity.smail2;
        usermail3 = LoginActivity.usermail2;


        //Creating SendMail object
        BackgroundEmail backgroundEmail = new BackgroundEmail(this,pmail3[0],smail3[0],usermail3[0],subjectString, contentString,s);

        //Executing backgroundEmail to send email
        backgroundEmail.execute();

        //Invoking Profile Activity - to reinitiate the process
        Intent i2 = new  Intent(email.this,ProfileActivity.class);
        startActivity(i2);
    }

    public void buttonClick() {
        //Initializing the views
        buttonSendingEmail = (Button) findViewById(R.id.send);

        //Adding click listener
        buttonSendingEmail.setOnClickListener(this);

        //Hide the button in the front end
        buttonSendingEmail.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        sendEmail();
    }
}


