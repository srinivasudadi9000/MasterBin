package bah.masterbin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bah.masterbin.model.Locality;
import bah.masterbin.model.Shifts;
import bah.masterbin.model.Ward;

public class Locality_View extends Activity implements View.OnClickListener {
    RecyclerView shift_recyler;
    ArrayList<Locality> localities;
    LocalityAdapter shiftAdapter;
    TextView header_title_tv;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locality_view);
        header_title_tv = (TextView) findViewById(R.id.header_title_tv);
        shift_recyler = (RecyclerView) findViewById(R.id.locality_recyler);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        header_title_tv.setText("Locality");

        SharedPreferences.Editor vendor = getBaseContext().getSharedPreferences("Shift", getBaseContext().MODE_PRIVATE).edit();
        vendor.putString("shift_name", getIntent().getStringExtra("shiftname").toString());
        vendor.commit();

       // Toast.makeText(getBaseContext(),getIntent().getStringExtra("shiftname").toString(),Toast.LENGTH_SHORT).show();
        GPSTracker gpsTracker = new GPSTracker(Locality_View.this);
        if (!Validation.isNetworkConnected(Locality_View.this)) {
            showInternet(Locality_View.this, "Please Check Internet Connection before login");
        }else {
            new Locality_View.JSONParse("asdfsd").execute();
        }
        localities = new ArrayList<Locality>();

        shift_recyler.setLayoutManager(new LinearLayoutManager(this));
        //recee_recyler.addOnItemTouchListener(new Recces_display.DrawerItemClickListener());
        shift_recyler.setHasFixedSize(true);
        shift_recyler.setItemViewCacheSize(20);
        shift_recyler.setDrawingCacheEnabled(true);
        shift_recyler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        shift_recyler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
    private void showInternet(Context activity, String message) {
        this.overridePendingTransition(R.anim.slideleft,
                R.anim.slideleft);
        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setMessage(message);
        alertbox.setTitle("Master Bin");
        alertbox.setIcon(R.drawable.vijayawadalog);

        alertbox.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        finish();
                     }
                });
        // String negativeText = context.getApplicationContext().getString(android.R.string.cancel);
        alertbox.show();
    }

    public void callnewone() {
        Intent i = new Intent(Locality_View.this, Dashboard.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GPSTracker gpsTracker = new GPSTracker(Locality_View.this);
        Location location = gpsTracker.getLocation();
        if (location != null) {

        } else {
            callnewone();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        wifichecking();
    }

    public void wifichecking() {
        GPSTracker gpsTracker = new GPSTracker(Locality_View.this);
        Location location = gpsTracker.getLocation();
        if (location != null) {

        } else {
            gpsTracker.showSettingsAlert();
        }
    }
    private class JSONParse extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        private ArrayList<NameValuePair> nameValuePairs;
        private JSONObject json;
        String id;

        public JSONParse(String id) {
            this.id = id;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            nameValuePairs = new ArrayList<NameValuePair>();


            SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
            String officerid = sharedPreferences.getString("intOfficerid", null);

            nameValuePairs.add(new BasicNameValuePair("intOfficerid", officerid));
            json = JSONParser.makeServiceCall("http://www.vmcbms.com/wsgetLocationListdata.aspx", 1, nameValuePairs);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONObject obj = new JSONObject(json.toString());
                if (obj.getString("status").equals("1")) {
                    JSONArray peoples = obj.getJSONArray("result");
                    //ToastText(getBaseContext(), peoples.toString(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < peoples.length(); i++) {
                        JSONObject cc = peoples.getJSONObject(i);

                        localities.add(new Locality(cc.getString("LocalityID"), cc.getString("Locality")));
                        shiftAdapter = new LocalityAdapter(localities, R.layout.locality, getApplicationContext());
                        shift_recyler.setAdapter(shiftAdapter);
                    }
                }else {
                    showInternet(Locality_View.this,"Don't Have Locations");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //ToastText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }

}
