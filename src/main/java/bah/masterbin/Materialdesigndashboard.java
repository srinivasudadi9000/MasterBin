package bah.masterbin;

import android.animation.Animator;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

public class Materialdesigndashboard extends Activity implements View.OnClickListener {
    CardView mycardview_desing_1, mycardview_desing_2, mycardview_desing_3, mycardview_desing_4, mycardview_desing_5, mycardview_desing_6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.materialdesigndashboard);
        mycardview_desing_1 = (CardView) findViewById(R.id.mycardview_desing_1);
        mycardview_desing_1.setOnClickListener(this);

        mycardview_desing_2 = (CardView) findViewById(R.id.mycardview_desing_2);
        mycardview_desing_2.setOnClickListener(this);

        mycardview_desing_3 = (CardView) findViewById(R.id.mycardview_desing_3);
        mycardview_desing_3.setOnClickListener(this);

        mycardview_desing_4 = (CardView) findViewById(R.id.mycardview_desing_4);
        mycardview_desing_4.setOnClickListener(this);

        mycardview_desing_5 = (CardView) findViewById(R.id.mycardview_desing_5);
        mycardview_desing_5.setOnClickListener(this);

        mycardview_desing_6 = (CardView) findViewById(R.id.mycardview_desing_6);
        mycardview_desing_6.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mycardview_desing_1:
                GPSTracker gpsTracker = new GPSTracker(Materialdesigndashboard.this);
                gpsTracker.getLocation();
                String loca= String.valueOf(gpsTracker.canGetLocation());
                String res = String.valueOf("GPSTracker"+loca+" lat "+gpsTracker.getLatitude()+" long "+gpsTracker.getLongitude());
                Toast.makeText(getBaseContext(),res,Toast.LENGTH_SHORT).show();

                GPSService gpsService = new GPSService(Materialdesigndashboard.this);
                gpsService.getLocation();
                String ress = String.valueOf(gpsService.getLatitude()+" "+gpsService.getLongitude());
                Toast.makeText(getBaseContext(),"GPSService"+ress,Toast.LENGTH_SHORT).show();
                callanim(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.lime), mycardview_desing_1);
                break;
            case R.id.mycardview_desing_2:
                callanim(getResources().getColor(R.color.orange), getResources().getColor(R.color.blue), mycardview_desing_2);
                break;

            case R.id.mycardview_desing_3:
                callanim(getResources().getColor(R.color.blue), getResources().getColor(R.color.lime), mycardview_desing_3);
                break;
            case R.id.mycardview_desing_4:
                callanim(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.blue), mycardview_desing_4);
                break;

            case R.id.mycardview_desing_5:
                callanim(getResources().getColor(R.color.nizamclearcolor), getResources().getColor(R.color.lime), mycardview_desing_5);
                break;
            case R.id.mycardview_desing_6:
                callanim(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.blue), mycardview_desing_6);
                break;
        }
    }

    public void callanim(final int colorcode, final int fullcolor, final CardView cardView) {
        int x = cardView.getTop();
        int y = cardView.getLeft();

        int startRadius = Math.max(cardView.getWidth(), cardView.getHeight());
        int endRadius = 10;

        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(cardView, x, y, startRadius, endRadius);
        }
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                cardView.setBackgroundColor(colorcode);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                cardView.setBackgroundColor(fullcolor);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                cardView.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));

            }
        });
        anim.start();
    }

}
