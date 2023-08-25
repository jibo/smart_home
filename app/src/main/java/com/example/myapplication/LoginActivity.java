package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.myapplication.user.User;
import com.example.myapplication.utils.JellyInterpolator;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    //声明控件
//    private Button mBtn_main_1;

    private EditText mEt_username1;
    private EditText mEt_password1;

    private TextView mBtn_main_1; //登录
    private TextView mBtn_main_2; //注册

    private ImageView egg;
    private View progress;

    private View mInputLayout;

    private float mWidth, mHeight;

    private LinearLayout mName, mPsw;
    private CheckBox cb_remember, cb_autologin;
    private SharedPreferences sharedPreferences;

    private boolean hide = true;
    private ImageView eye;
    private static int REQUEST_CODE = 1;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    Intent intent1 = null;

    private ArrayList<User> userArrayList = new ArrayList<>();

    private void veritify() {

        BiometricManager biometricManager = BiometricManager.from(this);
        int result = biometricManager.canAuthenticate(
                BIOMETRIC_WEAK);
        Log.e("Biometric_TAG", "judge: " + result);
        switch (result) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("Biometric_TAG", "应用可以使用生物特征！");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("Biometric_TAG", "设备v不支持！");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("Biometric_TAG", "生物特征当前无效！");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_WEAK);
                startActivityForResult(enrollIntent, REQUEST_CODE);

                break;
            default:
                Log.e("Biometric_TAG", "judge: " + result);
                break;
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

        initData();


        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "认证错误: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "认证成功!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "认证失败",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
        //
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("生物认证")
                .setSubtitle("使用你的生物信息登录")
                .setNegativeButtonText("使用账户密码")
                .setAllowedAuthenticators(BIOMETRIC_WEAK)
                .build();

        TextView biometricLoginButton = findViewById(R.id.biometric_login);
        biometricLoginButton.setOnClickListener(view -> {
            veritify();
            biometricPrompt.authenticate(promptInfo);
        });
    }

    private void initData() {
        egg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("作者by鸡脖、可汗、瑞仙")
                        .setMessage("测试账号：user\n" +
                                "测试密码：1234")
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
        // 获取SharedPreferences的实例
        sharedPreferences = this.getSharedPreferences("loginInfo", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("remember", false)) {
            cb_remember.setChecked(true);
            mEt_username1.setText(sharedPreferences.getString("account", ""));
            mEt_password1.setText(sharedPreferences.getString("password",""));
            autologin();
        }

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.eye:
                        if (hide == true) {
                            eye.setImageResource(R.mipmap.eye_open);
                            HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance();
                            mEt_password1.setTransformationMethod(method);
                            hide = false;
                        } else {
                            eye.setImageResource(R.mipmap.eye_close);
                            TransformationMethod method = PasswordTransformationMethod.getInstance();
                            mEt_password1.setTransformationMethod(method);
                            hide = true;
                        }
                        int index = mEt_password1.getText().toString().length();
                        mEt_password1.setSelection(index);
                        break;
                }
            }
        });

        /*mBtn_main_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });*/

        //登录
        mBtn_main_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    //从本界面获取输入的用户名和密码
                    String username1 = mEt_username1.getText().toString();
                    String password1 = mEt_password1.getText().toString();
                    if ("".equals(username1)){
                        Toast.makeText(LoginActivity.this,"账号为空，重新输入！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ("".equals(password1)){
                        Toast.makeText(LoginActivity.this,"密码为空，重新输入！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //校验账户
                    if (username1.equals("user") && password1.equals("1234")) {
                        if (cb_remember.isChecked()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("account", username1);
                            editor.putString("password", password1);
                            editor.apply();
                        }
                        mWidth = mBtn_main_1.getMeasuredWidth();
                        mHeight = mBtn_main_1.getMeasuredHeight();

                        mName.setVisibility(View.INVISIBLE);
                        mPsw.setVisibility(View.INVISIBLE);

                        inputAnimator(mInputLayout, mWidth, mHeight);

                        intent1 = new Intent(LoginActivity.this,MainActivity.class);
                        intent1.putExtra("username",username1);
                        intent1.putExtra("password",password1);

                        new Handler(new Handler.Callback() {
                        //处理接收到的消息的方法
                        @Override
                        public boolean handleMessage(Message arg0) {
                        //实现页面跳转
                            startActivity(intent1);
                            finish();
                            return false;
                        }
                        }).sendEmptyMessageDelayed(0, 3000); //表示延时三秒进行任务的执行
                    }else {
                        Toast.makeText(LoginActivity.this,"不存在此用户！",Toast.LENGTH_SHORT).show();
                    }
                }
        });

        //跳转注册
        mBtn_main_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,0x00);
            }
        });
    }

    private void initUI() {
        //找到控件
        mBtn_main_1 = (TextView)findViewById(R.id.btn_main_1);
        mBtn_main_2 = (TextView)findViewById(R.id.btn_main_2);
        mEt_username1 = findViewById(R.id.et_username1);
        mEt_password1 = findViewById(R.id.et_password1);

        eye = findViewById(R.id.eye);
        eye.setImageResource(R.mipmap.eye_close);
        cb_remember = findViewById(R.id.cb_remember);
        cb_autologin = findViewById(R.id.cb_autologin);

        cb_remember.setOnCheckedChangeListener(this);
        cb_autologin.setOnCheckedChangeListener(this);

        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);
        egg = findViewById(R.id.egg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0x00 && resultCode == 0x01){
            String name = data.getStringExtra("username");
            String pass = data.getStringExtra("password");
            mEt_username1.setText(name);
            mEt_password1.setText(pass);
            Toast.makeText(this, "你已经注册成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void autologin() {
        // 获取sharedPreferences中autologin对于的boolean值, true表示记住密码
        if (sharedPreferences.getBoolean("autologin", false)) {
            // 勾选自动登录
            cb_autologin.setChecked(true);
            // 跳转页面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);// 跳转到主界面
            startActivity(intent);
        }
    }

    // checkButton按钮的选中监听事件,compoundButton指的是checkButton控件， isChecked指的是是否勾选
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.cb_remember:
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("remember", true).apply();
                } else {
                    sharedPreferences.edit().putBoolean("remember", false).apply();
                }
                break;
            case R.id.cb_autologin:
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("autologin", true).apply();
                } else {
                    sharedPreferences.edit().putBoolean("autologin", false).apply();
                }
                break;
        }
    }
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }
}