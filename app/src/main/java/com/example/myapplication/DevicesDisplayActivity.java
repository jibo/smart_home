package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DevicesDisplayActivity extends AppCompatActivity {
    private ImageView bluetooth_eye;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_display);
        bluetooth_eye = findViewById(R.id.bluetooth_eye);
        bluetooth_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //监听按钮，如果点击，就跳转
                Intent intent = new Intent();
                if (intent != null){
                    //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                    intent.setClass(DevicesDisplayActivity.this, DevicesMangerActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(DevicesDisplayActivity.this,"打开失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}