package com.example.chatapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;//declare the FirebaseAuth object
    //Toolbar
    Toolbar mtoolbarMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();//Initialize the FirebaseAuth object

        //Toolbar
        mtoolbarMain=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbarMain);
        getSupportActionBar().setTitle("Chats");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();//It will get the current firebase user and store it into currentUser
        //updateUI(currentUser);

        //so if current user is not signed in then currentUser is==null then we have to pass the user to HomePage
        if(currentUser==null){
            sendToHome();
        }
    }

    public void sendToHome(){
        Intent home_Intent=new Intent(MainActivity.this,HomePageActivity.class);
        startActivity(home_Intent);
        //finish();//if we want that the user will not come to MainPage again by clicking back button we have to finish the activity

    }
    //for menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        //or
        //getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){

            case R.id.action_logout:

                //for logout get the code from Authentication documentation in firebase
                FirebaseAuth.getInstance().signOut();
                //after sign out the user must be again pass to HomePageActivity for that we have to call the method sendToRegister()
                sendToHome();
                break;

            case R.id.action_profile:
                //send to ProfileActivity
                Intent profile_intent=new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(profile_intent);
                break;

            case R.id.action_allusers:
                //send to AllUsersActivity
                Intent allusers_intent=new Intent(MainActivity.this,AllUsersActivity.class);
                startActivity(allusers_intent);
                break;
        }
        return true;
    }
}
