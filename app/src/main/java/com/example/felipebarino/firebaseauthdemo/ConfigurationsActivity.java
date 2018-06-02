package com.example.felipebarino.firebaseauthdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfigurationsActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private Button buttonAddDevice;
    private Button buttonDeleteDevice;

    private DrawerLayout drawerLayout2;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurations);

        buttonAddDevice = (Button) findViewById(R.id.buttonAddDevice);
        buttonAddDevice.setOnClickListener(this);

        buttonDeleteDevice = (Button) findViewById(R.id.buttonDeleteDevice);
        buttonDeleteDevice.setOnClickListener(this);

        drawerLayout2 = (DrawerLayout) findViewById(R.id.drawerLayout2);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout2, R.string.open, R.string.close);

        drawerLayout2.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == buttonAddDevice){
            Log.d("ConfigurationsActivity", "onClick: buttonAddDevice");
            finish();
            startActivity(new Intent(this, AddDeviceActivity.class));
        }
        if (view == buttonDeleteDevice){
            Log.d("ConfigurationsActivity", "onClick: buttonDeleteDevice");
            finish();
            startActivity(new Intent(this, DeleteDeviceActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_init)
        {
            finish();
            startActivity(new Intent(this, PrimaryActivity.class));
        }
        if(id == R.id.nav_account)
        {
            Toast.makeText(this, "Minha conta", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.nav_settings)
        {
            // já está
        }
        if(id == R.id.nav_logout)
        {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        return false;
    }


}
