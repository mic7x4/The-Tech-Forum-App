package com.michouze;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by MIC7X4 on 5/4/2017.
 */
public class SimpleBlog extends Application {

    @Override
    public void onCreate(){

        super.onCreate();

        if(!FirebaseApp.getApps(this).isEmpty()){

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);//this enable the offline capability this keeps the offline data
        //to the phone

        Picasso.Builder builder= new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built= builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

    }
}
