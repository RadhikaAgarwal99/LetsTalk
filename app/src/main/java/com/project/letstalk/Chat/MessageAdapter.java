package com.project.letstalk.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.letstalk.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    ArrayList<Chat> mChat;
    Context mcontext;

    FirebaseUser fuser;

    public MessageAdapter(Context mcontext, ArrayList<Chat> mChat){
        this.mcontext = mcontext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View layoutView = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, null, false);
            return new MessageAdapter.MessageViewHolder(layoutView);
        } else {
            View layoutView = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, null, false);
            return new MessageAdapter.MessageViewHolder(layoutView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, final int position) {
        Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView show_message;

        MessageViewHolder(View view){
            super(view);

            show_message = view.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}