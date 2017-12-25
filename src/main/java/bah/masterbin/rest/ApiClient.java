package bah.masterbin.rest;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by srinivas on 16/12/17.
 */

public class ApiClient {
    //
   // public static final String BASE_URL = "http://www.vmcbms.com/";
    public static final String BASE_URL = "http://www.vmcbms.com";
    private static Retrofit retrofit = null;

    public static Retrofit getSams(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        Log.d("callme",retrofit.toString());
        return retrofit;
    }
}
