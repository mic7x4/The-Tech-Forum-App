package com.michouze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private ImageButton mSetupImageBtn;
    private EditText mNameField;
    private Button mSetupBtn;
    private Uri mImageUri = null;
    private final int GALLERY_REQYEST = 1;

    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAth;
    private StorageReference mStorageImage;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAth= FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");

        mProgress = new ProgressDialog(this);
        mSetupImageBtn= (ImageButton)findViewById(R.id.setupImageBtn);
        mNameField = (EditText)findViewById(R.id.setupNameField);
        mSetupBtn = (Button)findViewById(R.id.setupSubmitBtn);

        mSetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 startSetupAccount();
            }
        });

        mSetupImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(getIntent().ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQYEST);

            }
        });



    }

    private void startSetupAccount() {

        final String name = mNameField.getText().toString().trim();
        final String user_id = mAth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && mImageUri != null){

            mProgress.setMessage("We Done With Setup ....");
            mProgress.show();

         StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUsers.child(user_id).child("name").setValue(name);
                    mDatabaseUsers.child(user_id).child("image").setValue(downloadUri);

                    mProgress.dismiss();

                    Intent mainIntent = new Intent(SetupActivity.this,welcome.class);
                    mainIntent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity( mainIntent);

                }
            });





        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQYEST && resultCode==RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);



        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mImageUri = result.getUri();

                mSetupImageBtn.setImageURI(mImageUri );

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}
