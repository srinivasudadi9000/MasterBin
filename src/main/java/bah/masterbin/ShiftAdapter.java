package bah.masterbin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bah.masterbin.model.Shifts;

/**
 * Created by srinivas on 18/12/17.
 */

public class ShiftAdapter extends  RecyclerView.Adapter<ShiftAdapter.ShiftHolder>{

    private List<Shifts> shifts;
     private int rowLayout;
    public Context context;

    public ShiftAdapter(List<Shifts> shifts, int rowLayout, Context context) {
        this.shifts = shifts;
         this.rowLayout = rowLayout;
        this.context = context;
    }
    @Override
    public ShiftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ShiftHolder(view);
     }

    @Override
    public void onBindViewHolder(ShiftHolder holder, final int position) {
        holder.shiftname.setText(shifts.get(position).getShift_name().toString());
        holder.shifttime.setText(shifts.get(position).getShift_time().toString());
        holder.shift_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(context,shifts.get(position).getShift_time(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context,Locality_View.class);
                i.putExtra("shiftname",shifts.get(position).getShift_name().toString());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
     }


    @Override
    public int getItemCount() {
        return shifts.size();
    }
    class ShiftHolder extends RecyclerView.ViewHolder{
        TextView shiftname, shifttime;
        LinearLayout shift_ll;
        public ShiftHolder(View itemView) {
            super(itemView);
            shift_ll = (LinearLayout)itemView.findViewById(R.id.shift_ll);
            shiftname = itemView.findViewById(R.id.shift_name);
            shifttime = itemView.findViewById(R.id.shift_time);
         }
    }

}
