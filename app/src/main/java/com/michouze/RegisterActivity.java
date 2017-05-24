package com.michouze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPassowrdField;

    private Button mRegistereButton;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth= FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");//storing users into database in the User directory

        mNameField =(EditText)findViewById(R.id.nameField);
        mEmailField =(EditText)findViewById(R.id.emailField);
        mPassowrdField =(EditText) findViewById(R.id.passwordField);

        mRegistereButton= (Button)findViewById(R.id.registerBtn);

        mRegistereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegister();
            }
        });
    }

    private void startRegister() {
      final  String name = mNameField.getText().toString().trim();
        String email= mEmailField.getText().toString().trim();
        String password = mPassowrdField.getText().toString().trim();


        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Signing Up..");
            mProgress.show();


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                         String user_id = mAuth.getCurrentUser().getUid();//when user login we get the Id of the user .

                        DatabaseReference current_user_db =  mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);//set the current user logged in name to the shit
                        current_user_db.child("image").startAt("default");//set image to the default
                        mProgress.dismiss();


                        Intent mainintent= new Intent(RegisterActivity.this,welcome.class);
                        mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainintent);



                    }

                }
            });


        }
    }
}
