package com.treeki.treekii;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private TextView registration;
    private EditText email;
    private EditText password;
    private EditText password2;
    private Button signin;
    private Button register;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();
        registration = (TextView) findViewById(R.id.tvRegister);
        email = (EditText) findViewById(R.id.Email4Signup);
        password = (EditText) findViewById(R.id.Password4Signup);
        password2 = (EditText) findViewById(R.id.Password4Confirm);
        signin = (Button) findViewById(R.id.btnBacktoLogin);
        register = (Button) findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // calling the login page with the button
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, SignInRegister.class);
                startActivity(intent);
            }
        });

//        check if the register information is correct and jump to login page
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerFunc();
            }
        });
    }

    //    check with login
    public void registerFunc() {
        String regEmail = email.getText().toString().trim(); //trims the end like @gmail.com
        String regPassword = password.getText().toString();
        String regPassword2 = password2.getText().toString();

//        check if name box and password box is empty.
        if (TextUtils.isEmpty(regEmail)) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(regPassword)) {
            Toast.makeText(this, "Please enter a valid password.", Toast.LENGTH_SHORT).show();
            return;

        }

        if (!regPassword2.equals(regPassword)) {
            Toast.makeText(this, "Your password don't match.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(regPassword.length() < 6 || regPassword2.length() < 6){
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(regPassword2)) {
            Toast.makeText(this, "Please enter a confirmed password.", Toast.LENGTH_SHORT).show();
        }

        else{

            progressDialog.setMessage("Registering Please Wait...");
            progressDialog.show();


            //creating a new user
            firebaseAuth.createUserWithEmailAndPassword(regEmail, regPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //checking if success
                            if(task.isSuccessful()){
                                //display some message here
                                Toast.makeText(SignupActivity.this,"Successfully registered", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignupActivity.this,SignInRegister.class);
                                startActivity(intent);
                                finish();
                            }else{
                                //display some message here
                                Toast.makeText(SignupActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

}

