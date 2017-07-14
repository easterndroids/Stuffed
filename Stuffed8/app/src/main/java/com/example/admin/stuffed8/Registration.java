package com.example.admin.stuffed8;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    //Variables declaration
    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private EditText editTextPrimaryEmail;
    private EditText editTextSecondaryEmail;
    private Button buttonRegister;

    //PHP script for registration
    private static final String REGISTER_URL = "http://stuffed.x10host.com/register.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPrimaryEmail = (EditText) findViewById(R.id.editTextPrimaryEmail);
        editTextSecondaryEmail = (EditText) findViewById(R.id.editTextSecondaryEmail);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            registerAppUser();
        }
    }

    private void registerAppUser() {
        //Capturing the user inputs
        String name = editTextName.getText().toString().trim().toLowerCase();
        String password = editTextPassword.getText().toString().trim().toLowerCase();
        String email = editTextEmail.getText().toString().trim().toLowerCase();
        String primaryemail = editTextPrimaryEmail.getText().toString().trim().toLowerCase();
        String secondaryemail = editTextSecondaryEmail.getText().toString().trim().toLowerCase();
        registerFirst(name,password,email,primaryemail,secondaryemail);
    }

    private void registerFirst(String name, String password, String email, String primaryemail, String secondaryemail) {
        class RegisterUser extends AsyncTask<String, Void, String>{
            ProgressDialog loading;
            RegistrationSub ruc = new RegistrationSub();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Registration.this, "Please Wait",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                Intent i2 = new  Intent(Registration.this,LoginActivity.class);
                startActivity(i2);
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("name",params[0]);
                data.put("password",params[1]);
                data.put("email",params[2]);
                data.put("primary_email",params[3]);
                data.put("secondary_email",params[4]);

                String result = ruc.sendRegisterRequest(REGISTER_URL,data);

                return  result;
            }
        }

        RegisterUser ruser = new RegisterUser();
        ruser.execute(name,password,email,primaryemail,secondaryemail);
    }
}

//Reference
//https://www.simplifiedcoding.net/android-user-registration-tutorial/