package com.example.felipebarino.firebaseauthdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDeviceActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener  {

    private Button buttonAdd;
    private Button buttonCancel;
    private EditText editTextId;
    private EditText editTextNick;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference, userDatabase, infoDatabase, devicesDatabase;

    private Device addedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);

        buttonCancel= (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);
        
        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextNick = (EditText) findViewById(R.id.editTextNick);

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

    }

    @Override
    public void onClick(View view) {
        if(view == buttonAdd){
            addedDevice = new Device();
            addedDevice.setId(editTextId.getText().toString().trim());
            addedDevice.setNick(editTextNick.getText().toString().trim());

            // manda pro banco de dados
            devicesDatabase.child(addedDevice.getId()).setValue(addedDevice);
            Log.d("AddDeviceActivity:\t", "onClick: Adicionar Dispositvo: sucess");

            // avisa ao usuário e limpa os campos de edição de texto
            Toast.makeText(AddDeviceActivity.this, "Dispositivo adicionado com sucesso", Toast.LENGTH_LONG).show();
            editTextId.setText("id");
            editTextNick.setText("Apelido");

            // volta para as configurações
            finish();
            startActivity(new Intent(this, ConfigurationsActivity.class));
        }
        if (view == buttonCancel){
            finish();
            startActivity(new Intent(this, ConfigurationsActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_init)
        {
            startActivity(new Intent(this, PrimaryActivity.class));
        }
        if(id == R.id.nav_account)
        {
            startActivity(new Intent(this, MyAccount.class));

        }
        if(id == R.id.nav_settings)
        {
            startActivity(new Intent(this, ConfigurationsActivity.class));
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
