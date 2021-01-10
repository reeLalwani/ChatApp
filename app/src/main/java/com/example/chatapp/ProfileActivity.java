package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView mtvPDisplayName,mtvPStatus;
    CircleImageView mProfileImage;
    FirebaseUser current_user;
    DatabaseReference mdbRef;

    Button mbtnChangeStatus,mbtnImage;
    public static final int GALLERY_PICK=100;

    //Firebase Storage
    StorageReference mStoreImages;
    //Progress Dialog
    ProgressDialog mUploadImageProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mtvPDisplayName=findViewById(R.id.tv_profile_dpname);
        mtvPStatus=findViewById(R.id.tv_profile_status);
        mProfileImage=findViewById(R.id.profile_photo);
        mbtnChangeStatus=findViewById(R.id.btn_change_pstatus);
        mbtnImage=findViewById(R.id.btn_change_pimage);
        //----------for refrencing-------------------------
        current_user= FirebaseAuth.getInstance().getCurrentUser();
        String current_user_id=current_user.getUid();
        mdbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        //------------------for StorageReference-----------------------------------
        mStoreImages= FirebaseStorage.getInstance().getReference();//this is pointing to the main folder i.e. chatapp 1884a

        //---------------------------------------------------------------------------------------------------------
        mdbRef.addValueEventListener(new ValueEventListener() {
            //valueEventListner is use to get values from database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//for data operations this method is used
               // Toast.makeText(ProfileActivity.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                String pname=dataSnapshot.child("name").getValue().toString();
                String pstatus=dataSnapshot.child("status").getValue().toString();
                String pimage=dataSnapshot.child("image").getValue().toString();

                mtvPDisplayName.setText(pname);
                mtvPStatus.setText(pstatus);
                //now we have to set the image now and to do this add library picasso to get image onile and set on icon

                //if we want image must be loaded if the image !=default
                //by using this while loading data it will not hide the image so load the image in if() statement with condition
                if(!pimage.equals("default")){
                    Picasso.with(ProfileActivity.this).load(pimage).placeholder(R.drawable.img).into(mProfileImage);//this line is use to set the image on image icon
                    //now also when we change the image while loading the new image it will hide the previous image but we dont want to hide the image
                    //it does so because picasso set the dafault to hide image while loading the new image so to change that add function
                    //.placeholder(R.drawable.default_avatar)
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {//for exceptions this method is use

            }
        });//valueEventListner for getting some  values if we want to access list of values there is another approach

        //for changeStatus()
        mbtnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to pass current status on new status
                String currentStatus=mtvPStatus.getText().toString();
                Intent statusUpdate_intent=new Intent(ProfileActivity.this,StatusUpdate.class);
                statusUpdate_intent.putExtra("current_status",currentStatus);
                startActivity(statusUpdate_intent);
            }
        });
        //----------------------------------For Image Update----------------------------------------------------------------
        mbtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1. It will open the Gallery of phone
                Intent gallery_intent=new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery_intent,"Select Image"),GALLERY_PICK);
                //2.We have to do cropping of image on selection
                //We have to use library :-ArthurHub and follow the usage steps

                // start picker to get image for cropping and then use the image in cropping activity
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(ProfileActivity.this);
//

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK&& resultCode==RESULT_OK){
            Uri imageUri=data.getData();

            //but as we have provide the phone gallery so we directly set the content as below
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)//define default aspect ratio:- square type
                    .start(this);//It should start the image crop activity
            //Toast.makeText(this, imageUri, Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                //---------------------------------For progress dialog ---------------------------------------------------
                mUploadImageProgress=new ProgressDialog(this);
                //show the progress dialog while uploading the image
                mUploadImageProgress.setTitle("Uploading Image");
                mUploadImageProgress.setMessage("Please Wait");
                mUploadImageProgress.setCanceledOnTouchOutside(false);
                mUploadImageProgress.show();

                Uri resultUri = result.getUri();//get the uri of that cropped image:- now to show the image in profile we have to store the uri of image into database
                //now to upload the file we require StorageReference object to work with

                //to give the name of the image same as uid
                String image_uid=current_user.getUid();
                StorageReference filepath=FirebaseStorage.getInstance().getReference().child("profile_images/"+image_uid+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            Task<Uri> result=task.getResult().getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String download_url=uri.toString();

                                    mdbRef.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mUploadImageProgress.dismiss();
                                                Toast.makeText(ProfileActivity.this,"Working database",Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(ProfileActivity.this,"Not responding database",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });

                           // Toast.makeText(ProfileActivity.this,"Working",Toast.LENGTH_LONG).show();
                        }
                        else {
                            mUploadImageProgress.dismiss();
                            Toast.makeText(ProfileActivity.this,"Try again ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}
