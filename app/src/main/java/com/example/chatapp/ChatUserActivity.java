package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ChatUserActivity extends AppCompatActivity {

    ImageView mimg_userProfile;
    TextView mtvDisplayName,mtvStatus;
    Button mbtnFriendRequest;
    ProgressDialog mprogressForLoadingData;
    DatabaseReference mdbRefGetValueFd;//for fd_id
    DatabaseReference mdbRefOurid;
    FirebaseUser Getour_id;

    //check for fd_state
    String fd_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        final ImageView mimg_userProfile=findViewById(R.id.img_userdp);
        final TextView mtvDisplayName=findViewById(R.id.tv_user_displayname_fd);
        final TextView mtvStatus=findViewById(R.id.tv_user_status_fd);
        final Button mbtnFriendRequest=findViewById(R.id.btn_friend_request);
        mprogressForLoadingData=new ProgressDialog(this);
        mprogressForLoadingData.setTitle("Loading data");
        mprogressForLoadingData.setMessage("Please wait while data get load.");
        mprogressForLoadingData.setCanceledOnTouchOutside(false);
        mprogressForLoadingData.show();

        //fd_state
        fd_state="not_friends";

        final String fd_id=getIntent().getStringExtra("user_id");//get via intent from AllUsersActivity
        mdbRefGetValueFd= FirebaseDatabase.getInstance().getReference().child("Users").child(fd_id);//based on uid we get data of that user:- the database is referring to its id
        //now we have to get dp,displayimage,status

        //creating dbRef for our_id
        mdbRefOurid=FirebaseDatabase.getInstance().getReference().child("Friends");
        Getour_id= FirebaseAuth.getInstance().getCurrentUser();

        mdbRefGetValueFd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String displayNameFd=dataSnapshot.child("name").getValue().toString();
                String statusFd=dataSnapshot.child("status").getValue().toString();
                String imageFd=dataSnapshot.child("image").getValue().toString();

                mtvDisplayName.setText(displayNameFd);
                mtvStatus.setText(statusFd);
                Picasso.with(ChatUserActivity.this).load(imageFd).placeholder(R.drawable.dp).into(mimg_userProfile);
                mprogressForLoadingData.dismiss();

                //--------------------------To Accept Friend Request-------------------------------------------------
                mdbRefOurid.child(Getour_id.getUid()).child(fd_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(fd_id)){
                            String req_type=dataSnapshot.child(fd_id).child("request_type").getValue().toString();

                            if(req_type.equals("recieved")){

                                fd_state="request_recieved";//friends
                                mbtnFriendRequest.setText("Accept Friend Request");
                            }
                            else if(req_type.equals("sent")){
                                fd_state="request_sent";
                                mbtnFriendRequest.setText("Cancel Friend Request");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //for SendFirendRequest Button
        mbtnFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        //onClickListener
        mbtnFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //-------------------------Send Friend Request State------------------------------------------------------
                 //now the button get disable when RequestSent
                mbtnFriendRequest.setEnabled(false);//once the button is clicked then we setEnable to not seen
                if(fd_state.equals("not_friends")){
                    //send friend request
                    mdbRefOurid.child(Getour_id.getUid()).child(fd_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){//for fd_user
                                //once the task is successful setEnable(true)
                                //and setText of button to Cancel Friend Request
                                mbtnFriendRequest.setEnabled(true);
                                fd_state="request_sent";// where 1 describes the state change to fd_request sent
                                mbtnFriendRequest.setText("Cancel Friend Request");
                                mdbRefOurid.child(fd_id).child(Getour_id.getUid()).child("request_type")
                                        .setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ChatUserActivity.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            else{
                                Toast.makeText(ChatUserActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                //-----------------------------------Cancel Request State--------------------------------------

                if(fd_state.equals("request_sent")){
                    mdbRefOurid.child(Getour_id.getUid()).child(fd_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        //remove value works like if fd_id will not have any value in it then delete the record
                        @Override

                        public void onSuccess(Void aVoid) {
                            mdbRefOurid.child(fd_id).child(Getour_id.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mbtnFriendRequest.setEnabled(true);
                                    fd_state="not_friends";//not friends
                                    mbtnFriendRequest.setText("Sent Friend Request");
                                }
                            });

                        }
                    });

                }
            }
        });
    }//onCreate()
}//Class
