package com.example.admin.stuffed8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 *Reference of code: https://www.tutorialspoint.com/android/android_studio.html
 *
 * Reference 2: http://www.androidhive.info/2011/10/android-login-and-registration-screen-design/
 *
 * Reference of image: https://www.google.ca/search?q=login+image&rlz=1C1CHBF_enCA714CA714&tbm=isch&tbo=u&source=univ&sa=X&ved=0ahUKEwjB5qmQk8PUAhXLbD4KHe3_BvAQsAQIJw&biw=1920&bih=918&scra=search#imgdii=d_ezByas9vgVpM:&imgrc=YuzNK3aZ080LkM:
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //PHP script URL to retrieve primary and secondary email addresses from Database
    public static final String JSON_URL = "http://stuffed.x10host.com/retrive.php";

    //Variables declaration
    public static String pmail2[];
    public static String smail2[];
    public static String usermail2[];
    String email = null;
    String password = null;

    //Defining views
    private EditText editTextEmail;
    private EditText editTextPassword;
    private AppCompatButton buttonLogin;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);

        //Adding click listener
        buttonLogin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    //Creating Request to Server
    private void sendPOSTRequest(){
        StringRequest stringPOSTRequest = new StringRequest(Request.Method.POST,JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showResponseJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_EMAIL, email);
                //Password is not being used in SQL query, passing the parameter for testing.
                params.put(Config.KEY_PASSWORD, password);
                //returning parameter
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringPOSTRequest);
    }

    //Retrieving JSON response from Server
    private void showResponseJSON(String json){
        ParseData parsejsobj = new ParseData(json);
        parsejsobj.parseData();
        //Getting values from JSON Data
        usermail2 = parsejsobj.usermail1;
        pmail2 = parsejsobj.pmail1;
        smail2 = parsejsobj.smail1;
    }

    //Login
    private void login(){

        //Getting values from edit texts
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        //final String primary_email = null;

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){
                            //Creating a shared preference
                            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Config.EMAIL_SHARED_PREF, email);

                            //Saving values to editor
                            editor.commit();

                            //Starting profile activity
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Error handling
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_EMAIL, email);
                params.put(Config.KEY_PASSWORD, password);
                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onTextClick(View v) {
        Intent i2 = new  Intent(LoginActivity.this,Registration.class);
        startActivity(i2);
    }


    @Override
    public void onClick(View v) {
        //Calling the login function
        login();
        //Calling the POST request to server
        sendPOSTRequest();
    }
}
