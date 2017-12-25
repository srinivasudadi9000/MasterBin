package bah.masterbin;

import android.animation.Animator;
import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MaterialDesign extends Activity implements OnClickListener {
    CardView mycardview_1, mycardview_2, mycardview_3, mycardview_4, mycardview_5, mycardview_6;
    LinearLayout myll_how;
    Boolean setcolor_status = false;
    TextView count_disply,count_2,count_3,count_4;
    LinearLayout myparent_ll;
    Handler progressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_design);

        myll_how = (LinearLayout) findViewById(R.id.myll_how);

        mycardview_1 = (CardView) findViewById(R.id.mycardview_1);
        mycardview_2 = (CardView) findViewById(R.id.mycardview_2);
        mycardview_3 = (CardView) findViewById(R.id.mycardview_3);
        mycardview_4 = (CardView) findViewById(R.id.mycardview_4);
        mycardview_5 = (CardView) findViewById(R.id.mycardview_5);
        mycardview_6 = (CardView) findViewById(R.id.mycardview_6);

        count_disply = (TextView) findViewById(R.id.count_disply);
        count_2 = (TextView) findViewById(R.id.count_2);
        count_3 = (TextView) findViewById(R.id.count_3);
        count_4 = (TextView) findViewById(R.id.count_4);
        myparent_ll = (LinearLayout)findViewById(R.id.myparent_ll);

        // countincreament("1");
        mycardview_1.setOnClickListener(this);
        mycardview_2.setOnClickListener(this);
        mycardview_3.setOnClickListener(this);
        mycardview_4.setOnClickListener(this);
        mycardview_5.setOnClickListener(this);
        mycardview_6.setOnClickListener(this);
        // callanim(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mycardview_1:

                callanim(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent), mycardview_1);
                break;
            case R.id.mycardview_2:
                 callanim(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.blue), mycardview_2);
                break;
            case R.id.mycardview_3:
                callanim(getResources().getColor(R.color.nizamclearcolor), getResources().getColor(R.color.colorAccent), mycardview_3);
                break;
            case R.id.mycardview_4:
                callanim(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.nizamclearcolor), mycardview_4);
                break;
            case R.id.mycardview_5:
                callanim(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.lime), mycardview_5);
                break;
            case R.id.mycardview_6:
                callanim(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.blue), mycardview_6);
                break;


        }
    }

    public void callanim(final int colorcode, final int fullcolor, final CardView cardView) {
        int x = cardView.getTop();
        int y = cardView.getBottom();

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

    public void countincreament(String card_1) {

        animation();

    }
    public void animation(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 200; i++) {
                    try {
                        String value = String.valueOf(i);
                        callanim(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent), mycardview_1);
                        count_disply.setText(value);
                        Thread.sleep(350);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
