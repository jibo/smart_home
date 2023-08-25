package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressBar2;
    private TextView appname;
    private LottieAnimationView lottie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        ActionBar actionBar = getSupportActionBar();//消除APP该Activity界面标题栏
        if(actionBar!=null){ //消除APP该Activity界面标题栏
            actionBar.hide(); //消除APP该Activity界面标题栏
        } ////消除APP该Activity界面标题栏
        setContentView(R.layout.activity_splash_screen);
        appname = findViewById(R.id.nameapp);
        lottie = findViewById(R.id.lottie);
        progressBar2=findViewById(R.id.pd2);

        //设置动画移动延迟和位置
        appname.animate().translationY(-1900).setDuration(2700).setStartDelay(0);
        lottie.animate().translationX(0).setDuration(2000).setStartDelay(2900);

        int progress = progressBar2.getProgress();
        progress+=10;
        progressBar2.setProgress(progress);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },5000);
    }
}