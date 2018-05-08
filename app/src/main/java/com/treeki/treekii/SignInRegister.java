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
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInRegister extends AppCompatActivity{
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "MainActivity";

    private EditText name;
    private EditText password;
    private Button signin;
    private Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_register);
        //calling register method on click
        name = (EditText)findViewById(R.id.etName);
        password = (EditText)findViewById(R.id.etPassword);
        signin = (Button)findViewById(R.id.btnSignin);
        signup = (Button)findViewById(R.id.btnSignup);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();


//        calling the login page with the button
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinFunc();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInRegister.this,SignupActivity.class);
//                validate(name.getText().toString(),password.getText().toString());
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
    }


    //    check with login
    public void signinFunc(){
        String signinName = name.getText().toString(); //trims the end like @gmail.com
        String signinPassword = password.getText().toString();

//        check if name box and password box is empty.
        if(TextUtils.isEmpty(signinName)){
            Toast.makeText(this,"Please enter a valid email address.",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(signinPassword)){
            Toast.makeText(this,"Please enter a valid password.",Toast.LENGTH_SHORT).show();
        }

        else{
            firebaseAuth.signInWithEmailAndPassword(signinName, signinPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(SignInRegister.this,"Successfully logged in", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignInRegister.this,QoTD.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e(TAG, "Login unsuccessful: " + task.getException().getMessage());
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignInRegister.this,"Login Failed", Toast.LENGTH_LONG).show();
                            }

                            // ...
                        }
                    });
        }

    }

}
