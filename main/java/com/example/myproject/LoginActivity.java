package com.example.myproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    FirebaseDatabase mydb;
    DatabaseReference myref;
    ProgressBar pb;
    private Boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);
        SharedPreferences prefs = getSharedPreferences("LoginPref", MODE_PRIVATE);
        String prefid = prefs.getString("uid", null);
        String prefpass = prefs.getString("pass", null);
        if (prefid != null && prefpass != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        pb = (ProgressBar)findViewById(R.id.progressBar2);
        pb.setVisibility(View.GONE);
    }
    public void onLogin(View view){
        final EditText id = (EditText)findViewById(R.id.idEditText);
        final EditText password = (EditText)findViewById(R.id.passwordEditText);
        pb = (ProgressBar)findViewById(R.id.progressBar2);
        pb.setVisibility(View.VISIBLE);
        Log.d("mapappdata",id.getText().toString());
        Log.d("mapappdata",password.getText().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                mydb = FirebaseDatabase.getInstance();
                myref = mydb.getReference();

                DatabaseReference userchild = myref.child("User");
                userchild.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        boolean found = false;
                        Log.d("mapappdata","here");
                        Log.d("mapappdata",dataSnapshot.toString());
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d("mapappdata","entrance loop here");
                            DataSnapshot email = ds.child("email");
                            DataSnapshot pswrd = ds.child("password");
                            Log.d("mapappdata","in loop here");
                            String uid = email.getValue().toString();
                            String pass = pswrd.getValue().toString();
                            Log.d("mapappdata","in loop gg here");
                            Log.d("mappappdata", "email Value is: " + uid);
                            Log.d("mappappdata", "password Value is: " + pass);
                            if (id.getText().toString().equals(uid) && password.getText().toString().equals(pass)) {
                                Log.d("mapappdata","in loop in if here");
                                found = true;
                                SharedPreferences prefs = getSharedPreferences("LoginPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("uid",uid);
                                editor.putString("pass",pass);
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                pb.setVisibility(View.GONE);
                                startActivity(intent);
                            }
                        }
                        Log.d("mapappdata","after loop here");
                        if (!found) {
                            pb.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("mapappdata", "Failed to read value.", error.toException());
                    }
                });

            }
        }).start();
    }
}
