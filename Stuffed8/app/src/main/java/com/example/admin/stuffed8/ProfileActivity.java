package com.example.admin.stuffed8;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements LocationListener {

    //TextView to show currently logged in user
    //Variables declaration
    Button get_location;
    private TextView textView;
    String pathToPicture = "";
    String email;
    private TrackGPS gps;
    LocationManager locationManager;

    //listen location changes
    LocationListener locationListener;
    String GPSLocation,GPSURL, streetName, cityName, countryName;
    Double latitude, longitude;
    Geocoder geocoder;
    List<Address> addresses;
    File picture;
    ArrayList<HashMap<String, String>> personList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondmain);

        //Initializing TextView
        textView = (TextView) findViewById(R.id.textView);

        //ListView
        //list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String,String>>();

        //Fetching email from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");

        //Showing the current logged in email to textview
        int index = email.indexOf('@');
        String email2 = email.substring(0,index);
        textView.setText("Hi, " + email2 + ". Welcome!");
    }

    //Logout function
    private void logout() {
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");

                        //Saving the sharedpreferences
                        //editor.commit();
                        editor.apply();

                        //Starting login activity
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuLogout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    Intent submitIntent = null;
    boolean isTakingPicture = false;
    boolean isGPS = false;

    public void bMainevent_OnClick(View v) {
        submitIntent = new Intent(this, email.class);
        takePicture();
        //findViews();
    }

    /* take picture */
    private void takePicture() {
        isTakingPicture = true;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        /* make sure intent is going to work */
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            /* create file for picture */
            File pictureFile = null;
            try {
                pictureFile = createPictureFile();
            } catch (IOException ex) {
                /* catch errors */
            }
            /* if file is created successfully, continue */
            if (pictureFile != null) {
                //URI pictureURI = pictureFile.toURI();
                    Uri pictureURI = FileProvider.getUriForFile(this,
                         "com.example.android.fileprovider",
                         pictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTakingPicture) {
            isTakingPicture = false;
            isGPS = true;
            findViews();
        }
    }

    void StartSendEmail()
    {
        isGPS = false;
        //Passing the values to email activity
        submitIntent.putExtra("pathtopic", pathToPicture);
        submitIntent.putExtra("gpsval", GPSURL);
        submitIntent.putExtra("useremailid", email);
        startActivity(submitIntent);
    }

    /* retrieve picture, show above button */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            File pictureFile = new  File(pathToPicture);
            /* if picture was taken, then display it */

        }

    }

    private File createPictureFile() throws IOException {
        /* use data as file name - once the rest of the application is more complete, we'll likely want to include the username */
        String dateTime = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        /* ex. filename could be username_dateTime */
        String pictureFileName = dateTime;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        /* create the file for the picture */
        picture = File.createTempFile(
                pictureFileName, ".jpg", storageDir
        );

        /* store the path to the picture that was taken */
        pathToPicture = picture.getAbsolutePath();
        return picture;
    }

//location code
    public void findViews() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
        }
        //else {
        //    locationManager.requestLocationUpdates("gps", 5000, 0, this);
        //
        //    return;
        //}
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // create class object
            gps = new TrackGPS(ProfileActivity.this);
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
            //Do not want to display the location message in the front view
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    //+ latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            getCompleteAddressString(latitude, longitude);
            GPSLocation = "  Latitude: " + latitude + "\n" + "  Longtitude: " + longitude
                    + "\n" + "  Street,City & Country Name: " + streetName + "\n\n";

            //Storing the Latitude and Longitude in the google map URL
            GPSURL = "http://maps.google.com/maps?z=12&t=m&q=" + latitude + "," + longitude;
            //Do not want to display the location message in the front view
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLocation: "
                   // + GPSLocation, Toast.LENGTH_LONG).show();
            // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(lon) + "\nLatitude:" + Double.toString(lat), Toast.LENGTH_SHORT).show();
            //Create a Uri from an intent string. Use the result to create an Intent.
            if (isGPS)
                StartSendEmail();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("location",latitude.toString());
        getCompleteAddressString(latitude,longitude);
        GPSLocation= "  Latitude: "+latitude+"\n"+"  Longtitude: "+longitude
                +"\n"+"  Street,City & Country Name: "+streetName+"\n";
        GPSURL = "http://maps.google.com/maps?z=12&t=m&q=" + latitude + "," + longitude;
        //Toast.makeText(getApplicationContext(), "Your Location is - \nLocation: "
                //+ GPSLocation, Toast.LENGTH_LONG).show();

        if (isGPS)
            StartSendEmail();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    //get streetName,cityName,CountryName based on longitude and latitude
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                streetName = addresses.get(0).getAddressLine(0);
                countryName = addresses.get(0).getAddressLine(2);
                cityName = addresses.get(0).getAddressLine(1);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }
}
