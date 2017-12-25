package bah.masterbin.model;

/**
 * Created by srinivas on 16/12/17.
 */

public class Ward {
   String ward_no,ward_id;
   public Ward(String ward_no, String ward_id){
       this.ward_id = ward_id;
       this.ward_no = ward_no;
   }

    public String getWard_id() {
        return ward_id;
    }

    public String getWard_no() {
        return ward_no;
    }
}
