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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrimaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private TextView textViewUser;
    private ListView listView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference, userDatabase, infoDatabase, devicesDatabase;

    private UserInformation userInformation;
    private UserDevice userDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.listview);

        // instancia o autorizador do firebase e vê se tem alguém logado
        // se não, vai pra tela de login
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // pega o banco de dados
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // pega o banco de dados do usuário, informações e dispositivos
        userDatabase = databaseReference.child(user.getUid());
        infoDatabase = userDatabase.child("info");
        devicesDatabase = userDatabase.child("devices");

        userInformation = new UserInformation("name", "lastname");
        userDevice = new UserDevice("000","nick", "OFF");

        // callback para mudanças nas informações do usuário
        infoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // quando atualiza as informações do usuário
                userInformation.setName(dataSnapshot.child("name").getValue().toString().trim());
                userInformation.setLastname(dataSnapshot.child("lastname").getValue().toString().trim());
                Log.d("PrimaryActivity:\t", "onDataChange:\n\r\tname:\t\t" +
                        dataSnapshot.child("name").getValue().toString().trim() + " \n\r\tlastname:\t" + dataSnapshot.child("lastname").getValue().toString().trim());
                textViewUser = (TextView) findViewById(R.id.textViewUser);
                textViewUser.setText("Olá, " + userInformation.getName() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PrimaryActivity:\t", "infoDatabase: onDataChange:\n\rcould not get new data");
            }
        });

        // callback para mudanças nas informações de dispositivos
        devicesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // quando atualiza as informações de dispositivos
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PrimaryActivity:\t", "devicesDatabase: onDataChange:\n\rcould not get new data");
            }
        });
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

        if(id == R.id.nav_account)
        {
            Toast.makeText(this, "Minha conta", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.nav_settings)
        {
            Toast.makeText(this, "Configurações", Toast.LENGTH_SHORT).show();
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
