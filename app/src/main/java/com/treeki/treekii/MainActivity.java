package com.treeki.treekii;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        Button buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    private void registerUser(){

        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();


        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(MainActivity.this,"Successfully registered", Toast.LENGTH_LONG).show();
                        }else{
                            //display some message here
                            Toast.makeText(MainActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
//    private void signIn(){
//        String email = editTextEmail.getText().toString().trim();
//        String password  = editTextPassword.getText().toString().trim();
//        firebaseAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
////                        Log.e(TAG, "Login unsuccessful: " + task.getException().getMessage());
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Toast.makeText(MainActivity.this,"Successfully logged in", Toast.LENGTH_LONG).show();
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(MainActivity.this,"Login Failed", Toast.LENGTH_LONG).show();
//                        }
//
//                        // ...
//                    }
//                });
//
//    }



    @Override
    public void onClick(View view) {
        //calling register method on click
      registerUser();
//        signIn();
    }
}
