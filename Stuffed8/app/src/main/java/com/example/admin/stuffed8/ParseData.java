package com.example.admin.stuffed8;

/**
 * Created by ADMIN on 7/12/2017.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseData {

    public static final String JSON_ARRAY = "result";
    public static final String KEY_USEREMAIL = "email";
    public static final String KEY_PMAIL = "primary_email";
    public static final String KEY_SMAIL = "secondary_email";

    //Variables declaration
    public static String pmail1[];
    public static String smail1[];
    public static String usermail1[];

    private JSONArray users = null;

    private String json;

    public ParseData(String json){
        this.json = json;
    }

    protected void parseData(){
        JSONObject jsonObject=null;
        try {

            jsonObject = new JSONObject(json);

            users = jsonObject.getJSONArray(JSON_ARRAY);

            usermail1 = new String[users.length()];
            pmail1 = new String[users.length()];
            smail1 = new String[users.length()];

            for(int i=0;i<users.length();i++) {
                JSONObject jobject = users.getJSONObject(i);
                 usermail1[i] = jobject.getString(KEY_USEREMAIL);
                 pmail1[i] = jobject.getString(KEY_PMAIL);
                 smail1[i] = jobject.getString(KEY_SMAIL);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

//Reference:
//https://www.simplifiedcoding.net/android-json-parsing-retrieve-from-mysql-database/