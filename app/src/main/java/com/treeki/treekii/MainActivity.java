package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText name;
    private EditText password;
    private TextView info;
    private Button signin;
    private Button signup;
    private int counter = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)findViewById(R.id.etName);
        password = (EditText)findViewById(R.id.etPassword);
        signin = (Button)findViewById(R.id.btnSignin);
        signup = (Button)findViewById(R.id.btnSignup);
        info = (TextView)findViewById(R.id.tvInfo);

        info.setText("No of attempts remaining:5");
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
    public void

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
