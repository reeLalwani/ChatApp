package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusUpdate extends AppCompatActivity {
    TextInputLayout mtilNewStatus;
    Button mbtnNewSatus;

    Toolbar mtoolbarStatusUpdate;

    DatabaseReference mdbRefStausUpdate;
    FirebaseUser mcurrent_user_id;

    //Progress Dialog
    ProgressDialog mprogressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);

        mtilNewStatus=findViewById(R.id.til_status);
        mbtnNewSatus=findViewById(R.id.btn_new_status);

        mtoolbarStatusUpdate=findViewById(R.id.status_update_toolbar);
        setSupportActionBar(mtoolbarStatusUpdate);
        getSupportActionBar().setTitle("Status Update");

        //Firebase
        mcurrent_user_id= FirebaseAuth.getInstance().getCurrentUser();
        String user_id=mcurrent_user_id.getUid();
        mdbRefStausUpdate= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        //to show current status on newStatus use Intents or you can also retrive with database
        String previous_status=getIntent().getStringExtra("current_status");

        mtilNewStatus.getEditText().setText(previous_status);
        mbtnNewSatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Progress
                mprogressDialog=new ProgressDialog(StatusUpdate.this);
                mprogressDialog.setTitle("saving Changes");
                mprogressDialog.setMessage("Please wait!!!!!");
                mprogressDialog.show();
                String newStatus=mtilNewStatus.getEditText().getText().toString();
                //now we have to add this status into database:-code is above
               mdbRefStausUpdate.child("status").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           mprogressDialog.dismiss();
                           Toast.makeText(StatusUpdate.this, "Status Update Successfully", Toast.LENGTH_SHORT).show();
                       }
                       else {
                           mprogressDialog.hide();
                           Toast.makeText(StatusUpdate.this, "Error while saving changes", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

            }
        });

        //---------------------------------------For image update------------------------------------------------------


    }

}
