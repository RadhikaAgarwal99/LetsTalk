package com.project.letstalk.User;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.project.letstalk.ChatActivity;
import com.project.letstalk.R;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListRecyclerViewHolder> {

    ArrayList<UserObject> userList;
    Context mcontext;
    boolean isChat;

    public UserListAdapter(Context mcontext, ArrayList<UserObject> userList) {
        this.mcontext = mcontext;
        this.userList = userList;
    }

    public UserListAdapter(Context mcontext, ArrayList<UserObject> userList, boolean isChat) {
        this.mcontext = mcontext;
        this.userList = userList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mcontext).inflate(R.layout.item_user, null, false);
        return new UserListAdapter.UserListRecyclerViewHolder(layoutView);
        /*RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListRecyclerViewHolder rcv = new UserListRecyclerViewHolder(layoutView);
        return rcv;
    */
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListRecyclerViewHolder holder, final int position) {
        final UserObject user = userList.get(position);

        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUid()).child("chat").child(key).setValue(true);
            }
        });

        holder.itemView.setTag(userList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, ChatActivity.class);
                intent.putExtra("userName", holder.mName.getText()) ;
                intent.putExtra("userid", user.getUid());
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListRecyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView mName, mPhone;

        public LinearLayout mLayout;

        public UserListRecyclerViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            mLayout = view.findViewById(R.id.layout);
        }
    }
}
