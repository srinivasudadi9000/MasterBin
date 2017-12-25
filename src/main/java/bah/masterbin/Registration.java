package bah.masterbin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import bah.masterbin.model.Ward;
import bah.masterbin.model.Ward_No;
import bah.masterbin.rest.ApiClient;
import bah.masterbin.rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.Settings.AUTHORITY;

public class Registration extends Activity implements View.OnClickListener {
    ImageView iv_back, bin_photo_img;
    EditText bin_number_et, locality_name_et;
    Spinner Ward_No_spinner, Bin_Cubic_Capacity_spinner;
    Button registration_btn;
    File otherImagefile1 = null;
    ArrayAdapter<String> adc1, adc2;
    ArrayList<Ward> wards = new ArrayList<Ward>();
    ArrayList<String> displaywards = new ArrayList<>();
    Uri iv_url1 = null;
    Bitmap bitmap = null;
    Bitmap afterEdit = null;
    String wardid, locality, bincubiccapacity;
    String imageselectd = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        clearPreferences();
        this.overridePendingTransition(R.anim.slideleft,
                R.anim.slideleft);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        bin_photo_img = (ImageView) findViewById(R.id.bin_photo_img);
        bin_number_et = (EditText) findViewById(R.id.bin_number_et);
        locality_name_et = (EditText) findViewById(R.id.locality_name_et);
        registration_btn = (Button) findViewById(R.id.registration_btn);
        Ward_No_spinner = (Spinner) findViewById(R.id.Ward_No_spinner);
        Bin_Cubic_Capacity_spinner = (Spinner) findViewById(R.id.Bin_Cubic_Capacity_spinner);
        //loginSams();
        iv_back.setOnClickListener(this);
        registration_btn.setOnClickListener(this);
        bin_photo_img.setOnClickListener(this);
        wards.add(new Ward("--Select--", "0"));
        displaywards.add("--Select--");
        turnGPSOn();
        if (!Validation.isNetworkConnected(Registration.this)) {
            showInternet(Registration.this, "Please Check Internet Connection before login");

        } else {
            new Registration.JSONParse("asdfsd").execute();
        }
       /* GPSTracker gpsTracker = new GPSTracker(Registration.this);
        if (!Validation.isNetworkConnected(Registration.this)) {
            showInternet(Registration.this, "Please Check Internet Connection before login");
        }else {
            new Registration.JSONParse("asdfsd").execute();
        }*/
        String wardcount = String.valueOf(wards.size());
        //ToastText(getBaseContext(), wardcount, Toast.LENGTH_SHORT).show();

