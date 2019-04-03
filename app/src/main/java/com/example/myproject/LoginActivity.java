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

    public static final String EXTRA_LAT = "com.example.myproject.loginactivity.lat";
    public static final String EXTRA_LONG = "com.example.myproject.loginactivity.long";
    FirebaseDatabase mydb;
    DatabaseReference myref;
    ProgressBar pb;
    private Boolean exit = false;
    String extralat,extralong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        extralat = intent.getStringExtra(EXTRA_LAT);
        extralong = intent.getStringExtra(EXTRA_LONG);
        SharedPreferences prefs = getSharedPreferences("LoginPref", MODE_PRIVATE);
        String prefid = prefs.getString("uid", null);
        String prefpass = prefs.getString("pass", null);
        String prefname = prefs.getString("name", null);
        String prefpol = prefs.getString("policy", null);
        Log.v("mapappdata","shared prefs: "+prefid+" "+prefpass+" "+prefname+" "+prefpol);
        if (prefid != null && prefpass != null) {
            intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra(HomeActivity.EXTRA_LAT,extralat);
            intent.putExtra(HomeActivity.EXTRA_LONG,extralong);
            intent.putExtra(HomeActivity.EXTRA_NAME,prefname);
            intent.putExtra(HomeActivity.EXTRA_POLICY,prefpol);
            startActivity(intent);
            finish();
        }
        pb = (ProgressBar) findViewById(R.id.progressBar2);
        pb.setVisibility(View.GONE);
    }

    public void onLogin(View view) {
        final EditText id = (EditText) findViewById(R.id.idEditText);
        final EditText password = (EditText) findViewById(R.id.passwordEditText);
        pb = (ProgressBar) findViewById(R.id.progressBar2);
        pb.setVisibility(View.VISIBLE);
        Log.d("mapappdata", id.getText().toString());
        Log.d("mapappdata", password.getText().toString());
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
                        Log.d("mapappdata", "here");
                        Log.d("mapappdata", dataSnapshot.toString());

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            DataSnapshot email = ds.child("email");
                            DataSnapshot pswrd = ds.child("password");
                            DataSnapshot policy = ds.child("policy");
                            String name = ds.getKey();
                            String uid = email.getValue().toString();
                            String pass = pswrd.getValue().toString();
                            String pol = policy.getValue().toString();
                            if (id.getText().toString().equals(uid) && password.getText().toString().equals(pass)) {
                                Log.d("mapappdata", "in loop in if here");
                                found = true;
                                SharedPreferences prefs = getSharedPreferences("LoginPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("uid", uid);
                                Log.v("mapappdata","This is the uid: "+uid);
                                editor.putString("pass", pass);
                                Log.v("mapappdata","This is the pass: "+pass);
                                editor.putString("name", name);
                                Log.v("mapappdata","This is the name: "+name);
                                editor.putString("policy", pol);
                                Log.v("mapappdata","This is the policy number: "+pol);
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra(HomeActivity.EXTRA_LAT,extralat);
                                intent.putExtra(HomeActivity.EXTRA_LONG,extralong);
                                intent.putExtra(HomeActivity.EXTRA_NAME,name);
                                intent.putExtra(HomeActivity.EXTRA_POLICY,pol);
                                pb.setVisibility(View.GONE);
                                startActivity(intent);
                                finish();
                            }
                        }
                        Log.d("mapappdata", "after loop here");
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
