package bah.masterbin.model;

/**
 * Created by srinivas on 18/12/17.
 */

public class Shifts {
    String shift_name,shift_time,shiftType;
    public Shifts(String shift_name, String shift_time,String shiftType){
        this.shift_name= shift_name;
        this.shift_time = shift_time;
        this.shiftType = shiftType;
    }

    public String getShift_name() {
        return shift_name;
    }

    public String getShift_time() {
        return shift_time;
    }

    public String getShiftType() {
        return shiftType;
    }
}
