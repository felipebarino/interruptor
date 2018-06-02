package com.example.felipebarino.firebaseauthdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

public class DeleteDeviceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    private TextView textViewUser;
    private ListView listViewDelete;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference, userDatabase, infoDatabase, devicesDatabase;

    private List<Device> devices;
    private UserInformation userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);

        devices = new ArrayList<Device>();
        devices.clear();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout3);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        listViewDelete = (ListView) findViewById(R.id.listviewDelete);
        final CustomAdaptor customAdaptor = new CustomAdaptor();
        listViewDelete.setAdapter(customAdaptor);

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

        // callback para mudanças nas informações do usuário
        infoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // quando atualiza as informações do usuário
                userInformation.setName(dataSnapshot.child("name").getValue().toString().trim());
                userInformation.setLastname(dataSnapshot.child("lastname").getValue().toString().trim());
                Log.d("DeleteActivity:\t", "onDataChange:\n\r\tname:\t\t" +
                        dataSnapshot.child("name").getValue().toString().trim() + "==" + userInformation.getName()
                        + " \n\r\tlastname:\t" + dataSnapshot.child("lastname").getValue().toString().trim()+ "==" + userInformation.getName());

                textViewUser = (TextView) findViewById(R.id.textViewUser);
                textViewUser.setText("Olá, " + userInformation.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DeleteActivity:\t", "infoDatabase: onDataChange:\n\rcould not get new data");
            }
        });

        // callback para mudanças nas informações de dispositivos
        devicesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // quando atualiza as informações de dispositivos
                Log.d("DeleteActivity: " ,"devicesDatabase: onDataChange: \n\tcount "+
                        dataSnapshot.getChildrenCount());

                devices.clear();
                // para o numero de filhos em dataSnapshot existe um único postSnapshot por iteração
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // pega o id
                    String id = postSnapshot.child("id").getValue().toString().trim();
                    // se não for o padrão
                    if(!TextUtils.equals(id, "0000")) {
                        // pega o dispositivo
                        Device post = postSnapshot.getValue(Device.class);
                        devices.add(post);
                    }
                }

                customAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DeleteActivity:\t", "devicesDatabase: onDataChange:\n\rcould not get new data");
            }
        });

    }

    class CustomAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("DeleteActivity: ", "CustomAdaptor: getCount:" +
                    String.valueOf(devices.size()));
            if (devices.size() == 0){
                return 1;
            } else{
                return devices.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if(devices.size()>0) {
                view = getLayoutInflater().inflate(R.layout.custom_layout_delete, null);

                TextView textView_nick = (TextView) view.findViewById(R.id.textView_nick_delete);
                Button buttonDelete = (Button) view.findViewById(R.id.buttonDelete);

                Log.d("DeleteActivity: ", "CustomAdaptor: delete: position: " +
                        String.valueOf(position) + ": " + String.valueOf(devices.get(position).getNick()));

                textView_nick.setText(devices.get(position).getNick());

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // deleta do banco de dados
                        devicesDatabase.child( devices.get(position).getId() ).removeValue();

                        Toast.makeText(DeleteDeviceActivity.this, "Dispositivo deletado", Toast.LENGTH_SHORT).show();

                    }
                });

            }else{
                view = getLayoutInflater().inflate(R.layout.custom_layout_no_device, null);
                TextView textView_noDevice = (TextView) view.findViewById(R.id.textView_noDevice);

                textView_noDevice.setText("Ainda não há dispositivos cadastrados.");
            }
            return view;
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
            finish();
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
