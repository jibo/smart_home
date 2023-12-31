package com.example.myapplication.utils;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user.Msg;

import java.util.List;


public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftMsg = view.findViewById(R.id.left_msg);
            rightMsg = view.findViewById(R.id.right_msg);
        }
    }

    public MsgAdapter(List<Msg> msgList){
        mMsgList = msgList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder,int position){
        Msg msg = mMsgList.get(position);
        if(msg.getType() == Msg.TYPE_RECEIVED){
            //如果是收到消息，则显示左边的消息布局
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }else if(msg.getType() == Msg.TYPE_SENT){
            //如果是发送消息，则显示右边的消息布局
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }
    public int getItemCount(){
        return mMsgList.size();
    }
}
