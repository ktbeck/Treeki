package com.treeki.treekii;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SplashActivity extends Activity {
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private static final String TAG = "SplashActivity";

    String question;
    String month;
    String day;
    String year;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));

		//music
		
		int x = 1;

        // get serializable file

        // Read the file

        try{
            File f = new File(getFilesDir(), "music.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            String j = null;
            try{
                j = (String) o.readObject();
                x = (Integer.valueOf(j) % 3) + 1;
            }
            catch(ClassNotFoundException c){
                c.printStackTrace();
            }
        }
        catch(IOException e){
            x = 1;
        }

        //write the file -- ensures same song doesn't play twice in a row

        try{
            File f = new File(getFilesDir(), "music.ser");
            FileOutputStream fo = new FileOutputStream(f);
            ObjectOutputStream o = new ObjectOutputStream(fo);
            String j = Integer.toString(x);
            o.writeObject(j);
            o.close();
            fo.close();
        }
        catch(IOException e){
            Toast.makeText(SplashActivity.this, "Something went wrong loadig music",
                    Toast.LENGTH_LONG).show();
        }

        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = getAssets().openFd("piano" + x + ".ogg");

            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mediaPlayer.prepare();
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();// play the audio
        Toast.makeText(SplashActivity.this, "Recording Playing",
                Toast.LENGTH_LONG).show();
		
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            goToNextActivity();
        }
        else {
            startSignIn();
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
                            Intent QoTD = new Intent(SplashActivity.this,QoTD.class);
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
        Intent Journal = new Intent(SplashActivity.this,Journal.class);
        startActivity(Journal);
        finish();
    }


    private void startSignIn() {
        Intent SignIn = new Intent(SplashActivity.this,SignInRegister.class);
        startActivity(SignIn);
        finish();
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
                                                Intent mainmenuIntent = new Intent(SplashActivity.this,MainMenuTest.class);
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
