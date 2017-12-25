package bah.masterbin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Dashboard extends Activity implements View.OnClickListener {
    Button garbage_point_monitoring, garbage_point_registeration,logout;
    GPSTracker gps;
    ProgressBar progressBar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        this.overridePendingTransition(R.anim.slideleft,
                R.anim.slideleft);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        clearPreferences();
        gps = new GPSTracker(Dashboard.this);
        if(gps.canGetLocation()){
           // startService(new Intent(this, Latitude.class));
        }else {
            gps.showSettingsAlert();
        }


        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(Dashboard.this);
        garbage_point_monitoring = (Button) findViewById(R.id.garbage_point_monitoring);
        garbage_point_monitoring.setOnClickListener(Dashboard.this);
        garbage_point_registeration = (Button) findViewById(R.id.garbage_point_registeration);
        garbage_point_registeration.setOnClickListener(Dashboard.this);
       // givepermission();
       /* if (Build.VERSION.SDK_INT >= 23) {
            givepermission();
        }
*/
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Dashboard.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1257);
        }

    }

    private void givepermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                 || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Dashboard.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1257);
        }else {
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
    public void callnewone(){
        Intent i = new Intent(Dashboard.this,Dashboard.class);
        startActivity(i);
        finish();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        GPSTracker gpsTracker = new GPSTracker(Dashboard.this);
        Location location = gpsTracker.getLocation();
        if (location != null){

        }else {
            callnewone();

/*
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            },30000);
*/
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                SharedPreferences.Editor vendor = getBaseContext().getSharedPreferences("Login", getBaseContext().MODE_PRIVATE).edit();
                vendor.putString("intUserid", "");
                vendor.putString("username","");
                vendor.putString("user_id","");
                vendor.putString("userlevel","");
                vendor.putString("intOfficerid", "");
                vendor.commit();
                Intent login = new Intent(Dashboard.this, Login.class);
                startActivity(login);
                finish();
                break;
            case R.id.garbage_point_registeration:


                if(gps.canGetLocation()){
                    garbage_point_registeration.setClickable(false);
                    progressBar1.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            garbage_point_registeration.setClickable(true);
                            progressBar1.setVisibility(View.GONE);
                            Intent registration = new Intent(Dashboard.this, Registration.class);
                            startActivity(registration);
                        }
                    },10000);

                }else {
                    gps.showSettingsAlert();
                }
                break;
            case R.id.garbage_point_monitoring:

                if(gps.canGetLocation()){
                    garbage_point_monitoring.setClickable(false);
                    progressBar1.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            garbage_point_monitoring.setClickable(true);
                            progressBar1.setVisibility(View.GONE);
                            Intent monitoring = new Intent(Dashboard.this, Monitoring.class);
                            startActivity(monitoring);
                        }
                    },10000);

                }else {
                    gps.showSettingsAlert();
                }

                break;

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

}
