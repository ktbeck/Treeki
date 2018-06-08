package com.treeki.treekii;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.io.*;
import java.util.ArrayList;

import android.view.View;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddText extends AppCompatActivity {

    public JSONObject jo = null;
    public JSONArray ja = null;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    ArrayList<String>EmailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start up the Location Service

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        final EditText first = findViewById(R.id.editText);
        Button b = findViewById(R.id.btn_add);
        Button back = (Button)findViewById(R.id.btnBack);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        try{
            File f = new File(getFilesDir(), "file.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            // Notice here that we are de-serializing a String object (instead of
            // a JSONObject object) and passing the String to the JSONObject’s
            // constructor. That’s because String is serializable and
            // JSONObject is not. To convert a JSONObject back to a String, simply
            // call the JSONObject’s toString method.
            String j = null;
            try{
                j = (String) o.readObject();
            }
            catch(ClassNotFoundException c){
                c.printStackTrace();
            }
            try {
                jo = new JSONObject(j);
                ja = jo.getJSONArray("data");
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        catch(IOException e){
            // Here, initialize a new JSONObject
            jo = new JSONObject();
            ja = new JSONArray();
            try{
                jo.put("data", ja);
            }
            catch(JSONException j){
                j.printStackTrace();
            }
        }

        b.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String firstText = first.getText().toString();

                JSONObject temp = new JSONObject();
                try {
                    temp.put("first", firstText);
                }
                catch(JSONException j){
                    j.printStackTrace();
                }

                ja.put(temp);

                // write the file
                try{
                    File f = new File(getFilesDir(), "file.ser");
                    FileOutputStream fo = new FileOutputStream(f);
                    ObjectOutputStream o = new ObjectOutputStream(fo);
                    String j = jo.toString();
                    o.writeObject(j);
                    o.close();
                    fo.close();
                }
                catch(IOException e){

                }

                //pop the activity off the stack
                Intent i = new Intent(AddText.this, SearchFriends.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        back.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

                //pop the activity off the stack
                Intent i = new Intent(AddText.this, SearchFriends.class);
                startActivity(i);
            }
        });

    }

//    private void setAdapter(final String searchedString) {
//        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int i = 0;
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                    String uid = snapshot.getValue(String.class);
//
//                    if(uid.contains(searchedString)){
//                        EmailList.add(uid);
//                        i++;
//                    }
//                    if(i == 5) break;
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
