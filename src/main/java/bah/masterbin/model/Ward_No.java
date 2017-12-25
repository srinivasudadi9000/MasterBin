package bah.masterbin.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by srinivas on 16/12/17.
 */

public class Ward_No {
/*
    @SerializedName("result")
    List<Ward> ward_list;*/

    @SerializedName("WardDetails")
    JsonElement status;

   /* public List<Ward> getVendors_list() {
        return ward_list;
    }
*/
    public JsonElement getStatus() {
        return status;
    }

}
