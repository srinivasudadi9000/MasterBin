package bah.masterbin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import bah.masterbin.model.Bin;
import bah.masterbin.model.Locality;
import bah.masterbin.model.Shifts;

public class GarbageStatus extends Activity implements View.OnClickListener {
    TextView shift_name, username_tv, location_name_tv;
    RadioGroup binradiogroup;
    ImageView binstatus_image, iv_back;
    Button submit_btn;
    RecyclerView bin_recyler;
    ArrayList<Bin> bins;
    BinAdapter binAdapter;
    TextView header_title_tv;
    String statusof_bin = "", ShiftId = "";
    Bitmap bitmap = null;
    Bitmap afterEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.garbagestatus);
        clearPreferences();
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        header_title_tv = (TextView) findViewById(R.id.header_title_tv);
        shift_name = (TextView) findViewById(R.id.shift_name);
        username_tv = (TextView) findViewById(R.id.username_tv);
        location_name_tv = (TextView) findViewById(R.id.location_name_tv);
        binradiogroup = (RadioGroup) findViewById(R.id.binradiogroup);
        binstatus_image = (ImageView) findViewById(R.id.binstatus_image);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        bin_recyler = (RecyclerView) findViewById(R.id.bin_recyler);
        submit_btn.setOnClickListener(this);
        binstatus_image.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        header_title_tv.setText("Monitoring Master Bin ");
        shift_name.setText("SHIFT : " + getIntent().getStringExtra("shift_name"));
        location_name_tv.setText(getIntent().getStringExtra("locality"));
        username_tv.setText("USER : " + getIntent().getStringExtra("username"));
        switch (getIntent().getStringExtra("shift_name")) {
            case "Morning":
                ShiftId = "1";
                break;
            case "Afternoon":
                ShiftId = "2";
                break;
            case "Night":
                ShiftId = "3";
                break;
        }
        setvaluestonull();
        bins = new ArrayList<Bin>();

        bin_recyler.setLayoutManager(new LinearLayoutManager(this));
        //recee_recyler.addOnItemTouchListener(new Recces_display.DrawerItemClickListener());
        bin_recyler.setHasFixedSize(true);
        bin_recyler.setItemViewCacheSize(20);
        bin_recyler.setDrawingCacheEnabled(true);
        bin_recyler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        if (!Validation.isNetworkConnected(GarbageStatus.this)) {
            showInternet(GarbageStatus.this, "Please Check Internet Connection before login");

        } else {
            new GarbageStatus.JSONParse(getIntent().getStringExtra("locality").toString()).execute();
        }

        binradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = binradiogroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                statusof_bin = radioButton.getText().toString();


            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                SharedPreferences ss = getSharedPreferences("Bin", MODE_PRIVATE);
                String binno = ss.getString("BinNo", "");
                //new GarbageStatus.InsertBin(getIntent().getStringExtra("locality").toString()).execute();
                if (statusof_bin.equals("")) {
                    showInternet(GarbageStatus.this, "Please Select Bin Status");
                }
                else if (binno.equals("")) {
                    showInternet(GarbageStatus.this, "Please Select Any One Bin");
                }
                else if (!Validation.isNetworkConnected(GarbageStatus.this)) {
                    showInternet(GarbageStatus.this, "Please Check Internet Connection Before Login");
                }else if (afterEdit==null){
                    showInternet(GarbageStatus.this, "Please Select Bin Image ");
                }
                else {
                    uploadImage(getIntent().getStringExtra("locality").toString());
                }
                break;
            case R.id.binstatus_image:
                //Toast.makeText(getBaseContext(), "lskjflasdkfas", Toast.LENGTH_SHORT).show();
                cameraIntent();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void showInternet(Context activity, String message) {
         AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setMessage(message);
        alertbox.setTitle("Master Bin");
        alertbox.setIcon(R.drawable.vijayawadalog);

        alertbox.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                     }
                });
        // String negativeText = context.getApplicationContext().getString(android.R.string.cancel);
        alertbox.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        wifichecking();
    }

    public void wifichecking() {
        GPSTracker gpsTracker = new GPSTracker(GarbageStatus.this);
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
        String Locality;

        public JSONParse(String Locality) {
            this.Locality = Locality;
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
            nameValuePairs.add(new BasicNameValuePair("Locality", Locality));
            json = JSONParser.makeServiceCall("http://www.vmcbms.com/wsgetBinListdata.aspx", 1, nameValuePairs);
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
                        // getting current lat and long
                        Double lat = 0.0, lat_long = 0.0,lat1=0.0,longitude2=0.0;
                       /* GPSTracker gpsTracker = new GPSTracker(GarbageStatus.this);
                        Location location = gpsTracker.getLocation();
                        if (location != null) {
                            lat = location.getLatitude();
                            lat_long = location.getLongitude();
                            lat1 = Double.valueOf(cc.getString("Latitude").toString());
                            longitude2 = Double.valueOf(cc.getString("Longitude").toString());
                        }*/
                        String shortlat,shortlong;
                        GPSService gpsService1 = new GPSService(GarbageStatus.this);
                        gpsService1.getLocation();
                        shortlat = String.valueOf(gpsService1.getLatitude());
                        shortlong = String.valueOf(gpsService1.getLongitude());
                        if (shortlat.length()>7){
                            lat = Double.valueOf(shortlat.substring(0,7));
                        }else {
                            lat = gpsService1.getLatitude();
                        }
                        if (shortlong.length()>7){
                            lat_long = Double.valueOf(shortlong.substring(0,7));
                        }else {
                            lat_long = gpsService1.getLatitude();
                        }
                        //Toast.makeText(getBaseContext(),"clati "+shortlat+"clongi  "+shortlong,Toast.LENGTH_SHORT).show();
                        lat1 = Double.valueOf(cc.getString("Latitude").toString().substring(0,7));
                        longitude2 = Double.valueOf(cc.getString("Longitude").toString().substring(0,7));

                        if (distance(lat,lat_long,lat1,longitude2)< 0.06){
                            String distanceinmiles = String.valueOf(distance(lat,lat_long,lat1,longitude2));
                            successmessge(GarbageStatus.this, " you are not at Bin clat "+lat+" clong "+lat_long+"   "
                                    +"olat "+lat1+"olongi "+longitude2+"  d:\n"+distanceinmiles+" : miles far from here Thankyou");

                            //  if (distance(lat,lat_long,lat1,longitude2)<0.0310686){
                            bins.add(new Bin(cc.getString("BinNo").toString(), cc.getString("Locality").toLowerCase()
                                    , cc.getString("Wardno").toString(), cc.getString("BinCubicCapacity").toString()
                                    , cc.getString("Latitude").toString(), cc.getString("Longitude").toString()
                                    , cc.getString("intOfficerid").toString(), cc.getString("IntBinID").toString()));
                        }else {
                            String distanceinmiles = String.valueOf(distance(lat,lat_long,lat1,longitude2));
                            successmessge(GarbageStatus.this, "sorry you are not at Bin "+lat+" clong "+lat_long+"   "
                                    +"olat "+lat1+"olongi "+longitude2+"  d: \n"+distanceinmiles+" : miles far from here Thankyou");
                           // Toast.makeText(getBaseContext(),"sorry you are not at Bin "+distanceinmiles+" : miles far from here Thankyou",Toast.LENGTH_SHORT).show();
                        }
                        binAdapter = new BinAdapter(bins, R.layout.bin, getApplicationContext());
                        bin_recyler.setAdapter(binAdapter);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //ToastText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    /** calculates the distance between two locations in MILES */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    private class InsertBin extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        private ArrayList<NameValuePair> nameValuePairs;
        private JSONObject json;
        String Locality;

        public InsertBin(String Locality) {
            this.Locality = Locality;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            nameValuePairs = new ArrayList<NameValuePair>();


            SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
            String officerid = sharedPreferences.getString("intOfficerid", null);

            SharedPreferences b = getSharedPreferences("Bin", MODE_PRIVATE);

            nameValuePairs.add(new BasicNameValuePair("intOfficerid", officerid));
            nameValuePairs.add(new BasicNameValuePair("intWardid", b.getString("Wardno", null)));
            nameValuePairs.add(new BasicNameValuePair("LocationName", Locality));
            nameValuePairs.add(new BasicNameValuePair("IntShiftID", shift_name.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("IntBinID", b.getString("IntBinID", null)));
            nameValuePairs.add(new BasicNameValuePair("CleanStatus", statusof_bin));
            nameValuePairs.add(new BasicNameValuePair("CapturePhotoName", "bin.jpg"));
            nameValuePairs.add(new BasicNameValuePair("CapturePhotoPath", getStringImage(afterEdit)));
            nameValuePairs.add(new BasicNameValuePair("CleanLatitude", b.getString("Latitude", null)));
            nameValuePairs.add(new BasicNameValuePair("CleanLangitude", b.getString("Longitude", null)));
            json = JSONParser.makeServiceCall("http://www.vmcbms.com/InsertBindClean.aspx", 2, nameValuePairs);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONObject obj = new JSONObject(json.toString());
                JSONArray peoples = obj.getJSONArray("result");
                Log.d("result", peoples.toString());

                for (int i = 0; i < peoples.length(); i++) {
                    successmessge(GarbageStatus.this, "Successfully Register Master Bin Thankyou");

                }
                //ToastText(getBaseContext(),peoples.toString(),Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
                //ToastText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void successmessge(Context activity, String message) {
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


    private void uploadImage(final String Locality) {

        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                //progress.dismiss();

                try {
                    JSONObject obj = new JSONObject(json.toString());
                    JSONArray peoples = obj.getJSONArray("result");
                    for (int i = 0; i < peoples.length(); i++) {
                        JSONObject jsonObject = peoples.getJSONObject(i);
                        if (jsonObject.getString("status").toString().equals("1")) {
                            successmessge(GarbageStatus.this, "Successfully Updated Clean Master Bin ");
                        } else {
                            successmessge(GarbageStatus.this, "Not Updated Clean Master Bin ");

                        }
                    }
                    Log.d("result", peoples.toString());
                    //ToastText(getBaseContext(),peoples.toString(),Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    //ToastText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                // Bitmap bitmap1 = params[0];
                String uploadImage1, uploadImage2, uploadImage3;


                HashMap<String, String> data = new HashMap<>();

                SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                String officerid = sharedPreferences.getString("intOfficerid", null);

                SharedPreferences b = getSharedPreferences("Bin", MODE_PRIVATE);

                String lat = "0.0", lat_long ="0.0";
                GPSTracker gpsTracker = new GPSTracker(GarbageStatus.this);
                Location location = gpsTracker.getLocation();
                if (location != null) {
                    lat = String.valueOf(location.getLatitude());
                    lat_long = String.valueOf(location.getLongitude());

                }

                data.put("intOfficerid", officerid);
                data.put("intWardid", b.getString("Wardno", null));
                data.put("LocationName", Locality);
                data.put("IntShiftID", ShiftId);
                data.put("IntBinID", b.getString("IntBinID", null));
                data.put("CleanStatus", statusof_bin);
                data.put("CapturePhotoName", "bin.jpg");
                data.put("CapturePhotoPath", getStringImage(afterEdit));
                data.put("CleanLatitude", lat);
                data.put("CleanLangitude",lat_long);
                System.out.print(data.toString());
                String result = rh.sendPostRequest("http://www.vmcbms.com/InsertBindClean.aspx", data);
                return result;


            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultcode", String.valueOf(requestCode));
        Log.d("resultcode", String.valueOf(resultCode));
        if (resultCode == -1) {
            onCaptureImageResult(data);
        }
    }

    private void cameraIntent() {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 0);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        GPSTracker gpsTracker = new GPSTracker(GarbageStatus.this);
        Location location = gpsTracker.getLocation();
        String lat = "0.0", lat_long = "0.0";
        if (location != null) {
            lat = String.valueOf(location.getLatitude() + "  ");
            lat_long = String.valueOf(location.getLongitude());
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        afterEdit = drawTextToBitmap(GarbageStatus.this, lat, lat_long, strDate, thumbnail);
        binstatus_image.setImageBitmap(afterEdit);
    }

    public String getStringImage(Bitmap bmp) {
        // Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.iot_all);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d("myimage", encodedImage.toString());
        return encodedImage;
    }

    public Bitmap drawTextToBitmap(Context mContext, String lat, String longitude, String cdate, Bitmap bitmap) {
        try {
            String mText = "lat: " + lat + "long: " + longitude;
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);
            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.RED);
            // text size in pixels
            paint.setTextSize((int) (8 * scale));
            paint.setTextAlign(Paint.Align.LEFT);
            // text shadow
            //  paint.setShadowLayer(1f, 0f, 1f, Color.RED);
            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 2;
            int y = (bitmap.getHeight() + bounds.height()) / 2;
            //change height
            // y = y - y / 2;
            canvas.drawText("  LAT: " + lat, x * scale / 8, y / 8, paint);
            canvas.drawText("  LONG: " + longitude, x * scale / 4, y / 4, paint);
            canvas.drawText("     " + cdate, x * scale / 2, y / 2, paint);
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    public void setvaluestonull() {
        SharedPreferences.Editor vendor = getSharedPreferences("Bin", MODE_PRIVATE).edit();
        vendor.putString("BinNo", "");
        vendor.putString("Locality", "");
        vendor.putString("Wardno", "");
        vendor.putString("BinCubicCapacity", "");
        vendor.putString("IntBinID", "");
        vendor.putString("Latitude", "");
        vendor.putString("Longitude", "");
        vendor.commit();
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
