package com.project.letstalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.letstalk.Chat.Chat;
import com.project.letstalk.Chat.MessageAdapter;
import com.project.letstalk.User.UserObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    Intent intent;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    ArrayList<Chat> mChat;

    String userid;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        String userName = intent.getStringExtra("userName");
        userid = intent.getStringExtra("userid");
        username.setText(userName);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();

                if(!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg);
                    Log.d("userid", "onClick: " + userid);
                } else {
                    Toast.makeText(ChatActivity.this, "You can't send an empty msg", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        if(userid != null) {
            reference = FirebaseDatabase.getInstance().getReference("user").child(userid);

            reference.addValueEventListener(new ValueEventListener() {
                private DatabaseError databaseError;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserObject user = dataSnapshot.getValue(UserObject.class);

                    readMessages(fuser.getUid(), userid);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
    }

    private void sendMessage(final String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);

        if(userid != null) {
            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(fuser.getUid())
                    .child(userid);

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        chatRef.child("id").setValue(userid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });

            final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")

                    .child(receiver).child(fuser.getUid());

            chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.exists())

                    {

                        chatRef1.child("id").setValue(fuser.getUid());

                    }

                }



                @Override

                public void onCancelled(@NonNull DatabaseError databaseError) {



                }

            });
        }
    }

    private void readMessages(final String myid, final String userid) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat != null && chat.getReceiver() != null && chat.getSender() != null && userid != null && myid != null && (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid))) {
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this, mChat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
