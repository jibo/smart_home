package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.user.User;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {


    private TextView mBtn_register_1;

    private ImageView backToLogin;

    private EditText et_register_1,et_register_2,et_register_3;

    SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUI();
        // 获取SharedPreferences的实例
        sharedPreferences = this.getSharedPreferences("loginInfo", MODE_PRIVATE);

        mBtn_register_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username  = et_register_1.getText().toString();
                String password1 = et_register_2.getText().toString();
                String password2 = et_register_3.getText().toString();
                if (!password1.equals(password2)) {
                    Toast.makeText(RegisterActivity.this,"两次密码不一致！",Toast.LENGTH_SHORT).show();
                }else if ( username.length() > 4){
                    Toast.makeText(RegisterActivity.this,"用户名过长！例如user",Toast.LENGTH_SHORT).show();
                }else if ( "".equals(username)){
                    Toast.makeText(RegisterActivity.this,"用户名不能为空！",Toast.LENGTH_SHORT).show();
                }else if ( "".equals(password1) || "".equals(password2)){
                    Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("username",username);
                    intent.putExtra("password",password1);
                    setResult(0x01,intent); //结果码
                    finish();
                }
            }
        });
        //返回登陆页面
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initUI() {
        mBtn_register_1 = (TextView) findViewById(R.id.btn_register_1);
        et_register_1 = findViewById(R.id.et_register_1);
        et_register_2 = findViewById(R.id.et_register_2);
        et_register_3 = findViewById(R.id.et_register_3);
        backToLogin = findViewById(R.id.backToLogin);
    }
}