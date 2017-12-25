package bah.masterbin;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import bah.masterbin.model.Shifts;

public class Monitoring extends Activity implements View.OnClickListener{
    RecyclerView shift_recyler;
    ArrayList<Shifts> shifts;
    ShiftAdapter shiftAdapter;
    TextView header_title_tv;
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        header_title_tv = (TextView)findViewById(R.id.header_title_tv);
        shift_recyler = (RecyclerView)findViewById(R.id.shift_recyler);
        iv_back.setOnClickListener(this);

        header_title_tv.setText("Shift Details ");


        shifts = new ArrayList<Shifts>();
        shifts.add(new Shifts("Morning","00:00 - 00:08","shiftA"));
        shifts.add(new Shifts("Afternoon","08:00 - 16:00","shiftB"));
        shifts.add(new Shifts("Night","16:00 - 23:59","shiftC"));

        shift_recyler.setLayoutManager(new LinearLayoutManager(this));
        //recee_recyler.addOnItemTouchListener(new Recces_display.DrawerItemClickListener());
        shift_recyler.setHasFixedSize(true);
        shift_recyler.setItemViewCacheSize(20);
        shift_recyler.setDrawingCacheEnabled(true);
        shift_recyler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        shiftAdapter = new ShiftAdapter(shifts, R.layout.shift, getApplicationContext());
        shift_recyler.setAdapter(shiftAdapter);
        shift_recyler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
    public void callnewone(){
        Intent i = new Intent(Monitoring.this,Dashboard.class);
        startActivity(i);
        finish();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        GPSTracker gpsTracker = new GPSTracker(Monitoring.this);
        Location location = gpsTracker.getLocation();
        if (location != null){

        }else {
            callnewone();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        wifichecking();
    }

    public void wifichecking(){
        GPSTracker gpsTracker = new GPSTracker(Monitoring.this);
        Location location = gpsTracker.getLocation();
        if (location != null){

        }else {
            gpsTracker.showSettingsAlert();
        }
    }
}
