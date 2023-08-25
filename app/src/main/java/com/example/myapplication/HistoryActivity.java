package com.example.myapplication;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private EditText Edi;

    private List<String> hisConLis = new ArrayList<>(); //历史提问信息

    private List<String> hisResLis = new ArrayList<>(); //历史相应信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Edi = findViewById(R.id.Edi);
        Bundle bundle = getIntent().getBundleExtra("bundleHis");
        hisConLis = bundle.getStringArrayList("hisConLis");
        hisResLis = bundle.getStringArrayList("hisResLis");
        for (int i=0;i<hisConLis.size();i++){
            Edi.append("我：" + hisConLis.get(i) + "\n");
            Edi.append("GPT：" + hisResLis.get(i) + "\n");
        }
    }
}