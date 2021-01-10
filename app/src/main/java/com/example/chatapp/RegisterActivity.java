package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mtilDisplayName,mtilEmail,mtilPassword;
    private Button mbtCreate;

    //Firebase Auth
    private FirebaseAuth mAuth;
    //Toolbar
    private Toolbar mtoolbar_register;
    //To show progress bar on create button
    private ProgressDialog mprogressRegister;
    //Database Reference
    DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mtilDisplayName=findViewById(R.id.til_display_name);
        mtilEmail=findViewById(R.id.til_email);
        mtilPassword=findViewById(R.id.til_password);
        mbtCreate=findViewById(R.id.btn_create);
        //FirebaseAuth
        mAuth=FirebaseAuth.getInstance();
        //Toolbar
        mtoolbar_register=findViewById(R.id.register_tool_bar);
        setSupportActionBar(mtoolbar_register);
        getSupportActionBar().setTitle("Register");
        //To sho the back button on action bar go the mainfest and provide parent to RegisterActivity as HomePageActivity
        //then set the back button as enable by using below 1 line of code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //now as back button is on action bar consider it as the menu so we require onOptionItemSlectedMenu()

        //Progress Dialog
        mprogressRegister=new ProgressDialog(this);

        mbtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dp_name=mtilDisplayName.getEditText().getText().toString();
                String email=mtilEmail.getEditText().getText().toString();
                String password=mtilPassword.getEditText().getText().toString();

                //we have to provide some authentications for registration
                if(!TextUtils.isEmpty(dp_name)||!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                    //if textfields are not empty then

                    registerUser(dp_name,email,password);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registerUser(final String dp_name, String email, String password) {
        mprogressRegister.setTitle("Registering User");
        mprogressRegister.setMessage("Please wait while the account is creating");
        mprogressRegister.setCanceledOnTouchOutside(false);//if user touch outside the progress bar should not go
        mprogressRegister.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //Have to store the data to database when user creates new account
                    //1. we have to get the uid
                    FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                    String uid=current_user.getUid();//get the uid:- available in database when user is created
                    //2.Create the DatabaseReference to point database:- declare global
                    dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    //3. Now we have to store 4 fields name image thumbimage status so use HashMap

                    HashMap<String,String> hm_fields=new HashMap<>();
                    hm_fields.put("name",dp_name);
                    hm_fields.put("image","image_link");
                    hm_fields.put("Thumbimage","thumbimage_link");
                    hm_fields.put("status","Hey there's i am using chatapp");
                    //till now we have successfully added the data into HashMap now we have to set data to dbRef
                   // dbRef.setValue(hm_fields);//setValue() is method to set the data into database
                    //now to check the data is added to database successfully add on complete listner on dbRef.setValue(hm_fields)
                    //and pass the intent to Chats
                    dbRef.setValue(hm_fields).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mprogressRegister.dismiss();//before going to next activity dismiss the progressBar
                            Intent main_intent=new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(main_intent);
                            //finish();//why don't you call finish(); when you want to return to MainActivity
                        }
                    });
//
                }
                else {
                    mprogressRegister.hide();//else just hide it
                    Toast.makeText(RegisterActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId()==android.R.id.home){
//            this.onBackPressed();
//            return true;
//        }
//       return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
