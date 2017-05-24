package com.michouze;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class welcome extends AppCompatActivity {


    private static TextView nbr;
    private static int clickcount=0;
    private RecyclerView mBloglist;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaselike;
    private DatabaseReference mDatabaseComment;

    private boolean mProcessLike= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mBloglist =(RecyclerView)findViewById(R.id.blog_list);
        mBloglist.setHasFixedSize(true);
        mBloglist.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(welcome.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//THE user wont be able to go back
                    startActivity(loginIntent);



                }

            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaselike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comment");
        mDatabaseUsers.keepSynced(true);





    }


    @Override
    protected void onStart() {
        super.onStart();




        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog,BlogViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(


                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, final int position) {

               final  String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescript(model.getDescript());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key);


                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(welcome.this,post_key,Toast.LENGTH_LONG).show();

                        Intent singelBlogIntent = new Intent(welcome.this,SingleBlogActivity.class);
                        singelBlogIntent.putExtra("blog_id",post_key);
                        startActivity(singelBlogIntent);


                    }
                });


                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        mProcessLike = true;

                        mDatabaselike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike) {


                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                            mDatabaselike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            mProcessLike = false;


                                        } else {

                                            mDatabaselike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                            mProcessLike = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                    }
                });


            }
            

        };





        mBloglist.setAdapter(firebaseRecyclerAdapter);

    }
    private void checkUserExist() {
        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(user_id)) {


                        Intent mainIntent = new Intent(welcome.this, welcome.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    } else {

                        Intent setupIntent = new Intent(welcome.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }



    public static class  BlogViewHolder extends RecyclerView.ViewHolder{

        View mview;

        ImageButton mLikeBtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;
        ImageButton mDislikeBtn;



        public BlogViewHolder(View itemView) {

            super(itemView);
            mview=itemView;

            mLikeBtn = (ImageButton)mview.findViewById(R.id.likeBtn);
            nbr  = (TextView)mview.findViewById(R.id.count);
            mDatabaseLike =FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);




        }


        public void setLikeBtn(final String post_key){



            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikeBtn.setImageResource(R.mipmap.like);
                        mLikeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                clickcount = clickcount+1;

                               if(clickcount==1){

                                    nbr.setText(String.valueOf(clickcount));


                               }
                            }
                        });


                    }else{
                        mLikeBtn.setImageResource(R.mipmap.nolike);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setTitle(String title){

            //TextView post_titled=(TextView)mview.findViewById(R.id.post_title);


        }

        public void setDescript(String descript){
            TextView post_description = (TextView)mview.findViewById(R.id.post_description);
            post_description.setText(descript);
        }


       public void setUsername(String username){
           TextView post_username= (TextView)mview.findViewById(R.id.post_username);
           post_username.setText(username);

       }

        public void setImage(Context ctx, String image){

            ImageView image_post =(ImageView)mview.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(image_post);
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_add){

            Intent in1 = new Intent(welcome.this,PostActivity.class);
            startActivity(in1);


        }

        if(item.getItemId()==R.id.action_logout){

            logout();


        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();//using this make sure mAuth(listner)is
    }


}
