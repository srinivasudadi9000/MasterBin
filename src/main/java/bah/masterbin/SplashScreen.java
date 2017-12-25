package bah.masterbin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        this.overridePendingTransition(R.anim.slideleft,
                R.anim.slideleft);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences ss = getSharedPreferences("Login",MODE_PRIVATE);
                String username = ss.getString("username","");
                if (username.length()>0){
                    Intent dashboard = new Intent(SplashScreen.this,Dashboard.class);
                    startActivity(dashboard);
                }else {
                    Intent dashboard = new Intent(SplashScreen.this,Login.class);
                    startActivity(dashboard);
                }
            }
        },2000);
    }
}
