package bah.masterbin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bah.masterbin.model.Locality;
import bah.masterbin.model.Ward;

public class Login extends Activity implements View.OnClickListener {
    EditText username_et, password_et;
    Button login_btn;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        /*this.overridePendingTransition(R.anim.slideleft,
                R.anim.slideleft);*/
        clearPreferences();
        turnGPSOn();
        gps = new GPSTracker(Login.this);
        if (gps.canGetLocation()) {
        } else {
           /* finish();
            Intent i = new Intent(Login.this,Login.class);
            startActivity(i);*/
            gps.showSettingsAlert();
        }
        login_btn = (Button) findViewById(R.id.login_btn);
        username_et = (EditText) findViewById(R.id.username_et);
        password_et = (EditText) findViewById(R.id.password_et);
        login_btn.setOnClickListener(this);
       /* if (Build.VERSION.SDK_INT >= 23) {
            givepermission();
        }*/
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Login.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1257);
        }
    }

    private void givepermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Login.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1257);
        } else {
            givepermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1257:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    givepermission();
                    // Toast.makeText(getApplicationContext(), "Please Allow Permissions to continue.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  Toast.makeText(getBaseContext(), "onresume", Toast.LENGTH_SHORT).show();
    }

    public void callnewone() {
        Intent i = new Intent(Login.this, Login.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        callnewone();
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  Toast.makeText(getBaseContext(), "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // Toast.makeText(getBaseContext(), "onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
      //  Toast.makeText(getBaseContext(), "onPostresume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if (!Validation.isNetworkConnected(Login.this)) {
                    showInternet(Login.this, "Please Check Internet Connection before login");
                } else {
                    if (username_et.getText().toString().length() == 0) {
                        showInternet(Login.this, "Username Should not be null");
                    } else if (password_et.getText().toString().length() == 0) {
                        showInternet(Login.this, "Password Should not be null");

                    } else {
                        if (gps.canGetLocation()) {
                            new Login.JSONParse(username_et.getText().toString(), password_et.getText().toString()).execute();
                        } else {
                            gps.showSettingsAlert();
                        }
                    }
                }
                break;
        }
    }

    private void internectconnection() {
        if (!Validation.isNetworkConnected(Login.this)) {
            showInternet(Login.this, "Please Check Internet Connection before login");
        }
    }

    private void showInternet(Context activity, String message) {

        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setMessage(message);
        alertbox.setTitle("MasterBin");
        alertbox.setIcon(R.drawable.vijayawadalog);
        alertbox.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        internectconnection();
                    }
                });
        // String negativeText = context.getApplicationContext().getString(android.R.string.cancel);
        alertbox.show();
        this.overridePendingTransition(R.anim.slideleft,
                R.anim.slideleft);
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        private ArrayList<NameValuePair> nameValuePairs;
        private JSONObject json;
        String username, password;

        public JSONParse(String username, String password) {
            this.username = username;
            this.password = password;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("OffUserid", username));
            nameValuePairs.add(new BasicNameValuePair("OffPassword", password));
            json = JSONParser.makeServiceCall("http://www.vmcbms.com/LoginService.aspx", 1, nameValuePairs);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONObject obj = new JSONObject(json.toString());
                if (obj.getString("status").equals("1")) {
                    JSONArray peoples = obj.getJSONArray("users");
                    // Toast.makeText(getBaseContext(), peoples.toString(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < peoples.length(); i++) {
                        JSONObject cc = peoples.getJSONObject(i);
                        SharedPreferences.Editor vendor = getBaseContext().getSharedPreferences("Login", getBaseContext().MODE_PRIVATE).edit();
                        vendor.putString("intUserid", cc.getString("intUserid"));
                        vendor.putString("username", cc.getString("username"));
                        vendor.putString("user_id", cc.getString("user_id"));
                        vendor.putString("userlevel", cc.getString("userlevel"));
                        vendor.putString("intOfficerid", cc.getString("intOfficerid"));
                        vendor.commit();

                        Intent dashboard = new Intent(Login.this, Dashboard.class);
                        startActivity(dashboard);
                        finish();
                    }
                } else {
                    showInternet(Login.this, "Invalid credentials pleasse contact admin and try again ..");

                }

            } catch (JSONException e) {
                e.printStackTrace();
                // Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void clearPreferences() {
        try {
            // clearing app data
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear YOUR_APP_PACKAGE_GOES HERE");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }


}
