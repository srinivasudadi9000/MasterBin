package bah.masterbin.model;

/**
 * Created by srinivas on 18/12/17.
 */

public class Bin{
    String BinNo,Locality,Wardno,BinCubicCapacity,Latitude,Longitude,intOfficerid,IntBinID;
    public Bin(String BinNo, String Locality,String Wardno,String BinCubicCapacity,String Latitude,String Longitude,
                  String intOfficerid,String IntBinID){
        this.BinNo= BinNo;   this.BinCubicCapacity= BinCubicCapacity;   this.Latitude= Latitude;
        this.Longitude= Longitude;   this.intOfficerid= intOfficerid;
        this.Locality = Locality; this.IntBinID = IntBinID;
        this.Wardno = Wardno;
    }

    public String getLocality() {
        return Locality;
    }

    public String getWardno() {
        return Wardno;
    }

    public String getBinCubicCapacity() {
        return BinCubicCapacity;
    }

    public String getBinNo() {
        return BinNo;
    }

    public String getIntBinID() {
        return IntBinID;
    }

    public String getIntOfficerid() {
        return intOfficerid;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }
}

