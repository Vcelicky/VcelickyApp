package com.example.jozef.vcelicky;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jozef.vcelicky.app.AppConfig;
import com.example.jozef.vcelicky.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class HiveDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    int hiveID;
    String token;
    final String TAG = "HiveDetailsActivity";
    ArrayAdapter<HiveBaseInfo> allAdapter;
    String hiveName;
    ListView menuListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hive_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        // hiveId
        hiveID =  intent.getIntExtra("hiveId",0);
        hiveName = intent.getExtras().getString("hiveName");
        token =  intent.getExtras().getString("token");
        toolbar.setTitle("Včelí úľ "+hiveName);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
/////////////////
        createTestData();
        allAdapter = new AdapterHiveDetails(this, hiveList);
        menuListView = (ListView) findViewById(R.id.hiveDetailsListView);
        menuListView.setAdapter(allAdapter);

        loadHiveBaseInfoServerReq(hiveName);

    }

    public void createTestData(){

        Calendar ts =  new GregorianCalendar(1995, 2, 29, 11, 22);
        ts.set(1995, 2, 29, 11, 22) ;
        hiveList.add(new HiveBaseInfo(1234, "Včelí úľ Alfa", 55 , 45, 70, 69, new GregorianCalendar(1995, 2, 29, 11, 20)));
        hiveList.add(new HiveBaseInfo(1235, "Včelí úľ Alfa", 40 , 43, 68, 50, new GregorianCalendar(1995, 2, 29, 11, 30)));
        hiveList.add(new HiveBaseInfo(1236, "Včelí úľ Alfa", 30 , 42, 68, 60, new GregorianCalendar(1995, 2, 29, 11, 40)));
        hiveList.add(new HiveBaseInfo(1237, "Včelí úľ Alfa", 40 , 45, 50, 53, new GregorianCalendar(1995, 2, 29, 11, 50)));
        hiveList.add(new HiveBaseInfo(1238, "Včelí úľ Alfa", 35 , 43, 68, 56, new GregorianCalendar(1995, 2, 29, 12, 00)));
        hiveList.add(new HiveBaseInfo(1239, "Včelí úľ Alfa", 32 , 49, 61, 89, new GregorianCalendar(1995, 2, 29, 12, 10)));
        hiveList.add(new HiveBaseInfo(1240, "Včelí úľ Alfa", 36 , 45, 68, 66, new GregorianCalendar(1995, 2, 29, 12, 20)));
        hiveList.add(new HiveBaseInfo(1241, "Včelí úľ Alfa", 36 , 45, 68, 66, new GregorianCalendar(1995, 2, 29, 12, 30)));
        hiveList.add(new HiveBaseInfo(1242, "Včelí úľ Alfa", 36 , 45, 68, 66, new GregorianCalendar(1995, 2, 29, 12, 40)));
        hiveList.add(new HiveBaseInfo(1243, "Včelí úľ Alfa", 36 , 45, 68, 66, new GregorianCalendar(1995, 2, 29, 12, 50)));
    }


    public void loadHiveBaseInfoServerReq( String hiveName){

        Log.d(TAG, "Load Hive Details Info method");
        String tag_json_obj = "json_obj_req";
        JSONObject jsonBody = new JSONObject();

        Log.d(TAG, ": " + hiveName);
        Log.d(TAG, ": " + token);
        try {
            jsonBody.put("device_name", hiveName);
            jsonBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        final String requestBody = jsonBody.toString();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_GET_HIVE_INFO_DETAILS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Load Hive Base Info From Server Response: " + response.toString());

                try {
                    ///////////////////////
                    int it = 0;
                    int ot = 0;
                    int h = 0;
                    int p = 0;

                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONArray jsonArray2 = jsonArray.getJSONArray(i); // proccess additional []
                        int recordValue = 0;
                        for(int j=0;j<jsonArray2.length();j++){
                            JSONObject jo= jsonArray2.getJSONObject(j);
                            String type = jo.getString("typ");

                            if (type.equals("IT")) {
                                Log.d(TAG, "found IT : ");
                                it = jo.getInt("hodnota");
                            }
                            if (type.equals("OT")) {
                                Log.d(TAG, "found OT : ");
                                ot = jo.getInt("hodnota");
                            }
                            if (type.equals("H")) {
                                Log.d(TAG, "found H : ");
                                h = jo.getInt("hodnota");
                            }
                            if (type.equals("P")) {
                                Log.d(TAG, "found P : ");
                                //TODO P (proximity is not in this model) need HOTFIX // Weight is mising
                            }
                            String timeStamp = jo.getString("cas");
                            GregorianCalendar timeStampGregCal = parseDateFromVcelickaApi(timeStamp);
                            // parse date from tomo API time format (day.month.year.hour.minute)

                            Log.d(TAG, "Cas: "+timeStamp);

                            recordValue++;
                            if (recordValue == 4) {                     // every record have 4 values after that new record is processed
                                Log.d(TAG, "I will add new record to list: ");
                                hiveList.add(new HiveBaseInfo(0, "hiveNameIsNotUsedHere", ot, it, h, 0,timeStampGregCal));
                                menuListView = (ListView) findViewById(R.id.hiveDetailsListView);
                                menuListView.setAdapter(allAdapter);
                                recordValue = 0;
                            }
                        }

                    }

////////////////////////////
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG, "Login Error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee){
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }


    // parse date from tomo API time format (day.month.year.hour.minute)
    public GregorianCalendar parseDateFromVcelickaApi(String timeStamp){
        String[] timeStampParts = timeStamp.split("\\.", -1);
        int year=0, month = 0, day = 0, hour = 0, minute = 0;
        for (int s=0; s<timeStampParts.length;s++){
            Log.d(TAG, "P: "+timeStampParts[s]);
            if (s == 0){
                day = Integer.parseInt(timeStampParts[s]);
            }
            if (s == 1){
                month = Integer.parseInt(timeStampParts[s]);
            }
            if (s == 2){
                year = Integer.parseInt(timeStampParts[s]);
            }
            if (s == 3){
                hour = Integer.parseInt(timeStampParts[s]);
            }
            if (s == 4){
                minute = Integer.parseInt(timeStampParts[s]);
            }
        }
        return new GregorianCalendar(year, month, day, hour, minute);

    }




    @Override

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about_project) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
