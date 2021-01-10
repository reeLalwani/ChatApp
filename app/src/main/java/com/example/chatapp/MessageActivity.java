package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView mProfileUser;
    TextView username;
    Toolbar mtoolbar;
    ImageButton mbtnSend;
    EditText mtypeMessage;
    FirebaseUser fuser;
    Users users=new Users();

    //added
    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        final String userId=getIntent().getStringExtra("user_id");

        //init
        //for actionbar
        mtoolbar=findViewById(R.id.user_tool_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mProfileUser=findViewById(R.id.civ_dp);
        username=findViewById(R.id.tvdisplayname);

        //init all
        mbtnSend=findViewById(R.id.sendBtn);
        mtypeMessage=findViewById(R.id.etxSendTxt);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        //added
        recyclerView=findViewById(R.id.rvMessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        reference=FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users=dataSnapshot.getValue(Users.class);
//                username.setText(users.getName());

                if(users.getImage().equals("default")){
                    mProfileUser.setImageResource(R.mipmap.ic_launcher);
                }else {
//                    Picasso.with(MessageActivity.this).load(users.getImage()).placeholder(R.drawable.img).into(mProfileUser);
                    Toast.makeText(MessageActivity.this, "Set image", Toast.LENGTH_SHORT).show();
                }
                readMessages(fuser.getUid(),userId,users.getImage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mbtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=mtypeMessage.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(),userId,msg);
                }else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                mtypeMessage.setText("");
            }
        });

        readMessages(fuser.getUid(),userId,users.getImage());
    }

    private void sendMessage(String sender,String reciever,String message){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",reciever);
        hashMap.put("message",message);

        databaseReference.child("Chats").push().setValue(hashMap);
    }

    public void readMessages(final String mid, final String uid, final String imageUrl){
        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReciever().equals(mid) && chat.getReciever().equals(uid)||
                            chat.getReciever().equals(uid) && chat.getSender().equals(mid)){
                        mChat.add(chat);

                    }
                    messageAdapter=new MessageAdapter(MessageActivity.this,mChat,imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
