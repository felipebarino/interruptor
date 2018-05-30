package com.example.felipebarino.firebaseauthdemo;

import android.content.Intent;
import android.os.Bundle;
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

public class PrimaryActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private Button buttonLogout;
    private TextView textViewUser;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabase;
    private FirebaseUser user;

    private UserInformation userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // instancia o autorizador do firebase e vê se tem alguém logado
        // se não, vai pra tela de login
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // pega o banco de dados
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // pega o banco de dados do usuário
        userDatabase = databaseReference.child(user.getUid());

        userInformation = new UserInformation("name", "lastname");
        Log.d("PrimaryActivity:\t", "userInfo:\n\r\tname:\t\t" +
                userInformation.getName() + " \n\r\tlastname:\t" + userInformation.getLastname());

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInformation.setName(dataSnapshot.child("name").getValue().toString().trim());
                userInformation.setLastname(dataSnapshot.child("lastname").getValue().toString().trim());
                Log.d("PrimaryActivity:\t", "onDataChange:\n\r\tname:\t\t" +
                        userInformation.getName() + " \n\r\tlastname:\t" + userInformation.getLastname());
                textViewUser = (TextView) findViewById(R.id.textViewUser);
                textViewUser.setText("Olá, " + userInformation.getName() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //textViewUser.setText(userInformation.getName());

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogout){
            // tela de logar
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }
    }
}
