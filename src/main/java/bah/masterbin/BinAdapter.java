package bah.masterbin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bah.masterbin.model.Bin;
import bah.masterbin.model.Shifts;

/**
 * Created by srinivas on 18/12/17.
 */

public class BinAdapter extends  RecyclerView.Adapter<BinAdapter.ShiftHolder>{

    private List<Bin> bins;
     private int rowLayout;
    public Context context;
    private int lastSelectedPosition = -1;

    public BinAdapter(List<Bin> bins, int rowLayout, Context context) {
        this.bins = bins;
         this.rowLayout = rowLayout;
        this.context = context;
    }
    @Override
    public ShiftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ShiftHolder(view);
     }

    @Override
    public void onBindViewHolder(final ShiftHolder holder, final int position) {
        holder.bin_name.setText(bins.get(position).getBinNo().toString());
        holder.bin_no.setText(bins.get(position).getLocality().toString());
        holder.bin_radio_btn.setChecked(lastSelectedPosition == position);
        holder.bin_radio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();

                SharedPreferences.Editor vendor = context.getSharedPreferences("Bin",context.MODE_PRIVATE).edit();
                vendor.putString("BinNo", bins.get(position).getBinNo().toString());
                vendor.putString("Locality",bins.get(position).getLocality().toString());
                vendor.putString("Wardno", bins.get(position).getWardno().toString());
                vendor.putString("BinCubicCapacity", bins.get(position).getBinCubicCapacity().toString());
                vendor.putString("IntBinID", bins.get(position).getIntBinID().toString());
                vendor.putString("Latitude", bins.get(position).getLatitude().toString());
                vendor.putString("Longitude", bins.get(position).getLongitude().toString());
                vendor.commit();

               /* Toast.makeText(context,
                        "selected offer is " + bins.get(position).getBinNo(),
                        Toast.LENGTH_LONG).show();*/
            }
        });
     }


    @Override
    public int getItemCount() {
        return bins.size();
    }
    class ShiftHolder extends RecyclerView.ViewHolder{
        TextView bin_name, bin_no;
        LinearLayout bin_ll;
        RadioButton bin_radio_btn;
        public ShiftHolder(View itemView) {
            super(itemView);
            bin_ll = (LinearLayout)itemView.findViewById(R.id.bin_ll);
            bin_name = itemView.findViewById(R.id.bin_name);
            bin_no = itemView.findViewById(R.id.bin_no);
            bin_radio_btn = itemView.findViewById(R.id.bin_radio_btn);

         }
    }

}