        adc1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, displaywards);
        adc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adc2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.cubic_capacity));
        adc2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Bin_Cubic_Capacity_spinner.setAdapter(adc2);

        Ward_No_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ToastText(getBaseContext(),wards.get(position).getWard_no().toString(),Toast.LENGTH_SHORT).show();
                wardid = wards.get(position).getWard_no().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //ToastText(getBaseContext(), "nothing selected", Toast.LENGTH_SHORT).show();
                wardid = "--Select--";
            }
        });

        Bin_Cubic_Capacity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ToastText(getBaseContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                bincubiccapacity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //ToastText(getBaseContext(), "nothing selected", Toast.LENGTH_SHORT).show();
                bincubiccapacity = "--Select--";
            }
        });

    }
    public void callnewone(){
        Intent i = new Intent(Registration.this,Dashboard.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GPSTracker gpsTracker = new GPSTracker(Registration.this);
        Location location = gpsTracker.getLocation();
        if (location != null){

        }else {
            callnewone();
        }
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

    private void uploadImage(final String intWardid, final String BinNo, final String Locality, final String BinCubicCapacity, final String GLatitude,
                             final String GLangitude, final String intOfficerid, final String BinPhotoName, final String BinPhotoPath) {

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
                    Log.d("result", peoples.toString());
                    for (int i = 0; i < peoples.length(); i++) {
                        JSONObject j = peoples.getJSONObject(i);
                        if (j.getString("status").equals("1")) {
                            successmessge(Registration.this, "Successfully Register Master Bin Thankyou");

                        } else {
                            successmessge(Registration.this, "Please try again bin not registered");

                        }
                    }
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

                data.put("intWardid", intWardid);
                data.put("BinNo", BinNo);
                data.put("Locality", Locality);
                data.put("BinCubicCapacity", BinCubicCapacity);
                data.put("GLatitude", GLatitude);
                data.put("GLangitude", GLangitude);
                data.put("intOfficerid", intOfficerid);
                data.put("BinPhotoName", "bin.png");
                data.put("BinPhotoPath", BinPhotoPath);

                System.out.print(data.toString());
                String result = rh.sendPostRequest("http://www.vmcbms.com/InsertBindMaster.aspx", data);
                return result;
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    private class InsertService extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        private ArrayList<NameValuePair> nameValuePairs;
        private JSONObject json;
        String intWardid, BinNo, Locality, BinCubicCapacity, GLatitude, GLangitude, intOfficerid, BinPhotoName, BinPhotoPath;

        public InsertService(String intWardid, String BinNo, String Locality, String BinCubicCapacity, String GLatitude,
                             String GLangitude, String intOfficerid, String BinPhotoName, String BinPhotoPath) {
            this.intWardid = intWardid;
            this.BinNo = BinNo;
            this.Locality = Locality;
            this.BinCubicCapacity = BinCubicCapacity;
            this.GLatitude = GLatitude;
            this.GLangitude = GLangitude;
            this.intOfficerid = intOfficerid;
            this.BinPhotoName = BinPhotoName;
            this.BinPhotoPath = BinPhotoPath;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {

            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("intWardid", intWardid));
            nameValuePairs.add(new BasicNameValuePair("BinNo", BinNo));
            nameValuePairs.add(new BasicNameValuePair("Locality", Locality));
            nameValuePairs.add(new BasicNameValuePair("BinCubicCapacity", BinCubicCapacity));
            nameValuePairs.add(new BasicNameValuePair("GLatitude", GLatitude));
            nameValuePairs.add(new BasicNameValuePair("GLangitude", GLangitude));
            nameValuePairs.add(new BasicNameValuePair("intOfficerid", intOfficerid));
            nameValuePairs.add(new BasicNameValuePair("BinPhotoName", BinPhotoName));
            nameValuePairs.add(new BasicNameValuePair("BinPhotoPath", BinPhotoPath));
            json = JSONParser.makeServiceCall("http://www.vmcbms.com/InsertBindMaster.aspx", 2, nameValuePairs);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONObject obj = new JSONObject(json.toString());
                JSONArray peoples = obj.getJSONArray("result");
                for (int i = 0; i < peoples.length(); i++) {
                    JSONObject j = peoples.getJSONObject(i);
                    if (j.getString("status").equals("1")) {
                        successmessge(Registration.this, "Successfully Register Master Bin Thankyou");

                    } else {
                        successmessge(Registration.this, "Please try again bin not registered");

                    }
                }
                //ToastText(getBaseContext(),peoples.toString(),Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
                //ToastText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.registration_btn:
                http:
//www.vmcbms.com/InsertBindMaster.aspx?intWardid=1000&BinNo=200&Locality=circle&BinCubicCapacity=2&GLatitude=20.22&
                // GLangitude=20.222&intOfficerid=1000&BinPhotoName=bin.jpg&BinPhotoPath=020323asdfasdfskldflaksdfjslafjsldkfjsdlf

                if (bin_number_et.getText().toString().length() == 0) {
                    showInternet(Registration.this, "Bin Number Should not be null");
                } else if (locality_name_et.getText().toString().length() == 0) {
                    showInternet(Registration.this, "Locality Should not be null");
                } else if (wardid.equals("--Select--")) {
                    showInternet(Registration.this, "Please select Ward Number");
                } else if (bincubiccapacity.equals("--Select--")) {
                    showInternet(Registration.this, "Please select BinCubicCapacity");
                } else if (imageselectd.equals("0")) {
                    showInternet(Registration.this, "Please Take Bin Image ");
                } else {
                    if (afterEdit != null) {
                        String lat = "0.0", lat_long = "0.0";
                        GPSTracker gpsTracker = new GPSTracker(Registration.this);
                        Location location = gpsTracker.getLocation();
                        if (location != null) {
                            lat = String.valueOf(location.getLatitude() + "  ");
                            lat_long = String.valueOf(location.getLongitude());
                            SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                            String officerid = sharedPreferences.getString("intOfficerid", null);
/*
                            new Registration.InsertService(wardid,bin_number_et.getText().toString(),locality_name_et.getText().toString(),
                                    bincubiccapacity,lat
                                    ,lat_long,officerid,"bin.jpg",getStringImage(afterEdit)).execute();
*/
                            if (!Validation.isNetworkConnected(Registration.this)) {
                                showInternet(Registration.this, "Please Check Internet Connection before login");
                            }else {
                                uploadImage(wardid, bin_number_et.getText().toString(), locality_name_et.getText().toString(),
                                        bincubiccapacity, lat
                                        , lat_long, officerid, "bin.jpg", getStringImage(afterEdit));
                            }

                        } else {
                            gpsTracker.showSettingsAlert();
                        }
                    }
                }
                // appLogin();
                break;
            case R.id.bin_photo_img:
                cameraIntent();
                break;
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        GPSTracker gpsTracker = new GPSTracker(Registration.this);
        Location location = gpsTracker.getLocation();
        String lat = "0.0", lat_long = "0.0";
        if (location != null) {
            lat = String.valueOf(location.getLatitude() + "  ");
            lat_long = String.valueOf(location.getLongitude());
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        afterEdit = drawTextToBitmap(Registration.this, lat, lat_long, strDate, thumbnail);
       // bin_photo_img.setMaxWidth(300);
        //bin_photo_img.setMaxHeight(300);
        bin_photo_img.setImageBitmap(afterEdit);
     /*   Uri tempUri = getImageUri(Registration.this, thumbnail);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
       // File finalFile = new File(getRealPathFromURI(String.valueOf(tempUri)));
        bitmap =(Bitmap) data.getExtras().get("data");

        // compressImage(finalFile.getAbsolutePath().toString());
        // ivImage.setImageBitmap(scaledBitmap);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ///thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            String lat="0.0",lat_long="0.0";
            //compressImage(destination.getAbsolutePath().toString());
            GPSTracker gpsTracker = new GPSTracker(Registration.this);
            Location location = gpsTracker.getLocation();
            if (location != null)
            {
                  lat = String.valueOf(location.getLatitude() + "  ");
                  lat_long = String.valueOf(location.getLongitude());
            }
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdf.format(c.getTime());

             afterEdit=drawTextToBitmap(Registration.this,lat , lat_long,strDate,bitmap);
            bin_photo_img.setImageBitmap(afterEdit);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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


    private void loginSams() {
        if (!Validation.isNetworkConnected(Registration.this)) {
            showInternet(Registration.this, "Please Check Internet Connection before login");

        } else {
            new Registration.JSONParse("asdfsd").execute();
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
                        loginSams();
                    }
                });
        // String negativeText = context.getApplicationContext().getString(android.R.string.cancel);
        alertbox.show();
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

            // nameValuePairs.add(new BasicNameValuePair("intOfficerid","1000"));
            //  json = JSONParser.makeServiceCall("http://www.vmcbms.com/wsgetWardsdata.aspx", 1,nameValuePairs);
            //?intGrievanceid=2017-VMC-3298&intOfficerid=1156

            /*nameValuePairs.add(new BasicNameValuePair("intGrievanceid","2017-VMC-3298"));
            nameValuePairs.add(new BasicNameValuePair("intOfficerid","1156"));
            json = JSONParser.makeServiceCall("http://www.vmc103.org/GrievanceDetbyNumber.aspx", 1,nameValuePairs);*/
            SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
            String officerid = sharedPreferences.getString("intOfficerid", null);

            nameValuePairs.add(new BasicNameValuePair("intOfficerid", officerid));
            json = JSONParser.makeServiceCall("http://www.vmcbms.com/wsgetWardsdata.aspx", 1, nameValuePairs);
            return json;
        }
// [{"CreateEvent":"success","status":1,"createevent_id":"1115  and Image URL : http:\/\/wwww.vmcbms.com\/BinImage\/1115_a.jpg"}]
        //{"BinDetails":"success","status":"1",
// "result":[{"BinNo":"ccyvy","Locality":"cycy","Wardno":"1002","BinCubicCapacity":"3.00","Latitude":"17.7282775",
//
// "Longitude":"83.3149562","intOfficerid":"2","IntBinID":"1068"}]}
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONObject obj = new JSONObject(json.toString());
                if (obj.getString("status").equals("1")) {
                    JSONArray peoples = obj.getJSONArray("result");
                    //ToastText(getBaseContext(), peoples.toString(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < peoples.length(); i++) {
                        JSONObject cc = peoples.getJSONObject(i);
                        wards.add(new Ward(cc.getString("intWardid"), cc.getString("WardNo")));
                        displaywards.add(cc.getString("WardNo"));
                        Ward_No_spinner.setAdapter(adc1);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //ToastText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultcode", String.valueOf(requestCode));
        Log.d("resultcode", String.valueOf(resultCode));
        if (resultCode == -1) {
            imageselectd = "selected";
            onCaptureImageResult(data);
        }
    }

    private void cameraIntent() {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 0);
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
