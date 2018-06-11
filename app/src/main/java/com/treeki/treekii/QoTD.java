package com.treeki.treekii;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


public class QoTD extends AppCompatActivity {

    //initialize
    private TextView QoTD;
    private EditText answer_edit;
    private ArrayList<String> past_dates;
    private String answer;
    private CheckBox priv;
    private CheckBox fav;
    private boolean priv_check;
    private boolean fav_check;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private static final String TAG = "QoTD_Activity";
    private Spinner spinner;
    String past_date;
    String question;
    String month;
    String day;
    String year;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qotd);

        getSupportActionBar().hide();
        setTitle("Question of the Day");

        priv = (CheckBox) findViewById(R.id.priv);
        fav = (CheckBox) findViewById(R.id.fav);
        spinner = (Spinner) findViewById(R.id.spinner);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"User: "+user.getUid());
        //get date
        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        date = month+"-"+day+"-"+year;

        QoTD = findViewById(R.id.QoTD);
        question = getIntent().getStringExtra("question");

        //get Database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(question == null) {
            mDatabase.child("Questions").child(month).child(day).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //get question at /Questions/date
                            question = dataSnapshot.getValue(String.class);

                            //error handling
                            if (question == null) {
                                Log.e(TAG, "Question at " + month + "/" + day + " is unexpectedly null");
                                Toast.makeText(getApplicationContext(), "can't fetch question", Toast.LENGTH_SHORT).show();
                            }
                            else
                                QoTD.setText(question);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "get Question onCancelled", databaseError.toException());
                        }
                    }
            );
        }


        Log.i(TAG,"QoTD: "+question);
        QoTD.setText(question);

        answer_edit = findViewById(R.id.answer);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rating, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        past_dates = new ArrayList<>();
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            past_date = childSnapshot.getKey();
                            Log.i(TAG,"past dates: "+past_date);
                            if (!past_date.equals("tags") && !past_date.equals("Username")) {
                                String t_month = past_date.split("-")[0];
                                String t_day = past_date.split("-")[1];
                                if (t_day.equals(day) && t_month.equals(month)) {
                                    Log.i(TAG,"Adding: "+past_date);
                                    past_dates.add(past_date);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG,"checkpast cancelled");
                    }
                }
        );
    }

    public void submit(View view) {
        priv_check = priv.isChecked();
        fav_check = fav.isChecked();
        String mood = String.valueOf(spinner.getSelectedItem());

        //Save the answer
        answer = answer_edit.getText().toString();
        if (!answer.equals("")){
            mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("answer").setValue(answer);
            mDatabase.child("Users").child(user.getUid()).child(date).child("mood").setValue(mood);
            mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("private").setValue(priv_check);
            mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("favorite").setValue(fav_check);

            if (past_dates!=null && past_dates.size() > 1)
                checkPast();
            else {
                Toast.makeText(getApplicationContext(), "Answer submitted!", Toast.LENGTH_SHORT).show();
                goToNextActivity();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Please input an answer.", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkPast() {
        String dates = "";
        for (String date: past_dates) {
            dates+=date+" ";
        }
        Toast.makeText(QoTD.this,"Past answers found from "+dates,Toast.LENGTH_LONG);
        Intent answeredQotd = new Intent(QoTD.this, answeredQoTD.class);
        answeredQotd.putStringArrayListExtra("dates", past_dates);
        answeredQotd.putExtra("question",question);
        answeredQotd.putExtra("source","QoTD");
        Log.i(TAG,"Found past QoTD. "+dates);
        startActivity(answeredQotd);
    }


    private void goToNextActivity() {
        mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("answer").addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //get question at /Questions/0101
                    String answer = dataSnapshot.getValue(String.class);

                    //error handling
                    if (answer == null) {
                        Log.i(TAG,"User did not journal");
                        startJournal();
                    }
                    //if no err, change the question
                    else {
                        //GOTO MAIN MENU
                        Log.i(TAG,"User did journal, going to main");
                        Intent mainmenuIntent = new Intent(QoTD.this,MainMenuTest.class);
                        startActivity(mainmenuIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "get Journal answer onCancelled", databaseError.toException());
                }
            }
        );
    }

    public void skipQoTD(View view){
        showNotification("Treeki", "Don't forget to come back and fill in your daily journal/answer.");
        startJournal();
    }

    private void startJournal() {
        Intent journal = new Intent(this, Journal.class);
        startActivity(journal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    /* Here is the event handler for the menu button that I forgot in class.
    The value returned by item.getItemID() is
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Signed out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, SignInRegister.class);
                startActivity(i);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    void showNotification(String title, String content) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "TreekiNotification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("TREEKI_NOTIFICATION_CHANNEL");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.smallerlogo) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setSound(uri) // set alarm sound for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), QoTD.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
