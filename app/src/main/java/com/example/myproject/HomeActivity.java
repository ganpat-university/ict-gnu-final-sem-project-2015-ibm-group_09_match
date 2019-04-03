package com.example.myproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.myproject.MyFragments.ClaimFragment;
import com.example.myproject.MyFragments.NewFragment;
import com.example.myproject.MyFragments.PolicyFragment;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_LAT = "com.example.myproject.homeactivity.lat";
    public static final String EXTRA_LONG = "com.example.myproject.homeactivity.long";
    public static final String EXTRA_NAME = "com.example.myproject.homeactivity.name";
    public static final String EXTRA_POLICY = "com.example.myproject.homeactivity.pol";
    private ActionBar toolbar;
    String extralat,extralong,extraname,extrapol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        extralat = intent.getStringExtra(EXTRA_LAT);
        extralong = intent.getStringExtra(EXTRA_LONG);
        extraname = intent.getStringExtra(EXTRA_NAME);
        extrapol = intent.getStringExtra(EXTRA_POLICY);
        Log.v("mapappdata","intents in home activity: "+extralat+" "+extralong+" "+extraname+" "+extrapol);
        toolbar = getSupportActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.nav_new);
        toolbar.setTitle("New Claim");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_new:
                    toolbar.setTitle("New Claim");
                    loadFragment(NewFragment.newInstance(extraname,extralat,extralong));
                    return true;
                case R.id.nav_claims:
                    toolbar.setTitle("Claims History");
                    loadFragment(ClaimFragment.newInstance(extraname));
                    return true;
                case R.id.nav_policy:
                    toolbar.setTitle("Policy Detail");
                    loadFragment(PolicyFragment.newInstance(extrapol));
                    return true;
                case R.id.nav_logout:
                    new AlertDialog.Builder(HomeActivity.this).setTitle("Logout")
                            .setMessage("Are you sure you want to Logout?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    logOut();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                    return true;
            }
            return false;
        }
    };

    public void logOut(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("uid");
        editor.remove("pass");
        editor.remove("name");
        editor.remove("policy");
        editor.apply();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
