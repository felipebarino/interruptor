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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfigurationsActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference, userDatabase, infoDatabase, devicesDatabase;

    private Button buttonAddDevice;
    private Button buttonDeleteDevice;

    private TextView textViewUser;

    private DrawerLayout drawerLayout2;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    private UserInformation userInformation;


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

        // pega o banco de dados
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // pega o banco de dados do usuário, informações e dispositivos
        userDatabase = databaseReference.child(user.getUid());
        infoDatabase = userDatabase.child("info");
        devicesDatabase = userDatabase.child("devices");
        userInformation = new UserInformation("name", "lastname");

        // callback para mudanças nas informações do usuário
        infoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // quando atualiza as informações do usuário
                userInformation.setName(dataSnapshot.child("name").getValue().toString().trim());
                userInformation.setLastname(dataSnapshot.child("lastname").getValue().toString().trim());
                Log.d("PrimaryActivity:\t", "onDataChange:\n\r\tname:\t\t" +
                        dataSnapshot.child("name").getValue().toString().trim() + "==" + userInformation.getName()
                        + " \n\r\tlastname:\t" + dataSnapshot.child("lastname").getValue().toString().trim()+ "==" + userInformation.getLastname());

                textViewUser = (TextView) findViewById(R.id.textViewUser);
                textViewUser.setText("Olá, " + userInformation.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PrimaryActivity:\t", "infoDatabase: onDataChange:\n\rcould not get new data");
            }
        });

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
            startActivity(new Intent(this, MyAccount.class));
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
