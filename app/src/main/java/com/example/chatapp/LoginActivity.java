package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mtilEmail,mtilPassword;
    Button mLoginbtn;
    //Toolbar
    Toolbar mtoolbarLogin;
    //FirebaseAuth
    FirebaseAuth mAuth;
    //ProgressDialog
    ProgressDialog mLoginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mtilEmail=findViewById(R.id.til_email);
        mtilPassword=findViewById(R.id.til_password);
        mLoginbtn=findViewById(R.id.btn_login);

        mtoolbarLogin=findViewById(R.id.login_tool_bar);
        setSupportActionBar(mtoolbarLogin);
        getSupportActionBar().setTitle("Login");
        //creating back button for login activity
        //set the back button as enable by using below 1 line of code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //FirebaseAuth
        mAuth=FirebaseAuth.getInstance();
        //ProgressDialog
        mLoginProgress=new ProgressDialog(this);
        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=mtilEmail.getEditText().getText().toString();
                String password=mtilPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                    mLoginProgress.setTitle("Signing in");
                    mLoginProgress.setMessage("Please wait while signing in");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginExistingUser(email,password);
                }

            }
        });
    }

    private void loginExistingUser(String email, String password) {

        //To loginExistingUser require FirebaseAuth object and its methods
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();
                    Intent main_intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(main_intent);
                    //finish();
                }
                else {
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "Incorrect Credentials!Cannot Sign in Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
