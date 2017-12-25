package bah.masterbin.rest;

import bah.masterbin.model.Ward_No;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by srinivas on 16/12/17.
 */

public  interface ApiInterface {

    @FormUrlEncoded
    //@POST("wsgetWardsdata.aspx/")
    @POST("/wsgetWardsdata.aspx?")
     Call<Ward_No> getVendors(@Field("intOfficerid") String intOfficerid);

}
