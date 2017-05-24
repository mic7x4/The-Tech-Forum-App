package com.michouze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mLoginEamilField;
    private EditText mLoginPasswordField;
    private Button mLoginBtn;
    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG  = "LoginActivity";
    private Button mNewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgress= new ProgressDialog(this);

        mLoginEamilField =(EditText)findViewById(R.id.loginEmailField);
        mLoginPasswordField=(EditText)findViewById(R.id.loginPasswordField);
        mLoginBtn = (Button)findViewById(R.id.loginBtn);
        mNewAccount = (Button)findViewById(R.id.newAccount);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mGoogleBtn = (SignInButton)findViewById(R.id.googleBtn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLogin();
            }


        });

mNewAccount.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);


    }
});




                    }







    private void checkLogin() {

        String email = (mLoginEamilField).getText().toString().trim();
        String password =(mLoginPasswordField).getText().toString().trim();


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Checking The Login ...!");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if(task.isSuccessful()){

                        mProgress.dismiss();

                        checkUserExist();


                    }else{

                        mProgress.dismiss();

                        Toast.makeText(LoginActivity.this,"Login Error you Need To Create A New Account",Toast.LENGTH_LONG).show();


                    }
                }
            });


        }


    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){


                    Intent mainIntent = new Intent(LoginActivity.this,welcome.class);
                    mainIntent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity( mainIntent);

                }else{

                    Intent setupIntent = new Intent(LoginActivity.this,SetupActivity.class);
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








































