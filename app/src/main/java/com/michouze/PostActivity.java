package com.michouze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

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

public class PostActivity extends AppCompatActivity {
    private ImageButton mselectimage;
    private EditText titlepost;
    private EditText postdescription;
    private Button submit;
    private Uri imageuri= null;
    private static final int GALLERY_REQUEST=1;

    private StorageReference mstorage;
    private DatabaseReference databasereference;


    private ProgressDialog progressdialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mstorage= FirebaseStorage.getInstance().getReference();//this is gonna be our firebase storage root storage
        databasereference= FirebaseDatabase.getInstance().getReference().child("Blog");//I created the child directory in order to save data into child directory
        //not in the root directory.


        mselectimage =(ImageButton)findViewById(R.id.selectimage);
        titlepost = (EditText)findViewById(R.id.titlefield);
        postdescription=(EditText)findViewById(R.id.postfield);
        progressdialog= new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        submit= (Button)findViewById(R.id.submitpost);





        mselectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT );
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_REQUEST );
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();
            }

            private void startPosting() {

                progressdialog.setMessage("Start Posting to Michouze.....!!!");
                progressdialog.show();

               final String titlestuff= titlepost.getText().toString().trim();
                final String descriptionstuff=  titlepost.getText().toString().trim();

                if(!TextUtils.isEmpty(titlestuff) && !TextUtils.isEmpty(descriptionstuff)&& imageuri!=null){

                    StorageReference storagepath=mstorage.child("Michouze_Images").child(imageuri.getLastPathSegment());//this will give the name to the Images
                    //but there is some methode that can generate the image names for you when you want to upload more Image

                    storagepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //you can use also the onfile upload listener

                           final Uri downloadurl = taskSnapshot.getDownloadUrl();//this helps in downloading the url

                            final DatabaseReference newpost = databasereference.push();//this create a unique random ID this want be overwritten or deleted any time

                            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    newpost.child("title").setValue(titlestuff);//takes the title added in the text and add it to the child title in database
                                    newpost.child("descript").setValue(descriptionstuff);//put the Post description into the child description
                                    newpost.child("image").setValue(downloadurl.toString());//conversion of url into String Before uploading
                                    newpost.child("uid").setValue(mCurrentUser.getUid());
                                    newpost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                startActivity(new Intent(PostActivity.this,welcome.class ));
                                            }
                                        }
                                    });



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                            progressdialog.dismiss(); //After Posting user must return to the home activity




                        }
                    });

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode== RESULT_OK){

            imageuri = data.getData();
            mselectimage.setImageURI(imageuri);
        }
    }
}
