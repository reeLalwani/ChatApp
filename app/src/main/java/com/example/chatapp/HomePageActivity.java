package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomePageActivity extends AppCompatActivity {

   private Button mbtnReg,mbtnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mbtnReg=findViewById(R.id.btn_reg);
        mbtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register_intent=new Intent(HomePageActivity.this,RegisterActivity.class);
                startActivity(register_intent);
               // finish();
            }
        });

        mbtnLogin=findViewById(R.id.btn_login);
        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent=new Intent(HomePageActivity.this,LoginActivity.class);
                startActivity(login_intent);
                //finish();
            }
        });
    }
}
