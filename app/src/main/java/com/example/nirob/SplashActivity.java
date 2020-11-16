package com.example.nirob;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar spl_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        spl_pb = findViewById(R.id.pb_spl);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNetworkavailable()){
                    spl_pb.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(SplashActivity.this, "Check your network connection", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        },1000);

    }

    private boolean isNetworkavailable(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if(connectivityManager != null){
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();

        }catch(NullPointerException e){
            return false;
        }
    }

}
