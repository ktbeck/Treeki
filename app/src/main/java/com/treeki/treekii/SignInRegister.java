package com.treeki.treekii;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SignInRegister extends AppCompatActivity{
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private static final String TAG = "MainActivity";

    private EditText name;
    private EditText password;
    private Button signin;
    private Button signup;
    String question;
    String month;
    String day;
    String year;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_register);
        //calling register method on click
        name = (EditText)findViewById(R.id.etName);
        password = (EditText)findViewById(R.id.etPassword);
        signin = (Button)findViewById(R.id.btnSignin);
        signup = (Button)findViewById(R.id.btnSignup);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));

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
                                goToNextActivity();
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

    private void startQoTD() {
        mDatabase.child("Questions").child(month).child(day).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get question at /Questions/date
                        question = dataSnapshot.getValue(String.class);

                        //error handling
                        if (question == null) {
                            Log.e(TAG, "Question at "+month+"/"+day+" is unexpectedly null");
                            Toast.makeText(getApplicationContext(),"can't fetch question",Toast.LENGTH_SHORT).show();
                        }
                        //if no err, send question
                        else {
                            Log.i(TAG, "Question at"+month+"/"+day+" is: "+question);
                            Intent QoTD = new Intent(SignInRegister.this,QoTD.class);
                            QoTD.putExtra("question",question);
                            startActivity(QoTD);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get Question onCancelled", databaseError.toException());
                    }
                }
        );
    }

    private void startJournal() {
        Intent intent = new Intent(SignInRegister.this,Journal.class);
        startActivity(intent);
    }

    private void goToNextActivity() {
        date = month+"-"+day+"-"+year;
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"Signed in: "+user.getUid());

        mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("answer").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get question at /Questions/0101
                        String answer = dataSnapshot.getValue(String.class);

                        //error handling
                        if (answer == null) {
                            Log.i(TAG,"User signed in but has not filled in QoTD.");
                            startQoTD();
                        }

                        else {

                            mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("answer").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //get question at /Questions/0101
                                            String answer = dataSnapshot.getValue(String.class);

                                            //error handling
                                            if (answer == null) {
                                                Log.i(TAG,"User signed in and finished QoTD but not journal");
                                                startJournal();
                                            }
                                            //if no err, change the question
                                            else {
                                                //GOTO MAIN MENU
                                                Intent mainmenuIntent = new Intent(SignInRegister.this,MainMenuTest.class);
                                                startActivity(mainmenuIntent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(TAG, "get Journal answer onCancelled", databaseError.toException());
                                        }
                                    }
                            );


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get QoTD answer onCancelled", databaseError.toException());
                    }
                }
        );

    }
}
