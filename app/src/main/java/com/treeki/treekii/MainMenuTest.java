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
    Button jour2;
    Button ques;
    Button ques2;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_test);
        jour = (Button) findViewById(R.id.jourButton);
        jour2 = (Button) findViewById(R.id.jourButton2);
        ques = (Button) findViewById(R.id.quesButton);
        ques2 = (Button) findViewById(R.id.quesButton2);
        jour.setVisibility(View.INVISIBLE);
        jour2.setVisibility(View.INVISIBLE);
        ques.setVisibility(View.INVISIBLE);
        ques2.setVisibility(View.INVISIBLE);
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
            jour2.setVisibility(View.INVISIBLE);
            ques2.setVisibility(View.INVISIBLE);
        }
        else {
            jour.setVisibility(View.INVISIBLE);
            ques.setVisibility(View.INVISIBLE);
        }

    }

    public void makeVisible2(View v) {
        if (jour2.getVisibility() == View.INVISIBLE){
            jour2.setVisibility(View.VISIBLE);
            ques2.setVisibility(View.VISIBLE);
            jour.setVisibility(View.INVISIBLE);
            ques.setVisibility(View.INVISIBLE);
        }
        else {
            jour2.setVisibility(View.INVISIBLE);
            ques2.setVisibility(View.INVISIBLE);
        }

    }

    public void pastJournals(View v) {
        Intent pastJournal = new Intent(this, PastJournals.class);
        startActivity(pastJournal);
    }

    public void pastQuestion(View v) {
        Intent pastQ = new Intent(this, PastQoTD.class);
        startActivity(pastQ);
    }

    public void friendPage(View v) {
        Intent friends = new Intent(this, Friends.class);
        startActivity(friends);
    }

    public void favoritePageQ(View v) {
        Intent favQs = new Intent(this, Favorites.class);
        favQs.putExtra("JorQ","QoTD");
        startActivity(favQs);
    }

    public void favoritePageJ(View v) {
        Intent favJs = new Intent(this, Favorites.class);
        favJs.putExtra("JorQ","Journal");
        startActivity(favJs);
    }

    public void tagsPage(View v) {
        Intent tags = new Intent(this, Tags.class);
        startActivity(tags);
    }

    public void logOut(View v) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(),"Signed out", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, SignInRegister.class);
        startActivity(i);
        finish();
    }

    public void graph(View v) {
        Intent g = new Intent(this, StatisticsActivity.class);
        startActivity(g);
    }

}
