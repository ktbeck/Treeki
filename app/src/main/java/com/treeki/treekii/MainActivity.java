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

public class MainActivity extends AppCompatActivity {

    private EditText name;
    private EditText password;
    private Button signin;
    private Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)findViewById(R.id.etName);
        password = (EditText)findViewById(R.id.etPassword);
        signin = (Button)findViewById(R.id.btnSignin);
        signup = (Button)findViewById(R.id.btnSignup);

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
                Intent intent = new Intent(MainActivity.this,SignupActivity.class);
//                validate(name.getText().toString(),password.getText().toString());
                startActivity(intent);
            }
        });

    }

//    check with login
    public void signinFunc(){
        String signinName = name.getText().toString().trim(); //trims the end like @gmail.com
        String signinPassword = password.getText().toString();

//        check if name box and password box is empty.
        if(TextUtils.isEmpty(signinName)){
            Toast.makeText(this,"Please enter a valid email address.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(signinPassword)){
            Toast.makeText(this,"Please enter a valid password.",Toast.LENGTH_SHORT).show();
            return;
        }

    }

//    private void validate(String userName, String userPassword){
//        if((userName.equals("Admin")) && (userPassword.equals("0"))){
//            Intent intent = new Intent(MainActivity.this,SecondActivity.class);
//            startActivity(intent);
//        }else{
//            counter--;
//
//            info.setText("No of attempts remaining:" + String.valueOf(counter));
//            if (counter == 0) {
//                signin.setEnabled(false);
//            }
//        }
//    }
}
