package com.michouze;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleBlogActivity extends AppCompatActivity {

    private String mPost_key=null;
    private DatabaseReference mDatabase;

    private ImageView mBlogSingleImage;
    private EditText mBlogSingleTitle;
    private EditText mBlogSingleDesc;
    private Button mBlogSimpleBtn;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog);

       mPost_key=getIntent().getExtras().getString("blog_id");

        mAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mBlogSingleTitle =(EditText)findViewById(R.id.singleBlogTitle);
        mBlogSingleDesc = (EditText)findViewById(R.id.singleBlogPost);
        mBlogSingleImage =(ImageView)findViewById(R.id.singleBlogImage);
        mBlogSimpleBtn =(Button)findViewById(R.id.singleRemoveBtn);


       // Toast.makeText(SingleBlogActivity.this,post_key,Toast.LENGTH_LONG).show();
        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title= (String)dataSnapshot.child("title").getValue();
                String post_descript =(String)dataSnapshot.child("descript").getValue();
                String post_image = (String)dataSnapshot.child("image").getValue();
                String post_uid=  (String)dataSnapshot.child("uid").getValue();


                mBlogSingleTitle.setText(post_title);
                mBlogSingleDesc.setText(post_descript);

                Picasso.with(SingleBlogActivity.this).load(post_image).into(mBlogSingleImage);



                if(mAuth.getCurrentUser().getUid().equals(post_uid )){

                    mBlogSimpleBtn.setVisibility(View.VISIBLE);


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mBlogSimpleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    mDatabase.child(mPost_key).removeValue();
                    Intent mainIntent = new Intent(SingleBlogActivity.this,welcome.class);
                    startActivity(mainIntent);

            }
        });

    }
}
