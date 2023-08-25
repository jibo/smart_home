package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private Button btn_1;
    private ImageView image_1;
    private ImageView image_2;
    private ImageView image_3;
    private TextView view_1;
    private TextView user_name;
    private ImageView led_show;
    private String host = "tcp://broker.emqx.io:1883";
    private String userName = "android";
    private String passWord = "android";
    private String mqtt_id = "2065917198_zb"; //id
    private String mqtt_sub_topic = "2065917198_zb"; //订阅主题
    private String mqtt_pub_topic = "2065917198_zb_ESP"; //发送主题
    private ScheduledExecutorService scheduler;
    private MqttClient client;
    private MqttConnectOptions options;
    private Handler handler;
    private Switch swh_status;
    private Switch swh_status1;
    private int led_flag = 1;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");
        user_name = findViewById(R.id.user_name);
        user_name.setText(name + "的家");
        user_name.setTextSize(30);

        btn_1 = findViewById(R.id.btn_1);//绑定id
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("hello");
                Toast.makeText(MainActivity.this,"hello",Toast.LENGTH_SHORT).show();//弹窗 在当前activity显示内容为hello短暂弹窗
            }
        });

        //点击图片进入相机界面
        image_1 = findViewById(R.id.image_1);
        image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //监听按钮，如果点击，就跳转
                Intent intent = new Intent();
                if (intent != null){
                    //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                    intent.setClass(MainActivity.this, CameraViewActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"打开失败",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //点击图片进入智能助手界面
        image_2 = findViewById(R.id.image_2);
        image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //监听按钮，如果点击，就跳转
                Intent intent = new Intent();
                if (intent != null){
                    //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                    intent.setClass(MainActivity.this, HelpAIActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"打开失败",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //点击图片进入设备管理界面
        image_3 = findViewById(R.id.image_3);
        image_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //监听按钮，如果点击，就跳转
                Intent intent = new Intent();
                if (intent != null){
                    //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                    intent.setClass(MainActivity.this, DevicesDisplayActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"打开失败",Toast.LENGTH_SHORT).show();
                }

            }
        });
        
        led_show = findViewById(R.id.led_show);
        swh_status1 = findViewById(R.id.swh_status1);
        swh_status1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked && led_flag == 0){
                    led_show.setImageResource(R.drawable.led_open);
                    publishmessageplus(mqtt_pub_topic,"{\"set_led\":1}");
                    Toast.makeText(MainActivity.this,"开灯",Toast.LENGTH_SHORT).show();
                    led_flag = 1;
                }else if(checked && led_flag == 1){
                    led_show.setImageResource(R.drawable.led_open);
                    Toast.makeText(MainActivity.this,"已开灯",Toast.LENGTH_SHORT).show();
                }else {
                    led_show.setImageResource(R.drawable.led_close);
                    publishmessageplus(mqtt_pub_topic,"{\"set_led\":0}");
                    led_flag = 0;
                    Toast.makeText(MainActivity.this,"关灯",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //两个控件联动
        view_1 = findViewById(R.id.view_1);



        Mqtt_init();
        startReconnect();

        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1: //开机校验更新回传
                        break;
                    case 2:  // 反馈回传

                        break;
                    case 3:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                        //在主进程 handler 里面更新UI  既保证了稳定性  又不影响网络传输
                        String T_val = msg.obj.toString().substring(msg.obj.toString().indexOf("temperature\":") + 13, msg.obj.toString().indexOf("}"));
                        view_1.setText(T_val + "℃");
                        //Toast.makeText(MainActivity.this,T_val ,Toast.LENGTH_SHORT).show();
                        break;
                    case 30:  //连接失败
                        Toast.makeText(MainActivity.this,"连接失败" ,Toast.LENGTH_SHORT).show();
                        break;
                    case 31:   //连接成功
                        Toast.makeText(MainActivity.this,"连接成功" ,Toast.LENGTH_SHORT).show();
                        try {
                            client.subscribe(mqtt_sub_topic,1);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private void Mqtt_init()
    {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, mqtt_id,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    //startReconnect();
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 3;   //收到消息标志位
                    msg.obj = topicName + "---" + message.toString();
                    handler.sendMessage(msg);// hander 回传
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void Mqtt_connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!(client.isConnected()) )  //如果还未连接
                    {
                        client.connect(options);
                        Message msg = new Message();
                        msg.what = 31;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    Mqtt_connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }
    private void publishmessageplus(String topic,String message2)
    {
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic,message);
        } catch (MqttException e) {

            e.printStackTrace();
        }
    }

}