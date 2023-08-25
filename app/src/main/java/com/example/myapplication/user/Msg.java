package com.example.myapplication.user;

public class Msg {
    public static final int TYPE_RECEIVED = 0;//表示收到一条消息
    public static final int TYPE_SENT = 1;//表示发送一条消息
    private String content;//表示消息内容
    private int type;//表示消息类型

    public Msg(String content,int type){
        this.content = content;
        this.type = type;
    }
    public String getContent(){
        return content;
    }
    public int getType(){
        return type;
    }
}
