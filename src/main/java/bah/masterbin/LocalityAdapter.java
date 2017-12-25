package bah.masterbin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bah.masterbin.model.Locality;
import bah.masterbin.model.Shifts;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by srinivas on 18/12/17.
 */

public class LocalityAdapter extends  RecyclerView.Adapter<LocalityAdapter.ShiftHolder>{

    private List<Locality> localities;
     private int rowLayout;
    public Context context;

    public LocalityAdapter(List<Locality> localities, int rowLayout, Context context) {
        this.localities = localities;
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
        holder.locality_tv.setText(localities.get(position).getLocality().toString());
         holder.locality_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context,localities.get(position).getLocality(),Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = context.getSharedPreferences("Shift",MODE_PRIVATE);
                String shift_name = sharedPreferences.getString("shift_name",null);
                SharedPreferences ss = context.getSharedPreferences("Login",MODE_PRIVATE);
                String username = ss.getString("username",null);

             //   Toast.makeText(context,shift_name+" "+username+" "+localities.get(position).getLocality().toString(),Toast.LENGTH_SHORT).show();

                Intent garbage = new Intent(context,GarbageStatus.class);
                garbage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                garbage.putExtra("shift_name",shift_name);
                garbage.putExtra("locality",localities.get(position).getLocality().toString());
                garbage.putExtra("username",username);
                context.startActivity(garbage);
            }
        });
     }


    @Override
    public int getItemCount() {
        return localities.size();
    }
    class ShiftHolder extends RecyclerView.ViewHolder{
        TextView locality_tv;
        LinearLayout locality_ll;
        public ShiftHolder(View itemView) {
            super(itemView);
            locality_ll = (LinearLayout)itemView.findViewById(R.id.locality_ll);
            locality_tv = itemView.findViewById(R.id.locality_tv);
          }
    }

}
