package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {
    private TextView registration;
    private EditText email;
    private EditText password;
    private EditText password2;
    private Button signin;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        registration = (TextView) findViewById(R.id.tvRegister);
        email = (EditText) findViewById(R.id.Email4Signup);
        password = (EditText) findViewById(R.id.Password4Signup);
        password2 = (EditText) findViewById(R.id.Password4Confirm);
        signin = (Button) findViewById(R.id.btnBacktoLogin);
        register = (Button) findViewById(R.id.btnRegister);

        // calling the login page with the button
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
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

        if (TextUtils.isEmpty(regPassword2)) {
            Toast.makeText(this, "Please enter a confirmed password.", Toast.LENGTH_SHORT).show();
        }

        else{
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}

