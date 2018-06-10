package com.treeki.treekii;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenuTest extends AppCompatActivity {

    private static final String TAG = "TestMainMenu";
    Button jour;
    Button ques;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_test);
        jour = (Button) findViewById(R.id.jourButton);
        ques = (Button) findViewById(R.id.quesButton);
        jour.setVisibility(View.INVISIBLE);
        ques.setVisibility(View.INVISIBLE);
    }
    //logout button
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

    public void makeVisible(View v) {
        if (jour.getVisibility() == View.INVISIBLE){
            jour.setVisibility(View.VISIBLE);
            ques.setVisibility(View.VISIBLE);
        }
        else {
            jour.setVisibility(View.INVISIBLE);
            ques.setVisibility(View.INVISIBLE);
        }

    }

    public void startJournal(View v) {
        Intent journal = new Intent(this, Journal.class);
        startActivity(journal);
    }

    public void startQuestion(View v) {
        Intent Q = new Intent(this, QoTD.class);
        startActivity(Q);
    }

    public void logOut(View v) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(),"Signed out", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, SignInRegister.class);
        startActivity(i);
    }

}
