package com.example.myapplication.utils;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpApi {
    OkHttpClient okHttpClient = new OkHttpClient();
    public void run(String content, Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "" +
                "{\n    \"model\": \"gpt-3.5-turbo\",\n\"messages\": [\n{\n\"role\": \"assistant\",\n\"content\": \"" + content + "\" \n}\n],\n\"safe_mode\": false\n}");
        Request request = new Request.Builder()
                .url("https://oa.api2d.net/v1/chat/completions")
                .method("POST", body)
                .addHeader("Authorization", "Bearer fk189538-zgNC9nksmmo53y18AsJHDtWsbsWhHsOw")
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Content-Type", "application/json")
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
