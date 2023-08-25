package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson2.JSONObject;
import com.example.myapplication.user.Msg;
import com.example.myapplication.utils.MsgAdapter;
import com.example.myapplication.utils.NetworkUtils;
import com.example.myapplication.utils.OkHttpApi;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HelpAIActivity extends AppCompatActivity {
    private ConnectivityManager networkService;

    final String TAG = "ActivityNetwork";

    private Button send;

    private Button history;

    private EditText input;

    private String content; //输入的内容

    private Bundle bundleHis = new Bundle();

    private int GETDATA_SUCCESS = 101;

    private int GETDATA_FAILED = 104;

    private List<Msg> msgList = new ArrayList<>();

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private List<String> hisConLis = new ArrayList<>(); //历史提问信息
    private List<String> hisResLis = new ArrayList<>(); //历史相应信息

    String data;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == GETDATA_SUCCESS){


                data = message.getData().getString("ResCont");

                Msg msgReceive = new Msg(data,Msg.TYPE_RECEIVED);

                msgList.add(msgReceive);

                // 将读取的内容追加显示在文本框中
            }else if ((message.what == GETDATA_FAILED)){

                data = message.getData().getString("ResCont");

                Msg msgReceive = new Msg(data,Msg.TYPE_RECEIVED);

                msgList.add(msgReceive);
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_ai);
        //初始化UI
        initUI();

        networkService = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (networkService != null) {
            //注册 NetworkCallBack
            networkService.registerDefaultNetworkCallback(networkCallBack);
        }
    }

    private void initUI() {

        send = findViewById(R.id.send);
        input = findViewById(R.id.input);
        history = findViewById(R.id.history);

        msgRecyclerView = findViewById(R.id.msg_recycler_view);

        initReceive();

        LinearLayoutManager layoutManager =new LinearLayoutManager(this);

        msgRecyclerView.setLayoutManager(layoutManager);

        adapter = new MsgAdapter(msgList);

        msgRecyclerView.setAdapter(adapter);

        if (networkService != null) {
            //注册 NetworkCallBack
            networkService.registerDefaultNetworkCallback(networkCallBack);
        }

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                bundleHis.putStringArrayList("hisConLis", (ArrayList<String>) hisConLis);
                bundleHis.putStringArrayList("hisResLis", (ArrayList<String>) hisResLis);
                intent.putExtra("bundleHis",bundleHis);
                intent.setClass(HelpAIActivity.this,HistoryActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initReceive(){

        Msg msgReceive1 = new Msg("你好！我是你的智能助手，有什么问题可以问我",Msg.TYPE_RECEIVED);
        msgList.add(msgReceive1);
        Msg msgReceive2 = new Msg("你可以可以尝试问我，“你好吗？”",Msg.TYPE_RECEIVED);
        msgList.add(msgReceive2);
    }

    private void Send() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用户在文本框内输入的内容

                content = input.getText().toString();

                if ("".equals(content)){
                    Toast.makeText(HelpAIActivity.this,"不能发送空消息",Toast.LENGTH_SHORT).show();
                }else {

                    Msg msg = new Msg(content,Msg.TYPE_SENT);

                    msgList.add(msg);

                    adapter.notifyItemInserted(msgList.size() - 1);//当有新消息时，刷新RecyclerView中的显示

                    msgRecyclerView.scrollToPosition(msgList.size() - 1);//将RecyclerView定位到最后一行

                    hisConLis.add(content);//放入历史提问信息
                    input.setText("");
                    initData();
                }
            }
        });
    }

    //子线程调API
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpApi api = new OkHttpApi();
                    api.run(content, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("错误======》", e.getMessage());

                            hisResLis.add(e.getMessage());//放入历史响应信息

                            //创建信息对象，封装对象
                            Message message = Message.obtain();

                            Bundle bundle = new Bundle();

                            bundle.putString("ResCont","服务器连接异常");

                            message.setData(bundle);

                            message.what = GETDATA_FAILED;
                            //向主进程发送信息
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                            //得到相应内容
                            String ResCont =  jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

                            hisResLis.add(ResCont);//放入历史响应信息

                            //创建信息对象，封装对象
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putString("ResCont",ResCont);
                            message.setData(bundle);
                            message.what = GETDATA_SUCCESS;
                            //向主进程发送信息
                            mHandler.sendMessage(message);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /*
     * 测试网络工具类方法
     * */
    @Override
    protected void onResume() {
        super.onResume();
        //Log.e(TAG,  "=====在onResume方法中判断 ： isMobileNetwork ：" + NetworkUtils.isMobileNetwork(this));
        //Log.e(TAG,  "=====在onResume方法中判断 ： isWifiNetwork ：" + NetworkUtils.isWifiNetwork(this));
        //Log.e(TAG,  "=====在onResume方法中判断 ： getConnectedNetworkType ：" + NetworkUtils.getConnectedNetworkType(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkService != null && networkCallBack != null) {
            networkService.unregisterNetworkCallback(networkCallBack);
        }
    }

    private final ConnectivityManager.NetworkCallback networkCallBack = new ConnectivityManager.NetworkCallback(){
        //当网络状态修改但仍旧是可用状态时调用
        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (NetworkUtils.isConnectedAvailableNetwork(getBaseContext())) {
                Log.d(TAG, "onCapabilitiesChanged ---> ====网络可正常上网===网络类型为： "+ NetworkUtils.getConnectedNetworkType(HelpAIActivity.this));
            }
            //表明此网络连接验证成功
            if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d(TAG, "===当前在使用Mobile流量上网===");
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d(TAG, "====当前在使用WiFi上网===");
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                    Log.d(TAG, "=====当前使用蓝牙上网=====");
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.d(TAG, "=====当前使用以太网上网=====");
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    Log.d(TAG, "===当前使用VPN上网====");
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                    Log.d(TAG, "===表示此网络使用Wi-Fi感知传输====");
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)) {
                    Log.d(TAG, "=====表示此网络使用LoWPAN传输=====");
                }
            }


        }

        @Override
        public void onAvailable(@NonNull Network network){
            super.onAvailable(network);
            Log.e(TAG, "==网络连接成功，通知可以使用的时候调用====onAvailable===");
            Toast.makeText(HelpAIActivity.this,"已连接网络！网络类型为：" + NetworkUtils.getConnectedNetworkType(HelpAIActivity.this),Toast.LENGTH_SHORT).show();
            Msg msgReceive1 = new Msg("你好！当前网络已连接！"  + NetworkUtils.getConnectedNetworkType(HelpAIActivity.this),Msg.TYPE_RECEIVED);
            msgList.add(msgReceive1);
            Send();

        }

        @Override
        public void onUnavailable() {
            Log.e(TAG, "==当网络连接超时或网络请求达不到可用要求时调用====onUnavailable===");
            super.onUnavailable();
            Toast.makeText(HelpAIActivity.this,"网络不可用！",Toast.LENGTH_SHORT).show();
            Msg msgReceive1 = new Msg("你好，网络不可用！",Msg.TYPE_RECEIVED);
            msgList.add(msgReceive1);
        }

        @Override
        public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
            Log.e(TAG, "==当访问指定的网络被阻止或解除阻塞时调用===onBlockedStatusChanged==");
            super.onBlockedStatusChanged(network, blocked);
        }

        @Override
        public void onLosing(@NonNull Network network, int maxMsToLive) {
            Log.e(TAG, "==当网络正在断开连接时调用===onLosing===");
            super.onLosing(network, maxMsToLive);
            Toast.makeText(HelpAIActivity.this,"网络正在断开！",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(@NonNull Network network) {
            Log.e(TAG, "==当网络已断开连接时调用===onLost===");
            super.onLost(network);
            Toast.makeText(HelpAIActivity.this,"网络已断开！",Toast.LENGTH_SHORT).show();
            Msg msgReceive1 = new Msg("你好，网络已断开！",Msg.TYPE_RECEIVED);
            msgList.add(msgReceive1);
        }


        @Override
        public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
            Log.e(TAG, "==当网络连接的属性被修改时调用===onLinkPropertiesChanged===");
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    };
}