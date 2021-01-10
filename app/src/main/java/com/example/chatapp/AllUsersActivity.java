package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    Toolbar mtoolbarAllUsers;
    RecyclerView mrvUsersList;

    //Database refreence to point to database
        private DatabaseReference mdbRefUsers;

        //FirebaseUser
    FirebaseUser current_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mtoolbarAllUsers = findViewById(R.id.allusers_toolbar);
        setSupportActionBar(mtoolbarAllUsers);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mdbRefUsers=FirebaseDatabase.getInstance().getReference().child("Users");//database Reference

        mrvUsersList = findViewById(R.id.allusers_list);
        //Now after creating the single user xml we have to use Firebase Recycler UI library for getting the list of data

        mrvUsersList.setHasFixedSize(true);//Set the fixed size for data not based on images
        mrvUsersList.setLayoutManager(new LinearLayoutManager(this));//recycler view is ready now create model class

        //FirebaseUser
        current_id= FirebaseAuth.getInstance().getCurrentUser();



        //now to get data we require model file:-Users.java
    }//end of ocCreate()

    @Override
    protected void onStart() {
        super.onStart();//since we want to access data real time we need to specify the code in onStart()

        FirebaseRecyclerAdapter<Users, UserViewHolder> mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,
                R.layout.single_user,
                UserViewHolder.class,
                mdbRefUsers
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, Users users, int i) {

                //added if
                if(!current_id.getUid().equals(getRef(i).getKey())){//condition for alluser to not display the details for current_id which is logged in
                    userViewHolder.setName(users.getName());//get name from database
                    userViewHolder.setStatus(users.getStatus());//get status from database
                    userViewHolder.setImage(users.getImage(),getApplicationContext());//get image from database with the help of Model file(Users.java)

                    final String click_user_id=getRef(i).getKey();
                    userViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent chatUser_intent=new Intent(AllUsersActivity.this,MessageActivity.class);
                            chatUser_intent.putExtra("user_id",click_user_id);
                            startActivity(chatUser_intent);
                        }
                    });

                }
                //added else
                else {
                    userViewHolder.itemView.setVisibility(View.GONE);
                }

                //to load the image into the list we need to compress it creating thumbnail (small size images) by using Compressor library


            }

        };


        //create Adapter



        //at last set adapter to recycler view
        mrvUsersList.setAdapter(mFirebaseRecyclerAdapter);

    }//onStart()
    //inner class
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View mview;//this view is set for onclick of recycler view items
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setName(String name){
            TextView mUserNameView=mview.findViewById(R.id.tv_user_name);
            mUserNameView.setText(name);//set name to list of user
        }
        public void setStatus(String status){
            TextView mUserStatus=mview.findViewById(R.id.tv_user_status);
            mUserStatus.setText(status);
        }
        public void setImage(String image,Context myctx){
            //added if else
            CircleImageView mUserImage=mview.findViewById(R.id.civ_dp);
            if(!image.isEmpty())
                //Picasso.with(mview.getContext()).load(image).placeholder(R.drawable.img).into(mUserImage);
                 Picasso.with(myctx).load(image).placeholder(R.drawable.img).into(mUserImage);
            else
                //Picasso.with(mview.getContext()).load(R.drawable.img).placeholder(R.drawable.img).into(mUserImage);//show if image is empty
                Picasso.with(myctx).load(R.drawable.img).placeholder(R.drawable.img).into(mUserImage);//show if image is empty

        }

    }//inner class
}//Class
