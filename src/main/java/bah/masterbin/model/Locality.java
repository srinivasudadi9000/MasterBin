package bah.masterbin.model;

/**
 * Created by srinivas on 18/12/17.
 */

public class Locality {
    String locationid,shift_time,Locality;
    public Locality(String locationid, String Locality){
        this.locationid= locationid;
         this.Locality = Locality;
    }

    public String getLocality() {
        return Locality;
    }

    public String getLocationid() {
        return locationid;
    }
}
